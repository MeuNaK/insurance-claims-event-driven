package dev.meuna.starter.common.events.claim;

import dev.meuna.starter.common.enums.claim.ClaimStatus;
import dev.meuna.starter.common.enums.kafka.KafkaTopics;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.KafkaEvent;

import java.math.BigDecimal;
import java.util.Date;

public record ClaimSubmittedEvent(
		Long claimId,
		Long policyId,
		BigDecimal policySumInsured,
		BigDecimal maxPerClaim,
		String claimNumber,
		Date incidentDate,
		Date claimSubmittedDate,
		Date policyEndDate,
		InsuranceRiskType type,
		BigDecimal dailyPayoutPercent,
		Integer waitingDays,
		Integer survivalDays,
		ClaimStatus status,
		Date policyStartDate,
		BigDecimal actualExpenses
) implements KafkaEvent {
	private static final String AGGREGATE_TYPE = "CLAIM";

	@Override
	public String aggregateType() {
		return AGGREGATE_TYPE;
	}

	@Override
	public String topicName() {
		return KafkaTopics.CLAIM_SUBMITTED.getTopicName();
	}
}
