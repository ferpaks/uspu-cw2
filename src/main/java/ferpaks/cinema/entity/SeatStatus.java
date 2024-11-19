package ferpaks.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeatStatus {
	FREE("Свободно"),
	BOOKED("Забронировано"),
	RESERVED("Бронируется"),
	UNAVAILABLE("Недоступно");

	private final String name;
}