package com.rinca.erisserver.exceptions;

import java.util.NoSuchElementException;

public class TopicNotFoundException extends NoSuchElementException {
	public TopicNotFoundException(String message) {
		super(message);
	}
}
