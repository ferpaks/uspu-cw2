package ferpaks.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hall {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false)
	@NotBlank(message = "Название зала не может быть пустым")
	@Size(min = 2, max = 32, message = "Название зала должно быть от 2 до 32 символов")
	private String name;

	// При удалении зала удаляются все места в нём
	@OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Seat> seats;

	@OneToMany(mappedBy = "hall", cascade = CascadeType.REMOVE)
	private List<Session> sessions;

	@ManyToOne
	@JoinColumn(name = "cinema_id", nullable = false)
	private Cinema cinema;
}