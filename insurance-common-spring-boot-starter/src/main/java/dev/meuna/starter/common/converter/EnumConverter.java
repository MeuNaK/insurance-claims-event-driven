package dev.meuna.starter.common.converter;

import dev.meuna.starter.common.interfaces.PersistableEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EnumConverter<E extends Enum<E> & PersistableEnum> implements AttributeConverter<E, Integer> {
	
	private final Class<E> enumClass;
	
	protected EnumConverter(Class<E> enumClass) {
		this.enumClass = enumClass;
	}
	
	
	@Override
	public Integer convertToDatabaseColumn(E attribute) {
		return attribute == null
		       ? null
		       : attribute.getId();
	}
	
	@Override
	public E convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		
		for (E constant : enumClass.getEnumConstants()) {
			if (constant.getId() == dbData) {
				return constant;
			}
		}
		throw new IllegalArgumentException("Unknown id " + dbData + " for enum " + enumClass.getSimpleName());
	}
}
