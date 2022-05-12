package com.ask.properties;

import com.ask.properties.config.Custom2Properties;
import com.ask.properties.config.Custom3Properties;
import com.ask.properties.config.CustomProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
@Slf4j
public class SpringPropertiesApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringPropertiesApplication.class, args);
  }

  @Bean
  public ApplicationRunner applicationRunner(CustomProperties customProperties, Custom2Properties custom2Properties,
      Custom3Properties custom3Properties) {
    return args -> log.info("customProperties: {}, custom2Properties: {}, custom3Properties: {}", customProperties,
        custom2Properties, custom3Properties);
  }

}
