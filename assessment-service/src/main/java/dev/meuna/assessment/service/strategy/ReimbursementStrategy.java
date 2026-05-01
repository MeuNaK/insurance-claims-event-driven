package dev.meuna.assessment.service.strategy;

import dev.meuna.assessment.dto.AssessmentResult;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static dev.meuna.starter.common.enums.risk.InsuranceRiskType.MEDICAL_EXPENSE;
import static dev.meuna.starter.common.enums.risk.InsuranceRiskType.REHABILITATION;

@Component
public class ReimbursementStrategy extends BaseClaimStrategy {
	
	private static final String REASON_NO_EXPENSES = "NO_EXPENSES_PROVIDED";
	
	@Override
	public boolean supports(InsuranceRiskType type) {
		return MEDICAL_EXPENSE.equals(type) || REHABILITATION.equals(type);
	}
	
	@Override
	protected AssessmentResult doAssess(ClaimSubmittedEvent event) {
		BigDecimal expenses = event.actualExpenses();
		
		if (expenses == null || expenses.compareTo(BigDecimal.ZERO) <= 0) {
			return AssessmentResult.rejected(REASON_NO_EXPENSES);
		}
		BigDecimal approved = cap(expenses, event);
		
		return AssessmentResult.approved(approved, 0);
	}
}
