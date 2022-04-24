package com.ask.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@Configuration
public class SecurityConfig1 extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/resources/**");
  }

  @Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.headers()
				.frameOptions().disable().and()
			.authorizeRequests()
				.antMatchers("/user/**").hasRole("USER")
				.anyRequest().authenticated().and()
			.formLogin()
				.loginPage("/user/login").permitAll()
				.defaultSuccessUrl("/index").and()
			.logout()
				.logoutUrl("/user/logout");
	}

}
