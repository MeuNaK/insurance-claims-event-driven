package dev.meuna.claims.service;

import dev.meuna.claims.dto.CreatePolicyRequest;
import dev.meuna.claims.dto.PolicyResponse;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;

public interface PolicyService {
	PolicyResponse getPolicy(Long policyId, InsuranceRiskType riskType);
	
	PolicyResponse createPolicy(CreatePolicyRequest request);
}
