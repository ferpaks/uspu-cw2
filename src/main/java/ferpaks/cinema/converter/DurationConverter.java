package ferpaks.cinema.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

/**
 * Преобразует Duration в Long, чтобы сохранялось в базу данных.
 */
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {
	private static final long DEFAULT_VALUE = 0L;

	@Override
	public Long convertToDatabaseColumn(Duration duration) {
		return duration != null ? duration.toMinutes() : DEFAULT_VALUE;
	}

	@Override
	public Duration convertToEntityAttribute(Long minutes) {
		return minutes != null ? Duration.ofMinutes(minutes) : Duration.ZERO;
	}
}