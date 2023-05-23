package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingDetailsRepository extends JpaRepository<BookingDetails, BookingDetailsId> {
    @Query("SELECT p FROM BookingDetails p WHERE CONCAT(p.booking.bookingDate, ' ', p.totalPrice,' ', p.booking.id,' ',p.totalPrice,' ', p.id.ticketCode) LIKE %?1%")
    List<BookingDetails> search(String keyword);

    BookingDetails findByIdTicketCode(String ticketCode);

    @Query("SELECT p.id.ticketCode FROM BookingDetails p WHERE p.booking = ?1 ")
    List<String> getTicketCode(Booking booking);

    @Query("SELECT p FROM BookingDetails p WHERE p.booking.id = ?1 ")
    BookingDetails getBookedTripDetailsByBooking(int id);
}
