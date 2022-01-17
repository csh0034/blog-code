package com.ask.springmqtt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("mqtt")
data class MqttProperties(
    val url: String,
    val port: Int,
    val qos: Int,
    val topic: String,
) {

    fun connectionInfo() = "$url:$port"
}
