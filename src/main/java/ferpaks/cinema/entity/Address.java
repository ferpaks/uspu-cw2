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
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"street_id", "house"})
})
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "street_id", nullable = false)
	private Street street;

	@Column(nullable = false)
	@NotBlank(message = "Номер дома не может быть пустым")
	private String house;

	@OneToOne(mappedBy = "address", cascade = CascadeType.REMOVE)
	private Cinema cinema;

	public String getFormattedAddress() {
		if (street == null || street.getType() == null || street.getName() == null) {
			throw new IllegalStateException("Некорректные данные улицы");
		}

		String streetName = street.getType().getShortName() + " " + street.getName();
		return getCity() + ", " + streetName + ", д." + house;
	}

	public City getCity() {
		if (street == null) {
			throw new IllegalStateException("Улица не задана");
		}

		return street.getCity();
	}
}