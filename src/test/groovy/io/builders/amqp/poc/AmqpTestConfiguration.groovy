package io.builders.amqp.poc

import io.builders.amqp.poc.consumer.Processor
import org.springframework.amqp.rabbit.test.RabbitListenerTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
@RabbitListenerTest
class AmqpTestConfiguration {

    @Bean
    @Primary
    Processor processor() {
        new Processor()
    }
}
