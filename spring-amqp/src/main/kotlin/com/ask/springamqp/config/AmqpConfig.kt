package com.ask.springamqp.config

import com.ask.springamqp.amqp.handler.SampleMessageHandler
import com.ask.springamqp.amqp.message.SampleMessage
import com.ask.springamqp.util.logger
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer
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

    private val log = logger()

    @Bean
    fun amqpInboundFlow(): IntegrationFlow {
        return integrationFlow(Amqp.inboundAdapter(messageListenerContainer(SAMPLE_QUEUE, SAMPLE_AUTO_DELETE_QUEUE))) {
            transform(Transformers.fromJson(SampleMessage::class.java))
            handle {
                sampleMessageHandler.handle(it.payload as SampleMessage)
            }
        }
    }

    @Bean
    fun deadLetterInboundFlow(): IntegrationFlow {
        return integrationFlow(Amqp.inboundAdapter(messageListenerContainer(SAMPLE_DEAD_LETTER_QUEUE))) {
            handle {
                log.info("dlq: $it")
            }
        }
    }

    private fun messageListenerContainer(vararg queueNames: String) = SimpleMessageListenerContainer(connectionFactory)
        .apply {
            setQueueNames(*queueNames)
            setPrefetchCount(1)
            setDefaultRequeueRejected(false)
            setAdviceChain(
                RetryInterceptorBuilder.stateless()
                    .backOffOptions(1000, 2.0, 10000)
                    .maxAttempts(5)
                    .recoverer(RejectAndDontRequeueRecoverer())
                    .build()
            )
        }

    @Bean
    fun sampleQueue(): Queue = QueueBuilder
        .durable(SAMPLE_QUEUE)
        .deadLetterRoutingKey(SAMPLE_DEAD_LETTER_QUEUE)
        .deadLetterExchange("")
        .build()

    @Bean
    fun sampleAutoDeleteQueue(): Queue = QueueBuilder
        .durable(SAMPLE_AUTO_DELETE_QUEUE)
        .autoDelete()
        .build()

    @Bean
    fun sampleExclusiveQueue(): Queue = QueueBuilder
        .nonDurable(SAMPLE_EXCLUSIVE_QUEUE)
        .exclusive()
        .build()

    @Bean
    fun sampleDeadLetterQueue(): Queue = QueueBuilder
        .durable(SAMPLE_DEAD_LETTER_QUEUE)
        .autoDelete()
        .build()

    @Bean
    fun jsonMessageConverter() = Jackson2JsonMessageConverter(objectMapper)

    companion object {
        const val SAMPLE_QUEUE = "sample.queue"
        const val SAMPLE_AUTO_DELETE_QUEUE = "sample.autoDelete"
        const val SAMPLE_EXCLUSIVE_QUEUE = "sample.exclusive"
        const val SAMPLE_DEAD_LETTER_QUEUE = "sample.deadLetter.queue"
    }

}
