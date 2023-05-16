package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingDetailsRepository extends JpaRepository<BookingDetails, BookingDetailsId> {
    @Query("SELECT p FROM BookingDetails p WHERE CONCAT(p.booking.booking_date, ' ', p.totalPrice,' ', p.booking.id) LIKE %?1%")
    List<BookingDetails> search(String keyword);

    BookingDetails findByIdTicketCode(String ticketCode);
}
