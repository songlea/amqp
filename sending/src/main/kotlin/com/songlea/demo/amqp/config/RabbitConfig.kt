package com.songlea.demo.amqp.config

import com.songlea.demo.amqp.AmqpSendingApplication
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * rabbitMq配置
 * 消息生产者声明队列Queue、交换器Exchange、Queue与Exchange绑定的routingKey
 *
 * @author Song Lea
 */
@Configuration
class RabbitConfig {

    /**
     * Queue（队列）是RabbitMQ的内部对象,用于存储消息.
     * RabbitMQ中的消息都只能存储在Queue中,生产者生产消息并最终投递到Queue中,消费者可以从Queue中获取消息并消费.
     * 多个消费者可以订阅同一个Queue,这时Queue中的消息会被平均分摊给多个消费者进行处理,而不是每个消费者都收到所有的消息并处理.
     */
    @Bean(name = ["queue"])
    fun queue(): Queue {
        /**
         * Message durability(小概率的丢失必须使用事务):
         * 如果我们希望即使在RabbitMQ服务重启的情况下，也不会丢失消息，
         * 我们可以将Queue与Message都设置为可持久化的（durable），这样可以保证绝大部分情况下我们的RabbitMQ消息不会丢失。
         * name：队列名，必须不能为null，若为""则自动生成名称
         * durable：是否持久化的队列(服务重启后队列还在)
         * exclusive：是否排它队列(队列仅由声明者的连接使用)
         * autoDelete：队列不在使用时服务是否自动删除它
         * arguments：声明队列的参数
         *      x-max-length：queue消息条数限制(限制加入queue中消息的条数，先进先出原则，超过10条后面的消息会顶替前面的消息)
         *      x-max-length-bytes：queue消息容量限制(该参数和x-max-length目的一样限制队列的容量，但是这个是靠队列大小（bytes）来达到限制)
         *      x-message-ttl：queue消息存活时间(创建queue时设置该参数可指定消息在该queue中待多久，
         *          可根据x-dead-letter-routing-key和x-dead-letter-exchange生成可延迟的死信队列)
         *      x-dead-letter-routing-key，x-dead-letter-exchange：创建queue时参数arguments设置了x-dead-letter-routing-key和x-dead-letter-exchange，
         *          会在x-message-ttl时间到期后把消息放到x-dead-letter-routing-key和x-dead-letter-exchange指定的队列中达到延迟队列的目的
         *      x-expires：queue存活时间(创建queue时参数arguments设置了x-expires参数，该queue会在x-expires到期后消失(即使有未消费的消息))
         *      x-max-priority：消息优先级(该参数会造成额外的CPU消耗)
         */
        return Queue(AmqpSendingApplication.TOPIC_QUEUE_NAME, true, false, false, null)
    }

    /**
     * Exchange:生产者将消息投递到Queue中，实际上这在RabbitMQ中这种事情永远都不会发生。
     * 实际的情况是，生产者将消息发送到Exchange，由Exchange将消息路由到一个或多个Queue中（或者丢弃）。
     * Exchange Types:(fanout、direct、topic、headers)
     * 1、fanout：fanout类型的Exchange路由会把所有发送到该Exchange的消息路由到所有与它绑定的Queue中
     * 2、direct：direct类型的Exchange路由会把消息路由到那些binding key与routing key完全匹配的Queue中
     * 3、topic：将消息路由到binding key与routing key相匹配的Queue中，规则：
     *      routing key为一个句点号“.”分隔的字符串
     *      binding key与routing key一样也是句点号“. ”分隔的字符串
     *      binding key中可以存在两种特殊字符“*”与“#”，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）
     * 4、headers：headers类型的Exchange不依赖于routing key与binding key的匹配规则来路由消息，而是根据发送的消息内容中的headers属性进行匹配。
     * 构造方法参数：
     *      name：Exchange名称
     *      durable：是否持久化
     *      autoDelete：不使用时不自动删除
     */
    @Bean(name = ["exchange"])
    fun exchange(): TopicExchange {
        return TopicExchange(AmqpSendingApplication.TOPIC_EXCHANGE_NAME, true, false)
    }

    /**
     * Binding:RabbitMQ中通过Binding将Exchange与Queue关联起来，这样RabbitMQ就知道如何正确地将消息路由到指定的Queue
     * Binding key:在绑定（Binding）Exchange与Queue的同时，一般会指定一个binding key；
     * 消费者将消息发送给Exchange时，一般会指定一个routing key；当binding key与routing key相匹配时，消息将会被路由到对应的Queue中
     */
    @Bean(name = ["binding"])
    fun binding(@Qualifier("queue") queue: Queue, @Qualifier("exchange") exchange: TopicExchange): Binding {
        /**
         * routing key:生产者在将消息发送给Exchange的时候，一般会指定一个routing key，来指定这个消息的路由规则，
         * 而这个routing key需要与Exchange Type及binding key联合使用才能最终生效，
         * 我们的生产者就可以在发送消息给Exchange时，通过指定routing key来决定消息流向哪里(大小限制255 bytes)
         */
        return BindingBuilder.bind(queue).to(exchange).with(AmqpSendingApplication.TOPIC_ROUTING_KEY)
    }

    // 声明RPC调用的Queue/Exchange/RoutingKey
    @Bean(name = ["rpcQueue"])
    fun rpcQueue(): Queue {
        return Queue(AmqpSendingApplication.RPC_QUEUE_NAME, true, false, false, null)
    }

    @Bean(name = ["rpcExchange"])
    fun rpcExchange(): DirectExchange {
        return DirectExchange(AmqpSendingApplication.RPC_EXCHANGE_NAME, true, false)
    }

    @Bean(name = ["rpcBinding"])
    fun rpcBinding(@Qualifier("rpcQueue") rpcQueue: Queue, @Qualifier("rpcExchange") rpcExchange: DirectExchange): Binding {
        return BindingBuilder.bind(rpcQueue).to(rpcExchange).with(AmqpSendingApplication.RPC_ROUTING_KEY)
    }

    /*
    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = Jackson2JsonMessageConverter()
        return template
    }

    @Bean
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(Jackson2JsonMessageConverter())
        return factory
    }

    @Bean(name = ["rabbitAdmin"])
    fun rabbitAdmin(rabbitTemplate: RabbitTemplate) : RabbitAdmin {
        return RabbitAdmin(rabbitTemplate)
    }
    */

    /**
     * ConnectionFactory、Connection、Channel:RabbitMQ对外提供的API中最基本的对象
     * Connection是RabbitMQ的socket链接,它封装了socket协议相关部分逻辑;
     * ConnectionFactory为Connection的制造工厂;
     * Channel是我们与RabbitMQ打交道的最重要的一个接口,我们大部分的业务操作是在Channel这个接口中完成的,包括定义Queue、定义Exchange、绑定Queue与Exchange、发布消息等.
     */
    /*
    @Bean
    fun container(connectionFactory: ConnectionFactory, listenerAdapter: MessageListenerAdapter): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer()
        container.setQueueNames(AmqpApplication.QUEUE_NAME)
        container.connectionFactory = connectionFactory
        container.messageListener = listenerAdapter
        /**
         * Prefetch count:限制Queue每次发送给每个消费者的消息数
         * 如果有多个消费者同时订阅同一个Queue中的消息，Queue中的消息会被平摊给多个消费者
         * 我们可以通过设置prefetchCount来限制Queue每次发送给每个消费者的消息数，
         * 比如我们设置prefetchCount=1，则Queue每次给每个消费者发送一条消息；消费者处理完这条消息后Queue会再给该消费者发送一条消息
         */
        container.setPrefetchCount(250)
        /**
         * Message acknowledgment:消息确认机制
         * 在实际应用中,可能会发生消费者收到Queue中的消息,但没有处理完成就宕机（或出现其他意外）的情况,这种情况下就可能会导致消息丢失
         * 为了避免这种情况发生,我们可以要求消费者在消费完消息后发送一个回执给RabbitMQ,RabbitMQ收到消息回执（Message acknowledgment）后才将该消息从Queue中移除
         * 如果RabbitMQ没有收到回执并检测到消费者的RabbitMQ连接断开,则RabbitMQ会将该消息发送给其他消费者（如果存在多个消费者）进行处理
         * 这里不存在timeout概念,一个消费者处理消息时间再长也不会导致该消息被发送给其他消费者,除非它的RabbitMQ连接断开
         * 这里会产生另外一个问题,如果我们的开发人员在处理完业务逻辑后,忘记发送回执给RabbitMQ,这将会导致严重的bug
         * Queue中堆积的消息会越来越多,消费者重启后会重复消费这些消息并重复执行业务逻辑…
         * 另外publish message是没有ack的
         */
        container.acknowledgeMode = AcknowledgeMode.AUTO
        return container
    }
    */

    /*
    @Bean
    fun listenerAdapter(receiver: Receiver): MessageListenerAdapter {
        return MessageListenerAdapter(receiver)
    }
     */
}
