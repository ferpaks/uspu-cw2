package ferpaks.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class City {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "Название города не может быть пустым")
	private String name;

	@OneToMany(mappedBy = "city", cascade = CascadeType.REMOVE)
	private List<Street> streets;
}