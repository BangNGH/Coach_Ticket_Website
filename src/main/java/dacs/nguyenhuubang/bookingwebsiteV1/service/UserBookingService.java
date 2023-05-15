package dacs.nguyenhuubang.bookingwebsiteV1.service;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.BookingRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserBookingService {
    private final TripService tripService;
    private final SeatService seatService;


    public void findTripAndSeatById(Integer selectedTripId, List<Long> seatIds) {



    }
}
