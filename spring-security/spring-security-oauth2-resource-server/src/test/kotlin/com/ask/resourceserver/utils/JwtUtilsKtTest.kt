package com.ask.resourceserver.utils

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.Instant

internal class JwtUtilsKtTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun generateJwt() {
    log.info("jwt: ${generateJwt("ASk", arrayOf("message:read", "message:write"))}")
    log.info("jwt: ${generateJwt("ASk", arrayOf("message:read", "message:write"), Instant.now())}")
  }

}
