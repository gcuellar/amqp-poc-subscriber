package io.builders.amqp.poc.config

import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.annotation.PostConstruct

@Configuration
class AmqpConfiguration {

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
    TopicExchange topicExchange() {
        ExchangeBuilder
                .topicExchange(TOPIC_EXCHANGE_NAME)
                .durable(true)
                .build()
    }

    @Bean
    FanoutExchange fanoutExchange() {
        ExchangeBuilder
                .fanoutExchange(FANOUT_EXCHANGE_NAME)
                .durable(true)
                .alternate(FANOUT_ALTERNATE_EXCHANGE_NAME)
                .build()
    }

    @Bean
    FanoutExchange fanoutAlternateExchange() {
        ExchangeBuilder
                .fanoutExchange(FANOUT_ALTERNATE_EXCHANGE_NAME)
                .durable(true)
                .build()
    }

    @Bean
    Queue queue() {
        QueueBuilder
                .nonDurable(QUEUE_NAME)
                .autoDelete()
                .build()
    }

    @Bean
    Queue alternateQueue() {
        QueueBuilder
                .durable(ALTERNATE_QUEUE_NAME)
                .build()
    }

    @Bean
    Declarables amqpConfig() {
        new Declarables(
            topicExchange(),
            fanoutExchange(),
            fanoutAlternateExchange(),
            queue(),
            alternateQueue(),
            BindingBuilder.bind(fanoutExchange()).to(topicExchange()).with("poc.event.#"),
            BindingBuilder.bind(queue()).to(fanoutExchange()),
            BindingBuilder.bind(alternateQueue()).to(fanoutAlternateExchange()))
    }

}
