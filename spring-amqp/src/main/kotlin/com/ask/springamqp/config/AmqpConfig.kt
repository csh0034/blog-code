package com.ask.springamqp.config

import com.ask.springamqp.amqp.handler.SampleMessageHandler
import com.ask.springamqp.amqp.message.SampleMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.amqp.dsl.Amqp
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.Transformers
import org.springframework.integration.dsl.integrationFlow

@Configuration
class AmqpConfig(
    private val connectionFactory: ConnectionFactory,
    private val sampleMessageHandler: SampleMessageHandler,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun amqpInboundFlow(): IntegrationFlow {
        return integrationFlow(Amqp.inboundAdapter(messageListenerContainer())) {
            transform(Transformers.fromJson(SampleMessage::class.java))
            handle {
                sampleMessageHandler.handle(it.payload as SampleMessage)
            }
        }
    }

    private fun messageListenerContainer() = SimpleMessageListenerContainer(connectionFactory)
        .apply {
            setQueueNames(SAMPLE_QUEUE)
            setPrefetchCount(1)
            setDefaultRequeueRejected(true)
        }

    @Bean
    fun sampleQueue(): Queue = QueueBuilder
        .durable(SAMPLE_QUEUE)
//        .autoDelete()
//        .exclusive()
        .build()

    @Bean
    fun jsonMessageConverter() = Jackson2JsonMessageConverter(objectMapper)

    companion object {
        const val SAMPLE_QUEUE = "sample.queue10"
    }
}
