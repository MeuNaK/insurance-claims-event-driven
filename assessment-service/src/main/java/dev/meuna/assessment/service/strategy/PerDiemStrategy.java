package dev.meuna.assessment.service.strategy;

import dev.meuna.assessment.dto.AssessmentResult;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static dev.meuna.starter.common.enums.risk.InsuranceRiskType.HOSPITALIZATION;
import static dev.meuna.starter.common.enums.risk.InsuranceRiskType.ILLNESS;

@Component
public class PerDiemStrategy extends BaseClaimStrategy {
	
	private static final String REASON_WAITING_PERIOD_NOT_MET  = "WAITING_PERIOD_NOT_MET";
	private static final String REASON_SURVIVAL_PERIOD_NOT_MET = "SURVIVAL_PERIOD_NOT_MET";
	
	@Override
	public boolean supports(InsuranceRiskType type) {
		return ILLNESS.equals(type) || HOSPITALIZATION.equals(type);
	}
	
	@Override
	protected AssessmentResult doAssess(ClaimSubmittedEvent event) {
		LocalDate incidentDate  = toLocalDate(event.incidentDate());
		LocalDate submittedDate = toLocalDate(event.claimSubmittedDate());
		
		LocalDate coveredStart = incidentDate.plusDays(orZero(event.waitingDays()));
		if (submittedDate.isBefore(coveredStart)) {
			return AssessmentResult.rejected(REASON_WAITING_PERIOD_NOT_MET);
		}
		
		LocalDate survivalCheck = incidentDate.plusDays(orZero(event.survivalDays()));
		if (submittedDate.isBefore(survivalCheck)) {
			return AssessmentResult.rejected(REASON_SURVIVAL_PERIOD_NOT_MET);
		}
		
		int payableDays = (int) ChronoUnit.DAYS.between(coveredStart, submittedDate) + 1;
		
		BigDecimal dailyRate = event.dailyPayoutPercent()
				.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
		BigDecimal raw = event.policySumInsured()
				.multiply(dailyRate)
				.multiply(BigDecimal.valueOf(payableDays));
		
		return AssessmentResult.approved(cap(raw, event), payableDays);
	}
}
