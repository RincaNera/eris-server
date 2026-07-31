package com.rinca.erisserver.controllers;

import com.rinca.erisserver.dto.MessageResponse;
import com.rinca.erisserver.dto.mappers.MessageResponseMapper;
import com.rinca.erisserver.exceptions.InvalidTokenException;
import com.rinca.erisserver.exceptions.TopicNotFoundException;
import com.rinca.erisserver.models.Message;
import com.rinca.erisserver.models.Topic;
import com.rinca.erisserver.models.User;
import com.rinca.erisserver.services.AuthService;
import com.rinca.erisserver.services.MessageService;
import com.rinca.erisserver.services.TopicService;
import com.rinca.erisserver.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

	private final TopicService topicService;
	private final MessageService messageService;
	private final AuthService authService;
	private final UserService userService;
	private final SimpMessagingTemplate messagingTemplate;

	public MessageController(
		TopicService topicService,
		MessageService messageService,
		AuthService authService,
		UserService userService,
		SimpMessagingTemplate messagingTemplate
	) {
		this.topicService = topicService;
		this.messageService = messageService;
		this.authService = authService;
		this.userService = userService;
		this.messagingTemplate = messagingTemplate;
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public MessageResponse sendMessage(
		@RequestParam String content,
		@RequestParam UUID topicId,
		@RequestParam(name = "file", required = false) List<MultipartFile> files) {
		Long userId = authService.getAuthenticatedUser()
			.orElseThrow(() -> new InvalidTokenException("Utilisateur non authentifié")).getId();
		User user = userService.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Utilisateur inexistant"));
		Topic topic = topicService.getTopicById(topicId)
			.orElseThrow(() -> new TopicNotFoundException("Topic non trouvé"));
		Message message = messageService.saveMessage(user, content, topic, files);
		MessageResponse messageResponse = MessageResponseMapper.toResponseMessage(message);
		messagingTemplate.convertAndSend("/topic/" + topic.getId(), messageResponse);
		return messageResponse;
	}

}
