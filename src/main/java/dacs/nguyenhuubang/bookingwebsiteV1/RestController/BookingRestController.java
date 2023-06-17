package dacs.nguyenhuubang.bookingwebsiteV1.RestController;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TripRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingRestController {
    private final BookingService bookingService;
    private final TripRepository tripRepository;
    private final UserService userService;

    @GetMapping("/search")
    @ResponseBody
    public List<Booking> searchBookings(@RequestParam("q") String q) {
        List<Booking> bookings = bookingService.search(q);
        return bookings;
    }

    @GetMapping("/search/bill")
    @ResponseBody
    public List<Booking> searchBills(@RequestParam("q") String q) {
        List<Booking> bookings = bookingService.search(q).stream().filter(i -> i.getIsPaid().equals(false)).toList();
        return bookings;
    }

    @GetMapping("/search/bookings-today")
    @ResponseBody
    public List<Booking> searchBookingsToday(@RequestParam("q") String q) {
        List<Booking> bookings = bookingService.search(q).stream().filter(i -> i.getBookingDate().equals(LocalDate.now())).toList();
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

    @GetMapping("/search-bill")
    public ModelAndView searchBookingBills(@RequestParam("keyword") String keyword, Principal principal) {
        String email = principal.getName();
        UserEntity user = userService.findbyEmail(email).get();
        List<Booking> searchList = bookingService.getBill(user, false);
        List<Booking> bookedTrip = bookingService.searchBookings(searchList, keyword);
        ModelAndView modelAndView = new ModelAndView("fragments/search_bill");
        if (bookedTrip.isEmpty()) {
            modelAndView.addObject("notFound", true);
        } else modelAndView.addObject("notFound", false);

        modelAndView.addObject("bookings", bookedTrip);
        modelAndView.addObject("header", "Danh sách vé xe đã đặt");
        modelAndView.addObject("currentPage", "Vé của tôi");
        return modelAndView;
    }

    @GetMapping("/search-receipts")
    public ModelAndView searchBookingReceipts(@RequestParam("keyword") String keyword, Model model, Principal principal) {
        String email = principal.getName();
        UserEntity user = userService.findbyEmail(email).get();
        List<Booking> searchList = bookingService.getBill(user, true);
        List<Booking> bookedTrip = bookingService.searchBookings(searchList, keyword);
        ModelAndView modelAndView = new ModelAndView("fragments/search_receipts");
        if (bookedTrip.isEmpty()) {
            modelAndView.addObject("notFound", true);
        } else modelAndView.addObject("notFound", false);

        modelAndView.addObject("bookings", bookedTrip);
        modelAndView.addObject("header", "Danh sách vé xe đã đặt");
        modelAndView.addObject("currentPage", "Vé của tôi");
        return modelAndView;
    }

}
