package ferpaks.cinema.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Конвертирует String из формы в Duration.
 */
@Component
public class StringToDurationConverter implements Converter<String, Duration> {
	@Override
	public Duration convert(String source) {
		try {
			return Duration.ofMinutes(Long.parseLong(source));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Неверный формат duration", e);
		}
	}
}