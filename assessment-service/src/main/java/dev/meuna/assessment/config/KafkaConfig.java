package dev.meuna.assessment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
	
	@Bean
	public NewTopic claimAssessTopic() {
		return new NewTopic("claim-assess", 1, (short) 1);
	}
	
	@Bean
	public NewTopic claimRejectedTopic() {
		return new NewTopic("claim-rejected", 1, (short) 1);
	}
}
