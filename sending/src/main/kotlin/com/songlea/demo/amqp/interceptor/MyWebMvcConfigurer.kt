package com.songlea.demo.amqp.interceptor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MyWebMvcConfigurer(@Autowired private val loginInterceptor: LoginInterceptor) : WebMvcConfigurer {

    // 添加拦截器
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loginInterceptor)
                // 匹配需要拦截的请求(优化级 > excludePath)
                .addPathPatterns("/home/*")
                // 匹配不拦截的请求(登录界面请求)
                .excludePathPatterns("/login/*")
    }
}