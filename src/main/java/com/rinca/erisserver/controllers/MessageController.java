package com.rinca.erisserver.controllers;

import com.rinca.erisserver.dto.MessageRequest;
import com.rinca.erisserver.dto.MessageResponse;
import com.rinca.erisserver.exceptions.InvalidTokenException;
import com.rinca.erisserver.exceptions.TopicNotFoundException;
import com.rinca.erisserver.models.Message;
import com.rinca.erisserver.models.Topic;
import com.rinca.erisserver.models.User;
import com.rinca.erisserver.services.AuthService;
import com.rinca.erisserver.services.MessageService;
import com.rinca.erisserver.services.TopicService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class MessageController {

    private final TopicService topicService;
    private final MessageService messageService;
    private final AuthService authService;

    public MessageController(TopicService topicService, MessageService messageService, AuthService authService) {
        this.topicService = topicService;
        this.messageService = messageService;
        this.authService = authService;
    }

    @MessageMapping("/topic/{topicId}")
    @SendTo("/topic/{topicId}")
    public MessageResponse sendMessage(MessageRequest messageRequest, @DestinationVariable UUID topicId) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new InvalidTokenException("Utilisateur non authentifié"));
        Topic topic = topicService.getTopicById(topicId)
                .orElseThrow(() -> new TopicNotFoundException("Topic non trouvé"));
        Message message = messageService.saveMessage(user, messageRequest.content(), topic);
        return new MessageResponse(
                message.getId(),
                user.getId(),
                user.getUsername(),
                user.getAvatar(),
                message.getCreatedAt(),
                message.getContent(),
                topicId.toString()
        );
    }

}
