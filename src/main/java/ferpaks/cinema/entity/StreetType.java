package ferpaks.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreetType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "Сокращённое обозначение улицы не может быть пустым")
	private String shortName;

	@Column(nullable = false, unique = true)
	@NotBlank(message = "Полное обозначение улицы не может быть пустым")
	private String fullName;
}