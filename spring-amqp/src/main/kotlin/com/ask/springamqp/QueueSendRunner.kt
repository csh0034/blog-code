package com.ask.springamqp

import com.ask.springamqp.config.AmqpConfig
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class QueueSendRunner(
    private val amqpOutboundGateway: AmqpConfig.AmqpOutboundGateway,
    private val rabbitTemplate: RabbitTemplate
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        amqpOutboundGateway.send("amqpOutboundGateway send!")
        rabbitTemplate.convertAndSend(AmqpConfig.SAMPLE_QUEUE, "rabbitTemplate send!")
    }
}
