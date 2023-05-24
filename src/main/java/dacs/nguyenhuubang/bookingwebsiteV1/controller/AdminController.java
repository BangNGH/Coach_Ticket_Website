package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.SeatHasBeenReseredException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatReservationRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class AdminController {
    private final BookingDetailsService bookingDetailsService;
    private final BookingService bookingService;
    private final CityService cityService;
    private final TripService tripService;
    private final SeatReservationRepository seatReservationRepo;
    private final UserService userService;
    private final SeatService seatService;
    private final SeatReservationService seatReservationService;

    @GetMapping("/bill")
    public String showBill(Model model) {
        return findPageBill(1, model, "id", "asc");
    }

    @GetMapping("/print-tickets/{id}")
    public String printTicket(@PathVariable("id") Integer id, RedirectAttributes ra) {
        ra.addFlashAttribute("successMessage", "Thành công vé ID:" + id);
        return "redirect:/admin";
    }

    @GetMapping("/bill-page/page/{pageNo}")
    public String findPageBill(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 6;

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
    public String adminHomePage(Model model) {
        List<BookingDetails> bookingDetailsList = bookingDetailsService.getBookings();
        List<Booking> bookings = bookingService.getBookings();
        List<City> cities = cityService.getCities();
        model.addAttribute("cities", cities);
        Float revenue = 0.0F;
        revenue = bookingDetailsList
                .stream()
                .collect(Collectors.summingDouble(BookingDetails::getTotalPrice))
                .floatValue();

        //Chart doanh thu theo tháng
        Map<YearMonth, Double> revenueByMonth = bookingDetailsList.stream()
                .collect(Collectors.groupingBy(
                        bookingDetails -> YearMonth.from(bookingDetails.getBooking().getBookingDate()),
                        TreeMap::new, // tự động sắp xếp các entry theo thứ tự của khóa (YearMonth).
                        Collectors.summingDouble(BookingDetails::getTotalPrice)
                ));
        System.out.println(revenueByMonth);

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
        Long numberOfReceipt = numberOfBookings - numberOfBill;

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
            List<Trip> foundTrips = tripService.findTripsByCitiesAndStartTime(startCity, endCity);
            Map<Integer, Integer> availableSeatsMap = new HashMap<>();
            Map<Integer, List<Seat>> loadAvailableSeatsMap = new HashMap<>();
            for (Trip trip : foundTrips) {
                int totalSeat = trip.getVehicle().getCapacity();
                int seatReserved = seatReservationRepo.checkAvailableSeat(trip, startTime);
                List<Seat> seatsAvailable = seatReservationRepo.listAvailableSeat(trip.getVehicle(), trip, startTime);
                int availableSeats = totalSeat - seatReserved;

                loadAvailableSeatsMap.put(trip.getId(), seatsAvailable);
                availableSeatsMap.put(trip.getId(), availableSeats);
            }

            model.addAttribute("foundTrips", foundTrips);
            model.addAttribute("loadAvailableSeatsMap", loadAvailableSeatsMap);
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
    public String bookRoundTrip(Principal p, Model model, @RequestParam(value = "bookedId", required = false) Integer bookedId, @RequestParam("startTime") LocalDate startTime, @RequestParam("selectedTripId") Integer selectedTripId,
                                @RequestParam("inputSelectedSeats") String inputSelectedSeats, RedirectAttributes re, @RequestParam(value = "endTime", required = false) LocalDate endTime) {

        try {
            String validEmail = p.getName();
            UserEntity checkUser = userService.findbyEmail(validEmail).get();
            if (!isValidEmail(validEmail) && !isValidEmail(checkUser.getAddress())) {
                model.addAttribute("user", checkUser);
                model.addAttribute("gbUserName", checkUser.getEmail());

                return "pages/fill_out_email";
            }
            System.out.println("User" + checkUser);
            System.out.println("endTime" + endTime);
            if (endTime != null) {
                List<Long> seatIds = new ArrayList<>();
                String[] seatIdArray = inputSelectedSeats.split(",");
                for (String seatId : seatIdArray) {
                    seatIds.add(Long.valueOf(seatId));
                }
                LocalTime now = LocalTime.now();
                Trip trip = tripService.get(selectedTripId);
                if (trip.getStartTime().compareTo(now) <= 0) {
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
                for (Trip trip2 : foundTrips) {
                    int totalSeat = trip2.getVehicle().getCapacity();
                    int seatReserved = seatReservationService.checkAvailableSeat(trip2, endTime);
                    List<Seat> seatsAvailable = seatReservationService.listAvailableSeat(trip2.getVehicle(), trip2, endTime);
                    int availableSeats = totalSeat - seatReserved;

                    loadAvailableSeatsMap.put(trip2.getId(), seatsAvailable);
                    availableSeatsMap.put(trip2.getId(), availableSeats);
                }

                model.addAttribute("foundTrips", foundTrips);
                model.addAttribute("bookedId", savedBooking.getId());
                model.addAttribute("loadAvailableSeatsMap", loadAvailableSeatsMap);
                model.addAttribute("availableSeatsMap", availableSeatsMap);
                model.addAttribute("header", "Tìm chuyến về");
                model.addAttribute("currentPage", "Tìm chuyến");
                model.addAttribute("startCity", trip.getRoute().getEndCity().getName());
                model.addAttribute("endCity", trip.getRoute().getStartCity().getName());
                model.addAttribute("startTime", endTime);
                return "admin/pages/find_trip";
            } else {
                return bookTrip(p, bookedId, model, startTime, selectedTripId, inputSelectedSeats, re);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            re.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin";
        }
    }

    @PostMapping("/book")
    @Transactional(rollbackFor = {Exception.class, Throwable.class, SeatHasBeenReseredException.class})
    public String bookTrip(Principal p, Integer bookedId, Model model, @RequestParam("startTime") LocalDate startTime, @RequestParam("selectedTripId") Integer selectedTripId,
                           @RequestParam("inputSelectedSeats") String inputSelectedSeats, RedirectAttributes re) {
        System.out.println("Booked" + bookedId);
        String validEmail = p.getName();
        UserEntity checkUser = userService.findbyEmail(validEmail).get();
        if (!isValidEmail(validEmail) && !isValidEmail(checkUser.getAddress())) {
            model.addAttribute("user", checkUser);
            model.addAttribute("gbUserName", checkUser.getEmail());
            return "pages/fill_out_email";
        }
        System.out.println("Check2" + checkUser);
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
            Trip trip = tripService.get(selectedTripId);
            if (trip.getStartTime().compareTo(now) <= 0) {
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

            if (bookedId != null) {
                Booking roundTrip = bookingService.get(bookedId);
                model.addAttribute("roundTrip", roundTrip);
                model.addAttribute("hasRoundTrip", true);
            }


            if (!seatsReserved.isEmpty()) {

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
                Float roundTripPrice = 0.0F;
                if (bookedId != null) {
                    System.out.println("Có vô");
                    Booking roundTrip = bookingService.get(bookedId);
                    roundTripPrice = roundTrip.getBookingDetails().get(0).getTotalPrice();
                }
                String totalPrice = String.valueOf(savedBookingDetails.getTotalPrice() + roundTripPrice);
                String sub_totalPrice = totalPrice.substring(0, totalPrice.length() - 2);

                model.addAttribute("bookingTransit", true);
                String paymentMethod = "Thanh toán tiền mặt ID: " + savedBooking.getId();
                model.addAttribute("paymentMethod", paymentMethod);
                model.addAttribute("totalPrice", sub_totalPrice);
                Booking myBooking = bookingService.get(savedBooking.getId());
                model.addAttribute("myBooking", myBooking);
                model.addAttribute("seatsReserved", seatsReserved);
                System.out.println("My booking" + myBooking);
                if (bookedId != null) {
                    Booking booking2 = bookingService.get(bookedId);
                    List<Seat> reservedSeat2 = seatReservationService.getReservedSeat(booking2);
                    model.addAttribute("myBooking2", booking2);
                    model.addAttribute("seatsReserved2", reservedSeat2);
                    model.addAttribute("hasRoundTrip", true);
                    model.addAttribute("bookingTransit", false);
                }
                return "admin/pages/booking_info";
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
        }
    }
}