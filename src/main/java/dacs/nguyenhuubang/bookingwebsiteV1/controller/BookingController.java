package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin/bookings")
@Controller
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final TripService tripService;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 6;
        Page<Booking> page = bookingService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Booking> bookings = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("bookings", bookings);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "admin/pages/admin_crud_bookings";
    }

    @GetMapping("")
    public String getBookings(Model model){
        return findPaginated(1, model, "id", "asc");
    }


    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("pageTitle", "Create New");
        Booking booking = new Booking();
        model.addAttribute("booking", booking);
        return getList(model);
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("booking") Booking booking, BindingResult bindingResult, RedirectAttributes re){
        if (bindingResult.hasErrors()) {
            return "admin/pages/booking_form";
        }
        bookingService.save(booking);
        re.addFlashAttribute("raMessage", "The booking has been saved successfully.");
        return "redirect:/admin/bookings";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try{
            Booking booking = bookingService.get(id);
            model.addAttribute("booking", booking);
            model.addAttribute("pageTitle", "Edit booking (ID: "+id+")");
            return getList(model);
        }catch (VehicleNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/bookings";
        }
    }

    private String getList(Model model) {
        List<Trip> trips = tripService.getTrips();
        List<UserEntity> users = userService.getUsers();
        if (users.isEmpty() || trips.isEmpty()){
            model.addAttribute("message", "Danh sách rỗng hoặc các khóa ngoại đã bị xóa");
            return "error_message";
        }
        model.addAttribute("trips", trips);
        model.addAttribute("users", users);
        return "admin/pages/booking_form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try {
            bookingService.delete(id);
            ra.addFlashAttribute("raMessage", "The booking (ID: " + id + ") has been deleted");
        } catch (VehicleNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CannotDeleteException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @GetMapping("/cancel-unpaid-tickets-over120")
    @Transactional
    public String cancelBill(Model model, RedirectAttributes re) {
        /*        try{*/
        bookingService.cancelAllUnpaidTickets(LocalDate.now(), LocalTime.now());
        re.addFlashAttribute("successMessage", "Hủy thành công các vé chưa thanh toán quá 120 phút");
        return "redirect:/admin/bill";
       /* }catch (Exception e){
            re.addFlashAttribute("errorMessage",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new Exception(e.getMessage());
           // return "redirect:/admin/bill";
        }*/

    }
}
