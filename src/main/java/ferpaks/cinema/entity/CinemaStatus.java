package ferpaks.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CinemaStatus {
	OPEN("Работает"),
	CLOSED("Закрыт");

	private final String name;
}