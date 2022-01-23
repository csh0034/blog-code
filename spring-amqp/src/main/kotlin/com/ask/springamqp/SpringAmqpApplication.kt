package com.ask.springamqp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringAmqpApplication

fun main(args: Array<String>) {
    runApplication<SpringAmqpApplication>(*args)
}
