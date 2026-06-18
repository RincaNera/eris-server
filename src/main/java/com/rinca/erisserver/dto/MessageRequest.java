package com.rinca.erisserver.dto;

public record MessageRequest(
		String content,
		String topicId
) {
}
