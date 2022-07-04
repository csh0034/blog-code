package com.ask.resourceserver.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController {

  @GetMapping("/message")
  fun message() = "GET message..."

  @PostMapping("/message")
  fun message(message: String) = "POST $message..."

}
