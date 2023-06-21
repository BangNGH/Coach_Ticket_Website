package dacs.nguyenhuubang.bookingwebsiteV1.controller.admin;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetailsId;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingDetailsService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin/booking-details")
@Controller
public class BookingDetailsController {

    private final BookingDetailsService bookingDetailsService;
    private final BookingService bookingService;


    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 8;
        Page<BookingDetails> page = bookingDetailsService.findPaginated(pageNo, pageSize, sortField, sortDir);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("bookingDetails", page.getContent());
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "admin/pages/admin_crud_booking_details";
    }

    @GetMapping("")
    public String getBookingDetails(Model model) {
        return findPaginated(1, model, "id", "asc");
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "Tạo Chi Tiết Hóa Đơn");
        BookingDetails bookingDetails = new BookingDetails();
        model.addAttribute("bookingDetails", bookingDetails);
        return getList(model);
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("bookingDetails") BookingDetails bookingDetails, BindingResult bindingResult, RedirectAttributes re, @RequestParam(value = "id.ticketCode", required = false) String ticketCode) {
        if (bindingResult.hasErrors()) {
            return "admin/pages/booking_details_form";
        }

        bookingDetailsService.save(bookingDetails, ticketCode);
        re.addFlashAttribute("raMessage", "Chi tiết hóa đơn được lưu thành công.");
        return "redirect:/admin/booking-details";
    }

  @GetMapping("/edit/{bookingId}/{ticketCode}")
 public String showEditForm(@PathVariable("bookingId") Integer bookingId, @PathVariable("ticketCode") String ticketCode, Model model, RedirectAttributes ra) {
        try {

         BookingDetailsId bookingDetailsId = new BookingDetailsId(bookingId, ticketCode);
            BookingDetails bookingDetails = bookingDetailsService.get(bookingDetailsId);
            model.addAttribute("bookingDetails", bookingDetails);
            model.addAttribute("pageTitle", "Edit booking-details (ID: " + bookingDetailsId.getBookingId()+"-"+bookingDetailsId.getTicketCode() + ")");
            return getList(model);
        } catch (ResourceNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/booking-details";
        }
    }

    @GetMapping("/delete/{bookingId}/{ticketCode}")
    public String delete(@PathVariable("bookingId") Integer bookingId, @PathVariable("ticketCode") String ticketCode, Model model, RedirectAttributes ra) {
        try {
            BookingDetailsId bookingDetailsId = new BookingDetailsId(bookingId, ticketCode);
            bookingDetailsService.delete(bookingDetailsId);
            ra.addFlashAttribute("raMessage", "The booking-details (ID: " + bookingId + ") has been deleted");
        } catch (VehicleNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (CannotDeleteException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/booking-details";
    }

    private String getList(Model model) {
        List<Booking> bookings = bookingService.getBookings();
        if (bookings.isEmpty()){
            model.addAttribute("message", "Danh sách rỗng hoặc các khóa ngoại đã bị xóa");
            return "error_message";
        }
        model.addAttribute("bookings", bookings);
        return "admin/pages/booking_details_form";
    }
}
