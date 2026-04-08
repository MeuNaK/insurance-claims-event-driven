package dev.meuna.assessment.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
	
	@GetMapping("/")
	public String chekState() {
		return "Assessment service is UP";
	}
}
