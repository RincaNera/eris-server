package com.rinca.erisserver.dto;

public record AuthResponse(
		String accessToken,
		Long userId,
		String username,
		String userAvatar
) {
}
