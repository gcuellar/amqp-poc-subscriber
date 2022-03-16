package io.builders.amqp.poc.config

import io.builders.amqp.poc.consumer.Processor
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.annotation.PostConstruct

@Configuration
class AMQPConfiguration {

    @Value('${rabbitmq.poc.exchange}')
    String EXCHANGE_NAME
    String QUEUE_NAME

    @PostConstruct
    void init(){
        QUEUE_NAME = UUID.randomUUID().toString()+"@"+EXCHANGE_NAME
    }

    @Bean
    RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory)

        Queue queue = QueueBuilder
                .nonDurable(QUEUE_NAME)
                .autoDelete()
                .build()
        FanoutExchange fanoutExchange = ExchangeBuilder
                .fanoutExchange(EXCHANGE_NAME)
                .durable(true)
                .build()

        rabbitAdmin.declareExchange(fanoutExchange)
        rabbitAdmin.declareQueue(queue)
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(fanoutExchange))
    }

    @Bean
    SimpleMessageListenerContainer container(CachingConnectionFactory connectionFactory, Processor processor) {
        new SimpleMessageListenerContainer().tap {
            it.setConnectionFactory(connectionFactory)
            it.setQueueNames(QUEUE_NAME)
            it.setMessageListener(processor)
        }
    }

}
