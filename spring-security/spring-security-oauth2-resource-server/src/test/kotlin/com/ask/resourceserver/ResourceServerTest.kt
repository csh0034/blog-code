package com.ask.resourceserver

import com.ask.resourceserver.utils.generateJwt
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
internal class ResourceServerTest(
  @Autowired private val mockMvc: MockMvc
) {

  @Test
  fun `유효한 jwt 일 경우 200`() {
    // given
    val jwt = generateJwt("ASk", arrayOf("message:read"))

    // expect
    mockMvc.get("/message") {
      header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
    }.andExpect {
      status { isOk() }
      content {
        contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
        string("GET message...")
      }
    }
  }

  @Test
  fun `헤더에 jwt 가 없을 경우 401`() {
    mockMvc.get("/message")
      .andExpect {
        status { isUnauthorized() }
      }
  }

  @Test
  fun `만료된 jwt 일 경우 401`() {
    // given
    val jwt = generateJwt("ASk", arrayOf("message:read", "message:write"), Instant.EPOCH)

    // expect
    mockMvc.get("/message") {
      header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
    }.andExpect {
      status { isUnauthorized() }
      header {
        string(HttpHeaders.WWW_AUTHENTICATE, containsString("Bearer error=\"invalid_token"))
        string(HttpHeaders.WWW_AUTHENTICATE, containsString("Jwt expired at"))
      }
    }
  }

  @Test
  fun `권한이 없는 jwt 일 경우 403`() {
    // given
    val jwt = generateJwt("ASk", arrayOf("message:read"))

    // expect
    mockMvc.post("/message") {
      header(HttpHeaders.AUTHORIZATION, "Bearer $jwt")
    }.andExpect {
      status { isForbidden() }
      header {
        string(HttpHeaders.WWW_AUTHENTICATE, containsString("Bearer error=\"insufficient_scope"))
      }
    }
  }

}
