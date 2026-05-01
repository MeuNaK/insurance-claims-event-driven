package dev.meuna.assessment.service.strategy;

import dev.meuna.assessment.dto.AssessmentResult;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;

public interface ClaimAssessmentStrategy {
	boolean supports(InsuranceRiskType claimType);
	AssessmentResult assess(ClaimSubmittedEvent event);
}
