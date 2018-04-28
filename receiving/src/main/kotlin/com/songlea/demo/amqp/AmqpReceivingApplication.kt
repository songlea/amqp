package com.songlea.demo.amqp

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * AMQP:即Advanced Message Queuing Protocol,高级消息队列协议(应用层协议)
 * 消息中间件主要用于组件之间的解耦,消息的发送者无需知道消息使用者的存在,反之亦然,RabbitMQ是一个开源的AMQP实现
 *
 * @author Song Lea
 */
@SpringBootApplication
// @EnableRabbit和@Configuration一起使用,可以加在类或者方法上,这个注解开启了容器对注册的bean的@RabbitListener检查
@EnableRabbit
class AmqpReceivingApplication {

    companion object {
        const val TOPIC_QUEUE_NAME = "spring-boot-topic-queue"
        const val RPC_QUEUE_NAME = "spring-boot-rpc-queue"
        // 界面发送的未消费的消息队列名
        const val CHAT_QUEUE_NAME = "chat-queue"
    }
}

fun main(args: Array<String>) {
    runApplication<AmqpReceivingApplication>(*args)
}
