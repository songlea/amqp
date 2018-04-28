package com.songlea.demo.amqp.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

/**
 * WebSocket配置
 *
 * @author Song Lea
 */
@Configuration
@EnableWebSocketMessageBroker  // 表示开启使用STOMP协议来传输基于代理的消息
@EnableWebSocket
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint(END_POINT_PATH).withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker(DESTINATION_PREFIX)
    }

    companion object {
        const val END_POINT_PATH = "/endpointMessage"
        const val DESTINATION_PREFIX = "/prefixMessage"
        const val TOPIC_REQUIRE = "$DESTINATION_PREFIX/getRabbitMQ"
    }
}