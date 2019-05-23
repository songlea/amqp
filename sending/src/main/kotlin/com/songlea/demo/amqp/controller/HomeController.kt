package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.message.ScheduleSender
import com.songlea.demo.amqp.model.ResponseData
import com.songlea.demo.amqp.model.UserModel
import com.songlea.demo.amqp.service.LoginService
import com.songlea.demo.amqp.util.ProjectCommonUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * 主页Controller
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/home")
class HomeController @Autowired constructor(private val scheduleSender: ScheduleSender,
                                            private val loginService: LoginService) {

    // 主页
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index() = "home"

    // 发送信息(暂存到RabbitMQ中)
    @RequestMapping(value = ["/chat"], method = [RequestMethod.POST])
    @ResponseBody
    fun chat(message: String?): ResponseData {
        if (message.isNullOrBlank())
            return ResponseData.ExceptionEnum.NO_MESSAGE.getResult()
        scheduleSender.sendChatMessage(message)
        // 将完成时间返回到前台
        return ResponseData(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
    }

    // 获取登录用户名
    @ResponseBody
    @RequestMapping(value = ["/username"], method = [RequestMethod.GET])
    fun getDefaultUser(request: HttpServletRequest): String {
        val userModel: UserModel? = request.session.getAttribute(ResponseData.USER_MODEL) as? UserModel
        return userModel?.username ?: ProjectCommonUtil.DEFAULT_EMPTY
    }

    // 修改密码
    @ResponseBody
    @RequestMapping(value = ["/updatePassword"], method = [RequestMethod.POST])
    fun updatePassword(request: HttpServletRequest, oldPassword: String?, newPassword: String?): ResponseData {
        if (oldPassword == newPassword)
            return ResponseData.ExceptionEnum.SAME_PASSWORD.getResult()
        if (oldPassword.isNullOrEmpty())
            return ResponseData.ExceptionEnum.NO_OLD_PASSWORD.getResult()
        if (newPassword.isNullOrEmpty())
            return ResponseData.ExceptionEnum.NO_NEW_PASSWORD.getResult()
        if (newPassword.length < 6)
            return ResponseData.ExceptionEnum.SHORT_PASSWORD.getResult()
        // 从session中获取登录用户
        val userModel: UserModel = request.session.getAttribute(ResponseData.USER_MODEL) as? UserModel
                ?: return ResponseData.ExceptionEnum.OVER_TIME_LOGIN.getResult()
        return loginService.updatePassword(request, userModel, oldPassword, newPassword)
    }
}