package com.rinca.erisserver.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
		@NotBlank String refreshToken
) { }
