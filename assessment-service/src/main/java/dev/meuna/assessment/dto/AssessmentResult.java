package dev.meuna.assessment.dto;

import java.math.BigDecimal;

public record AssessmentResult(
		boolean approved,
		String reasonCode,
		BigDecimal amount,
		int payableDays
) {
	public static AssessmentResult approved(BigDecimal amount, int payableDays) {
		return new AssessmentResult(true, null, amount, payableDays);
	}
	
	public static AssessmentResult rejected(String reasonCode) {
		return new AssessmentResult(false, reasonCode, BigDecimal.ZERO, 0);
	}
	
	public boolean isRejected() { return !approved; }
}
