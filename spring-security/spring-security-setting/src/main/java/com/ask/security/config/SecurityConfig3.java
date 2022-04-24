package com.ask.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
public class SecurityConfig3 {

  @Bean
  @Order(0)
  public SecurityFilterChain resources(HttpSecurity http) throws Exception {
    return http.requestMatchers(matchers -> matchers
            .antMatchers("/resources/**"))
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().permitAll())
        .requestCache(RequestCacheConfigurer::disable)
        .securityContext(AbstractHttpConfigurer::disable)
        .sessionManagement(AbstractHttpConfigurer::disable)
        .build();
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
        . logoutUrl("/user/logout").and()
        .build();
  }

}
