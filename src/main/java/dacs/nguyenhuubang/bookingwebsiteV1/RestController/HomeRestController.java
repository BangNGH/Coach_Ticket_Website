package dacs.nguyenhuubang.bookingwebsiteV1.RestController;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatReservationRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.CityService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeRestController {
    private final BookingService bookingService;
    private final CityService cityService;
    private final TripService tripService;
    private final SeatReservationRepository seatReservationRepo;
    private final TemplateEngine templateEngine;


    @GetMapping("/search")
    public ModelAndView searchTrips(@RequestParam("startCity") String sStartCity,
                                    @RequestParam("endCity") String sEndCity,
                                    @RequestParam("endTime") String endTime, @RequestParam("startTime") String startTime, @RequestParam("keyword") String keyword, Model model) {
        City startCity = cityService.findCityByName(sStartCity);
        City endCity = cityService.findCityByName(sEndCity);
        System.out.println(startCity.getName() + " " + endCity.getName());
        List<Trip> foundTripsWithoutSearch = tripService.findTripsByCitiesAndStartTime(startCity, endCity);
        List<Trip> foundTrips = searchTrips(foundTripsWithoutSearch, keyword);
        System.out.println(foundTrips);
        Map<Integer, Integer> availableSeatsMap = new HashMap<>();
        Map<Integer, List<Seat>> loadAvailableSeatsMap = new HashMap<>();
        for (Trip trip : foundTrips) {
            int totalSeat = trip.getVehicle().getCapacity();
            int seatReserved = seatReservationRepo.checkAvailableSeat(trip, LocalDate.parse(startTime));
            List<Seat> seatsAvailable = seatReservationRepo.listAvailableSeat(trip.getVehicle(), trip, LocalDate.parse(startTime));
            int availableSeats = totalSeat - seatReserved;

            loadAvailableSeatsMap.put(trip.getId(), seatsAvailable);
            availableSeatsMap.put(trip.getId(), availableSeats);
        }

        ModelAndView modelAndView = new ModelAndView("fragments/find_trip");
        modelAndView.addObject("foundTrips", foundTrips);
        if (foundTrips.isEmpty()) {
            modelAndView.addObject("isListEmpty", true);
        } else {
            modelAndView.addObject("isListEmpty", false);
        }
        modelAndView.addObject("loadAvailableSeatsMap", loadAvailableSeatsMap);
        modelAndView.addObject("availableSeatsMap", availableSeatsMap);
        modelAndView.addObject("header", "Tìm chuyến");
        modelAndView.addObject("currentPage", "Tìm chuyến");
        modelAndView.addObject("startCity", startCity.getName());
        modelAndView.addObject("endCity", endCity.getName());
        modelAndView.addObject("startTime", startTime);
        modelAndView.addObject("endTime", endTime);
        return modelAndView;
    }

    public List<Trip> searchTrips(List<Trip> trips, String searchParameter) {
        List<Trip> matchingTrips = new ArrayList<>();

        for (Trip trip : trips) {
            // Kiểm tra các trường trong đối tượng "Trip" với tham số truyền vào
            if (trip.getStartTime().toString().contains(searchParameter) || trip.getVehicle().getName().contains(searchParameter) || trip.getPrice().toString().contains(searchParameter)) {
                matchingTrips.add(trip);
            }
        }
        return matchingTrips;
    }


}
