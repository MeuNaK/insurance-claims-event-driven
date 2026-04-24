package dev.meuna.starter.outbox.autoconfig;

import dev.meuna.starter.outbox.repository.OutboxRepository;
import dev.meuna.starter.outbox.service.OutboxService;
import dev.meuna.starter.outbox.sql.OutboxTableInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@AutoConfiguration
@AutoConfigurationPackage(basePackageClasses = OutboxConfig.class)
@EnableConfigurationProperties(OutboxProperties.class)
public class OutboxConfig {
	
	@Bean
	@ConditionalOnMissingBean(OutboxTableInitializer.class)
	public static OutboxTableInitializer outboxTableInitializer(JdbcTemplate jdbcTemplate) {
		return new OutboxTableInitializer(jdbcTemplate);
	}
	
	@Bean
	@ConditionalOnMissingBean(OutboxRepository.class)
	public OutboxRepository outboxRepository(JdbcTemplate jdbcTemplate) {
		return new OutboxRepository(jdbcTemplate);
	}
	
	@Bean
	@ConditionalOnMissingBean(name = "outboxObjectMapper")
	public ObjectMapper outboxObjectMapper() {
		return new JsonMapper();
	}
	
	@Bean
	@ConditionalOnMissingBean(OutboxService.class)
	public OutboxService outboxService(OutboxRepository outboxRepository,
	                                   @Qualifier("outboxObjectMapper") ObjectMapper objectMapper,
	                                   OutboxProperties properties) {
		return new OutboxService(outboxRepository, objectMapper, properties);
	}
	
}
