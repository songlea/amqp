package com.songlea.demo.amqp.interceptor

import com.songlea.demo.amqp.model.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.regex.Pattern
import javax.servlet.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

/**
 * WebMvc配置
 *
 * @author Song Lea
 */
@Configuration
class MyWebMvcConfigurer(@Autowired private val loginInterceptor: LoginInterceptor) : WebMvcConfigurer, Filter {

    val druidUrlPattern: Pattern = Pattern.compile("^[\\s\\S]*/druid/[\\s\\S]*$")

    // 添加拦截器
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginInterceptor)
                // 匹配需要拦截的请求(优化级 > excludePath)
                .addPathPatterns("/home/*")
                // 匹配不拦截的请求(登录界面请求)
                .excludePathPatterns("/login/*")
    }

    // 过滤器
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (request is HttpServletRequest) {
            val url: String? = request.requestURI
            if (url != null && druidUrlPattern.matcher(url).matches()) {
                val cookies: Array<out Cookie> = request.cookies
                for (cookie: Cookie in cookies) {
                    if (cookie.name == ResponseData.COOKIE_NAME && cookie.value == request.session.id) {
                        // 登录未过期请求放行
                        chain?.doFilter(request, response)
                        return
                    }
                }
                // 界面输出提示重新登录
                response?.contentType = MediaType.TEXT_HTML_VALUE
                response?.characterEncoding = Charsets.UTF_8.name()
                response?.writer?.write("抱歉，您未登录或登录已失效，请重新" +
                        "<a style='text-decoration:none;' href='${request.contextPath}/login/index'> 登录 </a>！")
                return
            }
        }
        chain?.doFilter(request, response)
    }

    override fun destroy() {
    }

    override fun init(filterConfig: FilterConfig?) {
    }
}