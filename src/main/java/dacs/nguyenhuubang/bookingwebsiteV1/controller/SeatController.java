package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import java.util.List;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.SeatService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.VehiclesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/seats")
public class SeatController {
    private final SeatService seatService;
    private final VehiclesService vehiclesService;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo" )int pageNo, Model model){
        int pageSize = 5;
        Page<Seat> page = seatService.findPaginated(pageNo, pageSize);
        List<Seat> seats = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("seats", seats);
        return "admin/pages/admin_crud_seats";
    }

    @GetMapping("")
    public String getSeats(Model model){
        return findPaginated(1, model);
    }

    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("pageTitle", "Create New");
        model.addAttribute("seat", new Seat());
        List<Vehicle> vehicles = vehiclesService.getList();
        if (vehicles.isEmpty()){
            model.addAttribute("message", "Phương tiện có khóa ngoại liên quan đã bị xóa");
            return "error_message";
        }
        model.addAttribute("vehicles", vehicles);

        return "admin/pages/seat_form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("seat") Seat seat, BindingResult bindingResult, RedirectAttributes re){
        if (bindingResult.hasErrors()) {
            return "admin/pages/seat_form";
        }
            seatService.save(seat);
        re.addFlashAttribute("raMessage", "The vehicle has been saved successfully.");
        return "redirect:/admin/seats";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra){
        try{
            Seat seat = seatService.get(id);
            model.addAttribute("seat", seat);
            model.addAttribute("pageTitle", "Edit seat (ID: "+id+")");
            List<Vehicle> vehicles = vehiclesService.getList();
            model.addAttribute("vehicles", vehicles);
            return "admin/pages/seat_form";
        }catch (ResourceNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/seats";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model, RedirectAttributes ra){
        try{
            seatService.delete(id);
            ra.addFlashAttribute("raMessage", "The seat (ID: "+id+") has been deleted");
        }catch (ResourceNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }catch (CannotDeleteException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/seats";
    }

    @RequestMapping("/search")
    public String search(Model model, @Param("keyword") String keyword) {
        List<Seat> seats = seatService.search(keyword);
        model.addAttribute("seats", seats);
        model.addAttribute("keyword", keyword);
        return "admin/pages/admin_crud_seats";
    }


}
