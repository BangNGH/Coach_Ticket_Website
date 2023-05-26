package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CityNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatReservationRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping(value = {"", "/", "/home"})
@Controller
public class HomeController {
    private final UserService userService;
    private final CityService cityService;
    private final TripService tripService;
    private final BookingService bookingService;
    private final ContactService contactService;
    private final SeatReservationRepository seatReservationRepo;

    @RequestMapping(value = {"", "/"})
    public String home(Model model) {
        List<City> cities = cityService.getCities();
        model.addAttribute("cities", cities);
        List<Booking> bookingList = bookingService.getBookings();
/*        List<String> topDestinations = bookingList.stream()
                .map(booking -> booking.getTrip().getRoute().getEndCity().getName())
                .collect(Collectors.groupingBy(
                        destination -> destination,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());*/
        List<String> topDestinations = new ArrayList<>();
        Map<String, Long> destinationCountMap = new HashMap<>();

        for (Booking booking : bookingList) {
            String destination = booking.getTrip().getRoute().getEndCity().getName();
            destinationCountMap.put(destination, destinationCountMap.getOrDefault(destination, 0L) + 1);
        }

        // Map.Entry là một interface cung cấp các phương thức để truy cập và thao tác với các cặp khóa-giá trị trong một Map.
        //, sau khi sử dụng entrySet() để lấy tập hợp các cặp khóa-giá trị từ Map, chúng ta sử dụng Map.Entry để lưu trữ và thao tác với các cặp này
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(destinationCountMap.entrySet());
        //sắp xếp danh sách theo giá trị giảm dần của các cặp khóa-giá trị.
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        //được sử dụng để lấy giá trị nhỏ nhất giữa hai số.
        int count = Math.min(3, sortedEntries.size());
        for (int i = 0; i < count; i++) {
            String destination = sortedEntries.get(i).getKey();
            topDestinations.add(destination);
        }
        List<City> topDestinationCities = new ArrayList<>();
        for (String cityName : topDestinations
        ) {
            topDestinationCities.add(cityService.getCityByName(cityName));
        }


        //Chuyến đi phổ biến
        List<String> topTrips = new ArrayList<>();
        Map<String, Long> topTripsCountMap = new HashMap<>();

        for (Booking booking : bookingList) {
            String destination = booking.getTrip().getRoute().getName();
            topTripsCountMap.put(destination, topTripsCountMap.getOrDefault(destination, 0L) + 1);
        }

        // Map.Entry là một interface cung cấp các phương thức để truy cập và thao tác với các cặp khóa-giá trị trong một Map.
        //, sau khi sử dụng entrySet() để lấy tập hợp các cặp khóa-giá trị từ Map, chúng ta sử dụng Map.Entry để lưu trữ và thao tác với các cặp này
        List<Map.Entry<String, Long>> topTripsSortedEntries = new ArrayList<>(topTripsCountMap.entrySet());
        //sắp xếp danh sách theo giá trị giảm dần của các cặp khóa-giá trị.
        topTripsSortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        //được sử dụng để lấy giá trị nhỏ nhất giữa hai số.
        int topTripsCount = Math.min(4, topTripsSortedEntries.size());
        for (int i = 0; i < count; i++) {
            String destination = topTripsSortedEntries.get(i).getKey();
            topTrips.add(destination);
        }
        List<Trip> topTripList = new ArrayList<>();
        for (String routeName : topTrips
        ) {
            Trip trip = tripService.getTripByRouteName(routeName);
            Boolean isExists = topTripList.stream().anyMatch(i -> i != trip);
            topTripList.add(trip);
        }
        System.out.println(topTripList.size());
        System.out.println(topTripList.get(0).getRoute().getName());
        model.addAttribute("topDestinationCities", topDestinationCities);
        model.addAttribute("topTripList", topTripList);

        return "pages/home_page";
    }

    @RequestMapping(value = {"/about"})
    public String aboutMe(Model model) {
        model.addAttribute("header", "Về chúng tôi");
        model.addAttribute("currentPage", "Giới thiêu");
        model.addAttribute("contact", new Contact());
        return "about";
    }

    @PostMapping("/submit_contact")
    public String save(@Valid @ModelAttribute("contact") Contact city, BindingResult bindingResult, RedirectAttributes re, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                re.addFlashAttribute("errorMessage", "Vui lòng điền các dữ liệu hợp lệ");
                return "redirect:/home";
            }
            contactService.save(city);
            re.addFlashAttribute("successMessage", "Thư của bạn đã được gửi. Chúng tôi sẽ liên hệ sớm nhất");
            return "redirect:/home";
        } catch (Exception e) {
            re.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/home";
        }
    }

    @PostMapping("/save-email")
    private String saveEmail(@ModelAttribute("user") UserEntity user, @RequestParam("gbUserName") String gbUserName, Model model) {
        Optional<UserEntity> existsUser = userService.findByGithubUserName(user.getAddress());
        if (existsUser != null) {
            model.addAttribute("errorMessage", "Email này đã đuợc đăng ký rồi");
            return "pages/fill_out_email";
        }
        user.setProvider(Provider.GITHUB);
        user.setRole("USER");
        System.out.println("GITHUB username:" + gbUserName);
        user.setEnabled(true);
        userService.save(user);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        // Thiết lập Authentication mới cho SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        System.out.println("User github: " + user);
        return "success_message";
    }

    @RequestMapping(value = {"/find"})
    public String getTrips(Model model, RedirectAttributes re, @RequestParam("startCity") City startCity,
                           @RequestParam("endCity") City endCity, @RequestParam("startTime") LocalDate startTime,
                           @RequestParam(value = "endTime", required = false) LocalDate endTime) {
        if (startCity == endCity) {
            re.addFlashAttribute("errorMessage", "Vui lòng chọn thành phố khác nhau");
            return "redirect:/";
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

            return "pages/find_trip";
        } catch (RuntimeException e) {
            re.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/find-destination-city/{id}")
    private String findDestination(@PathVariable("id") Integer cityId, RedirectAttributes re, Model model) {
        try {
            City endCity = cityService.get(cityId);
            List<Trip> foundTrips = tripService.findTripsByDestination(endCity);
            Map<Integer, Integer> availableSeatsMap = new HashMap<>();
            Map<Integer, List<Seat>> loadAvailableSeatsMap = new HashMap<>();
            for (Trip trip : foundTrips) {
                int totalSeat = trip.getVehicle().getCapacity();
                int seatReserved = seatReservationRepo.checkAvailableSeat(trip, LocalDate.now());
                List<Seat> seatsAvailable = seatReservationRepo.listAvailableSeat(trip.getVehicle(), trip, LocalDate.now());
                int availableSeats = totalSeat - seatReserved;

                loadAvailableSeatsMap.put(trip.getId(), seatsAvailable);
                availableSeatsMap.put(trip.getId(), availableSeats);
            }

            model.addAttribute("foundTrips", foundTrips);
            model.addAttribute("loadAvailableSeatsMap", loadAvailableSeatsMap);
            model.addAttribute("availableSeatsMap", availableSeatsMap);
            model.addAttribute("header", "Tất cả chuyến đến " + endCity.getName());
            model.addAttribute("currentPage", "Tìm chuyến");
            model.addAttribute("startTime", LocalDate.now());
            model.addAttribute("startCity", " ");
            model.addAttribute("endCity", endCity.getName());
            model.addAttribute("endTime", "");

            return "pages/find_trip";
        } catch (CityNotFoundException e) {
            re.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/booking-popular-trip/{id}")
    private String findPopularTrip(@PathVariable("id") Integer tripId, RedirectAttributes re, Model model) {
        try {
            Trip foundTrip = tripService.get(tripId);
            City startCity = foundTrip.getRoute().getStartCity();
            City endCity = foundTrip.getRoute().getEndCity();

            List<Trip> foundTrips = tripService.findTripsByCitiesAndStartTime(startCity, endCity);
            Map<Integer, Integer> availableSeatsMap = new HashMap<>();
            Map<Integer, List<Seat>> loadAvailableSeatsMap = new HashMap<>();
            for (Trip trip : foundTrips) {
                int totalSeat = trip.getVehicle().getCapacity();
                int seatReserved = seatReservationRepo.checkAvailableSeat(trip, LocalDate.now());
                List<Seat> seatsAvailable = seatReservationRepo.listAvailableSeat(trip.getVehicle(), trip, LocalDate.now());
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
            model.addAttribute("startTime", LocalDate.now());
            model.addAttribute("endTime", "");

            return "pages/find_trip";
        } catch (CityNotFoundException e) {
            re.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
    }
}
