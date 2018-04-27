package com.songlea.demo.amqp.interceptor

import com.songlea.demo.amqp.model.ResponseData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 登录拦截器
 *
 * @author Song Lea
 */
@Configuration
class LoginInterceptor : HandlerInterceptorAdapter() {

    private val logger: Logger = LoggerFactory.getLogger(LoginInterceptor::class.java)

    // 请求之前
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val cookies: Array<Cookie>? = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                // 判断请求中的Cookie信息且有相同的SessionId,否则需要重新登录
                if (cookie.name == ResponseData.COOKIE_NAME && cookie.value == request.session.id)
                    return true
            }
        }
        if (request.requestURI.endsWith("index")) {
            // 约定所有以/index结尾的URL为加载界面的请求
            response.sendRedirect(request.contextPath + ResponseData.LOGIN_URL)
        } else {
            // ajax请求不能使用response.sendRedirect()来重定向
            response.writer.println("No Login")
        }
        logger.warn("请求【{}】中无对应Cookie信息,跳转到登录界面！", request.requestURI)
        return false
    }
}