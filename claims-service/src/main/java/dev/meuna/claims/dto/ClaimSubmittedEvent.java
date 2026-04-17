package dev.meuna.claims.dto;

import dev.meuna.starter.common.enums.claim.ClaimType;

import java.util.Date;

public record ClaimSubmittedEvent(
		Long claimId,
		Long policyId,
		Date incidentDate,
		ClaimType type,
		String description) {
	
}
