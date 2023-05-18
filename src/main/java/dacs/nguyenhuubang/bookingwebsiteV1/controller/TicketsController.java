package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@RequestMapping(value = {"/users/tickets"})
@Controller
public class TicketsController {

    private final UserService userService;
    private final BookingService bookingService;
    private final BookingDetailsService bookingDetailsService;

    @GetMapping("/manage-receipts")
    private String showReceipts(Model model, Principal principal) {
        System.out.println(userService);
        String email = principal.getName();
        UserEntity user = userService.findbyEmail(email).get();
        System.out.println(user.getEmail());
        List<Booking> bookedTrip = bookingService.getBookedTripsByUserId(user.getId(), true);
        if (bookedTrip.isEmpty()){
            model.addAttribute("notFound", true);
        }else  model.addAttribute("notFound", false);
        model.addAttribute("bookings", bookedTrip);
        model.addAttribute("header", "Danh sách vé xe đã đặt");
        model.addAttribute("currentPage", "Vé của tôi");

        return "pages/show_receipts";
    }

    @GetMapping("/basket")
    private String showBill(Model model, Principal principal) {
        String email = principal.getName();
        UserEntity user = userService.findbyEmail(email).get();
        System.out.println(user.getEmail());
        List<Booking> bookedTrip = bookingService.getBookedTripsByUserId(user.getId(), false);
        if (bookedTrip.isEmpty()){
            model.addAttribute("notFound", true);
        }else  model.addAttribute("notFound", false);

// Tính tổng tiền từ các đối tượng BookingDetails
        Float totalBill = (float) 0;
        for (Booking booking : bookedTrip) {
            for (BookingDetails bookingDetails : booking.getBookingDetails()) {
                totalBill += bookingDetails.getTotalPrice();
            }
        }
        model.addAttribute("totalBill", totalBill);
        model.addAttribute("bookings", bookedTrip);
        model.addAttribute("header", "Thanh toán vé");
        model.addAttribute("currentPage", "Vé chưa thanh toán");
        return "pages/show_basket";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try{
            bookingService.delete(id);
            ra.addFlashAttribute("raMessage", "Bạn đã hủy thành công vé (ID: "+id+")");
        }catch (VehicleNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }catch (CannotDeleteException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/tickets/basket";
    }
}
