package dev.meuna.assessment.exeption;

import dev.meuna.starter.common.enums.risk.InsuranceRiskType;

public class UnsupportedClaimTypeException extends RuntimeException {
	public UnsupportedClaimTypeException(InsuranceRiskType type) {
	}
}
