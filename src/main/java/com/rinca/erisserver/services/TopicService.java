package com.rinca.erisserver.services;

import com.rinca.erisserver.models.Topic;
import com.rinca.erisserver.repositories.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TopicService {
	private final TopicRepository topicRepository;

	public TopicService(TopicRepository topicRepository) {
		this.topicRepository = topicRepository;
	}

	public Topic createTopic(String name) {
		return topicRepository.save(new Topic(name));
	}

	public void deleteTopic(UUID id) {
		topicRepository.deleteById(id);
	}

	public void editTopic(Topic topic) {
		topicRepository.save(topic);
	}

	public Optional<Topic> getTopicById(UUID id) {
		return topicRepository.findById(id);
	}

	public List<Topic> getAllTopics() {
		return topicRepository.findAll();
	}
}
