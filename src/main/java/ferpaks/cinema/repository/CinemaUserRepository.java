package ferpaks.cinema.repository;

import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.CinemaUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CinemaUserRepository extends JpaRepository<CinemaUser, Long> {
	Optional<CinemaUser> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByRole(CinemaUserRole role);

	List<CinemaUser> findAllByRole(CinemaUserRole role);
}