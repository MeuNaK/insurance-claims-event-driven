package dev.meuna.claims.dto;

import dev.meuna.starter.claim.enums.ClaimType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Payload for creating a new claim")
public record CreateClaimRequest(
		@Schema(description = "Policy identifier")
		Long policyId,
		@Schema(description = "Incident date (ISO-8601)", example = "2026-04-10T12:00:00Z")
		Date incidentDate,
		@Schema(description = "Claim type", example = "ACCIDENT")
		ClaimType type,
		@Schema(description = "Free text claim description")
		String description
) {
}
