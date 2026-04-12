package dev.meuna.starter.claim.enums;

import dev.meuna.starter.converter.EnumConverter;
import dev.meuna.starter.interfaces.PersistableEnum;
import jakarta.persistence.Converter;
import lombok.Getter;


public enum ClaimStatus implements PersistableEnum {
	SUBMITTED(1),
	IN_REVIEW(2),
	APPROVED(3),
	REJECTED(4),
	;
	
	@Getter
	private final int id;
	
	ClaimStatus(int id) {
		this.id = id;
	}
	
	@Converter(autoApply = true)
	public static class ClaimStatusConverter extends EnumConverter<ClaimStatus> {
		public ClaimStatusConverter() {
			super(ClaimStatus.class);
		}
	}
}
