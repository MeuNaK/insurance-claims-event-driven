package dev.meuna.claims.dto;

import dev.meuna.starter.common.enums.claim.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload for updating claim status")
public record UpdateStatusRequest(
		@Schema(description = "New claim status", example = "UNDER_ASSESSMENT")
		ClaimStatus status
) {
}
