package com.ask.springamqp

import com.ask.springamqp.amqp.message.SampleMessage
import com.ask.springamqp.util.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean


@SpringBootTest
class AmqpAdminTest @Autowired constructor(
    private val amqpAdmin: AmqpAdmin,
    private val amqpTemplate: AmqpTemplate,
) {

    private val log = logger()

    @Test
    fun `direct exchange`() {
        // given
        amqpAdmin.declareQueue(Queue(TEST_QUEUE))
        val message = SampleMessage("테스트 제목", "테스트 내용")

        // when
        amqpTemplate.convertAndSend(TEST_QUEUE, message)
        val receivedMessage = amqpTemplate.receiveAndConvert(TEST_QUEUE)
        amqpAdmin.deleteQueue(TEST_QUEUE)

        // then
        assertAll(
            { assertThat(receivedMessage).isEqualTo(message) },
            { assertThat(amqpAdmin.getQueueInfo(TEST_QUEUE)).isNull() }
        )
    }

    @Test
    fun `fanout exchange`() {
        // given
        amqpAdmin.declareQueue(Queue(TEST_QUEUE))
        val message = SampleMessage("fanout", "fanout exchange")

        // when
        amqpTemplate.convertAndSend(FANOUT_EXCHANGE, "", message)

        val receivedMessage1 = amqpTemplate.receiveAndConvert(FANOUT_QUEUE_1)
        val receivedMessage2 = amqpTemplate.receiveAndConvert(FANOUT_QUEUE_2)

        amqpAdmin.deleteQueue(FANOUT_QUEUE_1)
        amqpAdmin.deleteQueue(FANOUT_QUEUE_2)

        // then
        assertAll(
            { assertThat(receivedMessage1).isEqualTo(message) },
            { assertThat(receivedMessage2).isEqualTo(message) },
            { assertThat(amqpAdmin.getQueueInfo(FANOUT_QUEUE_1)).isNull() },
            { assertThat(amqpAdmin.getQueueInfo(FANOUT_QUEUE_2)).isNull() }
        )
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        fun fanout(): Declarables {
            val fanoutQueue1 = Queue(FANOUT_QUEUE_1)
            val fanoutQueue2 = Queue(FANOUT_QUEUE_2)
            val fanoutExchange = FanoutExchange(FANOUT_EXCHANGE)
            return Declarables(
                fanoutQueue1,
                fanoutQueue2,
                fanoutExchange,
                BindingBuilder.bind(fanoutQueue1).to(fanoutExchange),
                BindingBuilder.bind(fanoutQueue2).to(fanoutExchange))
        }
    }

    companion object {
        const val TEST_QUEUE = "test.queue"
        const val FANOUT_EXCHANGE = "fanout.exchange"
        const val FANOUT_QUEUE_1 = "fanout.queue1"
        const val FANOUT_QUEUE_2 = "fanout.queue2"
    }
}
