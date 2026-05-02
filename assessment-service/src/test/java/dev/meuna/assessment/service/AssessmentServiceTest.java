package dev.meuna.assessment.service;

import dev.meuna.assessment.dto.AssessmentResult;
import dev.meuna.assessment.exeption.UnsupportedClaimTypeException;
import dev.meuna.assessment.service.strategy.ClaimAssessmentStrategy;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.KafkaEvent;
import dev.meuna.starter.common.events.claim.ClaimAssessedEvent;
import dev.meuna.starter.common.events.claim.ClaimRejectedEvent;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;
import dev.meuna.starter.outbox.service.OutboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {
	
	@Mock
	private ClaimAssessmentStrategy strategy;
	
	@Mock
	private OutboxService outboxService;
	
	private AssessmentService service;
	
	private final Long claimId = 10L;
	private final Long policyId = 101L;
	private final String claimNumber = "CLM-001";
	private final InsuranceRiskType claimType = InsuranceRiskType.DEATH;
	
	@BeforeEach
	void setUp() {
		// Inject strategy list directly via constructor (no Spring context needed)
		service = new AssessmentService(List.of(strategy), outboxService);
	}
	
	private ClaimSubmittedEvent buildEvent() {
		ClaimSubmittedEvent event = mock(ClaimSubmittedEvent.class);
		when(event.claimId()).thenReturn(claimId);
		when(event.policyId()).thenReturn(policyId);
		when(event.claimNumber()).thenReturn(claimNumber);
		when(event.type()).thenReturn(claimType);
		return event;
	}
	
	@Test
	@DisplayName("approved result → saves ClaimAssessedEvent to outbox")
	void approvedResult_savesClaimAssessedEvent() {
		ClaimSubmittedEvent event = buildEvent();
		AssessmentResult approved = AssessmentResult.approved(new BigDecimal("10000.00"), 0);
		
		when(strategy.supports(claimType)).thenReturn(true);
		when(strategy.assess(event)).thenReturn(approved);
		
		service.process(event);
		
		// Verify outboxService.save was called exactly once
		ArgumentCaptor<KafkaEvent> payloadCaptor = ArgumentCaptor.forClass(KafkaEvent.class);
		verify(outboxService, times(1)).save(eq(claimId.toString()), payloadCaptor.capture());
		
		// Verify the type and fields of the saved event
		KafkaEvent payload = payloadCaptor.getValue();
		assertThat(payload).isInstanceOf(ClaimAssessedEvent.class);
		
		ClaimAssessedEvent assessed = (ClaimAssessedEvent) payload;
		assertThat(assessed.claimId()).isEqualTo(claimId);
		assertThat(assessed.policyId()).isEqualTo(policyId);
		assertThat(assessed.claimNumber()).isEqualTo(claimNumber);
		assertThat(assessed.riskType()).isEqualTo(claimType);
		assertThat(assessed.approvedAmount()).isEqualByComparingTo("10000.00");
		assertThat(assessed.payableDays()).isZero();
		assertThat(assessed.decidedAt()).isNotNull();
	}
	
	@Test
	@DisplayName("rejected result → saves ClaimRejectedEvent to outbox")
	void rejectedResult_savesClaimRejectedEvent() {
		ClaimSubmittedEvent event = buildEvent();
		AssessmentResult rejected = AssessmentResult.rejected("INVALID_DATES");
		
		when(strategy.supports(claimType)).thenReturn(true);
		when(strategy.assess(event)).thenReturn(rejected);
		
		service.process(event);
		
		ArgumentCaptor<KafkaEvent> payloadCaptor = ArgumentCaptor.forClass(KafkaEvent.class);
		verify(outboxService, times(1)).save(eq(claimId.toString()), payloadCaptor.capture());
		
		KafkaEvent payload = payloadCaptor.getValue();
		assertThat(payload).isInstanceOf(ClaimRejectedEvent.class);
		
		ClaimRejectedEvent rejectedEvent = (ClaimRejectedEvent) payload;
		assertThat(rejectedEvent.claimId()).isEqualTo(claimId);
		assertThat(rejectedEvent.policyId()).isEqualTo(policyId);
		assertThat(rejectedEvent.claimNumber()).isEqualTo(claimNumber);
		assertThat(rejectedEvent.riskType()).isEqualTo(claimType);
		assertThat(rejectedEvent.reasonCode()).isEqualTo("INVALID_DATES");
		assertThat(rejectedEvent.decidedAt()).isNotNull();
	}
	
	@Test
	@DisplayName("no supporting strategy → throws UnsupportedClaimTypeException")
	void noSupportingStrategy_throwsUnsupportedClaimTypeException() {
		ClaimSubmittedEvent event = mock(ClaimSubmittedEvent.class);
		when(event.type()).thenReturn(claimType);
		
		when(strategy.supports(claimType)).thenReturn(false);
		
		assertThatThrownBy(() -> service.process(event))
				.isInstanceOf(UnsupportedClaimTypeException.class);
		
		// outboxService must not be called
		verifyNoInteractions(outboxService);
	}
	
	@Test
	@DisplayName("selects the first supporting strategy from the list")
	void selectsFirstSupportingStrategy() {
		ClaimSubmittedEvent event = buildEvent();
		AssessmentResult approved = AssessmentResult.approved(new BigDecimal("5000.00"), 0);
		
		ClaimAssessmentStrategy unsupported = mock(ClaimAssessmentStrategy.class);
		ClaimAssessmentStrategy supported   = mock(ClaimAssessmentStrategy.class);
		
		when(unsupported.supports(claimType)).thenReturn(false);
		when(supported.supports(claimType)).thenReturn(true);
		when(supported.assess(event)).thenReturn(approved);
		
		AssessmentService multiStrategyService =
				new AssessmentService(List.of(unsupported, supported), outboxService);
		
		multiStrategyService.process(event);
		
		verify(supported, times(1)).assess(event);
		verify(unsupported, never()).assess(any());
	}
}