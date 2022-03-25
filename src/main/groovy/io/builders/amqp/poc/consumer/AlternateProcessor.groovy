package io.builders.amqp.poc.consumer


import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class AlternateProcessor {

    @RabbitListener(queues = '#{amqpConfiguration.ALTERNATE_QUEUE_NAME}')
    void receive(String message){
        println "Alternate processor: Message payload ---> " + message
    }
}