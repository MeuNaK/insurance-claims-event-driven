package dev.meuna.payment.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
	
	@Bean
	public NewTopic paymentCompletedTopic() {
		return new NewTopic("payment-completed", 1, (short) 1);
	}
	
	@Bean
	public NewTopic paymentFailedTopic() {
		return new NewTopic("payment-failed", 1, (short) 1);
	}
}
