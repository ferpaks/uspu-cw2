package ferpaks.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This enumeration is used to indicate movie rating by Russian Age Rating System.
 * Это перечисление используется для отображения рейтинга фильма по Российской системе.
 */
@Getter
@AllArgsConstructor
public enum Rating {
	ZERO_PLUS("0+"),
	SIX_PLUS("6+"),
	TWELVE_PLUS("12+"),
	SIXTEEN_PLUS("16+"),
	EIGHTEEN_PLUS("18+");

	private final String name;
}