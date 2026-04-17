package dev.meuna.claims.service;

import dev.meuna.claims.dto.CreateClaimRequest;
import dev.meuna.claims.dto.CreateClaimResponse;
import dev.meuna.claims.entity.Claim;
import dev.meuna.starter.common.enums.claim.ClaimStatus;
import jakarta.transaction.Transactional;

import java.util.Optional;

public interface ClaimService {
	
	Iterable<Claim> findAll();
	
	Optional<Claim> findById(Long id);
	
	CreateClaimResponse create(CreateClaimRequest request);
	
	@Transactional
	Optional<Claim> updateStatus(Long id, ClaimStatus status);
}
