package ferpaks.cinema.controller;

import ferpaks.cinema.entity.*;
import ferpaks.cinema.service.BookingService;
import ferpaks.cinema.service.CinemaUserService;
import ferpaks.cinema.service.SeatService;
import ferpaks.cinema.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
	private final BookingService bookingService;
	private final CinemaUserService userService;
	private final SeatService seatService;
	private final SessionService sessionService;

	@GetMapping
	public String showBookingList(Model model, Authentication authentication) {
		if (userService.isAdmin(authentication)) {
			model.addAttribute("adminBookings", bookingService.findAll());
		} else {
			model.addAttribute("adminBookings", Collections.emptyList());
		}

		model.addAttribute("formatter", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
		model.addAttribute("userBookings", bookingService.findAll());
		return "booking/list";
	}

	@GetMapping("/new")
	public String showCreateForm(Model model, Authentication authentication) {

		String currentUsername = authentication.getName();
		CinemaUser currentUser = userService.findByUsername(currentUsername)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<Seat> seats = seatService.findAll();
		Map<Integer, List<Seat>> seatsGroupedByRow = seats.stream()
				.collect(Collectors.groupingBy(Seat::getRowNumber));

		model.addAttribute("currentUser", currentUser);
		model.addAttribute("seatsGroupedByRow", seatsGroupedByRow);
		model.addAttribute("sessionsData", sessionService.findAll());
		return "booking/add";
	}

	@PostMapping
	public String addBooking(@Valid @ModelAttribute("booking") Booking booking, BindingResult result, Model model, Authentication authentication) {
		if (result.hasErrors()) {
			model.addAttribute("bookingStatuses", BookingStatus.values());
			model.addAttribute("paymentStatuses", PaymentStatus.values());
			return "booking/add";
		}

		booking.setBookingStatus(BookingStatus.RESERVED);
		booking.setPaymentStatus(PaymentStatus.UNPAID);
		booking.setTotalPriceInRubles(booking.getSession().getMovie().getBasePriceInRubles());
		LocalDateTime now = LocalDateTime.now();
		booking.setReservationTime(now);
		booking.setBookingTime(now.plusMinutes(15));
		booking.setUser(userService.findByUsername(authentication.getName()).get());

		bookingService.save(booking);
		return "redirect:/bookings";
	}

	/*
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable("id") Long id, Model model) {
		Optional<Booking> booking = bookingService.findById(id);
		if (booking.isPresent()) {
			model.addAttribute("booking", booking.get());
			model.addAttribute("bookingStatuses", BookingStatus.values());
			model.addAttribute("paymentStatuses", PaymentStatus.values());
			return "booking/edit";
		}
		return "redirect:/bookings";
	}

	@PutMapping("/{id}")
	public String updateBooking(@PathVariable("id") Long id, @Valid @ModelAttribute("booking") Booking booking,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("bookingStatuses", BookingStatus.values());
			model.addAttribute("paymentStatuses", PaymentStatus.values());
			return "booking/edit";
		}
		bookingService.save(booking);
		return "redirect:/bookings";
	}
	 */

	@DeleteMapping("/{id}")
	public String deleteBooking(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		bookingService.deleteById(id);
		redirectAttributes.addFlashAttribute("successMessage", "Бронирование успешно удалено!");
		return "redirect:/bookings";
	}

	@GetMapping("/{id}")
	public String showDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Booking> booking = bookingService.findById(id);

		model.addAttribute("formatter", DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

		if (booking.isPresent()) {
			model.addAttribute("booking", booking.get());
			return "booking/detail";
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Такого бронирования нет.");
			return "redirect:/bookings";
		}
	}

	@PostMapping("/{id}/cancel")
	public String cancel(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
		Booking booking = bookingService.findById(id).get();

		if (authentication.getName().equals(booking.getUser().getUsername())) {
			booking.setBookingStatus(BookingStatus.CANCELED);

			if (booking.getPaymentStatus() == PaymentStatus.PAID) {
				BigDecimal newBalance = booking.getUser().getBalanceInRubles().add(booking.getTotalPriceInRubles());
				booking.getUser().setBalanceInRubles(newBalance);
				userService.save(booking.getUser());
			}

			booking.getSeat().setStatus(SeatStatus.FREE);
			seatService.save(booking.getSeat());
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Возможно, вы пытаетесь отменить не свой билет!");
		}
		return "redirect:/bookings";
	}

	@PostMapping("/{id}/pay")
	public String pay(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
		Booking booking = bookingService.findById(id).get();

		if (authentication.getName().equals(booking.getUser().getUsername())) {
			if (booking.getBookingStatus() == BookingStatus.RESERVED) {
				if (booking.getUser().getBalanceInRubles().compareTo(booking.getTotalPriceInRubles()) >= 0) {
					booking.getUser().setBalanceInRubles(booking.getUser().getBalanceInRubles().subtract(booking.getTotalPriceInRubles()));
					redirectAttributes.addFlashAttribute("successDescription", "Бронирование оплачено успешно!");

					Seat seat = booking.getSeat();
					seat.setStatus(SeatStatus.BOOKED);
					seatService.save(seat);

					booking.setPaymentStatus(PaymentStatus.PAID);
					booking.setBookingStatus(BookingStatus.CONFIRMED);

					bookingService.save(booking);
				} else {
					redirectAttributes.addFlashAttribute("errorDescription", "У вас не хватает денег на балансе!");
				}
			} else {
				redirectAttributes.addFlashAttribute("errorDescription", "Данное бронирование уже невозможно оплатить.");
			}
		} else {
			redirectAttributes.addFlashAttribute("errorDescription", "Возможно, вы пытаетесь купить не свой билет!");
		}
		return "redirect:/bookings";
	}
}