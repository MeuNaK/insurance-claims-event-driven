package dev.meuna.claims.controller;

import dev.meuna.claims.dto.CreateClaimRequest;
import dev.meuna.claims.dto.CreateClaimResponse;
import dev.meuna.claims.dto.UpdateStatusRequest;
import dev.meuna.claims.entity.Claim;
import dev.meuna.claims.service.ClaimService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ClaimController {
	
	private ClaimService claimService;
	
	@GetMapping("/claims")
	public Iterable<Claim> findAll() {
		return claimService.findAll();
	}
	
	@GetMapping("/claims/{id}")
	public ResponseEntity<Claim> findById(@PathVariable Long id) {
		return ResponseEntity.of(claimService.findById(id));
	}
	
	@PatchMapping("/claims/{id}")
	public ResponseEntity<Claim> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
		return ResponseEntity.of(claimService.updateStatus(id, request.status()));
	}
	
	@PostMapping("/claims")
	public ResponseEntity<CreateClaimResponse> create(@RequestBody CreateClaimRequest request) {
		CreateClaimResponse response = claimService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
