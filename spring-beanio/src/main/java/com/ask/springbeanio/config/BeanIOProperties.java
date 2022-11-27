package com.ask.springbeanio.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("beanio")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
@ToString
public class BeanIOProperties {

  private final String mapping;
  private final String streamName;
  private final String recordName;

}
