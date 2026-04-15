package dev.meuna.claims.dto;

import dev.meuna.starter.claim.enums.ClaimStatus;

public record UpdateStatusRequest(ClaimStatus status) {
}
