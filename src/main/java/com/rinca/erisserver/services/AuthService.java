package com.rinca.erisserver.services;

import com.rinca.erisserver.models.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;

	public AuthService(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public User login(String username, String password) {
		return (User) authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password)
		).getPrincipal();
	}

	public Optional<User> getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User user) {
			return Optional.of(user);
		}
		return Optional.empty();
 	}
}
