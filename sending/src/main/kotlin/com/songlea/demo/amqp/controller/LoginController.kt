package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.model.ResponseData
import com.songlea.demo.amqp.model.UserModel
import com.songlea.demo.amqp.service.LoginService
import com.songlea.demo.amqp.util.ProjectCommonUtil
import com.songlea.demo.amqp.util.IdentifyCodeUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.util.regex.Pattern
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 登录Controller
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/login")
class LoginController @Autowired constructor(private val loginService: LoginService) {

    // 常见邮箱正则表达式
    val emailPattern: Pattern = Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$")

    // 登录界面
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index(): String = "login"

    // 登录
    @ResponseBody
    @RequestMapping(value = ["/in"], method = [RequestMethod.POST])
    fun signIn(request: HttpServletRequest, response: HttpServletResponse,
               loginUsername: String?, loginPassword: String?, code: String?): ResponseData {
        // 界面传入参数的非空验证
        if (loginUsername.isNullOrBlank())
            return ResponseData.ExceptionEnum.NO_USER_NAME.getResult()
        if (loginPassword.isNullOrEmpty())
            return ResponseData.ExceptionEnum.NO_PASSWORD.getResult()
        if (code.isNullOrEmpty())
            return ResponseData.ExceptionEnum.NO_VERIFICATION_CODE.getResult()
        // 验证码正确性验证
        val sessionCode: String? = request.session.getAttribute(ResponseData.VERIFICATION_CODE_NAME) as? String
        if (sessionCode.isNullOrEmpty() || sessionCode?.toLowerCase() != code)
            return ResponseData.ExceptionEnum.ERROR_VERIFICATION_CODE.getResult()
        // 用户与密码的正确性验证
        val encryptPass: String = ProjectCommonUtil.getStringBySHA256(loginPassword!!)
        val user: UserModel? = loginService.getUserByUsernameAndPass(loginUsername!!, encryptPass)
        if (user != null) {
            // 将登录用户的基本信息保存在Session中
            request.session.setAttribute(ResponseData.USER_MODEL, user)
            // 将SessionId放到Cookie中判断登录状态
            val cookie = Cookie(ResponseData.COOKIE_NAME, request.session.id)
            cookie.maxAge = ResponseData.COOKIE_MAX_AGE
            cookie.isHttpOnly = ResponseData.COOKIE_HTTP_ONLY
            cookie.path = ResponseData.COOKIE_PATH
            cookie.secure = ResponseData.COOKIE_SECURE
            cookie.comment = ResponseData.COOKIE_COMMENT
            response.addCookie(cookie)
            return ResponseData(null)
        }
        return ResponseData.ExceptionEnum.ERROR_USER_OR_PASSWORD.getResult()
    }

    // 登出
    @RequestMapping(value = ["/out"], method = [RequestMethod.GET])
    fun signOut(request: HttpServletRequest, response: HttpServletResponse): String {
        val cookie = Cookie(ResponseData.COOKIE_NAME, request.session.id)
        cookie.maxAge = 0  // 时间设置为0则在浏览器中删除
        cookie.path = ResponseData.COOKIE_PATH
        cookie.isHttpOnly = ResponseData.COOKIE_HTTP_ONLY
        cookie.secure = ResponseData.COOKIE_SECURE
        response.addCookie(cookie)
        // 清除服务端相应request的session信息
        request.session.invalidate()
        return "redirect:" + request.contextPath + ResponseData.LOGIN_URL
    }

    // 获取验证码
    @RequestMapping(value = ["/getIdentifyCode"], method = [RequestMethod.GET])
    fun getIdentifyCode(request: HttpServletRequest, response: HttpServletResponse) {
        response.contentType = MediaType.IMAGE_JPEG_VALUE
        // 禁止图像在浏览器端缓存
        response.setHeader("Pragma", "no-cache")
        response.setHeader("Cache-Control", "no-cache")
        response.setDateHeader("Expires", 0)
        val codeUtil = IdentifyCodeUtil(100, 30, 4, 10)
        // 将验证码保存于Session中以便登录时验证
        request.session.setAttribute(ResponseData.VERIFICATION_CODE_NAME, codeUtil.getCode())
        codeUtil.write(response.outputStream)
    }

    // 用户注册
    @ResponseBody
    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
    fun register(username: String?, email: String?, password: String?): ResponseData {
        // 界面传入参数的验证
        if (username.isNullOrBlank())
            return ResponseData.ExceptionEnum.NO_USER_NAME.getResult()
        if (username!!.length > 32)
            return ResponseData.ExceptionEnum.LONG_USER_NAME.getResult()
        if (email.isNullOrBlank())
            return ResponseData.ExceptionEnum.NO_EMAIL.getResult()
        if (password.isNullOrEmpty())
            return ResponseData.ExceptionEnum.NO_PASSWORD.getResult()
        if (password!!.length < 6)
            return ResponseData.ExceptionEnum.SHORT_PASSWORD.getResult()
        if (!emailPattern.matcher(email!!).matches())
            return ResponseData.ExceptionEnum.INVALID_EMAIL.getResult()
        // 参数已进行非空验证
        return loginService.saveUserModel(username, email, password)
    }

    // 找回密码(发送一个随机密码到邮箱)
    @ResponseBody
    @RequestMapping(value = ["/resetPassword"], method = [RequestMethod.POST])
    fun resetPassword(email: String?): ResponseData {
        if (email.isNullOrBlank())
            return ResponseData.ExceptionEnum.NO_EMAIL.getResult()
        if (!emailPattern.matcher(email!!).matches())
            return ResponseData.ExceptionEnum.INVALID_EMAIL.getResult()
        // 已非空验证
        return loginService.resetPassword(email)
    }
}