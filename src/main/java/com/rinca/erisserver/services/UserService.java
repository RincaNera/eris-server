package com.rinca.erisserver.services;

import com.rinca.erisserver.models.User;
import com.rinca.erisserver.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User register(String username, String email, String rawPassword) {
		String encodedPassword = passwordEncoder.encode(rawPassword);
		User user = new User(username, email, encodedPassword, null);
		return userRepository.save(user);
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
}
