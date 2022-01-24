package com.ask.springamqp

import com.ask.springamqp.amqp.message.SampleMessage
import com.ask.springamqp.util.logger
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@SpringBootTest
class AmqpAdminTest @Autowired constructor(
    private val amqpAdmin: AmqpAdmin,
    private val amqpTemplate: AmqpTemplate
) {

    @Test
    fun `queue test`() {
        SampleMessage("테스트 제목", "테스트 내용")
            .run {
                amqpTemplate.convertAndSend(TEST_QUEUE, this)
            }
        amqpAdmin.deleteQueue(TEST_QUEUE)
    }

    @TestConfiguration
    class TestConfig {

        private val log = logger()

        @Bean
        fun testQueue() = Queue(TEST_QUEUE)

        @RabbitListener(queues = [TEST_QUEUE])
        fun handle(message: SampleMessage) {
            log.info("test message arrived : $message")
        }
    }

    companion object {
        const val TEST_QUEUE = "test.queue"
    }
}
