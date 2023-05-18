package dacs.nguyenhuubang.bookingwebsiteV1.controller;
import java.util.List;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@RequestMapping("/admin/bookings")
@Controller
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final TripService tripService;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo" )int pageNo, Model model){
        int pageSize = 5;
        Page<Booking> page = bookingService.findPaginated(pageNo, pageSize);
        List<Booking> bookings = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("bookings", bookings);
        return "admin/pages/admin_crud_bookings";
    }

    @GetMapping("")
    public String getBookings(Model model){
/*        List<Booking> bookings = bookingService.getBookings();
        model.addAttribute("bookings", bookings);*/

        return findPaginated(1, model);
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
        try{
            bookingService.delete(id);
            ra.addFlashAttribute("raMessage", "The booking (ID: "+id+") has been deleted");
        }catch (VehicleNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }catch (CannotDeleteException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

}
