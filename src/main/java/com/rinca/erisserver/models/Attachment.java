package com.rinca.erisserver.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Attachment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank private String originalFilename;
    @NotBlank private String contentType;
    @NotBlank private String filename;
    @NotNull  private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    public Attachment() {}

    public Attachment(Long id, String originalFilename, String contentType, String filename, Long fileSize, Message message) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.filename = filename;
        this.fileSize = fileSize;
        this.message = message;
    }

    public Attachment(String originalFilename, String contentType, String filename, Long fileSize, Message message) {
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.filename = filename;
        this.fileSize = fileSize;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
