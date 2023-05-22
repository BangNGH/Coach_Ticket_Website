package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.SeatHasBeenReseredException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatReservationRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TripRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.SeatReservationService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/seat-reservation")
public class SeatReservationController {
    private final BookingService bookingService;
    private final SeatService seatService;
    private final SeatReservationService seatReservationService;



    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 6;
        Page<SeatReservation> page = seatReservationService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<SeatReservation> seatReservations = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("seatReservations", seatReservations);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "admin/pages/admin_crud_seat_reservations";
    }

    @GetMapping("")
    public String getSeatReservation(Model model){
        return findPaginated(1, model, "id", "asc");
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "Create New");
        model.addAttribute("seatReservation", new dacs.nguyenhuubang.bookingwebsiteV1.entity.SeatReservation());
        List<Seat> seats = seatService.getList();
        List<Booking> tickets = bookingService.getBookings();
        if (tickets.isEmpty() || seats.isEmpty()) {
            model.addAttribute("message", "Các khóa ngoại liên quan đã bị xóa");
            return "error_message";
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("seats", seats);
        return "admin/pages/seat_reservation_form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "id", required = false) Integer id,@ModelAttribute("seatReservation") SeatReservation seatReservation, BindingResult bindingResult, RedirectAttributes re) {
        if (bindingResult.hasErrors()) {
            return "admin/pages/seat_reservation_form";
        }
        try{

            seatReservationService.save(seatReservation, id);
            re.addFlashAttribute("raMessage", "The seat-reservation has been saved successfully.");
        }catch (SeatHasBeenReseredException e){
            re.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/seat-reservation";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            SeatReservation seatReservation = seatReservationService.get(id);
            model.addAttribute("seatReservation", seatReservation);
            model.addAttribute("pageTitle", "Edit seat reservation (ID: " + id + ")");
            List<Seat> seats = seatService.getList();
            List<Booking> tickets = bookingService.getBookings();
            model.addAttribute("tickets", tickets);
            model.addAttribute("seats", seats);
            return "admin/pages/seat_reservation_form";
        } catch (ResourceNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/seat-reservation";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
/*            SeatReservation delSeatReservation = seatReservationService.get(id);
            Trip trip =delSeatReservation.getBooking().getTrip();
            LocalDate booking_date =delSeatReservation.getBooking().getBooking_date();
           updateAvailableSeats(trip.getId(), booking_date);*/
            seatReservationService.delete(id);
            ra.addFlashAttribute("raMessage", "The seat reservation (ID: " + id + ") has been deleted");
        } catch (ResourceNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/seat-reservation";
    }

/*    public void updateAvailableSeats(int tripId, LocalDate booking_date){
        seatReservationRepo.updateSeatReservation(tripId,booking_date);
    }*/

}
