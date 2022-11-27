package com.ask.springbeanio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringBeanioApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBeanioApplication.class, args);
  }

}
