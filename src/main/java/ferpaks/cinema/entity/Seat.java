package ferpaks.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "session_id")
	private Session session;

	@ManyToOne
	@JoinColumn(name = "hall_id", nullable = false)
	private Hall hall;

	@Column(nullable = false)
	@Min(value = 1, message = "Номер ряда должен быть больше нуля")
	private int rowNumber;

	@Column(nullable = false)
	@Min(value = 1, message = "Номер места должен быть больше нуля")
	private int seatNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeatStatus status = SeatStatus.FREE;

	@Column
	private LocalDateTime reservedAt;

	@OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Booking> bookings;
}