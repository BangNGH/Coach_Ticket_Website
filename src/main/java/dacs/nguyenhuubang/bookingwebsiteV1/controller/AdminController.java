package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingDetailsService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class AdminController {

    private final BookingDetailsService bookingDetailsService;
    private final BookingService bookingService;

    @GetMapping("/bill")
    public String adminBillPage(Model model) {

        return "admin/pages/manage_bill";
    }

    @RequestMapping(value = {"", "/", "/home"})
    public String adminHomePage(Model model) {
        List<BookingDetails> bookingDetailsList = bookingDetailsService.getBookings();
        List<Booking> bookings = bookingService.getBookings();

        Float revenue = 0.0F;
        revenue = bookingDetailsList
                .stream()
                .collect(Collectors.summingDouble(BookingDetails::getTotalPrice))
                .floatValue();

        //Chart doanh thu theo tháng
        Map<YearMonth, Double> revenueByMonth = bookingDetailsList.stream()
                .collect(Collectors.groupingBy(
                        bookingDetails -> YearMonth.from(bookingDetails.getBooking().getBooking_date()),
                        Collectors.summingDouble(BookingDetails::getTotalPrice)
                ));

        //Doanh thu tháng này
        YearMonth currentMonth = YearMonth.now();
        Double currentMonthRevenue = revenueByMonth.get(currentMonth);

        if (currentMonthRevenue != null) {
            // Do something with the current month revenue
            model.addAttribute("currentMonthRevenue", currentMonthRevenue);
        } else {
            // Tháng hiện tại chưa có doanh số
            model.addAttribute("currentMonthRevenue", "Tháng này chưa có doanh thu");
        }

        //Tính doanh thu ngày hôm nay
        LocalDate currentDate = LocalDate.now();
        Float revenueToday = 0.0F;
        revenueToday = (float) bookingDetailsList.stream()
                .filter(bookingDetails -> bookingDetails.getBooking().getBooking_date().equals(currentDate))
                .mapToDouble(BookingDetails::getTotalPrice)
                .sum();

        //Tính số lượng hóa đơn
        Long numberOfBookings = 0L;
        numberOfBookings = bookings
                .stream()
                .collect(Collectors.counting());

        Long numberOfBill = 0L;
        numberOfBill = bookings
                .stream().filter(booking -> booking.getIsPaid() == false)
                .collect(Collectors.counting());
        Long numberOfReceipt = numberOfBookings - numberOfBill;

        model.addAttribute("numberOfBill", numberOfBill);
        model.addAttribute("numberOfBookings", numberOfBookings);
        model.addAttribute("revenueByMonth", revenueByMonth);
        model.addAttribute("revenueToday", revenueToday);
        model.addAttribute("revenue", revenue);
        return "admin/pages/admin_landing_page";
    }


}