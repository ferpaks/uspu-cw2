package ferpaks.cinema.repository;

import ferpaks.cinema.entity.City;
import ferpaks.cinema.entity.Street;
import ferpaks.cinema.entity.StreetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreetRepository extends JpaRepository<Street, Long> {
	boolean existsByCityAndNameAndType(City city, String name, StreetType type);
}