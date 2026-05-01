package dev.meuna.assessment.service.strategy;

import dev.meuna.assessment.dto.AssessmentResult;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

public abstract class BaseClaimStrategy implements ClaimAssessmentStrategy {
	
	protected static final String REASON_INVALID_DATES = "INVALID_DATES";
	protected static final String REASON_POLICY_EXPIRED = "POLICY_EXPIRED";
	protected static final String REASON_OUTSIDE_POLICY_PERIOD = "OUTSIDE_POLICY_PERIOD";
	
	@Override
	public final AssessmentResult assess(ClaimSubmittedEvent event) {
		return validateCommon(event)
				.map(AssessmentResult::rejected)
				.orElseGet(() -> doAssess(event));
	}
	
	private Optional<String> validateCommon(ClaimSubmittedEvent event) {
		LocalDate incidentDate  = toLocalDate(event.incidentDate());
		LocalDate submittedDate = toLocalDate(event.claimSubmittedDate());
		LocalDate policyStart   = toLocalDate(event.policyStartDate());
		LocalDate policyEnd     = toLocalDate(event.policyEndDate());
		
		if (submittedDate.isBefore(incidentDate)) {
			return Optional.of(REASON_INVALID_DATES);
		}
		if (incidentDate.isBefore(policyStart) || incidentDate.isAfter(policyEnd)) {
			return Optional.of(REASON_OUTSIDE_POLICY_PERIOD);
		}
		if (submittedDate.isAfter(policyEnd)) {
			return Optional.of(REASON_POLICY_EXPIRED);
		}
		
		return Optional.empty();
	}
	
	protected abstract AssessmentResult doAssess(ClaimSubmittedEvent event);
	
	protected static LocalDate toLocalDate(Date value) {
		return value.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
	}
	
	protected static int orZero(Integer value) {
		return value == null ? 0 : Math.max(value, 0);
	}
	
	protected static BigDecimal cap(BigDecimal amount, ClaimSubmittedEvent event) {
		return amount
				.min(event.maxPerClaim())
				.min(event.policySumInsured())
				.setScale(2, RoundingMode.HALF_UP);
	}
}
