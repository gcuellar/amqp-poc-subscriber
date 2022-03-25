package io.builders.amqp.poc.consumer

import io.builders.amqp.poc.AmqpTestConfiguration
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness
import org.springframework.amqp.rabbit.test.mockito.LatchCountDownAndCallRealMethodAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.*

@SpringBootTest
@Import(AmqpTestConfiguration)
@ActiveProfiles("test")
class ProcessorTest {

    @Value('${rabbitmq.poc.topic-exchange}')
    private String exchange

    @Autowired
    private RabbitListenerTestHarness harness

    @Autowired
    private RabbitTemplate rabbitTemplate

    @Test
    void messageReceivedTest_Ok(){
        String msg = "Hello World!"
        Processor processorListener = this.harness.getSpy("processorListener")
        assertThat(processorListener).isNotNull()

        /*
            One thread listening one message. For two messages example go to:
             https://github.com/spring-projects/spring-amqp/blob/main/spring-rabbit-test/src/test/java/org/springframework/amqp/rabbit/test/examples/ExampleRabbitListenerSpyTest.java
         */
        LatchCountDownAndCallRealMethodAnswer answer = this.harness.getLatchAnswerFor("processorListener",1)
        doAnswer(answer).when(processorListener).receive(msg)

        rabbitTemplate.convertAndSend(exchange,"poc.event.#", msg)

        assertThat(answer.await(5)).isTrue()
        verify(processorListener,times(1)).receive(msg)
    }
}
