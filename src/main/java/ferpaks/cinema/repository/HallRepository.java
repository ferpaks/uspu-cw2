package ferpaks.cinema.repository;

import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HallRepository extends JpaRepository<Hall, Long> {
	public List<Hall> findAllByCinema_Manager(CinemaUser manager);
}