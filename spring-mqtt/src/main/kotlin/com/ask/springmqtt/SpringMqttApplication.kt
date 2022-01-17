package com.ask.springmqtt

import com.ask.springmqtt.config.MqttProperties
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
}

fun main(args: Array<String>) {
    runApplication<SpringMqttApplication>(*args)
}
