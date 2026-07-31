package com.rinca.erisserver.services;

import com.rinca.erisserver.models.Attachment;
import com.rinca.erisserver.models.Message;
import com.rinca.erisserver.models.Topic;
import com.rinca.erisserver.models.User;
import com.rinca.erisserver.repositories.AttachmentRepository;
import com.rinca.erisserver.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
	private final MessageRepository messageRepository;
	private final AttachmentRepository attachmentRepository;
	@Value("${app.messages.attachments-storage-path}")
	private String storagePath;

	public MessageService(
		MessageRepository messageRepository,
		AttachmentRepository attachmentRepository
	) {
		this.messageRepository = messageRepository;
		this.attachmentRepository = attachmentRepository;
	}

	public List<Message> getRecentMessages(Topic topic) {
		List<Long> ids = messageRepository.findTop50IdsByTopicOrderByCreatedAtDesc(topic);
		return messageRepository.findByIdsWithAttachmentsOrderByCreatedAtDesc(ids);
	}

	@Transactional
	public Message saveMessage(User user, String content, Topic topic, List<MultipartFile> files) {
		Message message = messageRepository.save(new Message(user, new Date(), content, topic));
		if (files != null) {
			List<Attachment> attachments = new ArrayList<>();
			files.forEach(file -> {
				Long fileSize = file.getSize();
				String originalFilename = file.getOriginalFilename();
				if (originalFilename == null || originalFilename.isBlank()) {
					originalFilename = "file";
				}

				String filename = UUID.randomUUID() + "_" + originalFilename;
				Path directory = Paths.get(storagePath, message.getId().toString());
				Path filepath = directory.resolve(filename);
				try {
					Files.createDirectories(directory);
					file.transferTo(filepath.toFile());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				Attachment attachment = new Attachment(originalFilename, file.getContentType(), filename, fileSize, message);
				this.attachmentRepository.save(attachment);
				attachments.add(attachment);
			});
			message.setAttachments(attachments);
		}
		return message;
	}

}
