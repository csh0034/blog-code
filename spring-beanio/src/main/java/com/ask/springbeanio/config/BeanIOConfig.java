package com.ask.springbeanio.config;

import lombok.RequiredArgsConstructor;
import org.beanio.StreamFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanIOConfig {

  private final BeanIOProperties beanIOProperties;

  @Bean
  public StreamFactory streamFactory() {
    StreamFactory factory = StreamFactory.newInstance();
    factory.loadResource(beanIOProperties.getMapping());
    return factory;
  }

}
