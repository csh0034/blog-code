package com.ask.springmqtt.mqtt.handler

import com.ask.springmqtt.mqtt.message.SampleMessage
import com.ask.springmqtt.util.logger
import org.springframework.stereotype.Component

@Component
class SampleMessageHandler {

    private val log = logger()

    fun handle(message: SampleMessage) {
        log.info("message arrived : $message")
    }
}
