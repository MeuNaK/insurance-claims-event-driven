package dev.meuna.starter.common.events;

public interface KafkaEvent {
	String aggregateType();
	String topicName();
}
