package ferpaks.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность, отражающая бронирование места на сеанс пользователем.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private CinemaUser user;

	@ManyToOne
	@JoinColumn(name = "seat_id", nullable = false)
	private Seat seat;

	@ManyToOne
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@Column(nullable = false)
	private LocalDateTime bookingTime;

	@Column(nullable = false)
	private LocalDateTime reservationTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookingStatus bookingStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus;

	@Column(nullable = false)
	@PositiveOrZero
	private BigDecimal totalPriceInRubles;
}