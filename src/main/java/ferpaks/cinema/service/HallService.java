package ferpaks.cinema.service;

import ferpaks.cinema.entity.Cinema;
import ferpaks.cinema.entity.CinemaUser;
import ferpaks.cinema.entity.Hall;
import ferpaks.cinema.entity.Seat;
import ferpaks.cinema.repository.CinemaRepository;
import ferpaks.cinema.repository.HallRepository;
import ferpaks.cinema.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HallService {
	private final HallRepository hallRepository;
	private final SeatRepository seatRepository;
	private final CinemaRepository cinemaRepository;

	public List<Hall> findAll() {
		return hallRepository.findAll();
	}

	public List<Hall> findAllByManager(CinemaUser manager) {
		return hallRepository.findAllByCinema_Manager( manager);
	}

	public Optional<Hall> findById(Long id) {
		return hallRepository.findById(id);
	}

	public Hall save(Hall hall) {
		return hallRepository.save(hall);
	}

	@Transactional
	public void saveWithSeats(Hall hall, Long cinemaId, int rows, int columns) {
		Cinema cinema = cinemaRepository.findById(cinemaId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid cinema ID: " + cinemaId));
		hall.setCinema(cinema);
		Hall savedHall = hallRepository.save(hall);

		List<Seat> seats = new ArrayList<>();
		for (int row = 1; row <= rows; row++) {
			for (int col = 1; col <= columns; col++) {
				Seat seat = new Seat();
				seat.setRowNumber(row);
				seat.setSeatNumber(col);
				seat.setHall(savedHall);
				seats.add(seat);
			}
		}
		seatRepository.saveAll(seats);
	}

	public void updateHall(Long id, Hall updatedHall) {
		Hall existingHall = hallRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Hall not found"));

		existingHall.setName(updatedHall.getName());

		hallRepository.save(existingHall);
	}

	@Transactional
	public void updateHallWithSeats(Long hallId, String name, Long cinemaId, int rows, int columns) {
		Hall hall = hallRepository.findById(hallId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid hall ID"));

		hall.setName(name);

		Cinema cinema = cinemaRepository.findById(cinemaId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid cinema ID"));
		hall.setCinema(cinema);

		// Удаляем старые сидения
		if (hall.getSeats() != null && !hall.getSeats().isEmpty()) {
			seatRepository.deleteAll(hall.getSeats());
			hall.getSeats().clear();
		}

		// Создаем новые сидения
		List<Seat> newSeats = new ArrayList<>();
		for (int i = 1; i <= rows; i++) {
			for (int j = 1; j <= columns; j++) {
				Seat seat = new Seat();
				seat.setRowNumber(i);
				seat.setSeatNumber(j);
				seat.setHall(hall);
				newSeats.add(seat);
			}
		}

		// Добавляем новые сидения в сущность и сохраняем их
		hall.getSeats().addAll(newSeats);
		seatRepository.saveAll(newSeats);

		hallRepository.save(hall);
	}

	public boolean existsById(Long id) {
		return hallRepository.existsById(id);
	}

	public void deleteById(Long id) {
		hallRepository.deleteById(id);
	}
}