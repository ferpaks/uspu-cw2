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
@Table(
		uniqueConstraints = @UniqueConstraint(
				columnNames = {"city_id", "name", "street_type_id"}
		)
)
public class Street {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(nullable = false)
	@NotBlank(message = "Название улицы не может быть пустым")
	private String name;

	@ManyToOne(optional = false)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	@ManyToOne(optional = false)
	@JoinColumn(name = "street_type_id", nullable = false)
	private StreetType type;

	@OneToMany(mappedBy = "street", cascade = CascadeType.REMOVE)
	private List<Address> addresses;
}