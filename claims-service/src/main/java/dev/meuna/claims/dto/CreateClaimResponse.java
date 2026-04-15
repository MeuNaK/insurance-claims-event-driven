package dev.meuna.claims.dto;

import dev.meuna.starter.claim.enums.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after claim creation")
public record CreateClaimResponse(
		@Schema(description = "Claim identifier")
		Long id,
		@Schema(description = "Human-readable claim number", example = "CLM-2026-000042")
		String claimNumber,
		@Schema(description = "Current claim status", example = "SUBMITTED")
		ClaimStatus status
) {
}
