package dacs.nguyenhuubang.bookingwebsiteV1.RestController;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.BookingDetailsRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/booking-details")
@RequiredArgsConstructor
public class BookingDetailsRestController {
    private final BookingDetailsRepository bookingDetailsRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/search")
    @ResponseBody
    public List<BookingDetails> searchUsers(@RequestParam("q") String q) {
        List<BookingDetails> bookings = bookingDetailsRepository.search(q);
        return bookings;
    }

    @GetMapping("/load-booking")
    public ResponseEntity<Booking> loadTripById(@RequestParam int tripId) {
        try {
            Booking booking = bookingRepository.findById(tripId).get();
            return new ResponseEntity<>(booking, HttpStatus.OK);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
