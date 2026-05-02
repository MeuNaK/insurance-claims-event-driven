package dev.meuna.claims.service;

import dev.meuna.claims.dto.ClaimResponse;
import dev.meuna.claims.dto.CreateClaimRequest;
import dev.meuna.claims.dto.CreateClaimResponse;
import dev.meuna.claims.dto.PolicyResponse;
import dev.meuna.claims.entity.Claim;
import dev.meuna.claims.exception.InvalidClaimStatusTransitionException;
import dev.meuna.claims.mapper.ClaimMapper;
import dev.meuna.claims.repository.ClaimRepository;
import dev.meuna.starter.common.enums.claim.ClaimStatus;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;
import dev.meuna.starter.outbox.service.OutboxService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClaimServiceImpl implements ClaimService {
	
	private ClaimRepository repository;
	private OutboxService outboxService;
	private PolicyService policyService;
	private final ClaimMapper claimMapper;
	
	@Override
	public Iterable<Claim> findAll() {
		return repository.findAll();
	}
	
	@Override
	public Optional<Claim> findById(Long id) {
		return repository.findById(id);
	}
	
	@Override
	@Transactional
	public CreateClaimResponse create(CreateClaimRequest request) {
		PolicyResponse policy = policyService.getPolicy(request.policyId(), request.type());

		Claim claim = new Claim();
		claim.setPolicyId(request.policyId());
		claim.setClaimNumber(generateClaimNumber(request.type(), request.incidentDate()));
		claim.setIncidentDate(request.incidentDate());
		claim.setClaimSubmittedDate(request.claimSubmittedDate());
		claim.setType(request.type());
		claim.setStatus(ClaimStatus.SUBMITTED);
		claim.setDescription(request.description());
		
		Claim saved = repository.save(claim);
		ClaimSubmittedEvent event = new ClaimSubmittedEvent(
				saved.getId(),
				saved.getPolicyId(),
				policy.sumInsured(),
				policy.maxPerClaim(),
				saved.getClaimNumber(),
				saved.getIncidentDate(),
				saved.getClaimSubmittedDate(),
				policy.endDate(),
				saved.getType(),
				policy.dailyPayoutPercent(),
				policy.waitingDays(),
				policy.survivalDays(),
				saved.getStatus(),
				policy.startDate(),
				saved.getActualExpenses()
				);
		outboxService.save(saved.getId().toString(), event);
		
		return new CreateClaimResponse(
				saved.getId(),
				saved.getClaimNumber(),
				saved.getStatus()
		);
	}

	private String generateClaimNumber(InsuranceRiskType type, Date incidentDate) {
		String typeCode = type == null ? "00" : String.format("%02d", type.getId());
		String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
		LocalDate date = incidentDate == null
				? LocalDate.now(ZoneOffset.UTC)
				: incidentDate.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
		String yearPart = String.format("%02d", date.getYear() % 100);
		return "CLM" + typeCode + "-" + yearPart + "-" + randomPart;
	}
	
	@Transactional
	@Override
	public Optional<Claim> updateStatus(Long id, ClaimStatus status) {
		return repository.findById(id)
				.map(claim -> {
					if (claim.getStatus() == ClaimStatus.REJECTED && status == ClaimStatus.APPROVED) {
						throw new InvalidClaimStatusTransitionException("Cannot approve a rejected claim.");
					}
					
					claim.setStatus(status);
					return repository.save(claim);
				});
	}
	
	@Override
	public List<ClaimResponse> findAllByPolicyId(Long policyId) {
		List<Claim> policyList = repository.findAllByPolicyId(policyId);
		return policyList.stream()
				.map(claimMapper::toResponse)
				.toList();
	}
}
