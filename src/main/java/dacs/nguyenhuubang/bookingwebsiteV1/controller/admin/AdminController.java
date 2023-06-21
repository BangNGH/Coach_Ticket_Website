package dacs.nguyenhuubang.bookingwebsiteV1.controller.admin;

import dacs.nguyenhuubang.bookingwebsiteV1.config.momo.MoMoSecurity;
import dacs.nguyenhuubang.bookingwebsiteV1.config.momo.PaymentRequest;
import dacs.nguyenhuubang.bookingwebsiteV1.config.vnpay.Config;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.event.SendEmailReminderEvent;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.SeatHasBeenReseredException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.codehaus.groovy.runtime.typehandling.NumberMath.abs;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class AdminController {
    private final BookingDetailsService bookingDetailsService;
    private final BookingService bookingService;
    private final CityService cityService;
    private final TripService tripService;
    private final UserService userService;
    private final SeatService seatService;
    private final SeatReservationService seatReservationService;
    private final ApplicationEventPublisher publisher;
    private final HttpServletRequest servletRequest;

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @GetMapping("/print-tickets/{id}")
    public String printTicket(@PathVariable("id") Integer id, RedirectAttributes ra) {
        ra.addFlashAttribute("successMessage", "Thành công vé ID:" + id);
        return "redirect:/admin";
    }

    @GetMapping("/bill")
    public String showBill(Model model) {
        return findPageBill(1, model, "id", "asc");
    }

    //Hóa đơn chưa thanh toán
    @GetMapping("/bill-page/page/{pageNo}")
    public String findPageBill(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 7;

        Page<Booking> bookedTripPage = bookingService.findPage(false, pageNo, pageSize, sortField, sortDir);
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
        return "admin/pages/show_bill";
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
        return "redirect:/admin/bill";
    }

    @RequestMapping(value = {"", "/", "/home"})
    public String adminHomePage(Model model, RedirectAttributes re) {
        List<BookingDetails> bookingDetailsList = bookingDetailsService.getBookings();
        List<Booking> bookings = bookingService.getBookings();
        List<City> cities = cityService.getCities();
        if (bookings.isEmpty()) {
            re.addFlashAttribute("errorMessage", "Chưa có hóa đơn nào cần quản lý.");
            return "redirect:/admin/bookings";
        }
        int count = 0;
        for (Booking check_booking : bookings
        ) {
            if (check_booking.getIsPaid() == false) {
                count++;
            }
        }
        if (count == bookings.size()) {
            re.addFlashAttribute("errorMessage", "Các hóa đơn đều chưa thanh toán!");
            return "redirect:/admin/bookings";
        }
        if (cities.isEmpty()) {
            re.addFlashAttribute("errorMessage", "Thêm dữ liệu cho bảng này");
            return "redirect:/admin/cities";
        }
        if (bookingDetailsList.isEmpty()) {
            re.addFlashAttribute("errorMessage", "Thêm dữ liệu cho bảng này");
            return "redirect:/admin/booking-details";
        }
        model.addAttribute("cities", cities);
        Float revenue = 0.0F;
        revenue = bookingDetailsList
                .stream().filter(bookingDetails -> bookingDetails.getBooking().getIsPaid() == true)
                .collect(Collectors.summingDouble(BookingDetails::getTotalPrice))
                .floatValue();

        //Chart doanh thu theo tháng
        Map<YearMonth, Double> revenueByMonth = bookingDetailsList.stream().filter(bookingDetails -> bookingDetails.getBooking().getIsPaid() == true)
                .collect(Collectors.groupingBy(
                        bookingDetails -> YearMonth.from(bookingDetails.getBooking().getBookingDate()),
                        TreeMap::new, // tự động sắp xếp các entry theo thứ tự của khóa (YearMonth).
                        Collectors.summingDouble(BookingDetails::getTotalPrice)
                ));

        //Doanh thu tháng này
        YearMonth currentMonth = YearMonth.now();
        Double currentMonthRevenue = revenueByMonth.get(currentMonth);

        if (currentMonthRevenue != null) {
            model.addAttribute("currentMonthRevenue", currentMonthRevenue);
        } else {
            // Tháng hiện tại chưa có doanh số
            model.addAttribute("currentMonthRevenue", "Tháng này chưa có doanh thu");
        }

        //% doanh thu so với tháng trước
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        Double lastMonthRevenue = revenueByMonth.get(lastMonth);
        if (lastMonthRevenue == null) {
            model.addAttribute("invalidPercentageOfSales", true);
        } else {
            //Độ chênh lệhc
            Double deviation = 0.0;
            if (currentMonthRevenue > lastMonthRevenue) {
                deviation = currentMonthRevenue - lastMonthRevenue;
                model.addAttribute("sign", "+");
            } else {
                deviation = (Double) abs(currentMonthRevenue - lastMonthRevenue);
                model.addAttribute("sign", "-");
                model.addAttribute("warnning", "true");
            }
            //% chênh lệch
            Double percentageOfSales = (deviation / lastMonthRevenue) * 100;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String roundedNumber = decimalFormat.format(percentageOfSales);
            model.addAttribute("percentageOfSales", roundedNumber);
        }


        //Tính doanh thu ngày hôm nay
        LocalDate currentDate = LocalDate.now();
        Float revenueToday = 0.0F;
        revenueToday = (float) bookingDetailsList.stream().filter(bookingDetails -> bookingDetails.getBooking().getIsPaid() == true)
                .filter(bookingDetails -> bookingDetails.getBooking().getBookingDate().equals(currentDate))
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

        model.addAttribute("numberOfBill", numberOfBill);
        model.addAttribute("numberOfBookings", numberOfBookings);
        model.addAttribute("revenueByMonth", revenueByMonth);
        model.addAttribute("revenueToday", revenueToday);
        model.addAttribute("revenue", revenue);
        return "admin/pages/admin_landing_page";
    }


    @RequestMapping(value = {"/find-trip"})
    public String getTrips(Model model, RedirectAttributes re, @RequestParam("startCity") City startCity,
                           @RequestParam("endCity") City endCity, @RequestParam("startTime") LocalDate startTime,
                           @RequestParam(value = "endTime", required = false) LocalDate endTime) {
        if (startCity == endCity) {
            re.addFlashAttribute("errorMessage", "Vui lòng chọn thành phố khác nhau");
            return "redirect:/admin";
        }
        try {
            List<Trip> foundTrips = new ArrayList<>();
            if (startTime.isEqual(LocalDate.now())) {
                foundTrips = tripService.findTripsByCitiesAndStartTime(startCity, endCity).stream()
                        .filter(trip -> trip.getStartTime().isAfter(LocalTime.now()))
                        .sorted(Comparator.comparing(Trip::getStartTime))
                        .collect(Collectors.toList());
            } else {
                foundTrips = tripService.findTripsByCitiesAndStartTime(startCity, endCity);
                foundTrips.sort(Comparator.comparing(Trip::getStartTime));
            }
            if (foundTrips.isEmpty()) {
                re.addFlashAttribute("errorMessage", "Hiện chưa có chuyến mà bạn tìm kiếm");
                return "redirect:/admin";
            }
            Map<Integer, Integer> availableSeatsMap = new HashMap<>();
            Map<Integer, List<Seat>> loadAvailableSeatsMap = new HashMap<>();
            Map<Integer, List<Seat>> loadReservedSeat = new HashMap<>();
            for (Trip trip : foundTrips) {
                int totalSeat = trip.getVehicle().getCapacity();
                int seatReserved = seatReservationService.checkAvailableSeat(trip, startTime);
                List<Seat> seatsAvailable = seatReservationService.listAvailableSeat(trip.getVehicle(), trip, startTime);
                List<Seat> listReservedSeat = seatReservationService.listReservedSeat(trip.getVehicle(), trip, startTime);
                int availableSeats = totalSeat - seatReserved;

                loadAvailableSeatsMap.put(trip.getId(), seatsAvailable);
                loadReservedSeat.put(trip.getId(), listReservedSeat);
                availableSeatsMap.put(trip.getId(), availableSeats);
            }

            model.addAttribute("foundTrips", foundTrips);
            model.addAttribute("loadAvailableSeatsMap", loadAvailableSeatsMap);
            model.addAttribute("listReservedSeat", loadReservedSeat);
            model.addAttribute("availableSeatsMap", availableSeatsMap);
            model.addAttribute("header", "Tìm chuyến");
            model.addAttribute("currentPage", "Tìm chuyến");
            model.addAttribute("startCity", startCity.getName());
            model.addAttribute("endCity", endCity.getName());
            model.addAttribute("startTime", startTime);
            model.addAttribute("endTime", endTime);

            return "admin/pages/find_trip";
        } catch (RuntimeException e) {
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            return "redirect:/admin";
        }
    }

    public boolean isValidEmail(String email) {
        // Regex pattern để kiểm tra định dạng email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @PostMapping("/booking-trip")
    @Transactional(rollbackFor = {Exception.class, Throwable.class, SeatHasBeenReseredException.class})
    public String bookRoundTrip(final HttpServletRequest request, Principal p, Model model, @RequestParam(value = "bookedId", required = false) Integer bookedId, @RequestParam("startTime") LocalDate startTime, @RequestParam("selectedTripId") Integer selectedTripId,
                                @RequestParam("inputSelectedSeats") String inputSelectedSeats, RedirectAttributes re, @RequestParam(value = "endTime", required = false) LocalDate endTime) {

        try {
            String validEmail = p.getName();
            UserEntity checkUser = userService.findbyEmail(validEmail).get();
            if (!isValidEmail(validEmail) && !isValidEmail(checkUser.getAddress())) {
                model.addAttribute("user", checkUser);
                model.addAttribute("gbUserName", checkUser.getEmail());

                return "pages/fill_out_email";
            }
            if (endTime != null) {
                List<Long> seatIds = new ArrayList<>();
                String[] seatIdArray = inputSelectedSeats.split(",");
                for (String seatId : seatIdArray) {
                    seatIds.add(Long.valueOf(seatId));
                }
                LocalTime now = LocalTime.now();
                LocalDate today = LocalDate.now();
                Trip trip = tripService.get(selectedTripId);
                if (today.isEqual(startTime) && trip.getStartTime().compareTo(now) <= 0) {
                    re.addFlashAttribute("errorMessage", "Chuyến này đã xuất phát rồi!");
                    return "redirect:/admin";
                }
                List<Seat> seatsReserved = new ArrayList<>();
                for (Long seatId : seatIds) {
                    Seat seat = seatService.get(seatId);
                    seatsReserved.add(seat);
                }
                //Save booking
                Trip bookingTrip = trip;
                Booking booking = new Booking();
                UserEntity user = checkUser;

                booking.setTrip(bookingTrip);
                booking.setBookingDate(startTime);
                booking.setIsPaid(true); //thanh toán tiền mặt
                booking.setUser(user);

                Booking savedBooking = bookingService.save(booking);

                //Save booking details
                BookingDetails bookingDetails = new BookingDetails();
                BookingDetailsId bookingDetailsId = new BookingDetailsId();
                bookingDetailsId.setBookingId(savedBooking.getId());

                bookingDetails.setId(bookingDetailsId);
                bookingDetails.setNumberOfTickets(seatsReserved.size());
                BookingDetails savedBookingDetails = bookingDetailsService.save(bookingDetails, " ");

                List<BookingDetails> list = new ArrayList<>();
                list.add(savedBookingDetails);
                savedBooking.setBookingDetails(list);
                bookingService.save(savedBooking);

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
                        seatReservationService.save(seatReservation, tempId);
                    }
                }

                List<Trip> foundTrips = tripService.findTripsByCitiesAndStartTime(trip.getRoute().getEndCity(), trip.getRoute().getStartCity());
                Map<Integer, Integer> availableSeatsMap = new HashMap<>();
                Map<Integer, List<Seat>> loadAvailableSeatsMap = new HashMap<>();
                Map<Integer, List<Seat>> loadReservedSeat = new HashMap<>();
                for (Trip trip2 : foundTrips) {
                    int totalSeat = trip2.getVehicle().getCapacity();
                    int seatReserved = seatReservationService.checkAvailableSeat(trip2, endTime);
                    List<Seat> seatsAvailable = seatReservationService.listAvailableSeat(trip2.getVehicle(), trip2, endTime);
                    List<Seat> listReservedSeat = seatReservationService.listReservedSeat(trip2.getVehicle(), trip2, startTime);
                    int availableSeats = totalSeat - seatReserved;

                    loadAvailableSeatsMap.put(trip2.getId(), seatsAvailable);
                    loadReservedSeat.put(trip2.getId(), listReservedSeat);
                    availableSeatsMap.put(trip2.getId(), availableSeats);
                }

                model.addAttribute("foundTrips", foundTrips);
                model.addAttribute("bookedId", savedBooking.getId());
                model.addAttribute("loadAvailableSeatsMap", loadAvailableSeatsMap);
                model.addAttribute("listReservedSeat", loadReservedSeat);
                model.addAttribute("availableSeatsMap", availableSeatsMap);
                model.addAttribute("header", "Tìm chuyến về");
                model.addAttribute("currentPage", "Tìm chuyến");
                model.addAttribute("startCity", trip.getRoute().getEndCity().getName());
                model.addAttribute("endCity", trip.getRoute().getStartCity().getName());
                model.addAttribute("startTime", endTime);
                return "admin/pages/find_trip";
            } else {
                return savedBooking(p, bookedId, model, startTime, selectedTripId, inputSelectedSeats, re, request);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            re.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/book")
    @Transactional(rollbackFor = {Exception.class, Throwable.class, SeatHasBeenReseredException.class})
    public String savedBooking(Principal p, Integer bookedId, Model model, @RequestParam("startTime") LocalDate startTime, @RequestParam("selectedTripId") Integer selectedTripId,
                               @RequestParam("inputSelectedSeats") String inputSelectedSeats, RedirectAttributes re, HttpServletRequest request) {
        String validEmail = p.getName();
        UserEntity checkUser = userService.findbyEmail(validEmail).get();
        if (!isValidEmail(validEmail) && !isValidEmail(checkUser.getAddress())) {
            model.addAttribute("user", checkUser);
            model.addAttribute("gbUserName", checkUser.getEmail());
            return "pages/fill_out_email";
        }
        List<Long> seatIds = new ArrayList<>();
        String[] seatIdArray = inputSelectedSeats.split(",");
        for (String seatId : seatIdArray) {
            seatIds.add(Long.valueOf(seatId));
        }

        if (seatIds.isEmpty()) {
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (bookedId != null)
                bookingService.delete(bookedId);
            return "redirect:/admin";
        }
        try {
            LocalTime now = LocalTime.now();
            LocalDate today = LocalDate.now();
            Trip trip = tripService.get(selectedTripId);
            if (today.isEqual(startTime) && trip.getStartTime().compareTo(now) <= 0) {
                re.addFlashAttribute("errorMessage", "Chuyến này đã xuất phát rồi!");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                if (bookedId != null)
                    bookingService.delete(bookedId);
                return "redirect:/admin";
            }
            List<Seat> seatsReserved = new ArrayList<>();
            for (Long seatId : seatIds) {
                Seat seat = seatService.get(seatId);
                seatsReserved.add(seat);
            }
            if (!seatsReserved.isEmpty()) {

                //Save booking
                Trip bookingTrip = trip;
                Booking booking = new Booking();
                UserEntity user = checkUser;

                booking.setTrip(bookingTrip);
                booking.setBookingDate(startTime);
                booking.setIsPaid(false);
                booking.setUser(user);

                Booking savedBooking = bookingService.save(booking);

                //Save booking details
                BookingDetails bookingDetails = new BookingDetails();
                BookingDetailsId bookingDetailsId = new BookingDetailsId();
                bookingDetailsId.setBookingId(savedBooking.getId());

                bookingDetails.setId(bookingDetailsId);
                bookingDetails.setNumberOfTickets(seatsReserved.size());
                BookingDetails savedBookingDetails = bookingDetailsService.save(bookingDetails, " ");

                List<BookingDetails> list = new ArrayList<>();
                list.add(savedBookingDetails);
                savedBooking.setBookingDetails(list);
                bookingService.save(savedBooking);

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
                        seatReservationService.save(seatReservation, tempId);
                    }
                }
                Float roundTripPrice = 0.0F;
                String roundTripId = "";
                if (bookedId != null) {
                    try {
                        Booking roundTrip = bookingService.get(bookedId);
                        model.addAttribute("roundTrip", roundTrip);
                        model.addAttribute("hasRoundTrip", true);
                        roundTripId = String.valueOf(bookedId);
                        roundTripPrice = roundTrip.getBookingDetails().get(0).getTotalPrice();
                    } catch (Exception e) {
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("header", "Xảy ra lỗi");
                        model.addAttribute("currentPage", "Lỗi");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return "error_message";
                    }
                } else model.addAttribute("hasRoundTrip", false);

                String bookingId = savedBooking.getId() + "_" + roundTripId;//nối chuỗi id gửi mail
                // lấy url thanh toán VNPAY
                long vnpay_Amount = (long) ((savedBookingDetails.getTotalPrice() + roundTripPrice) * 100);
                String vnpayPaymentUrl = paymentVnpay(vnpay_Amount, bookingId, request);
                //Lấy url thanh toán Momo
                String momoAmount = String.valueOf(savedBookingDetails.getTotalPrice() + roundTripPrice);
                String sub_momoAmount = momoAmount.substring(0, momoAmount.length() - 2);

                String momoPaymentUrl = paymentMomo(sub_momoAmount, bookingId, request);
                model.addAttribute("momo", momoPaymentUrl);

                model.addAttribute("vnpay", vnpayPaymentUrl);
                model.addAttribute("startTime", savedBooking.getBookingDate());
                model.addAttribute("currentPage", "thanh toán");
                model.addAttribute("amount", sub_momoAmount);
                model.addAttribute("bookingId", bookingId);
                return "admin/pages/payment_methods";
            } else {
                re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                if (bookedId != null)
                    bookingService.delete(bookedId);
                return "redirect:/admin";
            }
        } catch (ResourceNotFoundException e) {
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (bookedId != null)
                bookingService.delete(bookedId);
            return "redirect:/admin";
        } catch (SeatHasBeenReseredException e) {
            re.addFlashAttribute("errorMessage", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (bookedId != null)
                bookingService.delete(bookedId);
            return "redirect:/admin";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/book-cash-payment")
    public String cashPayment(@RequestParam("amount") String amount,
                              @RequestParam("bookingId") String bookingId, Model model) {
        String[] parts = bookingId.split("_");
        String part1 = parts[0];
        String part2 = parts.length > 1 ? parts[1] : "";
        if (!part2.isBlank()) {
            Booking booking = bookingService.get(Integer.parseInt(part2));
            booking.setIsPaid(true);
            Booking myBooking = bookingService.save(booking);
            List<Seat> reservedSeat = seatReservationService.getReservedSeat(myBooking);
            model.addAttribute("totalPrice2", amount);
            model.addAttribute("myBooking2", myBooking);
            model.addAttribute("seatsReserved2", reservedSeat);
            String paymentMethod = "Thanh toán tiền mặt ID: " + part2;
            model.addAttribute("paymentMethod2", paymentMethod);
            model.addAttribute("hasRoundTrip", true);
        }
        Booking booking2 = bookingService.get(Integer.parseInt(part1));
        booking2.setIsPaid(true);
        Booking myBooking2 = bookingService.save(booking2);
        List<Seat> reservedSeat2 = seatReservationService.getReservedSeat(myBooking2);
        model.addAttribute("totalPrice", amount);
        model.addAttribute("myBooking", myBooking2);
        model.addAttribute("seatsReserved", reservedSeat2);
        String paymentMethod = "Thanh toán tiền mặt ID: " + part1;
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("header", "Đặt vé thành công.");
        model.addAttribute("currentPage", "Hóa đơn");
        return "admin/pages/booking_info";
    }

    @GetMapping("/send-email-reminder/{id}")
    public String sendEmailForPayment(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Booking myBooking = bookingService.get(id);
            List<Seat> reservedSeat = seatReservationService.getReservedSeat(myBooking);
            String totalPrice = myBooking.getBookingDetails().get(0).getTotalPrice().toString().substring(0, myBooking.getBookingDetails().get(0).getTotalPrice().toString().length() - 2);
            sendEmail(totalPrice, myBooking, reservedSeat);
            ra.addFlashAttribute("raMessage", "Đã gửi email nhắc nhở cho (ID: " + id + ").");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return "redirect:/admin/bill";
    }

    private void sendEmail(String totalPrice, Booking myBooking, List<Seat> reservedSeat) {
        List<String> ticketCodes = bookingDetailsService.getTicketCodes(myBooking);
        String send_ticketCodes = "";
        for (String tc : ticketCodes) {
            String code = tc;
            send_ticketCodes += code + ", ";
        }
        send_ticketCodes = send_ticketCodes.substring(0, send_ticketCodes.length() - 2);

        String send_numberOfTicket = String.valueOf(reservedSeat.size());
        String send_reservedSeatNames = ""; // Chuỗi để lưu tên các ghế đã đặt
        for (Seat seat : reservedSeat) {
            String seatName = seat.getName();
            send_reservedSeatNames += seatName + ", "; // Thêm tên của ghế vào chuỗi
        }
        send_reservedSeatNames = send_reservedSeatNames.substring(0, send_reservedSeatNames.length() - 2);
        publisher.publishEvent(new SendEmailReminderEvent(myBooking, totalPrice, send_reservedSeatNames, send_numberOfTicket, send_ticketCodes, applicationUrl(servletRequest)));
    }


    @GetMapping("/send-email-reminder/all")
    @jakarta.transaction.Transactional
    public String sendEmailAll(RedirectAttributes re) {
        try {
            String bookedId = "";
            boolean sendEmail = false;
            List<Booking> bookings = bookingService.getBookings().stream()
                    .filter(i -> !i.getIsPaid())
                    .collect(Collectors.toList());
            for (Booking booking : bookings
            ) {
                if (booking.getBookingDate().isBefore(LocalDate.now())) {
                    bookedId += booking.getId() + ", ";
                    bookingService.delete(booking.getId());
                }
                // sử dụng until() để tính toán khoảng thời gian theo đơn vị phút bằng cách sử dụng ChronoUnit.MINUTES.
                long minutesUntilPayment = LocalTime.now().until(booking.getTrip().getStartTime(), ChronoUnit.MINUTES); // Khoảng thời gian tính bằng phút
                if (booking.getBookingDate().isEqual(LocalDate.now())) {
                    if (minutesUntilPayment > 120) {
                        List<Seat> reservedSeat = seatReservationService.getReservedSeat(booking);
                        String totalPrice = booking.getBookingDetails().get(0).getTotalPrice().toString().substring(0, booking.getBookingDetails().get(0).getTotalPrice().toString().length() - 2);
                        sendEmail(totalPrice, booking, reservedSeat);
                        sendEmail = true;
                    } else {
                        bookedId += booking.getId() + ", ";
                        bookingService.delete(booking.getId());
                    }
                }
            }
            if (sendEmail) {
                re.addFlashAttribute("raMessage", "Gửi email nhắc nhở thành công cho các hóa đơn chưa thanh toán");
            }
            if (!bookedId.isBlank()) {
                re.addFlashAttribute("cancelBooking", "Đã hủy các vé chưa thanh toán trước 120 phút: " + bookedId.substring(0, bookedId.length() - 2));
            }
            return "redirect:/admin/bill";
        } catch (Exception e) {
            re.addFlashAttribute("errorMessage", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "redirect:/admin/bill";
        }

    }

    private String paymentVnpay(long send_amount, String bookingId, HttpServletRequest request) throws UnsupportedEncodingException {
        //Thanh toán VNPAY
        long amount = send_amount;
        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", Config.vnp_Version);
        vnp_Params.put("vnp_Command", Config.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        String vnp_IpAddr = Config.getIpAddress(request);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_BankCode", "VnPayQR");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", bookingId);
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_Locale", "vn");
        String vnp_Returnurl = applicationUrl(request) + "/admin/vnpay-payment-result";
        vnp_Params.put("vnp_ReturnUrl", vnp_Returnurl);

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
        System.out.println("Called: " + paymentUrl);
        return paymentUrl;
    }

    public String paymentMomo(String send_amount, String bookingId, HttpServletRequest request) throws Exception {

        // Request params needed to request MoMo system
        String endpoint = "https://test-payment.momo.vn/gw_payment/transactionProcessor";
        String partnerCode = "MOMOOJOI20210710";
        String accessKey = "iPXneGmrJH0G8FOP";
        String serectkey = "sFcbSGRSJjwGxwhhcEktCHWYUuTuPNDB";

/*        String endpoint ="https://test-payment.momo.vn/v2/gateway/api/create";
        String partnerCode = "MOMOBKUN20180529";
        String accessKey = "klm05TvNBzhg7h7j";
        String serectkey = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa";*/

        String orderInfo = "Payment";
        String returnUrl = applicationUrl(request) + "/admin/momo-payment-result";
        String notifyUrl = "https://4c8d-2001-ee0-5045-50-58c1-b2ec-3123-740d.ap.ngrok.io/home";
        String amount = send_amount;
        String orderId = String.valueOf(System.currentTimeMillis());
        String requestId = String.valueOf(System.currentTimeMillis());
        String extraData = bookingId;

        // Before sign HMAC SHA256 signature
        String rawHash = "partnerCode=" +
                partnerCode + "&accessKey=" +
                accessKey + "&requestId=" +
                requestId + "&amount=" +
                amount + "&orderId=" +
                orderId + "&orderInfo=" +
                orderInfo + "&returnUrl=" +
                returnUrl + "&notifyUrl=" +
                notifyUrl + "&extraData=" +
                extraData;

        MoMoSecurity crypto = new MoMoSecurity();
        // Sign signature SHA256
        String signature = crypto.signSHA256(rawHash, serectkey);

        // Build body JSON request
        JSONObject message = new JSONObject();
        message.put("partnerCode", partnerCode);
        message.put("accessKey", accessKey);
        message.put("requestId", requestId);
        message.put("amount", amount);
        message.put("orderId", orderId);
        message.put("orderInfo", orderInfo);
        message.put("returnUrl", returnUrl);
        message.put("notifyUrl", notifyUrl);
        message.put("extraData", extraData);
        message.put("requestType", "captureMoMoWallet");
        message.put("signature", signature);

        String responseFromMomo = PaymentRequest.sendPaymentRequest(endpoint, message.toString());
        JSONObject jmessage = new JSONObject(responseFromMomo);
        String payUrl = jmessage.getString("payUrl");
        return payUrl;
    }

    @GetMapping("/momo-payment-result")
    public String momoPaymentResult(@RequestParam("amount") String amount,
                                    @RequestParam("errorCode") String errorCode,
                                    @RequestParam("extraData") String bookingId, Model model
    ) {
        if (errorCode.equals("0")) {
            String[] parts = bookingId.split("_");
            String part1 = parts[0];
            String part2 = parts.length > 1 ? parts[1] : "";
            if (!part2.isBlank()) {
                Booking booking = bookingService.get(Integer.parseInt(part2));
                booking.setIsPaid(true);
                Booking myBooking = bookingService.save(booking);
                List<Seat> reservedSeat = seatReservationService.getReservedSeat(myBooking);
                model.addAttribute("totalPrice2", amount);
                model.addAttribute("myBooking2", myBooking);
                model.addAttribute("seatsReserved2", reservedSeat);
                String paymentMethod = "Thanh toán Momo ID: " + part2;
                model.addAttribute("paymentMethod2", paymentMethod);
                model.addAttribute("hasRoundTrip", true);
            }
            Booking booking2 = bookingService.get(Integer.parseInt(part1));
            booking2.setIsPaid(true);
            Booking myBooking2 = bookingService.save(booking2);
            List<Seat> reservedSeat2 = seatReservationService.getReservedSeat(myBooking2);
            model.addAttribute("totalPrice", amount);
            model.addAttribute("myBooking", myBooking2);
            model.addAttribute("seatsReserved", reservedSeat2);
            String paymentMethod = "Thanh toán Momo ID: " + part1;
            model.addAttribute("paymentMethod", paymentMethod);
            model.addAttribute("header", "Đặt vé thành công.");
            model.addAttribute("currentPage", "Hóa đơn");
            return "admin/pages/booking_info";
        } else {
            model.addAttribute("message", "Xảy ra lỗi trong quá trình thanh toán");
            model.addAttribute("header", "Xảy ra lỗi");
            model.addAttribute("currentPage", "Lỗi");
            return "error_message";
        }

    }

    @GetMapping("/vnpay-payment-result")
    public String showResult(Model model, @RequestParam("vnp_Amount") String amount,
                             @RequestParam("vnp_OrderInfo") String bookingId,
                             @RequestParam("vnp_ResponseCode") String responseCode
    ) {
        if (responseCode.equals("00")) {
            String[] parts = bookingId.split("_");
            String part1 = parts[0];
            String part2 = parts.length > 1 ? parts[1] : "";
            Booking booking = bookingService.get(Integer.parseInt(part1));
            booking.setIsPaid(true);
            Booking myBooking = bookingService.save(booking);
            List<Seat> reservedSeat = seatReservationService.getReservedSeat(myBooking);
            String totalPrice = amount.substring(0, amount.length() - 2);

            if (!part2.isBlank()) {
                Booking booking2 = bookingService.get(Integer.parseInt(part2));
                booking2.setIsPaid(true);
                Booking myBooking2 = bookingService.save(booking2);
                List<Seat> reservedSeat2 = seatReservationService.getReservedSeat(myBooking2);
                model.addAttribute("myBooking2", myBooking2);
                model.addAttribute("seatsReserved2", reservedSeat2);
                model.addAttribute("hasRoundTrip", true);
            }

            String paymentMethod = "Thanh toán Vnpay ID: " + part1;
            model.addAttribute("paymentMethod", paymentMethod);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("myBooking", myBooking);
            model.addAttribute("seatsReserved", reservedSeat);
            model.addAttribute("currentPage", "Hóa đơn");
            model.addAttribute("header", "Đặt vé thành công.");
            return "admin/pages/booking_info";
        } else {
            model.addAttribute("message", "Xảy ra lỗi trong quá trình thanh toán");
            model.addAttribute("header", "Xảy ra lỗi");
            model.addAttribute("currentPage", "Lỗi");
            return "error_message";
        }

    }
}