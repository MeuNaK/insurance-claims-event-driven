package dev.meuna.claims.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
	
	@GetMapping("/")
	public String chekState() {
		return "Claims service is UP";
	}
}
