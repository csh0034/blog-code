package com.ask.resourceserver

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
internal class ResourceServerTest(
  @Autowired private val mockMvc: MockMvc
) {

  @Test
  fun `토큰이 없을 경우 401`() {
    mockMvc.get("/message")
      .andExpect {
        status { isUnauthorized() }
      }
  }

}
