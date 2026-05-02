package dev.meuna.claims.controller;

import dev.meuna.claims.dto.ClaimResponse;
import dev.meuna.claims.dto.CreatePolicyRequest;
import dev.meuna.claims.dto.PolicyResponse;
import dev.meuna.claims.service.ClaimService;
import dev.meuna.claims.service.PolicyService;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Policies", description = "Policy API for internal checks")
public class PolicyController {
	private final PolicyService policyService;
	private final ClaimService claimService;

	@Operation(summary = "Create policy")
	@PostMapping("/policies")
	public ResponseEntity<PolicyResponse> createPolicy(@RequestBody CreatePolicyRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(policyService.createPolicy(request));
	}

	@Operation(summary = "Get policy by policyId and riskType")
	@GetMapping("/policies/{policyId}")
	public PolicyResponse getPolicy(
			@Parameter(description = "Policy ID", example = "10001") @PathVariable Long policyId,
			@Parameter(description = "Risk type", example = "MEDICAL_EXPENSE") @RequestParam InsuranceRiskType riskType) {
		return policyService.getPolicy(policyId, riskType);
	}

	@Operation(summary = "Get all claims by policyId")
	@GetMapping("/policies/{policyId}/claims")
	public List<ClaimResponse> getClaimsByPolicyId(
			@Parameter(description = "Policy ID", example = "10001") @PathVariable Long policyId) {
		return claimService.findAllByPolicyId(policyId);
	}
}
