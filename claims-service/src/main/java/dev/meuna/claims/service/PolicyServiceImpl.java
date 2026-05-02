package dev.meuna.claims.service;

import dev.meuna.claims.dto.CreatePolicyRequest;
import dev.meuna.claims.dto.PolicyResponse;
import dev.meuna.claims.entity.Policy;
import dev.meuna.claims.exception.PolicyNotFoundException;
import dev.meuna.claims.mapper.ClaimMapper;
import dev.meuna.claims.repository.PolicyRepository;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {
	private final PolicyRepository policyRepository;
	private final ClaimMapper claimMapper;

	@Override
	public PolicyResponse getPolicy(Long policyId, InsuranceRiskType riskType) {
		return policyRepository.findByIdAndRiskType(policyId, riskType)
				.map(claimMapper::toResponse)
				.orElseThrow(() -> new PolicyNotFoundException(
						"Policy limit not found for policyId=%d riskType=%s".formatted(policyId, riskType)));
	}

	@Override
	public PolicyResponse createPolicy(CreatePolicyRequest request) {
		Policy policy = new Policy();
		policy.setRiskType(request.riskType());
		policy.setSumInsured(request.sumInsured());
		policy.setMaxPerClaim(request.maxPerClaim());
		policy.setStartDate(request.startDate());
		policy.setEndDate(request.endDate());
		policy.setDailyPayoutPercent(request.dailyPayoutPercent());
		policy.setWaitingDays(request.waitingDays());
		policy.setSurvivalDays(request.survivalDays());
		Policy save = policyRepository.save(policy);
		return claimMapper.toResponse(save);
	}
}
