package dev.meuna.claims.repository;

import dev.meuna.claims.entity.Policy;
import dev.meuna.starter.common.enums.risk.InsuranceRiskType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyRepository extends CrudRepository<Policy, Long> {
	Optional<Policy> findByIdAndRiskType(Long id, InsuranceRiskType riskType);
}
