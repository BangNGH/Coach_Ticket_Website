package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import dacs.nguyenhuubang.bookingwebsiteV1.config.Config;
import dacs.nguyenhuubang.bookingwebsiteV1.dto.paymentDTO;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.SeatHasBeenReseredException;
import dacs.nguyenhuubang.bookingwebsiteV1.security.UserService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UsersBookingController {

    private final BookingService bookingService;
    private final BookingDetailsService bookingDetailsService;
    private final UserService userService;

    private final TripService tripService;
    private final SeatService seatService;
    private final SeatReservationService seatReservationService;


    @PostMapping("/book")
    public String bookTrip(Model model, @RequestParam("startTime") LocalDate startTime, @RequestParam("selectedTripId") Integer selectedTripId,
                           @RequestParam("inputSelectedSeats") String inputSelectedSeats, RedirectAttributes re) {
        List<Long> seatIds = new ArrayList<>();
        String[] seatIdArray = inputSelectedSeats.split(",");
        for (String seatId : seatIdArray) {
            seatIds.add(Long.valueOf(seatId));
        }
        if (seatIds.isEmpty()) {
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            return "redirect:/home";
        }

        try {
            LocalTime now = LocalTime.now();
            Trip trip = tripService.get(selectedTripId);
            if (trip.getStartTime().compareTo(now) <= 0) {
                re.addFlashAttribute("errorMessage", "Chuyến này đã xuất phát rồi!");
                return "redirect:/home";
            }
            List<Seat> seatsReserved = new ArrayList<>();
            for (Long seatId : seatIds) {
                Seat seat = seatService.get(seatId);
                seatsReserved.add(seat);
            }
            if (!seatsReserved.isEmpty()) {
                model.addAttribute("trip", trip);
                model.addAttribute("seatsReserved", seatsReserved);
                model.addAttribute("startTime", startTime);
                model.addAttribute("header", "Xác nhận chuyến đi");
                model.addAttribute("currentPage", "xác nhận");
                return "pages/confirm_booking";
            }else {
                re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
                return "redirect:/home";
            }
        }catch (ResourceNotFoundException e){
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            return "redirect:/home";
        }
    }

    @PostMapping("/save")
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public String saveBooking(Model model,@ModelAttribute("trip")Trip trip, @RequestParam("date")@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date, RedirectAttributes re, @RequestParam("seatsReserved") List<Integer> seatIds) throws UnsupportedEncodingException {

        List<Seat> seatsReserved = new ArrayList<>();
        for (Integer seatId : seatIds) {
            Seat seat = seatService.get(Long.valueOf(seatId));
            seatsReserved.add(seat);
        }
        if (seatsReserved.isEmpty()){
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            return "redirect:/home";
        }

        //Save booking
        Trip bookingTrip = trip;
        Booking booking = new Booking();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity user = userService.findbyEmail(email).get();
        booking.setTrip(bookingTrip);
        booking.setBooking_date(date);
        booking.setIsPaid(false);
        booking.setUser(user);
        Booking savedBooking = bookingService.save(booking);

        //Save booking details
        BookingDetails bookingDetails = new BookingDetails();
        BookingDetailsId bookingDetailsId = new BookingDetailsId();
        bookingDetailsId.setBookingId(savedBooking.getId());

        bookingDetails.setId(bookingDetailsId);
        bookingDetails.setNumberOfTickets(seatsReserved.size());
        BookingDetails  savedBookingDetails = bookingDetailsService.save(bookingDetails, " ");

        try {
            //Save seat reservation
            Integer tempId = null;
            if (seatsReserved.size() == 1) {
                SeatReservation seatReservation = new SeatReservation();
                seatReservation.setBooking(savedBooking);
                seatReservation.setSeat(seatsReserved.get(0));
                seatReservationService.save(seatReservation, tempId);
            } else {
                for (Seat seat : seatsReserved) {
                    SeatReservation seatReservation = new SeatReservation();
                    seatReservation.setBooking(savedBooking);
                    seatReservation.setSeat(seat);
                    System.out.println(seatReservation.getSeat().getName());
                    System.out.println(savedBooking.getTrip().getRoute().getName());
                    seatReservationService.save(seatReservation, tempId);
                }
            }
        }catch (SeatHasBeenReseredException e){
            model.addAttribute("message", e.getMessage());
            return "error_message";
        }


        //Thanh toán
        long amount = (long)(savedBookingDetails.getTotalPrice()*100);
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", Config.vnp_Version);
        vnp_Params.put("vnp_Command", Config.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", String.valueOf(savedBooking.getId()));
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_Returnurl);
      //  vnp_Params.put("vnp_bookingId", );

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        paymentDTO paymentDTO = new paymentDTO();
        paymentDTO.setStatus("Ok");
        paymentDTO.setMessage("Successfully");
        paymentDTO.setURL(paymentUrl);

        model.addAttribute("vnpay", paymentUrl);
        return "pages/payment_methods";
    }

    @GetMapping("/payment-result")
    public String showResult(Model model,@RequestParam("vnp_Amount") String amount,
                             @RequestParam("vnp_PayDate") String date,
                             @RequestParam("vnp_OrderInfo") String bookingId,
                             @RequestParam("vnp_ResponseCode") String responseCode
                             ){
        if (responseCode.equals("00")){
            Booking booking =bookingService.get(Integer.parseInt(bookingId));
            booking.setIsPaid(true);
            Booking myBooking = bookingService.save(booking);


            List<Seat> reservedSeat = seatReservationService.getReservedSeat(myBooking);

            model.addAttribute("route", myBooking.getTrip().getRoute().getName());
            model.addAttribute("startDate", myBooking.getBooking_date());
            model.addAttribute("startTime", myBooking.getTrip().getStartTime());
            model.addAttribute("vehicle", myBooking.getTrip().getVehicle().getName());
            model.addAttribute("licensePlates", myBooking.getTrip().getVehicle().getLicensePlates());
            String totalPrice = amount.substring(0, amount.length() - 2);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("status", myBooking.getIsPaid());
            model.addAttribute("info", myBooking.getUser().getEmail());
            model.addAttribute("seatsReserved", reservedSeat);
        }
        else {
            model.addAttribute("error", "Xảy ra lỗi trong quá trình thanh toán");
        }
        return "pages/payment_result";
    }
}

