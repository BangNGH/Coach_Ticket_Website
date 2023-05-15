package dacs.nguyenhuubang.bookingwebsiteV1.RestController;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.SeatReservation;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.BookingRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.SeatReservationService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-reservation")
@RequiredArgsConstructor
public class SeatReservationRestController {

    private final SeatReservationService seatReservationService;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;


    @GetMapping("/search")
    @ResponseBody
    public List<SeatReservation> searchUsers(@RequestParam("q") String q) {
        List<SeatReservation> seatReservations = seatReservationService.search(q);
        return seatReservations;
    }
    @GetMapping("/load-seats")
    public ResponseEntity<List<Seat>> loadSeatsByBookingId(@RequestParam int bookingId) {
        try {
            // Truy vấn cơ sở dữ liệu để lấy danh sách "seat" theo "bookingId"
            Booking booking = bookingRepository.findById(bookingId).get();
            int vehicleID = booking.getTrip().getVehicle().getId();
            List<Seat> seats = seatRepository.findByVehicleId(vehicleID);

            // Trả về danh sách "seat" dưới dạng JSON
            return new ResponseEntity<>(seats, HttpStatus.OK);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
