package dev.meuna.claims.mapper;

import dev.meuna.claims.dto.ClaimResponse;
import dev.meuna.claims.dto.PolicyResponse;
import dev.meuna.claims.entity.Claim;
import dev.meuna.claims.entity.Policy;
import org.springframework.stereotype.Component;

@Component
public class ClaimMapper {
	
	public PolicyResponse toResponse(Policy policy) {
		return new PolicyResponse(
				policy.getId(),
				policy.getRiskType(),
				policy.getSumInsured(),
				policy.getMaxPerClaim(),
				policy.getStartDate(),
				policy.getEndDate(),
				policy.getDailyPayoutPercent(),
				policy.getWaitingDays(),
				policy.getSurvivalDays()
		);
	}
	
	public ClaimResponse toResponse(Claim claim) {
		return new ClaimResponse(
				claim.getId(),
				claim.getPolicyId(),
				claim.getClaimNumber(),
				claim.getIncidentDate(),
				claim.getClaimSubmittedDate(),
				claim.getType(),
				claim.getStatus(),
				claim.getDescription(),
				claim.getActualExpenses()
		);
	}
}