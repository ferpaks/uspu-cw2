package ferpaks.cinema.repository;

import ferpaks.cinema.entity.Booking;
import ferpaks.cinema.entity.BookingStatus;
import ferpaks.cinema.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findAllByUserUsername(String string);

	List<Booking> findByBookingStatusAndPaymentStatus(BookingStatus bookingStatus, PaymentStatus paymentStatus);


}