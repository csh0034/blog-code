package com.ask.resourceserver.utils

import com.ask.resourceserver.config.JWT_SECRET_KEY
import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit

private val jwtEncoder = NimbusJwtEncoder(ImmutableSecret(JWT_SECRET_KEY.toByteArray()))

fun generateJwt(subject: String, scopes: Array<String>, expiresAt: Instant = Instant.now().plus(1, ChronoUnit.HOURS)): String {
  val header = JwsHeader.with(MacAlgorithm.HS256)
    .type("JWT")
    .build()

  val claims = JwtClaimsSet.builder()
    .subject(subject)
    .expiresAt(expiresAt)
    .claim("scope", scopes)
    .build()

  return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
}
