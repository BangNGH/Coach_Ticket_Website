package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.UserNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.security.UserService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.SeatService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.UserRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UsersBookingController {

    private final UserBookingService userBookingService;
    private final TripService tripService;
    private final SeatService seatService;

    @PostMapping("/book")
    public String bookTrip(Model model, @RequestParam("startTime") LocalDate startTime, @RequestParam("selectedTripId") Integer selectedTripId,
                           @RequestParam("inputSelectedSeats") String inputSelectedSeats, RedirectAttributes re) {
        List<Long> seatIds = new ArrayList<>();
        String[] seatIdArray = inputSelectedSeats.split(",");
        for (String seatId : seatIdArray) {
            seatIds.add(Long.valueOf(seatId));
        }
        if (seatIds.isEmpty()) {
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            return "redirect:/home";
        }

        try {
            LocalTime now = LocalTime.now();
            Trip trip = tripService.get(selectedTripId);
            if (trip.getStartTime().compareTo(now) <= 0) {
                re.addFlashAttribute("errorMessage", "Chuyến này đã xuất phát rồi!");
                return "redirect:/home";
            }
            List<Seat> seatsReserved = new ArrayList<>();
            for (Long seatId : seatIds) {
                Seat seat = seatService.get(seatId);
                seatsReserved.add(seat);
            }
            if (!seatsReserved.isEmpty()) {
                model.addAttribute("trip", trip);
                model.addAttribute("seatsReserved", seatsReserved);
                model.addAttribute("startTime", startTime);
                model.addAttribute("header", "Xác nhận chuyến đi");
                model.addAttribute("currentPage", "xác nhận");
                return "pages/confirm_booking";
            }else {
                re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
                return "redirect:/home";
            }
        }catch (ResourceNotFoundException e){
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            return "redirect:/home";
        }
    }

    @PostMapping("/save")
    public String saveBooking(Model model,@ModelAttribute("trip")Trip trip, @RequestParam("date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date, RedirectAttributes re){


        return "page/confirm_payment";
    }

}
