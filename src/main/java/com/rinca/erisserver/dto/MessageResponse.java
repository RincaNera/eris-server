package com.rinca.erisserver.dto;

import java.util.Date;

public record MessageResponse(
		Long messageId,
		Long userId,
		String username,
		String avatar,
		Date createdAt,
		String content,
		String topicId
) {}
