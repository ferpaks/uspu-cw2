package ferpaks.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cinema {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	/**
	 * Название кинотеатра в интерфейсе приложения.
	 */
	@Column(nullable = false, unique = true)
	@NotBlank(message = "Название кинотеатра не может быть пустым.")
	private String name;

	/**
	 * Текст в конечной части ссылки на кинотеатр, чтобы не отображать там нечеловекочитаемый ID.
	 */
	@Column(nullable = false, unique = true)
	@NotBlank(message = "Текст ссылки не может быть пустым.")
	@Size(min = 3, max = 32, message = "Текст ссылки должен быть от 3 до 32 символов.")
	@Pattern(regexp = "^[a-z0-9-]+$", message = "Текст ссылки должен содержать только малые латинские буквы, цифры или минус.")
	private String linkName;

	@OneToOne(cascade = CascadeType.ALL)
	private Address address;

	@Enumerated(EnumType.STRING)
	private CinemaStatus status;

	@ManyToOne(optional = false)
	@JoinColumn(name = "manager_id", nullable = false)
	private CinemaUser manager;

	@OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Hall> halls;

	public boolean isOpen() {
		return CinemaStatus.OPEN.equals(this.status);
	}
}