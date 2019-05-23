package com.songlea.demo.amqp.controller

import com.rabbitmq.client.Channel
import com.songlea.demo.amqp.AmqpReceivingApplication
import com.songlea.demo.amqp.config.WebSocketConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * 接收消息Controller
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/message")
class MessageController(@Autowired private val simpMessagingTemplate: SimpMessagingTemplate) {

    private val logger: Logger = LoggerFactory.getLogger(MessageController::class.java)

    // 界面
    @RequestMapping(value = ["/index"], method = [RequestMethod.GET])
    fun index() = "message"

    // 监听RabbitMQ的指定队列并消费消息
    @RabbitListener(queues = [AmqpReceivingApplication.CHAT_QUEUE_NAME])
    @RabbitHandler
    fun getMessage(@Payload data: String?, message: Message, channel: Channel) {
        // 将从RabbitMQ中接收的数据通过WebSocket推送到界面
        simpMessagingTemplate.convertAndSend(WebSocketConfig.TOPIC_REQUIRE, data ?: "")
        logger.info("从RabbitMQ中获取到的消息【${data ?: ""}】推送到界面！")
        // 确认消费,从队列中删除此消息
        channel.basicAck(message.messageProperties.deliveryTag, false)
    }
}