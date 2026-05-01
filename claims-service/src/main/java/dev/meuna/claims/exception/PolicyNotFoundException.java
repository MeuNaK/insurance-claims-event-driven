package dev.meuna.claims.exception;

public class PolicyNotFoundException extends RuntimeException {
	public PolicyNotFoundException(String message) {
		super(message);
	}
}
