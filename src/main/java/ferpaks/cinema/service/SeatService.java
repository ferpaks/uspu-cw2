package ferpaks.cinema.service;

import ferpaks.cinema.entity.Cinema;
import ferpaks.cinema.entity.Seat;
import ferpaks.cinema.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeatService {
	private final SeatRepository seatRepository;

	public List<Seat> findAll() {
		return seatRepository.findAll();
	}

	public Optional<Seat> findById(Long id) {
		return seatRepository.findById(id);
	}

	public void save(Seat seat) {
		seatRepository.save(seat);
	}
}
