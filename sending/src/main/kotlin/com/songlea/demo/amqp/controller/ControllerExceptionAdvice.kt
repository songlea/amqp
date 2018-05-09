package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.model.ResponseData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

/**
 * 统一的异常处理类
 * 即把@ControllerAdvice注解内部使用@ExceptionHandler、@InitBinder、@ModelAttribute注解的方法
 * 应用到所有的 @RequestMapping注解的方法。
 *
 * @author Song Lea
 */
@ControllerAdvice
class ControllerExceptionAdvice {

    private val logger: Logger = LoggerFactory.getLogger(ControllerExceptionAdvice::class.java)

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun httpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseData {
        val uri = getRequestUri()
        logger.error("请求的方式不对(POST/GET)  接口地址：$uri", ex)
        return ResponseData.ExceptionEnum.EXCEPTION_METHOD_NOT_SUPPORTED.getResult()
    }

    @ResponseBody
    @ExceptionHandler(ServletRequestBindingException::class)
    fun servletRequestBindingExceptionHandler(ex: ServletRequestBindingException): ResponseData {
        val uri = getRequestUri()
        logger.error("请求的参数不完整  接口地址：$uri", ex)
        return ResponseData.ExceptionEnum.EXCEPTION_LACK_PARAMETER.getResult()
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun methodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException): ResponseData {
        val uri = getRequestUri()
        logger.error("请求方法参数格式不匹配  接口地址：$uri", ex)
        return ResponseData.ExceptionEnum.EXCEPTION_ARGUMENT_TYPE_MISMATCH.getResult()
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun httpMediaTypeNotSupportedException(ex: HttpMediaTypeNotSupportedException): ResponseData {
        val uri = getRequestUri()
        logger.error("请求的MIME类型不支持  接口地址：$uri", ex)
        return ResponseData.ExceptionEnum.EXCEPTION_MEDIA_TYPE_NOT_SUPPORTED.getResult()
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException::class)
    fun httpMediaTypeNotAcceptableException(ex: HttpMediaTypeNotAcceptableException): ResponseData {
        val uri = getRequestUri()
        logger.error("请求的MINE类型不接受  接口地址：$uri", ex)
        return ResponseData.ExceptionEnum.EXCEPTION_MEDIA_TYPE_NOT_ACCEPTABLE.getResult()
    }

    @ResponseBody
    @ExceptionHandler(Exception::class)
    fun defaultExceptionHandler(ex: Exception): ResponseData {
        val uri = getRequestUri()
        logger.error("请求出现异常  接口地址：$uri", ex)
        return ResponseData.ExceptionEnum.EXCEPTION_SYSTEM_BUSY.getResult()
    }

    // 获取请求的uri地址
    private fun getRequestUri(): String {
        val servletRequestAttributes: ServletRequestAttributes =
                RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        return servletRequestAttributes.request.requestURI ?: ""
    }
}