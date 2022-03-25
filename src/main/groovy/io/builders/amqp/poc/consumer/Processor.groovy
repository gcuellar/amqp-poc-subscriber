package io.builders.amqp.poc.consumer


import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class Processor {

    @RabbitListener(id="processorListener", queues = "#{amqpConfiguration.QUEUE_NAME}")
    void receive(String message){
        println "Processor: Message payload ---> " + message
    }
}
