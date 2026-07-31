package com.rinca.erisserver.repositories;

import com.rinca.erisserver.models.Message;
import com.rinca.erisserver.models.Topic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	@Query("SELECT m FROM Message m ")
	Message findByIdWithUser(Long id);
	@Query("SELECT m.id FROM Message m WHERE m.topic = :topic ORDER BY m.createdAt DESC LIMIT 50")
	List<Long> findTop50IdsByTopicOrderByCreatedAtDesc(@Param("topic") Topic topic);
	@Query("SELECT m FROM Message m LEFT JOIN FETCH m.attachments WHERE m.id IN :ids ORDER BY m.createdAt DESC")
	List<Message> findByIdsWithAttachmentsOrderByCreatedAtDesc(@Param("ids") List<Long> ids);
}
