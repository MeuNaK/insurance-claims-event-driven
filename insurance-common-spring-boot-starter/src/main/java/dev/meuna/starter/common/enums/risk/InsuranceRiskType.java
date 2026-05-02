package dev.meuna.starter.common.enums.risk;

import lombok.Getter;

@Getter
public enum InsuranceRiskType {
	DEATH(1),
	PERMANENT_DISABILITY(2),
	PARTIAL_DISABILITY_2(3),
	PARTIAL_DISABILITY_3(4),
	INJURY(5),
	ILLNESS(6),
	HOSPITALIZATION(7),
	MEDICAL_EXPENSE(8),
	REHABILITATION(9)
	;
	
	private final int id;
	
	InsuranceRiskType(int id) {
		this.id = id;
	}
}
