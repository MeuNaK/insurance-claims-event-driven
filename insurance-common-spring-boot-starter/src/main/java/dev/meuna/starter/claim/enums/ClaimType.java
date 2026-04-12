package dev.meuna.starter.claim.enums;

import dev.meuna.starter.converter.EnumConverter;
import dev.meuna.starter.interfaces.PersistableEnum;
import jakarta.persistence.Converter;
import lombok.Getter;

public enum ClaimType implements PersistableEnum {
	VEHICLE(1),
	PROPERTY(2),
	HEALTH(3),
	LIFE(4),
	TRAVEL(5),
	LIABILITY(6),
	CARGO(7),
	OTHER(8),
	;
	
	@Getter
	private final int id;
	
	ClaimType(int id) {
		this.id = id;
	}
	
	@Converter(autoApply = true)
	public static class ClaimTypeConverter extends EnumConverter<ClaimType> {
		public ClaimTypeConverter() {
			super(ClaimType.class);
		}
	}
}
