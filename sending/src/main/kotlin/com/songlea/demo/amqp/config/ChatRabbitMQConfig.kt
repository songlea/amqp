package com.songlea.demo.amqp.config

import com.songlea.demo.amqp.AmqpSendingApplication
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 定义界面发送的未消费的消息的队列、交换器与路由
 *
 * @see RabbitConfig
 * @author Song Lea
 */
@Configuration
class ChatRabbitMQConfig {

    @Bean(name = ["chatQueue"])
    fun chatQueue(): Queue {
        return Queue(AmqpSendingApplication.CHAT_QUEUE_NAME, true, false, false, null)
    }

    @Bean(name = ["chatExchange"])
    fun chatExchange(): TopicExchange {
        return TopicExchange(AmqpSendingApplication.CHAT_EXCHANGE_NAME, true, false)
    }

    @Bean(name = ["chatBinding"])
    fun chatBinding(@Qualifier("chatQueue") queue: Queue, @Qualifier("chatExchange") exchange: TopicExchange): Binding {
        return BindingBuilder.bind(queue).to(exchange).with(AmqpSendingApplication.CHAT_ROUTING_KEY)
    }
}