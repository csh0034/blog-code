package com.ask.resourceserver

import com.ask.resourceserver.config.JWT_SECRET_KEY
import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.time.Instant

internal class NimbusJwtEncoderTest {

  private val log = LoggerFactory.getLogger(javaClass)

  @Test
  fun `HS256 Jwt Signing`() {
    // given
    val jwtEncoder = NimbusJwtEncoder(ImmutableSecret(JWT_SECRET_KEY.toByteArray()))

    val header = JwsHeader.with(MacAlgorithm.HS256)
      .type("JWT")
      .build()

    val claims = JwtClaimsSet.builder()
      .subject("ASk")
      .issuedAt(Instant.now())
      .expiresAt(Instant.now().plusSeconds(3600))
      .claim("scope", arrayOf("message:read", "message:write"))
      .build()

    val jwtEncoderParameters = JwtEncoderParameters.from(header, claims)

    // when
    val jwt = jwtEncoder.encode(jwtEncoderParameters)

    // then
    log.info("jwt tokenValue: ${jwt.tokenValue}")
  }

}
