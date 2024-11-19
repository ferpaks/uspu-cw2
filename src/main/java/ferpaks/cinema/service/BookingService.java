package ferpaks.cinema.service;

import ferpaks.cinema.entity.*;
import ferpaks.cinema.repository.BookingRepository;
import ferpaks.cinema.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
	private final BookingRepository bookingRepository;
	private final SeatRepository seatRepository;

	public List<Booking> findAll() {
		return bookingRepository.findAll();
	}

	public List<Booking> findAllByUsername(String username) {
		return bookingRepository.findAllByUserUsername(username);
	}

	public Optional<Booking> findById(Long id) {
		return bookingRepository.findById(id);
	}

	public Booking save(Booking booking) {
		return bookingRepository.save(booking);
	}

	public void deleteById(Long id) {
		bookingRepository.deleteById(id);
	}

	// Каждые 10 секунд будет запускаться метод
	@Scheduled(fixedRate = 10_000)
	public void checkAndCancelExpiredBookings() {
		LocalDateTime now = LocalDateTime.now();

		List<Booking> expiredBookings = bookingRepository.findByBookingStatusAndPaymentStatus(BookingStatus.RESERVED, PaymentStatus.UNPAID);

		for (Booking booking : expiredBookings) {
			if (now.isAfter(booking.getBookingTime())) {
				booking.setBookingStatus(BookingStatus.EXPIRED);
				Seat seat = booking.getSeat();
				seat.setStatus(SeatStatus.FREE);
				seatRepository.save(seat);
				bookingRepository.save(booking);
			}
		}
	}
}