package dev.meuna.starter.outbox.service;

import dev.meuna.starter.outbox.model.OutboxEvent;
import dev.meuna.starter.outbox.model.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OutboxRelay {
	
	private final OutboxService outboxService;
	private final KafkaTemplate<String, String> kafka;
	
	@Scheduled(fixedDelayString = "${outbox.fixed-delay-ms:30000}")
	public void relay() {
		log.info("[Outbox] relay start");
		List<OutboxEvent> eventsToRelay = outboxService.findPendingAndFailed(List.of(OutboxStatus.FAILED, OutboxStatus.PENDING));
		log.info("[Outbox] number of events to relay={}", eventsToRelay.size());
		eventsToRelay.forEach(event -> {
			try {
				kafka.send(event.getEventType(), event.getPayload()).get();
				outboxService.markProcessed(event.getId());
			} catch (Exception e) {
				outboxService.markFailed(event.getId(), e.getMessage());
				log.error("[Outbox] have error when relay with event id={} type={} aggregate={}#{}",
						event.getId(), event.getEventType(), event.getAggregateType(), event.getAggregateId(), e);
			}
		});
		log.info("[Outbox] relay end");
	}
}
