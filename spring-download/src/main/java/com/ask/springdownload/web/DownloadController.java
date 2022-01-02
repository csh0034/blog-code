package com.ask.springdownload.web;

import java.io.File;
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

import com.ask.springdownload.util.FileUtils;

@Controller
public class DownloadController {

	public static final String SAMPLE_FILE_NAME = "스프링.png";

	@Value("classpath:static/spring.png")
	private Resource resource;

	@GetMapping("/download/img")
	public ResponseEntity<Resource> downloadImg() throws IOException {
		File file = resource.getFile();

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, FileUtils.detectMediaType(file))
			.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
				.filename(SAMPLE_FILE_NAME, StandardCharsets.UTF_8)
				.build()
				.toString())
			.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
			.body(resource);
	}

	@GetMapping("/download/file")
	public ResponseEntity<Resource> downloadFile() throws IOException {
		File file = resource.getFile();

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
			.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
				.filename(SAMPLE_FILE_NAME, StandardCharsets.UTF_8)
				.build()
				.toString())
			.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
			.body(resource);
	}
}
