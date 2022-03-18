package io.builders.amqp.poc.config

import io.builders.amqp.poc.consumer.AlternateProcessor
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

    @Value('${rabbitmq.poc.topic-exchange}')
    String TOPIC_EXCHANGE_NAME
    @Value('${rabbitmq.poc.fanout-exchange}')
    String FANOUT_EXCHANGE_NAME
    @Value('${rabbitmq.poc.fanout-alternate-exchange}')
    String FANOUT_ALTERNATE_EXCHANGE_NAME
    @Value('${rabbitmq.poc.queue-alternate}')
    String ALTERNATE_QUEUE_NAME

    String QUEUE_NAME

    @PostConstruct
    void init(){
        QUEUE_NAME = UUID.randomUUID().toString()+"@"+FANOUT_EXCHANGE_NAME
    }

    @Bean
    RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory){
        TopicExchange topicExchange = ExchangeBuilder
                .topicExchange(TOPIC_EXCHANGE_NAME)
                .durable(true)
                .build()

        FanoutExchange fanoutExchange = ExchangeBuilder
                .fanoutExchange(FANOUT_EXCHANGE_NAME)
                .durable(true)
                .alternate(FANOUT_ALTERNATE_EXCHANGE_NAME)
                .build()
        FanoutExchange fanoutAlternateExchange = ExchangeBuilder
                .fanoutExchange(FANOUT_ALTERNATE_EXCHANGE_NAME)
                .durable(true)
                .build()

        Queue queue = QueueBuilder
                .nonDurable(QUEUE_NAME)
                .autoDelete()
                .build()
        Queue alternateQueue = QueueBuilder
                .durable(ALTERNATE_QUEUE_NAME)
                .build()

        new RabbitAdmin(connectionFactory).tap {
            it.declareExchange(topicExchange)
            it.declareExchange(fanoutExchange)
            it.declareExchange(fanoutAlternateExchange)
            it.declareQueue(queue)
            it.declareQueue(alternateQueue)
            it.declareBinding(BindingBuilder.bind(fanoutExchange).to(topicExchange).with("poc.event.#"))
            it.declareBinding(BindingBuilder.bind(queue).to(fanoutExchange))
            it.declareBinding(BindingBuilder.bind(alternateQueue).to(fanoutAlternateExchange))
        }
    }

    @Bean
    SimpleMessageListenerContainer container(CachingConnectionFactory connectionFactory, Processor processor) {
        new SimpleMessageListenerContainer().tap {
            it.setConnectionFactory(connectionFactory)
            it.setQueueNames(QUEUE_NAME)
            it.setMessageListener(processor)
        }
    }

    @Bean
    SimpleMessageListenerContainer alternateContainer(CachingConnectionFactory connectionFactory, AlternateProcessor processor) {
        new SimpleMessageListenerContainer().tap {
            it.setConnectionFactory(connectionFactory)
            it.setQueueNames(ALTERNATE_QUEUE_NAME)
            it.setMessageListener(processor)
        }
    }

}
