package io.builders.amqp.poc.consumer

import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Component

@Component
class AlternateProcessor extends MessageListenerAdapter{

    AlternateProcessor() {
        super()
        setDefaultListenerMethod("receive")
        this.containerAckMode(AcknowledgeMode.MANUAL)
    }

    void receive(String message){
        println "Alternate processor: Message payload ---> " + message
    }
}