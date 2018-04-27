package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.model.ResponseData
import com.songlea.demo.amqp.util.IdentifyCodeUtil
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
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
class LoginController {

    // 登录界面
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index(): String = "login"

    // 登录
    @RequestMapping(value = ["/in"], method = [RequestMethod.POST])
    @ResponseBody
    fun signIn(request: HttpServletRequest, response: HttpServletResponse,
               loginUsername: String?, loginPassword: String?, code: String?): ResponseData {
        // 界面传入参数的非空验证
        if (loginUsername.isNullOrBlank())
            return ResponseData(ResponseData.LOGIN_PAGE_ERROR_CODE, ResponseData.NO_USER_NAME)
        if (loginPassword.isNullOrEmpty())
            return ResponseData(ResponseData.LOGIN_PAGE_ERROR_CODE, ResponseData.NO_PASSWORD)
        if (code.isNullOrEmpty())
            return ResponseData(ResponseData.LOGIN_PAGE_ERROR_CODE, ResponseData.NO_VERIFICATION_CODE)
        // 验证码正确性验证
        val sessionCode: String? = request.session.getAttribute(ResponseData.VERIFICATION_CODE_NAME) as? String
        if (sessionCode.isNullOrEmpty() || sessionCode?.toLowerCase() != code)
            return ResponseData(ResponseData.LOGIN_PAGE_ERROR_CODE, ResponseData.ERROR_VERIFICATION_CODE)
        // 用户与密码正确性验证
        if (ResponseData.USERNAME == loginUsername && ResponseData.PASSWORD == loginPassword) {
            val cookie = Cookie(ResponseData.COOKIE_NAME, request.session.id)
            cookie.maxAge = ResponseData.COOKIE_MAX_AGE
            cookie.isHttpOnly = ResponseData.COOKIE_HTTP_ONLY
            cookie.path = ResponseData.COOKIE_PATH
            cookie.secure = ResponseData.COOKIE_SECURE
            cookie.comment = ResponseData.COOKIE_COMMENT
            response.addCookie(cookie)
            return ResponseData(null)
        }
        return ResponseData(ResponseData.LOGIN_PAGE_ERROR_CODE, ResponseData.ERROR_USER_OR_PASSWORD)
    }

    // 登出
    @RequestMapping(value = ["/out"], method = [RequestMethod.GET])
    fun signOut(request: HttpServletRequest, response: HttpServletResponse): String {
        val cookie = Cookie(ResponseData.COOKIE_NAME, request.session.id)
        // 时间设置为0则在浏览器中删除
        cookie.maxAge = 0
        cookie.isHttpOnly = ResponseData.COOKIE_HTTP_ONLY
        cookie.path = ResponseData.COOKIE_PATH
        cookie.secure = ResponseData.COOKIE_SECURE
        cookie.comment = ResponseData.COOKIE_COMMENT
        response.addCookie(cookie)
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
        val code = IdentifyCodeUtil(100, 30, 4, 10)
        // 将验证码保存于Session中以便登录时验证
        request.session.setAttribute(ResponseData.VERIFICATION_CODE_NAME, code.getCode())
        code.write(response.outputStream)
    }

    // 获取登录用户名
    fun getDefaultUser(): String {
        return ResponseData.USERNAME
    }
}