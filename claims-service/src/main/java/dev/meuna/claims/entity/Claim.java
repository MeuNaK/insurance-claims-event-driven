package dev.meuna.claims.entity;

import dev.meuna.starter.claim.enums.ClaimStatus;
import dev.meuna.starter.claim.enums.ClaimType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
	
	@Column(name = "type")
	private ClaimType type;
	
	@Column(name = "status")
	private ClaimStatus status;
	
	@Column(name = "description")
	private String description;
	
}
