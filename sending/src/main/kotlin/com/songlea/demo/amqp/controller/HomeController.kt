package com.songlea.demo.amqp.controller

import com.songlea.demo.amqp.message.ScheduleSender
import com.songlea.demo.amqp.model.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.text.SimpleDateFormat
import java.util.*

/**
 * 主页Controller
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/home")
class HomeController(@Autowired private val scheduleSender: ScheduleSender) {

    // 主页
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index() = "home"

    // 发送信息(暂存到RabbitMQ中)
    @RequestMapping(value = ["/chat"], method = [RequestMethod.POST])
    @ResponseBody
    fun chat(message: String?): ResponseData {
        if (message.isNullOrBlank())
            return ResponseData(ResponseData.HOME_PAGE_ERROR_CODE, ResponseData.NO_MESSAGE)
        scheduleSender.sendChatMessage(message!!)
        // 将完成时间返回到前台
        return ResponseData(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
    }
}