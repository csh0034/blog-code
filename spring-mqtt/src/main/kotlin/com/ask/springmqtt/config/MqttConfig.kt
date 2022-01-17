package com.ask.springmqtt.config

import com.ask.springmqtt.mqtt.handler.SampleMessageHandler
import com.ask.springmqtt.mqtt.message.SampleMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.dsl.Transformers
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter
import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.handler.annotation.Header

@Configuration
class MqttConfig(
    private val sampleMessageHandler: SampleMessageHandler,
    private val mqttProperties: MqttProperties,
    private val objectMapper: ObjectMapper,
) {

    companion object {
        const val MQTT_OUTBOUND_CHANNEL = "outboundChannel"
    }

    @Bean
    fun mqttPahoClientFactory(): MqttPahoClientFactory {
        return DefaultMqttPahoClientFactory()
            .apply {
                connectionOptions = connectOptions()
            }
    }

    private fun connectOptions(): MqttConnectOptions {
        return MqttConnectOptions()
            .apply {
                serverURIs = arrayOf(mqttProperties.connectionInfo())
            }
    }

    @Bean
    fun mqttInboundFlow() = integrationFlow(mqttChannelAdapter()) {
        transform(Transformers.fromJson(SampleMessage::class.java))
        handle {
            sampleMessageHandler.handle(it.payload as SampleMessage)
        }
    }

    private fun mqttChannelAdapter(): MqttPahoMessageDrivenChannelAdapter {
        return MqttPahoMessageDrivenChannelAdapter(
            MqttClient.generateClientId(),
            mqttPahoClientFactory(),
            mqttProperties.topic)
            .apply {
                setCompletionTimeout(5000)
                setConverter(DefaultPahoMessageConverter())
                setQos(mqttProperties.qos)
            }
    }

    @Bean
    fun mqttOutboundFlow() = integrationFlow(MQTT_OUTBOUND_CHANNEL) {
        transform<Any> {
            when (it) {
                is SampleMessage -> objectMapper.writeValueAsString(it)
                else -> it
            }
        }
        handle(mqttOutboundMessageHandler())
    }

    private fun mqttOutboundMessageHandler(): MessageHandler {
        return MqttPahoMessageHandler(MqttAsyncClient.generateClientId(), mqttPahoClientFactory())
            .apply {
                setAsync(true)
                setDefaultTopic(mqttProperties.topic)
                setDefaultQos(mqttProperties.qos)
            }
    }

    @MessagingGateway(defaultRequestChannel = MQTT_OUTBOUND_CHANNEL)
    interface MqttOutboundGateway {

        @Gateway
        fun publish(@Header(MqttHeaders.TOPIC) topic: String, data: String)

        @Gateway
        fun publish(data: SampleMessage)
    }
}
