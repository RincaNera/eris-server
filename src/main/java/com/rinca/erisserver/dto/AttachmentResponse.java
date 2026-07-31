package com.rinca.erisserver.dto;


public record AttachmentResponse(
    Long id,
    String originalFilename,
    String contentType,
    String filename,
    Long fileSize
) {
}
