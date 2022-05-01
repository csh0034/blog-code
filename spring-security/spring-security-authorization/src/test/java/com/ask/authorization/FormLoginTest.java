package com.ask.authorization;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class FormLoginTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void login() throws Exception {
    // given
    String username = "user01";
    String password = "111111";

    // when
    ResultActions result = mockMvc.perform(formLogin()
        .user(username)
        .password(password));

    // then
    result.andExpectAll(status().is3xxRedirection(),
        redirectedUrl("/"),
        authenticated().withUsername(username).withRoles("USER"));
  }

  @Test
  void loginFail() throws Exception {
    // given
    String username = "user01";
    String password = "invalid";

    // when
    ResultActions result = mockMvc.perform(formLogin()
        .user(username)
        .password(password));

    // then
    result.andExpectAll(status().is3xxRedirection(),
        redirectedUrl("/login?error"),
        unauthenticated());
  }

}
