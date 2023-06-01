package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.ShuttleBus;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TransitionService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = {"/users/tickets"})
@Controller
public class TicketsController {

    private final UserService userService;
    private final BookingService bookingService;
    private final TransitionService transitionService;


    @GetMapping("/transit-form/{id}")
    public String showTransitForm(@PathVariable("id") Integer id, Model model) {
        try {
            Booking booking = bookingService.get(id);
            ShuttleBus shuttlebus = new ShuttleBus();
            shuttlebus.setBooking(booking);
            model.addAttribute("booking", booking);
            model.addAttribute("shuttlebus", shuttlebus);
            return "pages/transit-form";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("message", "Không tìm thấy mã đặt vé của bạn");
            return "pages/error_message";
        }
    }

    @PostMapping("/transit-form")
    public String saveShippingInfo(@Valid @ModelAttribute("shuttlebus") ShuttleBus shuttleBus, BindingResult bindingResult, Model model, RedirectAttributes re) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("shuttlebus", shuttleBus);
            return "pages/transit-form";
        }
        try {
            transitionService.save(shuttleBus);
            re.addFlashAttribute("successMessage", "Cám ơn bạn đã chọn chúng tôi.");
            return "redirect:/";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("message", "Không tìm thấy mã đặt vé của bạn");
            return "pages/error_message";
        }
    }

    @GetMapping("/page/{pageNo}")
    public String findPageReceipt(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Principal principal) {
        int pageSize = 6;
        String email = principal.getName();
        UserEntity user = userService.findbyEmail(email).get();
        Page<Booking> bookedTripPage = bookingService.findPage(user.getId(), true, pageNo, pageSize, sortField, sortDir);
        List<Booking> bookedTrip = bookedTripPage.getContent();
        if (bookedTrip.isEmpty()) {
            model.addAttribute("notFound", true);
        } else model.addAttribute("notFound", false);

        model.addAttribute("bookings", bookedTrip);
        model.addAttribute("header", "Danh sách vé xe đã đặt");
        model.addAttribute("currentPage", "Vé của tôi");

        model.addAttribute("currentPage1", pageNo);
        model.addAttribute("totalPages", bookedTripPage.getTotalPages());
        model.addAttribute("totalItems", bookedTripPage.getTotalElements());
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        System.out.println("Boking details" + bookedTrip.get(0).getId());
        return "pages/show_receipts";
    }

    @GetMapping("/manage-receipts")
    public String viewPage(Model model, Principal principal) {
        return findPageReceipt(1, model, "id", "asc", principal);

    }

    @GetMapping("/basket")
    public String showBill(Model model, Principal principal) {
        return findPageBill(1, model, "id", "asc", principal);
    }

    @GetMapping("/bill-page/page/{pageNo}")
    public String findPageBill(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Principal principal) {
        int pageSize = 6;
        String email = principal.getName();
        UserEntity user = userService.findbyEmail(email).get();
        Page<Booking> bookedTripPage = bookingService.findPage(user.getId(), false, pageNo, pageSize, sortField, sortDir);
        List<Booking> bookedTrip = bookedTripPage.getContent();
        if (bookedTrip.isEmpty()) {
            model.addAttribute("notFound", true);
        } else model.addAttribute("notFound", false);

        model.addAttribute("bookings", bookedTrip);
        model.addAttribute("header", "Thanh toán vé");
        model.addAttribute("currentPage", "Vé chưa thanh toán");

        model.addAttribute("currentPage1", pageNo);
        model.addAttribute("totalPages", bookedTripPage.getTotalPages());
        model.addAttribute("totalItems", bookedTripPage.getTotalElements());

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "pages/show_basket";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            bookingService.delete(id);
            ra.addFlashAttribute("raMessage", "Bạn đã hủy thành công vé (ID: " + id + ")");
        } catch (VehicleNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CannotDeleteException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/tickets/basket";
    }

}
