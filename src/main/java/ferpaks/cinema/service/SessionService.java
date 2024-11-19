package ferpaks.cinema.service;

import ferpaks.cinema.entity.*;
import ferpaks.cinema.repository.CinemaRepository;
import ferpaks.cinema.repository.SeatRepository;
import ferpaks.cinema.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {
	private final SessionRepository sessionRepository;
	private final CinemaRepository cinemaRepository;
	private final SeatRepository seatRepository;

	public List<Session> findAll() {
		return sessionRepository.findAll();
	}

	public Optional<Session> findById(Long id) {
		return sessionRepository.findById(id);
	}

	public List<Session> findAllByHall(Hall hall) {
		return sessionRepository.findAllByHall(hall);
	}

	public Session save(Session session) {
		return sessionRepository.save(session);
	}

	@Transactional
	public void deleteById(Long id) {
		sessionRepository.deleteById(id);
	}

	public void update(Long id, Session session) {
		Session existingSession = sessionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Session not found, " + id));

		existingSession.setHall(session.getHall());
		existingSession.setMovie(session.getMovie());
		existingSession.setSessionStartTime(session.getSessionStartTime());

		sessionRepository.save(existingSession);
	}

	@Transactional
	public void saveWithSeats(Session session, Long cinemaId, int rows, int columns) {
		Cinema cinema = cinemaRepository.findById(cinemaId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid cinema ID: " + cinemaId));
		Session savedSession = sessionRepository.save(session);

		List<Seat> seats = new ArrayList<>();
		for (int row = 1; row <= rows; row++) {
			for (int col = 1; col <= columns; col++) {
				Seat seat = new Seat();
				seat.setRowNumber(row);
				seat.setSeatNumber(col);
				seat.setSession(savedSession);
				seats.add(seat);
			}
		}
		seatRepository.saveAll(seats);
	}

	public boolean isSessionStartsInPast(Session session) {
		return session.getSessionStartTime().isBefore(LocalDateTime.now());
	}

	public void createSessionFromHall(Session session) {
		Hall hall = session.getHall();
		List<Seat> hallSeats = hall.getSeats();

		List<Seat> sessionSeats = hallSeats.stream().map(
				hallSeat -> {
					Seat sessionSeat = new Seat();
					sessionSeat.setSession(session);
					sessionSeat.setHall(hall);
					sessionSeat.setRowNumber(hallSeat.getRowNumber());
					sessionSeat.setSeatNumber(hallSeat.getSeatNumber());
					sessionSeat.setStatus(SeatStatus.FREE);

					return sessionSeat;
				}
		).toList();

		session.getSeats().clear();
		session.getSeats().addAll(sessionSeats);

		save(session);
	}

	public List<Session> findAllByManager(CinemaUser manager) {
		return sessionRepository.findByHall_Cinema_Manager(manager);
	}

}