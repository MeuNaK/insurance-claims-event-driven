package dev.meuna.claims.entity;

import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity(name = "policy")
public class Policy {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "risk_type", nullable = false)
	private InsuranceRiskType riskType;

	@Column(name = "sum_insured", nullable = false)
	private BigDecimal sumInsured;

	@Column(name = "max_per_claim", nullable = false)
	private BigDecimal maxPerClaim;

	@Column(name = "start_date", nullable = false)
	private Date startDate;

	@Column(name = "end_date", nullable = false)
	private Date endDate;

	@Column(name = "daily_payout_percent", nullable = false)
	private BigDecimal dailyPayoutPercent;

	@Column(name = "waiting_days", nullable = false)
	private Integer waitingDays;

	@Column(name = "survival_days", nullable = false)
	private Integer survivalDays;
}
