package dev.meuna.claims.controller;

import dev.meuna.claims.dto.CreateClaimRequest;
import dev.meuna.claims.dto.CreateClaimResponse;
import dev.meuna.claims.dto.UpdateStatusRequest;
import dev.meuna.claims.entity.Claim;
import dev.meuna.claims.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Claims", description = "API for creating and managing insurance claims")
public class ClaimController {
	
	private ClaimService claimService;
	
	@Operation(summary = "Get all claims", description = "Returns a list of all claims")
	@ApiResponse(responseCode = "200", description = "Claims returned successfully")
	@GetMapping("/claims")
	public Iterable<Claim> findAll() {
		return claimService.findAll();
	}
	
	@Operation(summary = "Get claim by id", description = "Returns claim details by claim id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Claim found"),
			@ApiResponse(responseCode = "404", description = "Claim not found", content = @Content)
	})
	@GetMapping("/claims/{id}")
	public ResponseEntity<Claim> findById(@Parameter(description = "Claim ID", example = "1") @PathVariable Long id) {
		return ResponseEntity.of(claimService.findById(id));
	}
	
	@Operation(
			summary = "Update claim status",
			description = "Updates claim status by claim id",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "New claim status payload",
					required = true
			)
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Claim status updated successfully"),
			@ApiResponse(responseCode = "422", description = "Invalid status transition", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "404", description = "Claim not found", content = @Content)
	})
	@PatchMapping("/claims/{id}")
	public ResponseEntity<Claim> updateStatus(
			@Parameter(description = "Claim ID", example = "1") @PathVariable Long id,
			@RequestBody UpdateStatusRequest request
	) {
		return ResponseEntity.of(claimService.updateStatus(id, request.status()));
	}
	
	@Operation(
			summary = "Create claim",
			description = "Creates a new insurance claim",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Claim creation payload",
					required = true
			)
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Claim created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content)
	})
	@PostMapping("/claims")
	public ResponseEntity<CreateClaimResponse> create(
			@RequestBody CreateClaimRequest request
	) {
		CreateClaimResponse response = claimService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
