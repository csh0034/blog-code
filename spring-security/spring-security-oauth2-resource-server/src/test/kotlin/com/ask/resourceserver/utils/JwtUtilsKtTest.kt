package com.ask.resourceserver.utils

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class JwtUtilsKtTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun encode() {
    val jwt = encode("ASk", arrayOf("message:read", "message:write"))
    log.info("jwt: $jwt")
  }

}
