package dev.meuna.claims.dto;

import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "Policy details")
public record PolicyResponse(
		@Schema(description = "Policy identifier")
		Long id,
		@Schema(description = "Risk type", example = "ILLNESS")
		InsuranceRiskType riskType,
		@Schema(description = "Policy sum insured", example = "1500000.00")
		BigDecimal sumInsured,
		@Schema(description = "Maximum payout per single claim", example = "300000.00")
		BigDecimal maxPerClaim,
		@Schema(description = "Policy start date (ISO-8601)", example = "2026-01-01T00:00:00Z")
		Date startDate,
		@Schema(description = "Policy end date (ISO-8601)", example = "2026-12-31T23:59:59Z")
		Date endDate,
		@Schema(description = "Daily payout percent", example = "0.5")
		BigDecimal dailyPayoutPercent,
		@Schema(description = "Waiting days before payout starts", example = "7")
		Integer waitingDays,
		@Schema(description = "Survival days requirement", example = "30")
		Integer survivalDays
) {}