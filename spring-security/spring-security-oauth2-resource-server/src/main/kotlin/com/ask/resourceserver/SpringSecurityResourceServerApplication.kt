package com.ask.resourceserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringSecurityResourceServerApplication

fun main(args: Array<String>) {
    runApplication<SpringSecurityResourceServerApplication>(*args)
}
