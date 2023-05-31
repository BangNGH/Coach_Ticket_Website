package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.ShuttleBus;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TransitionRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TransitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/transit")
public class TransitController {

    private final TransitionService transitionService;
    private final BookingService bookingService;
    private final TransitionRepository transitionRepository;

    @GetMapping("/transit-today/page/{pageNo}")
    public String findPaginatedTransitToday(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 6;
        Page<ShuttleBus> page = transitionService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<ShuttleBus> cities = page.getContent();
        List<ShuttleBus> transitToday = cities.stream().filter(i -> i.getBooking().getBookingDate().equals(LocalDate.now())).toList();
        if (transitToday.isEmpty()) {
            model.addAttribute("isListEmpty", true);
        } else model.addAttribute("isListEmpty", false);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("cities", transitToday);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/pages/show_transit_today";
    }

    @GetMapping("/transit-today")
    public String getTransitToday(Model model) {
        return findPaginatedTransitToday(1, model, "id", "asc");
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 6;
        Page<ShuttleBus> page = transitionService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<ShuttleBus> cities = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("cities", cities);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/pages/admin_crud_transit";
    }

    @GetMapping("")
    public String getCities(Model model) {
        return findPaginated(1, model, "id", "asc");
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<Booking> bookings = bookingService.getBookings();
        model.addAttribute("pageTitle", "Tạo Chuyến Mới");
        model.addAttribute("city", new ShuttleBus());
        model.addAttribute("bookings", bookings);

        return "admin/pages/transit_form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("city") ShuttleBus city, BindingResult bindingResult, RedirectAttributes re, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                List<Booking> bookings = bookingService.getBookings();
                model.addAttribute("bookings", bookings);
                return "admin/pages/transit_form";
            }
            transitionService.save(city);
            re.addFlashAttribute("raMessage", "Tạo chuyến trung chuyển thành công");
            return "redirect:/admin/transit";
        } catch (Exception e) {
            re.addFlashAttribute("raMessage", e.getMessage());
            return "redirect:/admin/transit";
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            ShuttleBus city = transitionService.get(id);
            model.addAttribute("city", city);
            model.addAttribute("pageTitle", "Sửa chuyến (ID: " + id + ")");
            List<Booking> bookings = bookingService.getBookings();
            model.addAttribute("bookings", bookings);
            return "admin/pages/transit_form";
        } catch (ResourceNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/transit";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            ShuttleBus transition = transitionRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyến trung chuyển với ID: " + id));

            transitionRepository.delete(transition);
            ra.addFlashAttribute("raMessage", "Chuyến trung chuyển (ID: " + id + ") đã bị xóa");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/transit";
    }

}
