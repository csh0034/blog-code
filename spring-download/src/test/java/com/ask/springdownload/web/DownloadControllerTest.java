package com.ask.springdownload.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(DownloadController.class)
@Slf4j
class DownloadControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void downloadImg() throws Exception {
    // when
    MvcResult mvcResult = mockMvc.perform(get("/download/img"))
        .andExpect(status().isOk())
        .andReturn();

    // then
    MockHttpServletResponse response = mvcResult.getResponse();

    int contentLength = response.getContentLength();
    String contentType = response.getContentType();
    String contentDisposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);

    assertAll(
        () -> assertThat(contentLength).isEqualTo(9183),
        () -> assertThat(contentType).isEqualTo(MediaType.IMAGE_PNG_VALUE),
        () -> assertThat(contentDisposition).contains("inline", "UTF-8")
    );

    // inline; filename*=UTF-8''%EC%8A%A4%ED%94%84%EB%A7%81.png
    log.info("contentDisposition : {}", contentDisposition);
  }

  @Test
  void downloadFile() throws Exception {
    // when
    MvcResult mvcResult = mockMvc.perform(get("/download/file"))
        .andExpect(status().isOk())
        .andReturn();

    // then
    MockHttpServletResponse response = mvcResult.getResponse();

    int contentLength = response.getContentLength();
    String contentType = response.getContentType();
    String contentDisposition = response.getHeader(HttpHeaders.CONTENT_DISPOSITION);

    assertAll(
        () -> assertThat(contentLength).isEqualTo(9183),
        () -> assertThat(contentType).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE),
        () -> assertThat(contentDisposition).contains("attachment", "UTF-8")
    );

    // attachment; filename*=UTF-8''%EC%8A%A4%ED%94%84%EB%A7%81.png
    log.info("contentDisposition : {}", contentDisposition);
  }
}
