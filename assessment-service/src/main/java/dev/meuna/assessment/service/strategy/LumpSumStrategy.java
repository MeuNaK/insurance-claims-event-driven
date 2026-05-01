package dev.meuna.assessment.service.strategy;

import dev.meuna.assessment.dto.AssessmentResult;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class LumpSumStrategy extends BaseClaimStrategy {
	
	private static final Map<InsuranceRiskType, BigDecimal> COVERAGE = Map.of(
			InsuranceRiskType.DEATH, new BigDecimal("100"),
			InsuranceRiskType.PERMANENT_DISABILITY, new BigDecimal("100"),
			InsuranceRiskType.PARTIAL_DISABILITY_2, new BigDecimal("75"),
			InsuranceRiskType.PARTIAL_DISABILITY_3, new BigDecimal("50"));
	
	@Override
	public boolean supports(InsuranceRiskType type) {
		return COVERAGE.containsKey(type);
	}
	
	@Override
	protected AssessmentResult doAssess(ClaimSubmittedEvent event) {
		BigDecimal percent = COVERAGE.get(event.type())
				.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
		
		BigDecimal raw = event.policySumInsured().multiply(percent);
		return AssessmentResult.approved(cap(raw, event), 0);
	}
}
