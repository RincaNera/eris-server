package com.rinca.erisserver.services;

import com.rinca.erisserver.exceptions.InvalidTokenException;
import com.rinca.erisserver.models.RefreshToken;
import com.rinca.erisserver.models.User;
import com.rinca.erisserver.repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final int expiryTime;

	public RefreshTokenService(
			RefreshTokenRepository refreshTokenRepository,
			@Value("${security.jwt.refresh-token.expiration-time}") int expiryTime
	) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.expiryTime = expiryTime;
	}

	public RefreshToken generateToken(User user) {
		String uuid = UUID.randomUUID().toString();
		Date expiry = new Date(new Date().getTime() + expiryTime);
		return refreshTokenRepository.save(new RefreshToken(uuid, user, expiry));
	}

	public void revokeToken(String token) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new InvalidTokenException("Token invalide ou inexistant"));
		refreshToken.setValid(false);
		refreshTokenRepository.save(refreshToken);
	}

	public RefreshToken refreshToken(String token) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new InvalidTokenException("Token invalide ou inexistant"));
		if (!refreshToken.isValid() || new Date().after(refreshToken.getExpiryDate())) {
			throw new InvalidTokenException("Token invalide");
		}
		User user = refreshToken.getUser();
		refreshToken.setValid(false);
		refreshTokenRepository.save(refreshToken);
		return generateToken(user);
	}
}
