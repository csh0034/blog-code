package com.ask.springbeanio.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BeanIODto {

  private Map<String, Object> header;
  private Map<String, Object> body;
  private String end;

}
