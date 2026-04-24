package dev.meuna.starter.outbox.sql;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class OutboxTableInitializer  {
	
	private final JdbcTemplate jdbcTemplate;
	
	private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS outbox_events (
                id               UUID         PRIMARY KEY,
                aggregate_type   VARCHAR(255) NOT NULL,
                aggregate_id     VARCHAR(255) NOT NULL,
                event_type       VARCHAR(255) NOT NULL,
                payload          TEXT         NOT NULL,
                status           VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
                retry_count      INT          NOT NULL DEFAULT 0,
                created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                processed_at     TIMESTAMP,
                error_message    TEXT
            )
            """;
	
	private static final List<String> DDL_STATEMENTS = List.of(
			CREATE_TABLE_SQL,
			"""
			CREATE INDEX IF NOT EXISTS idx_outbox_status_created
			ON outbox_events (status, created_at)
			""",
			"""
			CREATE INDEX IF NOT EXISTS idx_outbox_aggregate
			ON outbox_events (aggregate_type, aggregate_id)
			"""
	);
	
	@PostConstruct
	public void initialize() {
		log.info("[Outbox] Initializing outbox_events table...");
		DDL_STATEMENTS.forEach(jdbcTemplate::execute);
		log.info("[Outbox] outbox_events table and indexes are ready.");
	}
}