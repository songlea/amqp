package com.songlea.demo.amqp

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus

/**
 * AMQP:即Advanced Message Queuing Protocol,高级消息队列协议(应用层协议)
 * 消息中间件主要用于组件之间的解耦,消息的发送者无需知道消息使用者的存在,反之亦然,RabbitMQ是一个开源的AMQP实现
 *
 * @author Song Lea
 */
@SpringBootApplication
// @EnableRabbit和@Configuration一起使用,可以加在类或者方法上,这个注解开启了容器对注册的bean的@RabbitListener检查
@EnableRabbit
class AmqpSendingApplication {

    companion object {
        const val TOPIC_EXCHANGE_NAME = "spring-boot-topic-exchange"
        const val TOPIC_QUEUE_NAME = "spring-boot-topic-queue"
        const val TOPIC_ROUTING_KEY = "spring.boot.topic.#"
        const val CONFIRM_CALLBACK_ROUTING_KEY = "spring.boot.topic.test"

        const val RPC_EXCHANGE_NAME = "spring-boot-rpc-exchange"
        const val RPC_QUEUE_NAME = "spring-boot-rpc-queue"
        const val RPC_ROUTING_KEY = "spring.boot.rpc"
        const val RETURN_CALLBACK_ROUTING_KEY = "testReturnCallbackRoutingKey"

        // 保存界面发送的未消费的消息
        const val CHAT_EXCHANGE_NAME = "chat-exchange"
        const val CHAT_QUEUE_NAME = "chat-queue"
        const val CHAT_ROUTING_KEY = "chat.topic.#"
        const val CHAT_ROUTING_KEY_SONG = "chat.topic.song"
    }

    @Bean
    // Spring Boot 2.0+以上使用WebServerFactoryCustomizer,而EmbeddedServletContainerCustomizer已经删除
    fun containerCustomizer(): WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        return WebServerFactoryCustomizer { configurableServletWebServerFactory ->
            val error404Page = ErrorPage(HttpStatus.NOT_FOUND, "/404.html")
            val error500Page = ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html")
            // 设置自定义的400与500界面
            configurableServletWebServerFactory.addErrorPages(error404Page, error500Page)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<AmqpSendingApplication>(*args)
}
