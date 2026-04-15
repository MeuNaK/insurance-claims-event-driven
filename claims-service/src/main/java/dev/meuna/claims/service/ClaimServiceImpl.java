package dev.meuna.claims.service;

import dev.meuna.claims.dto.CreateClaimRequest;
import dev.meuna.claims.dto.CreateClaimResponse;
import dev.meuna.claims.entity.Claim;
import dev.meuna.claims.repository.ClaimRepository;
import dev.meuna.claims.exception.InvalidClaimStatusTransitionException;
import dev.meuna.starter.claim.enums.ClaimStatus;
import dev.meuna.starter.claim.enums.ClaimType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClaimServiceImpl implements ClaimService {
	
	private ClaimRepository repository;
	
	@Override
	public Iterable<Claim> findAll() {
		return repository.findAll();
	}
	
	@Override
	public Optional<Claim> findById(Long id) {
		return repository.findById(id);
	}
	
	@Override
	public CreateClaimResponse create(CreateClaimRequest request) {
		Claim claim = new Claim();
		claim.setPolicyId(request.policyId());
		claim.setClaimNumber(generateClaimNumber(request.type(), request.incidentDate()));
		claim.setIncidentDate(request.incidentDate());
		claim.setType(request.type());
		claim.setStatus(ClaimStatus.SUBMITTED);
		claim.setDescription(request.description());
		
		Claim saved = repository.save(claim);
		
		return new CreateClaimResponse(
				saved.getId(),
				saved.getClaimNumber(),
				saved.getStatus()
		);
	}

	private String generateClaimNumber(ClaimType type, Date incidentDate) {
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
}
