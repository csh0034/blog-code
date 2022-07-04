package com.ask.resourceserver.utils

import com.ask.resourceserver.config.JWT_SECRET_KEY
import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.time.Instant

private val jwtEncoder = NimbusJwtEncoder(ImmutableSecret(JWT_SECRET_KEY.toByteArray()))

fun encode(subject: String, roles: Array<String>): String {
  val header = JwsHeader.with(MacAlgorithm.HS256)
    .type("JWT")
    .build()

  val claims = JwtClaimsSet.builder()
    .subject(subject)
    .issuedAt(Instant.now())
    .expiresAt(Instant.now().plusSeconds(3600))
    .claim("scope", roles)
    .build()

  return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
}
