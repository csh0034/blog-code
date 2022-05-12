package com.ask.properties.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("custom3")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
@ToString
public class Custom3Properties {

  private final String content;
  private final Nested nested;

  @RequiredArgsConstructor
  @Getter
  @ToString
  public static class Nested {

    private final String title;

  }

}
