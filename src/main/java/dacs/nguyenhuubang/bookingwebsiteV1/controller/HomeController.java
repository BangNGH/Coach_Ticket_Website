package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatReservationRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TripRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping(value = {"", "/", "/home"})
@Controller
public class HomeController {

    private final CityService cityService;
    private final TripRepository tripRepository;
    private final SeatReservationRepository seatReservationRepo;



    @RequestMapping(value = {"", "/"})
    public String home(Model model) {
        List<City> cities = cityService.getCities();
        model.addAttribute("cities",cities);
        return "pages/home_page";
    }
    @RequestMapping(value = {"/find"})
    public String getTrips(Model model, RedirectAttributes re, @RequestParam("startCity") City startCity,
                           @RequestParam("endCity") City endCity, @RequestParam("startTime") LocalDate startTime
                          ) {
        if (startCity.equals(endCity)){
            model.addAttribute("errorMessage", "Start-city and End-city must be difference!");
            return "redirect:/";
        }
        List<Trip> foundTrips = tripRepository.findTripsByCitiesAndStartTime(startCity, endCity);
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

        return "pages/find_trip";
    }


}
