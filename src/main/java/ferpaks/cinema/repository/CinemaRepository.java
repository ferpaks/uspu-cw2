package ferpaks.cinema.repository;

import ferpaks.cinema.entity.Address;
import ferpaks.cinema.entity.Cinema;
import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {
	boolean existsByLinkName(String linkName);

	Optional<Cinema> findByLinkName(String linkName);
	void deleteByLinkName(String linkName);

	List<Cinema> findByAddressStreetCity(City city);

	boolean existsByAddressId(Long addressId);

	List<Cinema> findAllByManager(CinemaUser manager);

	@Query("SELECT c.address FROM Cinema c WHERE c.address IS NOT NULL")
	List<Address> findAllOccupiedAddresses();
}