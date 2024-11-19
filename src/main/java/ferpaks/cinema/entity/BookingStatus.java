package ferpaks.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {
	CONFIRMED("Подтверждено"),   // (Оплата завершена)
	RESERVED("Зарезервировано"), // (Оплата ожидается)
	EXPIRED("Истекло"),          // (Оплаты не было)
	CANCELED("Отменено");        // (Оплата возвращается)

	private final String name;
}