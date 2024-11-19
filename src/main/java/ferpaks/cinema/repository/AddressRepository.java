package ferpaks.cinema.repository;

import ferpaks.cinema.entity.Address;
import ferpaks.cinema.entity.Street;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
	boolean existsByStreetAndHouse(Street street, String house);

	Optional<Address> findByStreetAndHouse(Street street, String house);
}