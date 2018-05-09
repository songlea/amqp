package com.songlea.demo.amqp.service

import com.songlea.demo.amqp.dao.LoginDao
import com.songlea.demo.amqp.model.ResponseData
import com.songlea.demo.amqp.model.UserModel
import com.songlea.demo.amqp.util.ProjectCommonUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

/**
 * 登录Service层
 *
 * @author Song Lea
 */
@Service
class LoginService @Autowired constructor(private val loginDao: LoginDao, private val javaMailSender: JavaMailSender) {

    @Value("\${spring.mail.username}")
    private var from: String = ""

    // 根据用户名与密码判断用户表是否有记录
    @Transactional(readOnly = true)
    fun getUserByUsernameAndPass(username: String, password: String): UserModel? {
        return loginDao.getUserByUsernameAndPass(username, password)
    }

    // 保存用户注册信息
    @Transactional(propagation = Propagation.REQUIRED)
    fun saveUserModel(username: String, email: String, password: String): ResponseData {
        // 重复性校验
        if (loginDao.checkRepeatUsername(username))
            return ResponseData.ExceptionEnum.REPEAT_USER_NAME.getResult()
        if (loginDao.checkRepeatEmail(email))
            return ResponseData.ExceptionEnum.REPEAT_EMAIL.getResult()
        // 密码加密
        val encryptPass: String = ProjectCommonUtil.getStringBySHA256(password)
        return if (loginDao.saveUserModel(username, email, encryptPass) > 0)
            ResponseData(null)
        else
            ResponseData.ExceptionEnum.EXCEPTION_SYSTEM_BUSY.getResult()
    }

    // 找回/重置密码
    @Transactional(propagation = Propagation.REQUIRED)
    fun resetPassword(email: String): ResponseData {
        // 动态生成一个8位数的密码
        val password: String = ProjectCommonUtil.randomPassword()
        // 密码加密与本地更新
        val update: Int = loginDao.resetPassword(email, ProjectCommonUtil.getStringBySHA256(password))
        return if (update > 0) {
            val text = "您好 $email！\n您重置的新密码：$password\n如果您没有请求重置密码，请忽略这封邮件。"
            // 发送重置密码邮件
            ProjectCommonUtil.mailNotice(javaMailSender, from, arrayOf(email), "重置密码信息", text)
            ResponseData(null)
        } else {
            ResponseData.ExceptionEnum.NO_REGISTER_EMAIL.getResult()
        }
    }

    // 修改密码
    @Transactional(propagation = Propagation.REQUIRED)
    fun updatePassword(request: HttpServletRequest, userModel: UserModel, oldPassword: String,
                       newPassword: String): ResponseData {
        // 验证老密码
        val validateUser: UserModel = loginDao.getUserByUsernameAndPass(userModel.username,
                ProjectCommonUtil.getStringBySHA256(oldPassword))
                ?: return ResponseData.ExceptionEnum.WRONG_OLD_PASSWORD.getResult()
        // 更新新密码
        val update: Int = loginDao.resetPassword(validateUser.email, ProjectCommonUtil.getStringBySHA256(newPassword))
        return if (update > 0) {
            // 清除服务器session,强制浏览器重新登录
            request.session.invalidate()
            ResponseData(null)
        } else {
            ResponseData.ExceptionEnum.EXCEPTION_SYSTEM_BUSY.getResult()
        }
    }
}