package com.ask.springmqtt

import com.ask.springmqtt.config.MqttConfig.MqttOutboundGateway
import com.ask.springmqtt.config.MqttProperties
import com.ask.springmqtt.mqtt.message.SampleMessage
import com.ask.springmqtt.util.logger
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@ConfigurationPropertiesScan
class SpringMqttApplication {

    private val log = logger()

    @Bean
    fun printMqttProperties(mqttProperties: MqttProperties) = ApplicationRunner() {
        log.info("=====================")
        log.info("$mqttProperties")
        log.info("=====================")
    }

    @Bean
    fun sendSampleMessage(mqttOutboundGateway: MqttOutboundGateway) = ApplicationRunner() {
        mqttOutboundGateway.publish(SampleMessage("title1", "message1"))
    }
}

fun main(args: Array<String>) {
    runApplication<SpringMqttApplication>(*args)
}
