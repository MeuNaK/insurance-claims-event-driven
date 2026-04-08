package dev.meuna.payment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
	
	@GetMapping("/")
	public String chekState() {
		return "Payment service is UP";
	}
}
