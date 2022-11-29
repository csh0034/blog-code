package com.ask.springbeanio.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class BeanIOUtilsIntegrationTest {

  @Autowired
  private BeanIOUtils beanIOUtils;

  @Test
  void marshal() {
    // given
    BeanIODto dto = new BeanIODto();

    Map<String, Object> header = new HashMap<>();
    header.put("TX_CODE", "a12");
    header.put("GUID", UUID.randomUUID().toString());
    header.put("LENGTH", 45);
    dto.setHeader(header);

    Map<String, Object> body = new HashMap<>();
    body.put("MESSAGE", "HI !!");
    body.put("TO", "010-1234-5678");
    dto.setBody(body);

    // when
    String result = beanIOUtils.marshal(dto);

    // then
    assertThat(result).hasSize(79);
    log.info("result: {}", result);
  }

  @Test
  void unmarshal() {
    // given
    String record = "a12            b1ef6fcf-6371-4459-9ecb-b7e1d48a00045HI !!     010-1234-5678  @@";

    // when
    BeanIODto dto = beanIOUtils.unmarshal(record);

    // then
    assertAll(
        () -> assertThat(dto).isNotNull(),
        () -> assertThat(dto.getHeader()).hasSize(3),
        () -> assertThat(dto.getBody()).hasSize(2),
        () -> assertThat(dto.getEnd()).isEqualTo("@@")
    );
    log.info("dto: {}", dto);
  }

}
