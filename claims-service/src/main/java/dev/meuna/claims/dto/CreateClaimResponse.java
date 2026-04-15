package dev.meuna.claims.dto;

import dev.meuna.starter.claim.enums.ClaimStatus;

public record CreateClaimResponse(Long id,
                                  String claimNumber,
                                  ClaimStatus status) {
}
