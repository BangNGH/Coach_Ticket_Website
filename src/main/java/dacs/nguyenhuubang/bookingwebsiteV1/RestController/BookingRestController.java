package dacs.nguyenhuubang.bookingwebsiteV1.RestController;
import dacs.nguyenhuubang.bookingwebsiteV1.dto.TicketsSearchResultDTO;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TripRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingDetailsService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingRestController {
    private final BookingService bookingService;
    private final TripRepository tripRepository;
    @GetMapping("/search")
    @ResponseBody
    public List<Booking> searchUsers(@RequestParam("q") String q) {
        List<Booking> bookings = bookingService.search(q);
        return bookings;
    }

    @GetMapping("/load-trip")
    public ResponseEntity<Trip> loadTripById(@RequestParam int tripId) {
        try {
            Trip trip = tripRepository.findById(tripId).get();
            return new ResponseEntity<>(trip, HttpStatus.OK);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
