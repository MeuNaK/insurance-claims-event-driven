package dev.meuna.starter.outbox.repository;

import dev.meuna.starter.outbox.model.OutboxEvent;
import dev.meuna.starter.outbox.model.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
public class OutboxRepository {

	private final JdbcTemplate jdbcTemplate;

	public OutboxEvent save(OutboxEvent event) {
		if (event.getId() == null) {
			event.setId(UUID.randomUUID());
			insert(event);
		} else {
			update(event);
		}
		return event;
	}

	public List<OutboxEvent> findByStatusOrderByCreatedAt(OutboxStatus status, int limit) {
		return jdbcTemplate.query(
				"""
				SELECT id,
				       aggregate_type,
				       aggregate_id,
				       event_type,
				       payload,
				       status,
				       retry_count,
				       created_at,
				       processed_at,
				       error_message
				FROM outbox_events
				WHERE status = ?
				ORDER BY created_at
				LIMIT ?
				""",
				this::mapRow,
				status.name(),
				limit
		);
	}

	public List<OutboxEvent> findByStatusesOrderByCreatedAt(List<OutboxStatus> statuses, int limit) {
		if (statuses == null || statuses.isEmpty() || limit <= 0) {
			return List.of();
		}
		String sql = """
				SELECT id,
				       aggregate_type,
				       aggregate_id,
				       event_type,
				       payload,
				       status,
				       retry_count,
				       created_at,
				       processed_at,
				       error_message
				FROM outbox_events
				WHERE status IN (%s)
				ORDER BY created_at
				LIMIT ?
				""".formatted(String.join(", ", Collections.nCopies(statuses.size(), "?")));
		List<Object> args = new ArrayList<>();
		statuses.forEach(status -> args.add(status.name()));
		args.add(limit);
		return jdbcTemplate.query(
				sql,
				this::mapRow,
				args.toArray()
		);
	}

	public Optional<OutboxEvent> findById(UUID id) {
		List<OutboxEvent> rows = jdbcTemplate.query(
				"""
				SELECT id,
				       aggregate_type,
				       aggregate_id,
				       event_type,
				       payload,
				       status,
				       retry_count,
				       created_at,
				       processed_at,
				       error_message
				FROM outbox_events
				WHERE id = ?
				""",
				this::mapRow,
				id
		);
		return rows.stream().findFirst();
	}

	private void insert(OutboxEvent event) {
		jdbcTemplate.update(
				"""
				INSERT INTO outbox_events (
				    id, aggregate_type, aggregate_id, event_type, payload, status, retry_count, created_at, processed_at, error_message
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""",
				event.getId(),
				event.getAggregateType(),
				event.getAggregateId(),
				event.getEventType(),
				event.getPayload(),
				event.getStatus() == null ? OutboxStatus.PENDING.name() : event.getStatus().name(),
				event.getRetryCount(),
				toTimestamp(event.getCreatedAt()),
				toTimestamp(event.getProcessedAt()),
				event.getErrorMessage()
		);
	}

	private void update(OutboxEvent event) {
		jdbcTemplate.update(
				"""
				UPDATE outbox_events
				SET aggregate_type = ?,
				    aggregate_id = ?,
				    event_type = ?,
				    payload = ?,
				    status = ?,
				    retry_count = ?,
				    created_at = ?,
				    processed_at = ?,
				    error_message = ?
				WHERE id = ?
				""",
				event.getAggregateType(),
				event.getAggregateId(),
				event.getEventType(),
				event.getPayload(),
				event.getStatus().name(),
				event.getRetryCount(),
				toTimestamp(event.getCreatedAt()),
				toTimestamp(event.getProcessedAt()),
				event.getErrorMessage(),
				event.getId()
		);
	}

	private OutboxEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
		return OutboxEvent.builder()
				.id(UUID.fromString(rs.getString("id")))
				.aggregateType(rs.getString("aggregate_type"))
				.aggregateId(rs.getString("aggregate_id"))
				.eventType(rs.getString("event_type"))
				.payload(rs.getString("payload"))
				.status(OutboxStatus.valueOf(rs.getString("status")))
				.retryCount(rs.getInt("retry_count"))
				.createdAt(toInstant(rs.getTimestamp("created_at")))
				.processedAt(toInstant(rs.getTimestamp("processed_at")))
				.errorMessage(rs.getString("error_message"))
				.build();
	}

	private static Timestamp toTimestamp(Instant instant) {
		return instant == null ? null : Timestamp.from(instant);
	}

	private static Instant toInstant(Timestamp timestamp) {
		return timestamp == null ? null : timestamp.toInstant();
	}
}
