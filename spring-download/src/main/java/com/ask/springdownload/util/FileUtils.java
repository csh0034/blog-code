package com.ask.springdownload.util;

import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.io.IOException;
import lombok.NoArgsConstructor;
import org.apache.tika.Tika;

@NoArgsConstructor(access = PRIVATE)
public final class FileUtils {

  private static final Tika TIKA = new Tika();

  public static String detectMediaType(File file) {
    try {
      return TIKA.detect(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
