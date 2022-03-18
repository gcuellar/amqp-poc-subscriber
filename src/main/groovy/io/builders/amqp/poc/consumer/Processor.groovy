package io.builders.amqp.poc.consumer

import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Component

@Component
class Processor extends MessageListenerAdapter{

    Processor() {
        super()
        setDefaultListenerMethod("receive")
        this.containerAckMode(AcknowledgeMode.AUTO)
    }

    void receive(String message){
        println "Processor: Message payload ---> " + message
    }
}
