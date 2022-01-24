package com.ask.springamqp

import com.ask.springamqp.amqp.message.SampleMessage
import com.ask.springamqp.config.AmqpConfig
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class QueueSendRunner(
    private val amqpTemplate: AmqpTemplate
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        SampleMessage("샘플 제목", "샘플 내용")
            .run {
                amqpTemplate.convertAndSend(AmqpConfig.SAMPLE_QUEUE, this)
            }
    }
}
