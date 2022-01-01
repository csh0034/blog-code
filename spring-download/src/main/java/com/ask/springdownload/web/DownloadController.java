package com.ask.springdownload.web;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DownloadController {

	@Value("classpath:static/spring.png")
	private Resource resource;

	@GetMapping("/download/img")
	public ResponseEntity<Resource> downloadImg() throws IOException {
		File file = resource.getFile();

		Tika tika = new Tika();
		String mediaType = tika.detect(file);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, mediaType)
			.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
				.filename(file.getName())
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
				.filename(file.getName())
				.build()
				.toString())
			.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
			.body(resource);
	}

}
