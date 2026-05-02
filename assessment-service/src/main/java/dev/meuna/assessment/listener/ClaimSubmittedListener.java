package dev.meuna.assessment.listener;

import tools.jackson.databind.ObjectMapper;
import dev.meuna.starter.common.events.claim.ClaimSubmittedEvent;
import dev.meuna.assessment.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimSubmittedListener {
	
	private final AssessmentService assessmentService;
	private final ObjectMapper objectMapper;
	
	@KafkaListener(
			topics = "claim-submitted",
			groupId = "assessment-service",
			containerFactory = "kafkaListenerContainerFactory"
	)
	public void handle(
			@Payload String raw,
			@Header(KafkaHeaders.OFFSET) long offset,
			@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
			Acknowledgment ack) {
		
		ClaimSubmittedEvent event;
		try {
			event = objectMapper.readValue(raw, ClaimSubmittedEvent.class);
		} catch (Exception e) {
			log.error("Failed to deserialize ClaimSubmitted offset={} partition={}", offset, partition, e);
			ack.acknowledge();
			return;
		}
		
		log.info("Received ClaimSubmitted claimId={} offset={} partition={}",
				event.claimId(), offset, partition);
		
		assessmentService.process(event);
		
		ack.acknowledge();
	}
}
