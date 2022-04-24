package com.ask.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig4 {

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
    return http.csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .authorizeRequests(authorize -> authorize
            .antMatchers("/user").hasRole("USER")
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/user/login").permitAll()
            .defaultSuccessUrl("/index"))
        .logout(logout -> logout
            .logoutUrl("/user/logout"))
        .build();
  }

}
