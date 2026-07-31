package com.rinca.erisserver.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record MessageRequest(
		String content,
		String topicId,
		List<MultipartFile> files
) {
}
