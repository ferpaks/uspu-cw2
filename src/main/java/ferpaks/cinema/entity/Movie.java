package ferpaks.cinema.entity;

import ferpaks.cinema.converter.DurationConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false)
	@NotBlank(message = "Название фильма не может быть пустым")
	private String title;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "Текст ссылки не может быть пустым.")
	@Size(min = 3, max = 32, message = "Текст ссылки должен быть от 3 до 32 символов.")
	@Pattern(regexp = "^[a-z0-9-]+$", message = "Текст ссылки должен содержать только малые латинские буквы, цифры или минус.")
	private String linkTitle;

	@Column(length = 1000)
	@NotBlank(message = "Описание фильма не может быть пустым")
	private String description;

	@Convert(converter = DurationConverter.class)
	@Column(nullable = false)
	private Duration duration;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "movie_genre",
			joinColumns = @JoinColumn(name = "movie_id"),
			inverseJoinColumns = @JoinColumn(name = "genre_id"))
	private Set<Genre> genres;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Rating rating;

	/*
	@Column(nullable = false)
	@NotBlank(message = "Ссылка на плакат не может быть пустой")
	private String posterUrl;
	 */

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MovieStatus status;

	@Column(nullable = false)
	@DecimalMin(value = "0.0", message = "Цена должна быть больше нуля")
	private BigDecimal basePriceInRubles;

	@OneToMany(mappedBy = "movie", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Session> sessions;

	public String getFormattedDuration() {
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		return String.format("%02d:%02d", hours, minutes);
	}
}