package ferpaks.cinema.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность, отражающая купленный билет.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false, unique = true)
	private String code;

	@OneToOne
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking booking;

	@Column(nullable = false)
	private LocalDateTime purchasedAt;

	@Column(nullable = false)
	private BigDecimal priceAtPurchase;

	@PrePersist
	public void prePersist() {
		this.purchasedAt = LocalDateTime.now();
	}
}