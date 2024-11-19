package ferpaks.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
	PAID("Оплачено"),
	UNPAID("Не оплачено");

	private final String name;
}