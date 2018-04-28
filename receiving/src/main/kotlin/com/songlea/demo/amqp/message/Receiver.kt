package com.songlea.demo.amqp.message

import com.alibaba.fastjson.JSON
import com.rabbitmq.client.Channel
import com.songlea.demo.amqp.AmqpReceivingApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

/**
 *息消费方
 *
 * @author Song Lea
 */
@Component
class Receiver {

    private val logger: Logger = LoggerFactory.getLogger(Receiver::class.java)

    // 详细的绑定,指定Queue、Exchange与RoutingKey的定义
    // @RabbitListener(bindings = (arrayOf(QueueBinding(value = Queue(value = AmqpApplication.QUEUE_NAME, durable = "true"),
    //        exchange = Exchange(value = AmqpApplication.TOPIC_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
    //        key = arrayOf(AmqpApplication.TOPIC_ROUTING_KEY)))))
    // 只绑定对应的Queue名称,即从这个队列中获取消息
    @RabbitListener(queues = [AmqpReceivingApplication.TOPIC_QUEUE_NAME])
    // isDefault当为true时声明当有效载荷没有匹配的RabbitHandler时这是默认的回调方法,只能声明一个这样的方法
    @RabbitHandler()
    // 一个Message有两个部分：payload(有效载荷)和label(标签), payload顾名思义就是传输的数据
    // label是exchange的名字或者说是一个tag,它描述了payload,而且RabbitMQ也是通过这个label来决定把这个Message发给哪个Consumer
    fun handlePublishMessage(@Payload data: String?, message: Message, channel: Channel) {
        val receiverData: List<Student>? = JSON.parseArray(data, Student::class.java)
        if (Math.random() > 0.5 && receiverData != null) {
            // requeue 值为 true 表示该消息重新放回队列头,值为 false 表示放弃这条消息
            // channel.basicReject(message.messageProperties.deliveryTag, true)
            channel.basicNack(message.messageProperties.deliveryTag, false, true)
            logger.info("Reject publish data: < $receiverData > & requeue!")
        } else {
            // 确认这个消息收到了会将其从队列中移除
            // multiple：如果为true则表示连续取得多条消息才会发确认,false则只确认当前消息,true能够提高效率
            channel.basicAck(message.messageProperties.deliveryTag, false)
            logger.info("Received publish data: < $receiverData > & remove!")
        }
    }

    // RPC调用的消费者
    @RabbitListener(queues = [AmqpReceivingApplication.RPC_QUEUE_NAME])
    @RabbitHandler()
    fun handleRpcMessage(@Payload data: String?, message: Message, channel: Channel): String {
        val receiverData: Student? = JSON.parseObject(data, Student::class.java)
        logger.info("Received rpc data: < $receiverData > & remove!")
        message.messageProperties.contentType = MessageProperties.CONTENT_TYPE_JSON
        // 消息消费成功后的返回数据
        return "success"
    }

    /*
    // 死信队列
    const val DEAD_EXCHANGE_NAME = "x-dead-letter-exchange"
    const val DEAD_ROUTING_KEY = "x-dead-letter-routing-key"
    const val DEAD_QUEUE_NAME = "x-dead-letter-queue-name"
    var newChannel: Channel? = null
    try {
        // 声明Exchange
        newChannel = channel.connection.createChannel()
        newChannel?.exchangeDeclare(DEAD_EXCHANGE_NAME, BuiltinExchangeType.DIRECT)
        // 声明Queue
        newChannel?.queueDeclare(DEAD_QUEUE_NAME, true, false, false, null)
        // Queue通过RoutingKey绑定到相关的Exchange上
        newChannel?.queueBind(DEAD_QUEUE_NAME, DEAD_EXCHANGE_NAME, DEAD_ROUTING_KEY)
        newChannel?.basicPublish(DEAD_EXCHANGE_NAME, DEAD_ROUTING_KEY, AMQP.BasicProperties.Builder()
                .correlationId(java.util.UUID.randomUUID().toString()).build(),
                e.message?.toByteArray(Charsets.UTF_8) ?: "nothing".toByteArray(Charsets.UTF_8))
    } finally {
        newChannel?.close()
    }
   */
}