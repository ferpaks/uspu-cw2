package ferpaks.cinema.repository;

import ferpaks.cinema.entity.Seat;
import ferpaks.cinema.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	List<Seat> findByStatusAndReservedAtBefore(SeatStatus status, LocalDateTime time);
}