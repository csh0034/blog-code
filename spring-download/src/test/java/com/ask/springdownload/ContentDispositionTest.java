package com.ask.springdownload;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.ContentDisposition;

@Slf4j
class ContentDispositionTest {

  @ParameterizedTest
  @CsvSource({
      "스프링.png, inline; filename*=UTF-8''%EC%8A%A4%ED%94%84%EB%A7%81.png",
      "스프링1234.png, inline; filename*=UTF-8''%EC%8A%A4%ED%94%84%EB%A7%811234.png",
      "스프링-!@#$%.png, inline; filename*=UTF-8''%EC%8A%A4%ED%94%84%EB%A7%81-!%40#$%25.png"
  })
  void buildContentDisposition(String filename, String expected) {
    // when
    ContentDisposition contentDisposition = ContentDisposition.inline()
        .filename(filename, StandardCharsets.UTF_8)
        .build();

    // then
    assertThat(contentDisposition.toString()).isEqualTo(expected);
  }
}
