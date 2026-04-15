package dev.meuna.claims.dto;

import dev.meuna.starter.claim.enums.ClaimType;

import java.util.Date;

public record CreateClaimRequest(Long policyId,
                                 Date incidentDate,
                                 ClaimType type,
                                 String description) {
}
