package com.songlea.demo.amqp.message

import com.songlea.demo.amqp.AmqpSendingApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.support.CorrelationData
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@EnableScheduling
@Component
class ScheduleSender(@Autowired private val rabbitTemplate: RabbitTemplate) {

    private val logger: Logger = LoggerFactory.getLogger(ScheduleSender::class.java)

    init {
        rabbitTemplate.apply {
            /**
             * ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调。
             * 如果生产者需要消息发送后的回调，需要对rabbitTemplate设置ConfirmCallback对象，
             * 由于不同的生产者需要对应不同的ConfirmCallback，如果rabbitTemplate设置为单例bean，
             * 则所有的rabbitTemplate实际的ConfirmCallback为最后一次申明的ConfirmCallback。
             */
            // 消息是否到达交换机的回调
            this.setConfirmCallback({ correlationData, ack, cause ->
                run {
                    // https://stackoverflow.com/questions/40058613/how-to-configure-end-to-end-publisher-confirms-with-spring-amqp
                    // 只有在负责队列的Erlang进程中发生内部错误时才会传递basic.nack，这时参数ack才会为false。
                    // 类似的，消费者方面的ack/nack(即channel.basicNack或channel.basicReject或channel.basicAck)纯粹是消费
                    // 者是否有已接收消息的责任，且nack允许消息被重新入队列,丢弃或路由到死信队列。
                    // 一旦消息被发布，消费者就没有回到发布者的通信，如果你需要这样的通信就需要设置回复队列。
                    // 如果想要发布者与消费者之间的紧密耦合，可以使用Spring Remoting(RPC) Over RabbitMQ，这时消费者引发异常，
                    // 它将被传播回发布者。(该机制仅支持Java Serializable对象)
                    if (ack)
                        logger.info("Message【CorrelationData.id：${correlationData.id}】成功到达Exchange")
                    else
                        logger.info("Message【CorrelationData.id：${correlationData.id}】未到达Exchange：$cause")
                }
            })

            /**
             * ReturnCallback接口用于实现消息发送到RabbitMQ交换器，但无相应队列与交换器绑定时的回调。
             * (exchange有但routingKey没有与对应的queue绑定时的回调函数)
             * 需要rabbitTemplate.setMandatory(true)生效(已在application.yml文件中配置)
             */
            // 消息是否到达正确的消息队列，如果没有会把消息返回
            this.setReturnCallback { message, replyCode, replyText,
                                     exchange, routingKey ->
                run {
                    logger.info("Exchange无对应的RoutingKey到Queue时回调：" +
                            "message:$message text: $replyText code: $replyCode exchange: $exchange routingKey :$routingKey")
                }
            }
            // 数据由json格式进行转换
            this.messageConverter = Jackson2JsonMessageConverter()

            // RPC调用时的接收数据与等待返回超时时间
            this.setReceiveTimeout(10_000) // 默认为0，此时是非阻塞的当没有可用的消息时返回null
            this.setReplyTimeout(10_1000) // 默认5000
        }
    }

    // @Scheduled(cron = "0/40 * * * * ?")
    fun sendMessage() {
        val date: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        /**
         * 发送消息(publish)参数
         * exchange:交换机名称
         * routingKey:路由关键字
         * object:发送的消息内容
         * correlationData:消息发布者确认的关联数据(ID)
         */
        // 会有confirmCallback回调(因为消息能到达Exchange)
        val publishConfirmId: String = UUID.randomUUID().toString()
        val publishData: List<Student> = listOf(Student(1, "publish_${Math.random()}"))
        rabbitTemplate.convertAndSend(AmqpSendingApplication.TOPIC_EXCHANGE_NAME,
                AmqpSendingApplication.CONFIRM_CALLBACK_ROUTING_KEY, publishData, CorrelationData(publishConfirmId))
        logger.info("Sending publish message @ $date，correlationData.id：$publishConfirmId ，data：$publishData")

        // 会有confirmCallback回调与returnCallback回调(因为消息能到达Exchange但无法路由到对应的Queue)
        val returnId = UUID.randomUUID().toString()
        rabbitTemplate.convertAndSend(AmqpSendingApplication.TOPIC_EXCHANGE_NAME,
                AmqpSendingApplication.RETURN_CALLBACK_ROUTING_KEY, "noneTest", CorrelationData(returnId))
        logger.info("Sending publish message @ $date，id：$returnId，but no routingKey to queue.")

        /**
         * 发送消息(RPC)参数
         * exchange:交换机名称
         * routingKey:路由关键字
         * message:发送的消息
         * messagePostProcessor:消息发送前的处理器
         * correlationData:消息发布者确认的关联数据(ID)
         * responseType:回复数据的转换类型
         */
        val rpcConfirmId = UUID.randomUUID().toString()
        val rpcData = Student(2, "rpc_${Math.random()}")
        val ret: Any? = rabbitTemplate.convertSendAndReceive(AmqpSendingApplication.RPC_EXCHANGE_NAME,
                AmqpSendingApplication.RPC_ROUTING_KEY, rpcData, null, CorrelationData(rpcConfirmId))
        // 这里返回的String无法被Jackson2JsonMessageConverter转换成json,自己解析成String并处理
        // RabbitMQ的RPC模式的机制暂只支持Java Serializable对象
        val response: String? = (ret as? ByteArray)?.toString(Charsets.UTF_8) ?: ret?.toString()
        logger.info("Sending rpc message @ $date & return: $response")
    }
}