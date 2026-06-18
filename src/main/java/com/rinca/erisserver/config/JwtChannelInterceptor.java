package com.rinca.erisserver.config;

import com.rinca.erisserver.exceptions.InvalidTokenException;
import com.rinca.erisserver.models.User;
import com.rinca.erisserver.repositories.UserRepository;
import com.rinca.erisserver.services.JwtService;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtChannelInterceptor implements ChannelInterceptor {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	public JwtChannelInterceptor(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@Override
	public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
				String authHeader = accessor.getFirstNativeHeader("authorization");
				if (authHeader == null || !authHeader.startsWith("Bearer ")) {
					throw new InvalidTokenException("Token manquant");
				}
				String token = authHeader.substring(7);
				if (!jwtService.isTokenValid(token)) {
					throw new InvalidTokenException("Token invalide ou expiré");
				}
				Long username = jwtService.extractUsername(token);
				User user = userRepository.findById(username)
						.orElseThrow(() -> new InvalidTokenException("Utilisateur non trouvé."));
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
				accessor.setUser(authentication);
		}
		return message;
	}
}
