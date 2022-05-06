package com.ask.properties.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("custom2")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
@ToString
public class Custom2Properties {

  private final String content;

}
