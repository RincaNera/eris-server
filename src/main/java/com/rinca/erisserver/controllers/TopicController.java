package com.rinca.erisserver.controllers;

import com.rinca.erisserver.dto.*;
import com.rinca.erisserver.dto.mappers.MessageResponseMapper;
import com.rinca.erisserver.exceptions.TopicNotFoundException;
import com.rinca.erisserver.models.Topic;
import com.rinca.erisserver.services.MessageService;
import com.rinca.erisserver.services.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {

	private final TopicService topicService;
	private final MessageService messageService;
	private final SimpMessagingTemplate messagingTemplate;

	public TopicController(TopicService topicService, MessageService messageService, SimpMessagingTemplate messagingTemplate) {
		this.topicService = topicService;
		this.messageService = messageService;
		this.messagingTemplate = messagingTemplate;
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public Topic create(@Valid @RequestBody TopicCreateRequest request) {
		Topic topic = topicService.createTopic(request.name());
		messagingTemplate.convertAndSend(
			"/topic/system",
			new SystemEvent("CREATE", topic.getId().toString(), topic.getName())
		);
		return topic;
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID id) {
		topicService.deleteTopic(id);
		messagingTemplate.convertAndSend(
			"/topic/system",
			new SystemEvent("DELETE", id.toString(), null)
		);
	}

	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void edit(@PathVariable UUID id, @Valid @RequestBody TopicEditRequest request) {
		Topic topic = topicService.getTopicById(id).orElseThrow(() -> new TopicNotFoundException("Le topic demandé n'existe pas."));
		request.getName().ifPresent(topic::setName);
		topicService.editTopic(topic);
		messagingTemplate.convertAndSend(
			"/topic/system",
			new SystemEvent("EDIT", id.toString(), topic.getName())
		);
	}

	@GetMapping("")
	@ResponseStatus(HttpStatus.OK)
	public List<TopicListResponse> getAll() {
		return topicService.getAllTopics()
			.stream()
			.map(t -> new TopicListResponse(t.getId().toString(), t.getName()))
			.toList();
	}

	@GetMapping("/{id}/messages")
	@ResponseStatus(HttpStatus.OK)
	public List<MessageResponse> getHistory(@PathVariable UUID id) {
		Topic topic = topicService.getTopicById(id)
			.orElseThrow(() -> new TopicNotFoundException("Le topic demandé n'existe pas"));
		return this.messageService.getRecentMessages(topic)
			.stream()
			.map(MessageResponseMapper::toResponseMessage)
			.toList();
	}
}
