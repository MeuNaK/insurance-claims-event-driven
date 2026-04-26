package dev.meuna.starter.outbox.autoconfig;

import dev.meuna.starter.outbox.service.OutboxRelay;
import dev.meuna.starter.outbox.service.OutboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@AutoConfiguration
@EnableScheduling
@ConditionalOnProperty(
		prefix = "outbox.scheduling",
		name = "enabled",
		havingValue = "true")
public class OutboxSchedulingConfig {
	
	@Bean
	@ConditionalOnMissingBean(OutboxRelay.class)
	public OutboxRelay outboxRelay(OutboxService outboxService, KafkaTemplate<String, String> kafkaTemplate) {
		log.info("Outbox scheduler activate");
		return new OutboxRelay(outboxService, kafkaTemplate);
	}
}
