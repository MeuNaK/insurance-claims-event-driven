package dev.meuna.assessment.service;

import dev.meuna.assessment.dto.AssessmentResult;
import dev.meuna.assessment.exeption.UnsupportedClaimTypeException;
import dev.meuna.assessment.service.strategy.ClaimAssessmentStrategy;
import dev.meuna.starter.common.events.claim.ClaimAssessedEvent;
import dev.meuna.starter.common.events.claim.ClaimRejectedEvent;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;
import dev.meuna.starter.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentService {
	
	private final List<ClaimAssessmentStrategy> strategies;
	private final OutboxService outboxService;
	
	@Transactional
	public void process(ClaimSubmittedEvent event) {
		ClaimAssessmentStrategy strategy = strategies.stream()
				.filter(s -> s.supports(event.type()))
				.findFirst()
				.orElseThrow(() -> new UnsupportedClaimTypeException(event.type()));
		
		AssessmentResult result = strategy.assess(event);
		
		if (result.isRejected()) {
			publishRejected(event, result.reasonCode());
		} else {
			publishApproved(event, result);
		}
	}
	
	private void publishApproved(ClaimSubmittedEvent event, AssessmentResult result) {
		ClaimAssessedEvent assessed = new ClaimAssessedEvent(
				event.claimId(),
				event.policyId(),
				event.claimNumber(),
				event.type(),
				result.amount(),
				result.payableDays(),
				new Date()
		);
		outboxService.save(event.claimId().toString(), assessed);
		log.info("Claim approved claimId={} amount={} payableDays={}",
				event.claimId(), result.amount(), result.payableDays());
	}
	
	private void publishRejected(ClaimSubmittedEvent event, String reasonCode) {
		ClaimRejectedEvent rejected = new ClaimRejectedEvent(
				event.claimId(),
				event.policyId(),
				event.claimNumber(),
				event.type(),
				reasonCode,
				new Date()
		);
		outboxService.save(event.claimId().toString(), rejected);
		log.info("Claim rejected claimId={} reason={}", event.claimId(), reasonCode);
	}
}
