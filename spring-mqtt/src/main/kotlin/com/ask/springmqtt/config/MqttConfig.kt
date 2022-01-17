package com.ask.springmqtt.config

import com.ask.springmqtt.mqtt.handler.SampleMessageHandler
import com.ask.springmqtt.mqtt.message.SampleMessage
import org.eclipse.paho.client.mqttv3.MqttClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.Transformers
import org.springframework.integration.dsl.integrationFlow
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter

@Configuration
class MqttConfig(
    private val sampleMessageHandler: SampleMessageHandler,
    private val mqttProperties: MqttProperties,
) {

    @Bean
    fun mqttFlow() = integrationFlow(mqttChannelAdapter()) {
        transform(Transformers.fromJson(SampleMessage::class.java))
        handle {
            sampleMessageHandler.handle(it.payload as SampleMessage)
        }
    }

    private fun mqttChannelAdapter(): MqttPahoMessageDrivenChannelAdapter {
        val adapter = MqttPahoMessageDrivenChannelAdapter(
            mqttProperties.connectionInfo(),
            MqttClient.generateClientId(),
            mqttProperties.topic)
        adapter.setCompletionTimeout(5000)
        adapter.setConverter(DefaultPahoMessageConverter())
        adapter.setQos(mqttProperties.qos)
        return adapter
    }
}
