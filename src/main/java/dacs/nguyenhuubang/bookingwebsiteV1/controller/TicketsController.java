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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
            if (booking.getTrip().getRoute().getStartCity().getName().trim().equals("Sài Gòn")) {
                model.addAttribute("storeAddress", "Trường Đại học Hutech - KCN Cao");
            }
            if (booking.getTrip().getRoute().getStartCity().getName().trim().equals("Tây Ninh")) {
                model.addAttribute("storeAddress", "Bến xe Tây Ninh");
            }
            if (booking.getTrip().getRoute().getStartCity().getName().trim().equals("Vũng Tàu")) {
                model.addAttribute("storeAddress", "Bến xe Vũng Tàu");
            }
            model.addAttribute("shuttlebus", shuttlebus);
            model.addAttribute("header", "Đặt trung chuyển vé: " + booking.getBookingDetails().get(0).getId().getTicketCode());
            model.addAttribute("currentPage", "Đặt trung chuyển.");
            return "pages/transit-form";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("errorMessage", "Không tìm thấy mã đặt vé của bạn");
            return "redirect:/users/tickets/manage-receipts";
        }
    }

    @PostMapping("/transit-form")
    public String saveShippingInfo(@Valid @ModelAttribute("shuttlebus") ShuttleBus shuttleBus, BindingResult bindingResult, Model model, RedirectAttributes re) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("shuttlebus", shuttleBus);
            return "pages/transit-form";
        }
        try {
            ShuttleBus savedTransit = transitionService.save(shuttleBus);
            Booking booking = savedTransit.getBooking();
            booking.setShuttleBus(savedTransit);
            bookingService.save(booking);
            re.addFlashAttribute("raMessage", "Chúng tôi đã lưu thông tin trung chuyển của bạn.");
            return "redirect:/users/tickets/manage-receipts";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("message", "Không tìm thấy mã đặt vé của bạn");
            return "pages/error_message";
        }
    }

    @GetMapping("/transit/edit/{id}")
    public String showEditTransitForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            ShuttleBus shuttleBus = transitionService.findByBookingId(id);
            Booking booking = bookingService.get(id);
            if (booking == null) {
                throw new ResourceNotFoundException("Không tìm thấy hóa đơn nào với ID: " + id);
            }
            if (shuttleBus == null) {
                ShuttleBus shuttlebus = new ShuttleBus();
                shuttlebus.setBooking(booking);
                model.addAttribute("booking", booking);
                if (booking.getTrip().getRoute().getStartCity().getName().trim().equals("Sài Gòn")) {
                    model.addAttribute("storeAddress", "Trường Đại học Hutech - KCN Cao");
                }
                if (booking.getTrip().getRoute().getStartCity().getName().trim().equals("Tây Ninh")) {
                    model.addAttribute("storeAddress", "Bến xe Tây Ninh");
                }
                if (booking.getTrip().getRoute().getStartCity().getName().trim().equals("Vũng Tàu")) {
                    model.addAttribute("storeAddress", "Bến xe Vũng Tàu");
                }
                model.addAttribute("shuttlebus", shuttlebus);
                model.addAttribute("header", "Đặt trung chuyển vé: " + booking.getBookingDetails().get(0).getId().getTicketCode());
                model.addAttribute("currentPage", "Đặt trung chuyển.");
                return "pages/transit-form";
            } else {
                //edit
                model.addAttribute("booking", shuttleBus.getBooking());
                if (shuttleBus.getBooking().getTrip().getRoute().getStartCity().getName().trim().equals("Sài Gòn")) {
                    model.addAttribute("storeAddress", "Trường Đại học Hutech - KCN Cao");
                }
                if (shuttleBus.getBooking().getTrip().getRoute().getStartCity().getName().trim().equals("Tây Ninh")) {
                    model.addAttribute("storeAddress", "Bến xe Tây Ninh");
                }
                if (shuttleBus.getBooking().getTrip().getRoute().getStartCity().getName().trim().equals("Vũng Tàu")) {
                    model.addAttribute("storeAddress", "Bến xe Vũng Tàu");
                }
                model.addAttribute("shuttlebus", shuttleBus);
                model.addAttribute("isEdit", true);
                model.addAttribute("header", "Đặt trung chuyển vé: " + booking.getBookingDetails().get(0).getId().getTicketCode());
                model.addAttribute("currentPage", "Đặt trung chuyển.");
                return "pages/transit-form";
            }
        } catch (ResourceNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/tickets/manage-receipts";
        }
    }

    @GetMapping("/page/{pageNo}")
    public String findPageReceipt(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Principal principal) {
        int pageSize = 6;
        String email = principal.getName();
        UserEntity user = userService.findbyEmail(email).get();
        Page<Booking> bookedTripPage = bookingService.findPage(user.getId(), true, pageNo, pageSize, sortField, sortDir);
        List<Booking> bookedTrip = bookedTripPage.getContent().stream()
                .sorted(Comparator.comparing(Booking::getBookingDate))
                .collect(Collectors.toList());
        bookedTrip.sort(Comparator.comparing(Booking::getBookingDate));
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
        List<Booking> bookedTrip = bookedTripPage.getContent().stream()
                .sorted(Comparator.comparing(Booking::getBookingDate))
                .collect(Collectors.toList());
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
