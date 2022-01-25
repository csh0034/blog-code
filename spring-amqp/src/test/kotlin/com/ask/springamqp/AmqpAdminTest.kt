package com.ask.springamqp

import com.ask.springamqp.amqp.message.SampleMessage
import com.ask.springamqp.util.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AmqpAdminTest @Autowired constructor(
    private val amqpAdmin: AmqpAdmin,
    private val amqpTemplate: AmqpTemplate
) {

    private val log = logger()

    @Test
    fun `queue test`() {
        amqpAdmin.declareQueue(Queue(TEST_QUEUE))

        val message = SampleMessage("테스트 제목", "테스트 내용")
        amqpTemplate.convertAndSend(TEST_QUEUE, message)

        val receivedMessage = amqpTemplate.receiveAndConvert(TEST_QUEUE)
        log.info("message $receivedMessage")

        amqpAdmin.deleteQueue(TEST_QUEUE)

        assertThat(message).isEqualTo(receivedMessage)
    }

    companion object {
        const val TEST_QUEUE = "test.queue"
    }
}
