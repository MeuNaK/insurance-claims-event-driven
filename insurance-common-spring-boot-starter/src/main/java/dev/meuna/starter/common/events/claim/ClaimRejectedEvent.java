package dev.meuna.starter.common.events.claim;

import dev.meuna.starter.common.enums.kafka.KafkaTopics;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.KafkaEvent;

import java.util.Date;

public record ClaimRejectedEvent(
		Long claimId,
		Long policyId,
		String claimNumber,
		InsuranceRiskType riskType,
		String reasonCode,
		Date decidedAt
) implements KafkaEvent {
	private static final String AGGREGATE_TYPE = "PAYMENT_REJECT";

	@Override
	public String aggregateType() {
		return AGGREGATE_TYPE;
	}

	@Override
	public String topicName() {
		return KafkaTopics.CLAIM_REJECTED.getTopicName();
	}
}
