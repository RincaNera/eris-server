package com.rinca.erisserver.services;

import com.rinca.erisserver.models.Message;
import com.rinca.erisserver.models.Topic;
import com.rinca.erisserver.models.User;
import com.rinca.erisserver.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
	private final MessageRepository messageRepository;

	public MessageService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	public List<Message> getRecentMessages(Topic topic) {
		return messageRepository.findTop50ByTopicOrderByCreatedAtDesc(topic);
	}

	public Message saveMessage(User user, String content, Topic topic) {
		Message message = new Message(user, new Date(), content, topic);
		return messageRepository.save(message);
	}
}
