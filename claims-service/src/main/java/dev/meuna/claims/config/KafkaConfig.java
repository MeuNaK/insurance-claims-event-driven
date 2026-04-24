package dev.meuna.claims.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Slf4j
@EnableScheduling
public class KafkaConfig {
	
	@Bean
	public NewTopic claimSubmittedTopic() {
		return TopicBuilder.name("claim-submitted").build();
	}
}
