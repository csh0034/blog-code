package com.ask.resourceserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import javax.crypto.spec.SecretKeySpec

@Configuration
class ResourceServerConfig {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
    .csrf() { it.disable() }
    .logout() { it.disable() }
    .sessionManagement() { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
    .oauth2ResourceServer { it.jwt() }
    .authorizeHttpRequests {
      it.antMatchers(HttpMethod.GET, "/message/**").hasAuthority("SCOPE_message:read")
        .antMatchers(HttpMethod.POST, "/message/**").hasAuthority("SCOPE_message:write")
        .anyRequest().authenticated()
    }
    .build()

  @Bean
  fun jwtDecoder(): JwtDecoder {
    val secretKey = SecretKeySpec("0123456789-0123456789-0123456789".toByteArray(), "HmacSHA256")
    return NimbusJwtDecoder.withSecretKey(secretKey)
      .build()
  }

}
