package com.ask.springamqp.amqp.handler

import com.ask.springamqp.amqp.message.SampleMessage
import com.ask.springamqp.util.logger
import org.springframework.stereotype.Component

@Component
class SampleMessageHandler {

    private val log = logger()

    fun handle(message: SampleMessage) {
        log.info("message arrived : $message")
    }
}
