package ferpaks.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovieStatus {
	ACTIVE("В прокате"),
	ARCHIVED("В архиве");

	private final String name;
}