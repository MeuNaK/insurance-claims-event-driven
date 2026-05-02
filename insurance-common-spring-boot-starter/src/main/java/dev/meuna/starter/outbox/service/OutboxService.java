package dev.meuna.starter.outbox.service;

import dev.meuna.starter.common.events.KafkaEvent;
import dev.meuna.starter.outbox.autoconfig.OutboxProperties;
import dev.meuna.starter.outbox.exeptions.OutboxSerializationException;
import dev.meuna.starter.outbox.model.OutboxEvent;
import dev.meuna.starter.outbox.model.OutboxStatus;
import dev.meuna.starter.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class OutboxService{
	
	private final OutboxRepository outboxRepository;
	private final ObjectMapper objectMapper;
	private final OutboxProperties properties;
	
	/**
	 *
	 * @param aggregateType the type of entity or business object
	 * @param aggregateId the identifier of the specific aggregate instance
	 * @param eventType the type of event
	 * @param payload message object. Will storage as a JSON
	 * @return
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public OutboxEvent save(String aggregateType,
	                        String aggregateId,
	                        String eventType,
	                        Object payload) {
		try {
			String json = objectMapper.writeValueAsString(payload);
			return save(aggregateType, aggregateId, eventType, json);
		} catch (JacksonException e) {
			throw new OutboxSerializationException(
					"Failed to serialize payload for event: " + eventType, e);
		}
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public OutboxEvent save(String aggregateId, KafkaEvent event) {
		return save(event.aggregateType(), aggregateId, event.topicName(), event);
	}
	
	@Transactional(propagation = Propagation.MANDATORY)
	public OutboxEvent save(String aggregateType,
	                        String aggregateId,
	                        String eventType,
	                        String jsonPayload) {
		OutboxEvent event = OutboxEvent.builder()
				.aggregateType(aggregateType)
				.aggregateId(aggregateId)
				.eventType(eventType)
				.payload(jsonPayload)
				.status(OutboxStatus.PENDING)
				.build();
		
		OutboxEvent saved = outboxRepository.save(event);
		log.debug("[Outbox] Saved id={} type={} aggregate={}#{}",
				saved.getId(), eventType, aggregateType, aggregateId);
		return saved;
	}

	@Transactional(readOnly = true)
	public List<OutboxEvent> findPendingAndFailed(List<OutboxStatus> statusList) {
		return outboxRepository.findByStatusesOrderByCreatedAt(statusList, properties.getBatchLimit());
	}
	
	public void markProcessed(UUID id) {
		Optional<OutboxEvent> outboxEventOptional = outboxRepository.findById(id);
		if (outboxEventOptional.isPresent()) {
			OutboxEvent outboxEvent = outboxEventOptional.get();
			outboxEvent.markProcessed();
			outboxRepository.save(outboxEvent);
		} else {
			throw new RuntimeException("outboxEvent with id=" + id + " not found");
		}
	}
	
	public void markFailed(UUID id, String message) {
		Optional<OutboxEvent> outboxEventOptional = outboxRepository.findById(id);
		if (outboxEventOptional.isPresent()) {
			OutboxEvent outboxEvent = outboxEventOptional.get();
			outboxEvent.markFailed(message);
			outboxRepository.save(outboxEvent);
		} else {
			log.error("[Outbox] event not found when markFailed eventId={}", id);
		}
	}
}
