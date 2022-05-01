package com.ask.authorization;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationTest {

  @Autowired
  private MockMvc mockMvc;

  @DisplayName("인증된 유저가 아닐 경우 로그인 페이지로 이동")
  @Test
  void withoutUser() throws Exception {
    // when
    ResultActions result = mockMvc.perform(get("/"));

    // then
    result.andExpectAll(status().is3xxRedirection(),
        redirectedUrlPattern("**/login"),
        unauthenticated());
  }

  @DisplayName("USER 권한일 경우 user 페이지 접근 가능")
  @Test
  @WithUserDetails("user01")
  void userAccess() throws Exception {
    // when
    ResultActions result = mockMvc.perform(get("/user"));

    // then
    result.andExpect(status().isOk());
  }

  @DisplayName("ADMIN 권한일 경우 admin 페이지 접근 가능")
  @Test
  @WithUserDetails("admin01")
  void adminAccess() throws Exception {
    // when
    ResultActions result = mockMvc.perform(get("/admin"));

    // then
    result.andExpect(status().isOk());
  }

  @DisplayName("USER 권한일 경우 admin 페이지 접근 실패")
  @Test
  @WithUserDetails("user01")
  void userAccessDenied() throws Exception {
    // when
    ResultActions result = mockMvc.perform(get("/admin"));

    // then
    result.andExpectAll(status().isForbidden(),
        authenticated().withUsername("user01").withRoles("USER"));
  }

  @DisplayName("ADMIN 권한일 경우 user 페이지 접근 실패")
  @Test
  @WithUserDetails("admin01")
  void adminAccessDenied() throws Exception {
    // when
    ResultActions result = mockMvc.perform(get("/user"));

    // then
    result.andExpectAll(status().isForbidden(),
        authenticated().withUsername("admin01").withRoles("ADMIN"));
  }

}
