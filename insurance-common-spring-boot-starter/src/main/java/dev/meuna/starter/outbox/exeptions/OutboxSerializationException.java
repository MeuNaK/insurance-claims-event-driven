package dev.meuna.starter.outbox.exeptions;

public class OutboxSerializationException extends RuntimeException {
	
	public OutboxSerializationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public OutboxSerializationException(String message) {
		super(message);
	}
}
