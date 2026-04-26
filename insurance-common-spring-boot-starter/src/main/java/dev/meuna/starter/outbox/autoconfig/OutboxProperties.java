package dev.meuna.starter.outbox.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "outbox")
public class OutboxProperties {
	private String schema = "public";
	private Integer batchLimit = 100;
}
