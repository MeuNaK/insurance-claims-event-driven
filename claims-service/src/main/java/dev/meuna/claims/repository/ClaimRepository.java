package dev.meuna.claims.repository;

import dev.meuna.claims.entity.Claim;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimRepository extends CrudRepository<Claim, Long> {
	
	Optional<Claim> findByClaimNumber(String claimNumber);
}
