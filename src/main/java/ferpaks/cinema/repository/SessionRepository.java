package ferpaks.cinema.repository;

import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.Hall;
import ferpaks.cinema.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
	List<Session> findAllByHall(Hall hall);

	List<Session> findByHall_Cinema_Manager(CinemaUser manager);
}