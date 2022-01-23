package com.ask.springamqp.config

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.Transformers
import org.springframework.integration.dsl.integrationFlow

@Configuration
class AmqpConfig(
    private val connectionFactory: ConnectionFactory,
    private val rabbitTemplate: RabbitTemplate
) {

    companion object {
        const val SAMPLE_QUEUE = "sample.queue1"
        const val AMQP_OUTBOUND_CHANNEL = "outboundChannel"
    }

    @Bean
    fun amqpInboundFlow(): IntegrationFlow {
        return integrationFlow(Amqp.inboundAdapter(connectionFactory, SAMPLE_QUEUE)) {
            transform(Transformers.objectToString())
            handle {
                println(it.payload)
            }
        }
    }

    @Bean
    fun amqpOutboundFlow(): IntegrationFlow {
        return integrationFlow(AMQP_OUTBOUND_CHANNEL) {
            handle(Amqp.outboundAdapter(rabbitTemplate).routingKey(SAMPLE_QUEUE))
        }
    }

    @MessagingGateway(defaultRequestChannel = AMQP_OUTBOUND_CHANNEL)
    interface AmqpOutboundGateway {

        @Gateway
        fun send(data: String)
    }
}
