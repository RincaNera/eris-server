package com.rinca.erisserver.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicCreateRequest(
		@NotBlank String name
) {
}
