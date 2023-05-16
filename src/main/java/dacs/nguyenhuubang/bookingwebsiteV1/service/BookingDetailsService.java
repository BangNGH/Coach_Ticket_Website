package dacs.nguyenhuubang.bookingwebsiteV1.service;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetailsId;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.BookingDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingDetailsService {
    private final BookingDetailsRepository bookingDetailsRepo;
    private final BookingService bookingService;

    public List<BookingDetails> getBookings() {
        return bookingDetailsRepo.findAll();
    }

    public BookingDetails save(BookingDetails bookingDetails, String existsTicketCode) {

        if (existsTicketCode.isBlank()) {
            Booking foundBooking = bookingService.get(bookingDetails.getId().getBookingId());
            bookingDetails.setBooking(foundBooking);
            Float totalPrice = bookingDetails.getNumberOfTickets() * foundBooking.getTrip().getPrice();
            bookingDetails.setTotalPrice(totalPrice);
            String ticketCode = UUID.randomUUID().toString().substring(0, 8);
            bookingDetails.getId().setTicketCode(ticketCode);
            bookingDetailsRepo.save(bookingDetails);
        }
        else {
            Booking foundBooking = bookingService.get(bookingDetails.getId().getBookingId());
            BookingDetails exists = bookingDetailsRepo.findByIdTicketCode(existsTicketCode);
            exists.setBooking(foundBooking);
            exists.setNumberOfTickets(bookingDetails.getNumberOfTickets());
            Float totalPrice = bookingDetails.getNumberOfTickets() * foundBooking.getTrip().getPrice();
            exists.setTotalPrice(totalPrice);
            BookingDetailsId id = new BookingDetailsId();
            id.setBookingId(foundBooking.getId());
            id.setTicketCode(existsTicketCode);
            bookingDetailsRepo.save(exists);
        }
        return bookingDetails;
    }

    public BookingDetails get(BookingDetailsId bookingDetailsId) {
        Optional<BookingDetails> result = bookingDetailsRepo.findById(bookingDetailsId);
        return result.orElseThrow(() -> new ResourceNotFoundException("Not found booking-details with ID: " + bookingDetailsId + "!"));
    }

    public void delete(BookingDetailsId bookingDetailsId) {
        if (!bookingDetailsRepo.existsById(bookingDetailsId)) {
            throw new ResourceNotFoundException("Could not find any booking-details with ID " + bookingDetailsId);
        }
        bookingDetailsRepo.deleteById(bookingDetailsId);
    }

    public List<BookingDetails> search(String keyword) {
        if (keyword != null) {
            return bookingDetailsRepo.search(keyword);
        }
        return bookingDetailsRepo.findAll();
    }

    public Page<BookingDetails> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return bookingDetailsRepo.findAll(pageable);
    }
}
