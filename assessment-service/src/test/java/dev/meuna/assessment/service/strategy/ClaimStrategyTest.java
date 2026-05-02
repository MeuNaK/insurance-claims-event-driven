package dev.meuna.assessment.service.strategy;

import dev.meuna.assessment.dto.AssessmentResult;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for claim assessment strategies.
 *
 * Shared base-validation logic (INVALID_DATES, OUTSIDE_POLICY_PERIOD, POLICY_EXPIRED)
 * is exercised via LumpSumStrategy as a representative concrete implementation.
 */
class ClaimStrategyTest {
	
	// -----------------------------------------------------------------------
	// Helpers
	// -----------------------------------------------------------------------
	
	private static Date date(int year, int month, int day) {
		return Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant());
	}
	
	/**
	 * Creates a fully-valid base event and returns a Mockito mock so individual
	 * tests can override only the fields they care about.
	 *
	 * Default timeline:
	 *   policyStart  = 2024-01-01
	 *   incidentDate = 2024-06-01
	 *   submitted    = 2024-06-10
	 *   policyEnd    = 2024-12-31
	 */
	private ClaimSubmittedEvent baseEvent(InsuranceRiskType type) {
		ClaimSubmittedEvent e = mock(ClaimSubmittedEvent.class);
		when(e.type()).thenReturn(type);
		when(e.policyStartDate()).thenReturn(date(2024, 1, 1));
		when(e.policyEndDate()).thenReturn(date(2024, 12, 31));
		when(e.incidentDate()).thenReturn(date(2024, 6, 1));
		when(e.claimSubmittedDate()).thenReturn(date(2024, 6, 10));
		when(e.policySumInsured()).thenReturn(new BigDecimal("10000"));
		when(e.maxPerClaim()).thenReturn(new BigDecimal("10000"));
		return e;
	}
	
	// -----------------------------------------------------------------------
	// BaseClaimStrategy – common validation (tested via LumpSumStrategy)
	// -----------------------------------------------------------------------
	
	@Nested
	@DisplayName("BaseClaimStrategy – common validation")
	class BaseValidation {
		
		private LumpSumStrategy strategy;
		private ClaimSubmittedEvent event;
		
		@BeforeEach
		void setUp() {
			strategy = new LumpSumStrategy();
			event = baseEvent(InsuranceRiskType.DEATH);
		}
		
		@Test
		@DisplayName("rejects when submittedDate is before incidentDate")
		void rejectsInvalidDates() {
			when(event.claimSubmittedDate()).thenReturn(date(2024, 5, 31)); // before incident
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("INVALID_DATES");
		}
		
		@Test
		@DisplayName("rejects when incidentDate is before policyStartDate")
		void rejectsIncidentBeforePolicyStart() {
			when(event.incidentDate()).thenReturn(date(2023, 12, 31));
			when(event.claimSubmittedDate()).thenReturn(date(2024, 1, 5));
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("OUTSIDE_POLICY_PERIOD");
		}
		
		@Test
		@DisplayName("rejects when incidentDate is after policyEndDate")
		void rejectsIncidentAfterPolicyEnd() {
			when(event.incidentDate()).thenReturn(date(2025, 1, 1));
			when(event.claimSubmittedDate()).thenReturn(date(2025, 1, 5));
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("OUTSIDE_POLICY_PERIOD");
		}
		
		@Test
		@DisplayName("rejects when submittedDate is after policyEndDate")
		void rejectsPolicyExpired() {
			when(event.claimSubmittedDate()).thenReturn(date(2025, 1, 1)); // after policy end
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("POLICY_EXPIRED");
		}
		
		@Test
		@DisplayName("passes validation when all dates are on boundary (same day)")
		void passesWhenDatesOnBoundary() {
			when(event.incidentDate()).thenReturn(date(2024, 1, 1));       // == policyStart
			when(event.claimSubmittedDate()).thenReturn(date(2024, 12, 31)); // == policyEnd
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
		}
	}
	
	// -----------------------------------------------------------------------
	// LumpSumStrategy
	// -----------------------------------------------------------------------
	
	@Nested
	@DisplayName("LumpSumStrategy")
	class LumpSumStrategyTest {
		
		private LumpSumStrategy strategy;
		
		@BeforeEach
		void setUp() {
			strategy = new LumpSumStrategy();
		}
		
		@Test
		@DisplayName("supports DEATH, PERMANENT_DISABILITY, PARTIAL_DISABILITY_2, PARTIAL_DISABILITY_3")
		void supportsCorrectTypes() {
			assertThat(strategy.supports(InsuranceRiskType.DEATH)).isTrue();
			assertThat(strategy.supports(InsuranceRiskType.PERMANENT_DISABILITY)).isTrue();
			assertThat(strategy.supports(InsuranceRiskType.PARTIAL_DISABILITY_2)).isTrue();
			assertThat(strategy.supports(InsuranceRiskType.PARTIAL_DISABILITY_3)).isTrue();
		}
		
		@Test
		@DisplayName("does not support ILLNESS or MEDICAL_EXPENSE")
		void doesNotSupportOtherTypes() {
			assertThat(strategy.supports(InsuranceRiskType.ILLNESS)).isFalse();
			assertThat(strategy.supports(InsuranceRiskType.MEDICAL_EXPENSE)).isFalse();
		}
		
		@Test
		@DisplayName("DEATH pays 100% of sum insured")
		void deathPaysFullSum() {
			ClaimSubmittedEvent event = baseEvent(InsuranceRiskType.DEATH);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
			assertThat(result.amount()).isEqualByComparingTo("10000.00");
			assertThat(result.payableDays()).isZero();
		}
		
		@Test
		@DisplayName("PERMANENT_DISABILITY pays 100% of sum insured")
		void permanentDisabilityPaysFullSum() {
			ClaimSubmittedEvent event = baseEvent(InsuranceRiskType.PERMANENT_DISABILITY);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
			assertThat(result.amount()).isEqualByComparingTo("10000.00");
		}
		
		@Test
		@DisplayName("PARTIAL_DISABILITY_2 pays 75% of sum insured")
		void partialDisability2Pays75Percent() {
			ClaimSubmittedEvent event = baseEvent(InsuranceRiskType.PARTIAL_DISABILITY_2);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
			assertThat(result.amount()).isEqualByComparingTo("7500.00");
		}
		
		@Test
		@DisplayName("PARTIAL_DISABILITY_3 pays 50% of sum insured")
		void partialDisability3Pays50Percent() {
			ClaimSubmittedEvent event = baseEvent(InsuranceRiskType.PARTIAL_DISABILITY_3);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
			assertThat(result.amount()).isEqualByComparingTo("5000.00");
		}
		
		@Test
		@DisplayName("caps payout at maxPerClaim when sum insured exceeds it")
		void capsAtMaxPerClaim() {
			ClaimSubmittedEvent event = baseEvent(InsuranceRiskType.DEATH);
			when(event.policySumInsured()).thenReturn(new BigDecimal("50000"));
			when(event.maxPerClaim()).thenReturn(new BigDecimal("8000"));
			AssessmentResult result = strategy.assess(event);
			assertThat(result.amount()).isEqualByComparingTo("8000.00");
		}
		
		@Test
		@DisplayName("caps payout at policySumInsured when maxPerClaim is higher")
		void capsAtPolicySumInsured() {
			ClaimSubmittedEvent event = baseEvent(InsuranceRiskType.DEATH);
			when(event.policySumInsured()).thenReturn(new BigDecimal("3000"));
			when(event.maxPerClaim()).thenReturn(new BigDecimal("99999"));
			AssessmentResult result = strategy.assess(event);
			assertThat(result.amount()).isEqualByComparingTo("3000.00");
		}
	}
	
	// -----------------------------------------------------------------------
	// PerDiemStrategy
	// -----------------------------------------------------------------------
	
	@Nested
	@DisplayName("PerDiemStrategy")
	class PerDiemStrategyTest {
		
		private PerDiemStrategy strategy;
		
		@BeforeEach
		void setUp() {
			strategy = new PerDiemStrategy();
		}
		
		private ClaimSubmittedEvent perDiemEvent(InsuranceRiskType type) {
			ClaimSubmittedEvent e = baseEvent(type);
			// dailyPayoutPercent: 1% per day of sum insured (100/day on 10 000)
			when(e.dailyPayoutPercent()).thenReturn(new BigDecimal("1"));
			when(e.waitingDays()).thenReturn(0);
			when(e.survivalDays()).thenReturn(0);
			return e;
		}
		
		@Test
		@DisplayName("supports ILLNESS and HOSPITALIZATION")
		void supportsCorrectTypes() {
			assertThat(strategy.supports(InsuranceRiskType.ILLNESS)).isTrue();
			assertThat(strategy.supports(InsuranceRiskType.HOSPITALIZATION)).isTrue();
		}
		
		@Test
		@DisplayName("does not support DEATH or MEDICAL_EXPENSE")
		void doesNotSupportOtherTypes() {
			assertThat(strategy.supports(InsuranceRiskType.DEATH)).isFalse();
			assertThat(strategy.supports(InsuranceRiskType.MEDICAL_EXPENSE)).isFalse();
		}
		
		@Test
		@DisplayName("rejects when submission is within waiting period")
		void rejectsWithinWaitingPeriod() {
			ClaimSubmittedEvent event = perDiemEvent(InsuranceRiskType.ILLNESS);
			// incident = 2024-06-01, waiting = 10 days → covered starts 2024-06-11
			when(event.waitingDays()).thenReturn(10);
			when(event.claimSubmittedDate()).thenReturn(date(2024, 6, 5)); // before coveredStart
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("WAITING_PERIOD_NOT_MET");
		}
		
		@Test
		@DisplayName("rejects when submission is within survival period")
		void rejectsWithinSurvivalPeriod() {
			ClaimSubmittedEvent event = perDiemEvent(InsuranceRiskType.ILLNESS);
			// survival = 30 days → survival check date = 2024-07-01
			when(event.survivalDays()).thenReturn(30);
			when(event.claimSubmittedDate()).thenReturn(date(2024, 6, 15)); // before survivalCheck
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("SURVIVAL_PERIOD_NOT_MET");
		}
		
		@Test
		@DisplayName("calculates correct payable days (no waiting, no survival)")
		void calculatesPayableDays() {
			// incident = 2024-06-01, submitted = 2024-06-10 → 10 days (inclusive)
			ClaimSubmittedEvent event = perDiemEvent(InsuranceRiskType.ILLNESS);
			// submitted is already 2024-06-10 in baseEvent
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
			assertThat(result.payableDays()).isEqualTo(10);
			// 10 000 * 1% * 10 = 1000.00
			assertThat(result.amount()).isEqualByComparingTo("1000.00");
		}
		
		@Test
		@DisplayName("payable days start from coveredStart when there is a waiting period")
		void payableDaysStartAfterWaitingPeriod() {
			ClaimSubmittedEvent event = perDiemEvent(InsuranceRiskType.HOSPITALIZATION);
			// incident = 2024-06-01, waiting = 5 → coveredStart = 2024-06-06
			when(event.waitingDays()).thenReturn(5);
			when(event.claimSubmittedDate()).thenReturn(date(2024, 6, 10));
			// days between 2024-06-06 and 2024-06-10 inclusive = 5
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
			assertThat(result.payableDays()).isEqualTo(5);
		}
		
		@Test
		@DisplayName("caps payout at maxPerClaim")
		void capsPayoutAtMaxPerClaim() {
			ClaimSubmittedEvent event = perDiemEvent(InsuranceRiskType.ILLNESS);
			when(event.maxPerClaim()).thenReturn(new BigDecimal("500"));
			// raw = 10 000 * 1% * 10 = 1000, capped at 500
			AssessmentResult result = strategy.assess(event);
			assertThat(result.amount()).isEqualByComparingTo("500.00");
		}
		
		@Test
		@DisplayName("null waitingDays treated as zero")
		void nullWaitingDaysTreatedAsZero() {
			ClaimSubmittedEvent event = perDiemEvent(InsuranceRiskType.ILLNESS);
			when(event.waitingDays()).thenReturn(null);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
		}
		
		@Test
		@DisplayName("null survivalDays treated as zero")
		void nullSurvivalDaysTreatedAsZero() {
			ClaimSubmittedEvent event = perDiemEvent(InsuranceRiskType.ILLNESS);
			when(event.survivalDays()).thenReturn(null);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
		}
	}
	
	// -----------------------------------------------------------------------
	// ReimbursementStrategy
	// -----------------------------------------------------------------------
	
	@Nested
	@DisplayName("ReimbursementStrategy")
	class ReimbursementStrategyTest {
		
		private ReimbursementStrategy strategy;
		
		@BeforeEach
		void setUp() {
			strategy = new ReimbursementStrategy();
		}
		
		private ClaimSubmittedEvent reimbursementEvent(InsuranceRiskType type) {
			ClaimSubmittedEvent e = baseEvent(type);
			when(e.actualExpenses()).thenReturn(new BigDecimal("3000"));
			return e;
		}
		
		@Test
		@DisplayName("supports MEDICAL_EXPENSE and REHABILITATION")
		void supportsCorrectTypes() {
			assertThat(strategy.supports(InsuranceRiskType.MEDICAL_EXPENSE)).isTrue();
			assertThat(strategy.supports(InsuranceRiskType.REHABILITATION)).isTrue();
		}
		
		@Test
		@DisplayName("does not support DEATH or ILLNESS")
		void doesNotSupportOtherTypes() {
			assertThat(strategy.supports(InsuranceRiskType.DEATH)).isFalse();
			assertThat(strategy.supports(InsuranceRiskType.ILLNESS)).isFalse();
		}
		
		@Test
		@DisplayName("approves actual expenses within policy limits")
		void approvesActualExpenses() {
			ClaimSubmittedEvent event = reimbursementEvent(InsuranceRiskType.MEDICAL_EXPENSE);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isTrue();
			assertThat(result.amount()).isEqualByComparingTo("3000.00");
			assertThat(result.payableDays()).isZero();
		}
		
		@Test
		@DisplayName("rejects when actualExpenses is null")
		void rejectsNullExpenses() {
			ClaimSubmittedEvent event = reimbursementEvent(InsuranceRiskType.MEDICAL_EXPENSE);
			when(event.actualExpenses()).thenReturn(null);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("NO_EXPENSES_PROVIDED");
		}
		
		@Test
		@DisplayName("rejects when actualExpenses is zero")
		void rejectsZeroExpenses() {
			ClaimSubmittedEvent event = reimbursementEvent(InsuranceRiskType.MEDICAL_EXPENSE);
			when(event.actualExpenses()).thenReturn(BigDecimal.ZERO);
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("NO_EXPENSES_PROVIDED");
		}
		
		@Test
		@DisplayName("rejects when actualExpenses is negative")
		void rejectsNegativeExpenses() {
			ClaimSubmittedEvent event = reimbursementEvent(InsuranceRiskType.REHABILITATION);
			when(event.actualExpenses()).thenReturn(new BigDecimal("-100"));
			AssessmentResult result = strategy.assess(event);
			assertThat(result.approved()).isFalse();
			assertThat(result.reasonCode()).isEqualTo("NO_EXPENSES_PROVIDED");
		}
		
		@Test
		@DisplayName("caps approved amount at maxPerClaim")
		void capsAtMaxPerClaim() {
			ClaimSubmittedEvent event = reimbursementEvent(InsuranceRiskType.REHABILITATION);
			when(event.actualExpenses()).thenReturn(new BigDecimal("15000"));
			when(event.maxPerClaim()).thenReturn(new BigDecimal("5000"));
			AssessmentResult result = strategy.assess(event);
			assertThat(result.amount()).isEqualByComparingTo("5000.00");
		}
		
		@Test
		@DisplayName("caps approved amount at policySumInsured")
		void capsAtPolicySumInsured() {
			ClaimSubmittedEvent event = reimbursementEvent(InsuranceRiskType.MEDICAL_EXPENSE);
			when(event.actualExpenses()).thenReturn(new BigDecimal("12000"));
			when(event.policySumInsured()).thenReturn(new BigDecimal("10000"));
			when(event.maxPerClaim()).thenReturn(new BigDecimal("99999"));
			AssessmentResult result = strategy.assess(event);
			assertThat(result.amount()).isEqualByComparingTo("10000.00");
		}
	}
}