package com.rinca.erisserver.controllers;

import com.rinca.erisserver.dto.AuthResponse;
import com.rinca.erisserver.dto.LoginRequest;
import com.rinca.erisserver.dto.RegisterRequest;
import com.rinca.erisserver.models.RefreshToken;
import com.rinca.erisserver.models.User;
import com.rinca.erisserver.services.AuthService;
import com.rinca.erisserver.services.JwtService;
import com.rinca.erisserver.services.RefreshTokenService;
import com.rinca.erisserver.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final UserService userService;
	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;
	private final JwtService jwtService;

	@Value("${security.jwt.refresh-token.expiration-time}")
	private int cookieMaxAge;

	public AuthController(
			UserService userService,
			AuthService authService,
			RefreshTokenService refreshTokenService,
			JwtService jwtService
	) {
		this.userService = userService;
		this.authService = authService;
		this.refreshTokenService = refreshTokenService;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@Valid @RequestBody RegisterRequest request) {
		userService.register(request.username(), request.email(), request.password());
	}

	@PostMapping("/login")
	public AuthResponse login(
			@Valid @RequestBody LoginRequest request,
			HttpServletResponse response
	) {
		User user = authService.login(request.username(), request.password());
		String jwt = jwtService.generateToken(user);
		String refresh = refreshTokenService.generateToken(user).getToken();
		response.addCookie(createRefreshCookie(refresh));
		return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getAvatar());
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout(
			@CookieValue("refreshToken") String refreshToken,
			HttpServletResponse response
	) {
		Cookie cookie = createRefreshCookie("");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		refreshTokenService.revokeToken(refreshToken);
	}

	@PostMapping("/refresh")
	public AuthResponse refresh(
			@CookieValue("refreshToken") String refreshToken,
			HttpServletResponse response
	) {
		RefreshToken token = refreshTokenService.refreshToken(refreshToken);
		User user = token.getUser();
		String jwt = jwtService.generateToken(user);
		response.addCookie(createRefreshCookie(token.getToken()));
		return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getAvatar());
	}

	private Cookie createRefreshCookie(String refreshToken) {
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(true);
		refreshCookie.setMaxAge(cookieMaxAge/1000);
		refreshCookie.setPath("/");
		return refreshCookie;
	}

}
