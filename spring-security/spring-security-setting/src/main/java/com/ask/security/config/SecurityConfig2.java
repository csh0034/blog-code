package com.ask.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
public class SecurityConfig2 {

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().antMatchers("/resources/**");
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf().disable()
        .headers()
          .frameOptions().disable().and()
        .authorizeRequests()
          .antMatchers("/user/**").hasRole("USER")
          .anyRequest().authenticated().and()
        .formLogin()
          .loginPage("/user/login").permitAll()
          .defaultSuccessUrl("/index").and()
        .logout()
          .logoutUrl("/user/logout").and()
        .build();
  }

}
