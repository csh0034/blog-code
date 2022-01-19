package com.ask.springmqtt

import com.ask.springmqtt.config.MqttConfig
import com.ask.springmqtt.mqtt.message.SampleMessage
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class MessageSendRunner(
    private val mqttOutboundGateway: MqttConfig.MqttOutboundGateway
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        mqttOutboundGateway.publish(SampleMessage("title1", "message1"))
    }
}
