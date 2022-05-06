package com.ask.properties.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("custom")
@ConstructorBinding
public class CustomProperties {

  private final String content;

  public CustomProperties(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return "CustomProperties{" +
        "content='" + content + '\'' +
        '}';
  }

}
