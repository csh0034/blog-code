package com.ask.springbeanio.config;

import lombok.RequiredArgsConstructor;
import org.beanio.Marshaller;
import org.beanio.StreamFactory;
import org.beanio.Unmarshaller;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeanIOUtils {

  private final StreamFactory streamFactory;
  private final BeanIOProperties beanIOProperties;

  public String marshal(BeanIODto dto) {
    Marshaller marshaller = streamFactory.createMarshaller(beanIOProperties.getStreamName());
    return marshaller.marshal(beanIOProperties.getRecordName(), dto).toString();
  }

  public BeanIODto unmarshal(String record) {
    Unmarshaller unmarshaller = streamFactory.createUnmarshaller(beanIOProperties.getStreamName());
    return (BeanIODto) unmarshaller.unmarshal(record);
  }

}
