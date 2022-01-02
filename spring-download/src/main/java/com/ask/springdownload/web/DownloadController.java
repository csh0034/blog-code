package com.ask.springdownload.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DownloadController {

	private static final String SAMPLE_FILE_NAME = "스프링.png";

	@Value("classpath:static/spring.png")
	private Resource resource;

	@GetMapping("/download/img")
	public ResponseEntity<Resource> downloadImg() {
		return ResponseEntity.ok()
			.contentType(MediaType.IMAGE_PNG)
			.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
				.filename(SAMPLE_FILE_NAME, StandardCharsets.UTF_8)
				.build()
				.toString())
			.body(resource);
	}

	@GetMapping("/download/file")
	public ResponseEntity<Resource> downloadFile() {
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
				.filename(SAMPLE_FILE_NAME, StandardCharsets.UTF_8)
				.build()
				.toString())
			.body(resource);
	}
}
