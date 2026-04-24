package dev.meuna.starter.outbox.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OutboxEvent {
	
	private UUID id;
	private String aggregateType;
	private String aggregateId;
	private String eventType;
	private String payload;
	private OutboxStatus status;
	@Builder.Default
	private Instant createdAt = Instant.now();
	private Instant processedAt;
	@Builder.Default
	private int retryCount = 0;
	private String errorMessage;
	
	public void markProcessed() {
		this.status = OutboxStatus.SENT;
		this.processedAt = Instant.now();
	}
	
	public void markFailed(String error) {
		this.retryCount++;
		this.status = OutboxStatus.FAILED;
		this.errorMessage = error;
	}
}
