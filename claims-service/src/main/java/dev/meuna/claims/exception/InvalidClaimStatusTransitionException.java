package dev.meuna.claims.exception;

public class InvalidClaimStatusTransitionException extends RuntimeException {
	
	public InvalidClaimStatusTransitionException(String message) {
		super(message);
	}
}
