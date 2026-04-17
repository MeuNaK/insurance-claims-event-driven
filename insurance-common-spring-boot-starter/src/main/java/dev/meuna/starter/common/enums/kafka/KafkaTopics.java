package dev.meuna.starter.common.enums.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KafkaTopics {
	CLAIM_SUBMITTED("claim-submitted"),
	CLAIM_ASSESSED("claim-assess"),
	CLAIM_REJECTED("claim-rejected"),
	PAYMENT_COMPLETED("payment-completed"),
	PAYMENT_FAILED("payment-failed");
	
	private final String topicName;
	
}
