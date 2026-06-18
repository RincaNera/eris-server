package com.rinca.erisserver.repositories;

import com.rinca.erisserver.models.Message;
import com.rinca.erisserver.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	List<Message> findTop50ByTopicOrderByCreatedAtDesc(Topic topic);
}
