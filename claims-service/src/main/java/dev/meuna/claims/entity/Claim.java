package dev.meuna.claims.entity;

import dev.meuna.starter.common.enums.claim.ClaimStatus;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity(name = "claim")
public class Claim {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "policy_id")
	private Long policyId;
	
	@Column(name = "claim_number")
	private String claimNumber;
	
	@Column(name = "incident_date")
	private Date incidentDate;

	@Column(name = "claim_submitted_date")
	private Date claimSubmittedDate;
	
	@Column(name = "type")
	private InsuranceRiskType type;
	
	@Column(name = "status")
	private ClaimStatus status;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "actual_expenses")
	private BigDecimal actualExpenses;
	
}
