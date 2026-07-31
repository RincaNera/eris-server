package com.rinca.erisserver.dto.mappers;

import com.rinca.erisserver.dto.AttachmentResponse;
import com.rinca.erisserver.dto.MessageResponse;
import com.rinca.erisserver.models.Attachment;
import com.rinca.erisserver.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageResponseMapper {
    public static MessageResponse toResponseMessage(Message message) {
        List<Attachment> attachments = message.getAttachments() == null ? new ArrayList<>() : message.getAttachments();
        return new MessageResponse(
            message.getId(),
            message.getUser().getId(),
            message.getUser().getUsername(),
            message.getUser().getAvatar(),
            message.getCreatedAt(),
            message.getContent(),
            message.getTopic().getId().toString(),
            attachments.stream()
                .map(attachment -> new AttachmentResponse(
                    attachment.getId(),
                    attachment.getOriginalFilename(),
                    attachment.getContentType(),
                    attachment.getFilename(),
                    attachment.getFileSize()
                )).toList()
        );
    }
}
