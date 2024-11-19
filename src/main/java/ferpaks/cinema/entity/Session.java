package ferpaks.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {
	// TODO Вынести в константы
	private static final BigDecimal BASIC_MODIFIER = new BigDecimal("1.0");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "movie_id", nullable = false)
	private Movie movie;

	@ManyToOne
	@JoinColumn(name = "hall_id", nullable = false)
	private Hall hall;

	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Seat> seats = new ArrayList<>();

	@OneToMany(mappedBy = "session", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Booking> bookings;

	@Column(nullable = false)
	private LocalDateTime sessionStartTime;

	public LocalDateTime getEndTime() {
		return sessionStartTime.plusMinutes(movie.getDuration().toMinutes());
	}
}