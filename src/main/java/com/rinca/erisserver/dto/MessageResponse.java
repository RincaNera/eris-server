package com.rinca.erisserver.dto;

import com.rinca.erisserver.models.Attachment;

import java.util.Date;
import java.util.List;

public record MessageResponse(
		Long messageId,
		Long userId,
		String username,
		String avatar,
		Date createdAt,
		String content,
		String topicId,
		List<AttachmentResponse> attachments
) {}
