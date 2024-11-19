package ferpaks.cinema.controller;

import ferpaks.cinema.entity.*;
import ferpaks.cinema.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {
	private final CinemaUserService userService;
	private final BookingService bookingService;
	private final SessionService sessionService;
	private final MovieService movieService;
	private final HallService hallService;
	private final SeatService seatService;

	@GetMapping
	public String showList(Model model, Authentication authentication) {

		String username = authentication.getName();
		CinemaUser currentUser = userService.findByUsername(username).get();

		List<Session> sessions;

		if (currentUser.getRole() == CinemaUserRole.ADMIN) {
			sessions = sessionService.findAll();
		} else if (currentUser.getRole() == CinemaUserRole.MANAGER) {
			sessions = sessionService.findAllByManager(currentUser);
		} else {
			sessions = sessionService.findAll();
		}

		model.addAttribute("sessionsData", sessions);
		model.addAttribute("formatter", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

		return "session/list";
	}

	@GetMapping("/new")
	public String showAddForm(Model model, Authentication authentication) {
		List<Hall> halls = hallService.findAll();
		halls.sort(Comparator.comparing((Hall hall) -> hall.getCinema().getName())
				.thenComparing(Hall::getName));

		String currentUsername = authentication.getName();

		model.addAttribute("sessionData", new Session());
		model.addAttribute("movies", movieService.findAll());
		model.addAttribute("halls", halls);
		model.addAttribute("hallsByManager", hallService.findAllByManager(userService.findByUsername(currentUsername).get()));

		return "session/add";
	}

	@PostMapping
	public String addSession(@RequestParam("movieId") Long movieId, @RequestParam("hallId") Long hallId, @Valid @ModelAttribute("sessionData") Session session, BindingResult result, Model model) {
		Hall hall = hallService.findById(hallId).orElseThrow(() -> new IllegalArgumentException("Invalid hall ID."));
		Movie movie = movieService.findById(movieId).orElseThrow(() -> new IllegalArgumentException("Invalid movie ID."));

		if (movie == null || hall == null) {
			throw new IllegalArgumentException("Invalid Movie or Hall ID");
		}

		session.setHall(hall);
		session.setMovie(movie);

		if (sessionService.isSessionStartsInPast(session)) {
			result.rejectValue("sessionStartTime", "error.session", "Время начала сеанса не может быть в прошлом.");
		}

		List<Session> existingSessions = sessionService.findAllByHall(session.getHall());
		for (Session existingSession : existingSessions) {
			LocalDateTime existingStart = existingSession.getSessionStartTime();
			LocalDateTime existingEnd = existingStart.plusMinutes(existingSession.getMovie().getDuration().toMinutes());

			LocalDateTime newStart = session.getSessionStartTime();
			LocalDateTime newEnd = newStart.plusMinutes(session.getMovie().getDuration().toMinutes());

			if ((newStart.isBefore(existingEnd) && newStart.isAfter(existingStart)) ||
					(newEnd.isAfter(existingStart) && newEnd.isBefore(existingEnd)) ||
					(newStart.isEqual(existingStart) || newEnd.isEqual(existingEnd))
			) {
				model.addAttribute("sessionError", "В указанное время уже есть <a href=\"/cinema/sessionsData/detail/" + existingSession.getId() + "\">сеанс</a> в этом зале.");
				model.addAttribute("movies", movieService.findAll());
				model.addAttribute("halls", hallService.findAll());
				return "session/add";
			}
		}

		if (result.hasErrors()) {
			model.addAttribute("movies", movieService.findAll());
			model.addAttribute("halls", hallService.findAll());
			return "session/add";
		}
		sessionService.save(session);
		return "redirect:/sessions";
	}

	@GetMapping("/{id}/bookings/new")
	public String showAddBookingForm(@PathVariable Long id, Model model) {
		Session session = sessionService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid session ID: " + id));

		// Проверка, что объект movie не равен null
		if (session.getMovie() == null) {
			throw new IllegalStateException("Session with ID " + id + " does not have an associated movie.");
		}

		// Убедитесь, что у сеанса есть места. Если их нет, создайте их из зала.
		if (session.getSeats() == null || session.getSeats().isEmpty()) {
			sessionService.createSessionFromHall(session);
		}

		Map<Integer, List<Seat>> seatsGroupedByRow = session.getSeats().stream()
				.collect(Collectors.groupingBy(Seat::getRowNumber));

		model.addAttribute("sessionData", session);
		model.addAttribute("hall", session.getHall());
		model.addAttribute("cinema", session.getHall().getCinema());
		model.addAttribute("seatsGroupedByRow", seatsGroupedByRow);

		return "booking/add";
	}

	/**
	 * @param selectedSeats Строка, содержащая ID'ы выбранных мест, разделённые запятыми.
	 * @param model         Объект Model, используемый для передачи свойств в представление.
	 * @return Строка-перенаправление.
	 */
	@PostMapping("/{id}/bookings")
	public String addBooking(@PathVariable Long id, @RequestParam String selectedSeats, Model model, Authentication authentication) {
		Session session = sessionService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid session ID: " + id));

		String[] seatIds = selectedSeats.split(",");

		for (String seatIdString : seatIds) {
			try {
				Long seatId = Long.parseLong(seatIdString.trim());
				Seat seat = seatService.findById(seatId).orElseThrow(() -> new IllegalArgumentException("Invalid seat ID: " + seatId));

				if (seat.getSession() == null) {
					seat.setSession(session);
				}

				// Проверяем, что место связано с текущим сеансом
				if (seat.getSession().getId().equals(session.getId()) && seat.getStatus() == SeatStatus.FREE) {
					Booking booking = new Booking();
					LocalDateTime now = LocalDateTime.now();
					seat.setStatus(SeatStatus.RESERVED);

					booking.setSeat(seat);
					booking.setSession(session);
					booking.setPaymentStatus(PaymentStatus.UNPAID);
					booking.setBookingStatus(BookingStatus.RESERVED);
					booking.setReservationTime(now);
					booking.setBookingTime(now.plusMinutes(15));
					booking.setUser(userService.findByUsername(authentication.getName()).orElseThrow(() -> new EntityNotFoundException("User not found")));
					booking.setTotalPriceInRubles(session.getMovie().getBasePriceInRubles());

					// Сохраняем изменения
					seatService.save(seat);
					bookingService.save(booking);
				} else {
					System.out.println("Место с ID " + seatId + " не доступно для бронирования или не связано с этим сеансом.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Ошибка при разборе ID места: " + seatIdString);
			}
		}

		return "redirect:/bookings";
	}


	@GetMapping("/{id}")
	public String showDetail(@PathVariable Long id, Model model) {
		Session session = sessionService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid session ID: " + id));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		String formattedStartTime = session.getSessionStartTime().format(formatter);

		model.addAttribute("sessionData", session);
		model.addAttribute("formattedStartTime", formattedStartTime);

		return "session/detail";
	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
		String currentUsername = authentication.getName();
		CinemaUser currentUser = userService.findByUsername(currentUsername).get();

		List<Hall> halls = new ArrayList<>();
		if (currentUser.getRole() == CinemaUserRole.ADMIN) {
			halls = hallService.findAll();
		} else if (currentUser.getRole() == CinemaUserRole.MANAGER) {
			halls = hallService.findAllByManager(currentUser);
		} else {
			halls = Collections.emptyList();
		}

		Session session = sessionService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid session ID: " + id));
		model.addAttribute("sessionData", session);
		model.addAttribute("movies", movieService.findAll());
		model.addAttribute("halls", halls);
		model.addAttribute("formatter", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
		return "session/edit";
	}

	@PutMapping("/{id}")
	public String updateSession(@PathVariable Long id, @RequestParam("hallId") Long hallId, @Valid @ModelAttribute("sessionData") Session session, BindingResult result, Model model) {
		if (sessionService.isSessionStartsInPast(session)) {
			result.rejectValue("sessionStartTime", "error.session", "Время начала сеанса не может быть в прошлом.");
		}

		List<Session> existingSessions = sessionService.findAllByHall(session.getHall());
		for (Session existingSession : existingSessions) {
			LocalDateTime existingStart = existingSession.getSessionStartTime();
			LocalDateTime existingEnd = existingStart.plusMinutes(existingSession.getMovie().getDuration().toMinutes());

			LocalDateTime newStart = session.getSessionStartTime();
			LocalDateTime newEnd = newStart.plusMinutes(session.getMovie().getDuration().toMinutes());

			if ((newStart.isBefore(existingEnd) && newStart.isAfter(existingStart)) ||
					(newEnd.isAfter(existingStart) && newEnd.isBefore(existingEnd)) ||
					(newStart.isEqual(existingStart) || newEnd.isEqual(existingEnd))
			) {
				model.addAttribute("sessionError", "В указанное время уже есть <a href=\"/cinema/sessionsData/detail/" + existingSession.getId() + "\">сеанс</a> в этом зале.");
				model.addAttribute("movies", movieService.findAll());
				model.addAttribute("halls", hallService.findAll());
				return "session/edit";
			}


		}
		if (result.hasErrors()) {
			model.addAttribute("movies", movieService.findAll());
			model.addAttribute("halls", hallService.findAll());
			return "session/edit";
		}

		Hall hall = hallService.findById(hallId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid hall ID: " + hallId));
		session.setHall(hall);

		sessionService.update(id, session);
		return "redirect:/sessions";
	}

	@DeleteMapping("/{id}")
	public String deleteSession(@PathVariable Long id) {
		sessionService.deleteById(id);
		return "redirect:/sessions";
	}
}