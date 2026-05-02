package dev.meuna.claims.dto;

import dev.meuna.starter.common.enums.claim.ClaimStatus;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "Claim details")
public record ClaimResponse(
		@Schema(description = "Claim identifier")
		Long id,
		@Schema(description = "Policy identifier")
		Long policyId,
		@Schema(description = "Human-readable claim number", example = "CLM01-26-ABC123")
		String claimNumber,
		@Schema(description = "Incident date (ISO-8601)", example = "2026-04-10T12:00:00Z")
		Date incidentDate,
		@Schema(description = "Claim submitted date (ISO-8601)", example = "2026-04-15T12:00:00Z")
		Date claimSubmittedDate,
		@Schema(description = "Insurance risk type", example = "ILLNESS")
		InsuranceRiskType type,
		@Schema(description = "Current claim status", example = "SUBMITTED")
		ClaimStatus status,
		@Schema(description = "Free text claim description")
		String description,
		@Schema(description = "Actual expenses (only for MEDICAL_EXPENSE, REHABILITATION)")
		BigDecimal actualExpenses
) {}