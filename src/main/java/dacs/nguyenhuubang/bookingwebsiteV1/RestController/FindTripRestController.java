package dacs.nguyenhuubang.bookingwebsiteV1.RestController;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.CityService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.SeatReservationService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class FindTripRestController {
    private final BookingService bookingService;
    private final CityService cityService;
    private final TripService tripService;
    private final SeatReservationService seatReservationService;

    @GetMapping("/search/sort")
    public ModelAndView sortTrips(@RequestParam("vehicle") String vehicle, @RequestParam("sortTime") String sSortTime, @RequestParam("sortPrice") String sortPrice, @RequestParam("startCity") String sStartCity,
                                  @RequestParam("endCity") String sEndCity,
                                  @RequestParam("endTime") String endTime, @RequestParam("startTime") String startTime, Model model) {
        City startCity = cityService.findCityByName(sStartCity);
        City endCity = cityService.findCityByName(sEndCity);
        List<Trip> foundTrips = tripService.findTripsByCitiesAndStartTime(startCity, endCity);
        foundTrips.sort(Comparator.comparing(Trip::getStartTime));
        if (!sSortTime.isBlank()) {
            if (sSortTime.equalsIgnoreCase("time03_10"))
                foundTrips = foundTrips.stream()
                        .filter(trip -> trip.getStartTime().isAfter(LocalTime.parse("03:00"))
                                && trip.getStartTime().isBefore(LocalTime.parse("10:00")))
                        .collect(Collectors.toList());
            if (sSortTime.equalsIgnoreCase("time10_16"))
                foundTrips = foundTrips.stream()
                        .filter(trip -> trip.getStartTime().isAfter(LocalTime.parse("10:00"))
                                && trip.getStartTime().isBefore(LocalTime.parse("16:00")))
                        .collect(Collectors.toList());
            if (sSortTime.equalsIgnoreCase("time16_23"))
                foundTrips = foundTrips.stream()
                        .filter(trip -> trip.getStartTime().isAfter(LocalTime.parse("16:00"))
                                && trip.getStartTime().isBefore(LocalTime.parse("23:00")))
                        .collect(Collectors.toList());
        }
        if (!sortPrice.isBlank()) {
            if (sortPrice.equalsIgnoreCase("priceDesc"))
                foundTrips.sort(Comparator.comparing(Trip::getPrice));
            if (sortPrice.equalsIgnoreCase("priceAsc"))
                foundTrips.sort(Comparator.comparing(Trip::getPrice, Collections.reverseOrder()));
        }
        if (!vehicle.isBlank()) {
            String vehicleName = "";
            if (vehicle.equalsIgnoreCase("sixt"))
                vehicleName = "Xe 16 chỗ";
            if (vehicle.equalsIgnoreCase("fourtf"))
                vehicleName = "Xe 45 chỗ";
            if (vehicle.equalsIgnoreCase("lms"))
                vehicleName = "Xe Limousine";

            String finalVehicleName = vehicleName.trim();
            foundTrips = foundTrips.stream().filter(i -> i.getVehicle().getName().trim().equalsIgnoreCase(finalVehicleName)).collect(Collectors.toList());
        }

        Map<Integer, Integer> availableSeatsMap = new HashMap<>();
        Map<Integer, List<Seat>> loadAvailableSeatsMap = new HashMap<>();
        Map<Integer, List<Seat>> loadReservedSeat = new HashMap<>();
        for (Trip trip : foundTrips) {
            int totalSeat = trip.getVehicle().getCapacity();
            int seatReserved = seatReservationService.checkAvailableSeat(trip, LocalDate.parse(startTime));
            List<Seat> seatsAvailable = seatReservationService.listAvailableSeat(trip.getVehicle(), trip, LocalDate.parse(startTime));
            List<Seat> listReservedSeat = seatReservationService.listReservedSeat(trip.getVehicle(), trip, LocalDate.parse(startTime));
            int availableSeats = totalSeat - seatReserved;

            loadAvailableSeatsMap.put(trip.getId(), seatsAvailable);
            availableSeatsMap.put(trip.getId(), availableSeats);
            loadReservedSeat.put(trip.getId(), listReservedSeat);
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
        modelAndView.addObject("listReservedSeat", loadReservedSeat);
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

    @GetMapping("/search-by-destination/sort")
    public ModelAndView sortTripsByDestination(@RequestParam("vehicle") String vehicle, @RequestParam("sortTime") String sSortTime, @RequestParam("sortPrice") String sortPrice,
                                               @RequestParam("endCity") String sEndCity,
                                               @RequestParam("endTime") String endTime, Model model) {
        City endCity = cityService.findCityByName(sEndCity);
        List<Trip> foundTrips = tripService.findTripsByDestination(endCity).stream()
                .filter(trip -> trip.getStartTime().isAfter(LocalTime.now()))
                .sorted(Comparator.comparing(Trip::getStartTime))
                .collect(Collectors.toList());
        System.out.println("foundTrips: " + foundTrips);
        if (!sSortTime.isBlank()) {
            if (sSortTime.equalsIgnoreCase("time03_10"))
                foundTrips = foundTrips.stream()
                        .filter(trip -> trip.getStartTime().isAfter(LocalTime.parse("03:00"))
                                && trip.getStartTime().isBefore(LocalTime.parse("10:00")))
                        .collect(Collectors.toList());
            if (sSortTime.equalsIgnoreCase("time10_16"))
                foundTrips = foundTrips.stream()
                        .filter(trip -> trip.getStartTime().isAfter(LocalTime.parse("10:00"))
                                && trip.getStartTime().isBefore(LocalTime.parse("16:00")))
                        .collect(Collectors.toList());
            if (sSortTime.equalsIgnoreCase("time16_23"))
                foundTrips = foundTrips.stream()
                        .filter(trip -> trip.getStartTime().isAfter(LocalTime.parse("16:00"))
                                && trip.getStartTime().isBefore(LocalTime.parse("23:00")))
                        .collect(Collectors.toList());
        }
        if (!sortPrice.isBlank()) {
            if (sortPrice.equalsIgnoreCase("priceDesc"))
                foundTrips.sort(Comparator.comparing(Trip::getPrice));
            if (sortPrice.equalsIgnoreCase("priceAsc"))
                foundTrips.sort(Comparator.comparing(Trip::getPrice, Collections.reverseOrder()));
        }
        if (!vehicle.isBlank()) {
            String vehicleName = "";
            if (vehicle.equalsIgnoreCase("sixt"))
                vehicleName = "Xe 16 chỗ";
            if (vehicle.equalsIgnoreCase("fourtf"))
                vehicleName = "Xe 45 chỗ";
            if (vehicle.equalsIgnoreCase("lms"))
                vehicleName = "Xe Limousine";

            String finalVehicleName = vehicleName.trim();
            foundTrips = foundTrips.stream().filter(i -> i.getVehicle().getName().trim().equalsIgnoreCase(finalVehicleName)).collect(Collectors.toList());
        }

        Map<Integer, Integer> availableSeatsMap = new HashMap<>();
        Map<Integer, List<Seat>> loadAvailableSeatsMap = new HashMap<>();
        Map<Integer, List<Seat>> loadReservedSeat = new HashMap<>();
        for (Trip trip : foundTrips) {
            int totalSeat = trip.getVehicle().getCapacity();
            int seatReserved = seatReservationService.checkAvailableSeat(trip, LocalDate.now());
            List<Seat> seatsAvailable = seatReservationService.listAvailableSeat(trip.getVehicle(), trip, LocalDate.now());
            List<Seat> listReservedSeat = seatReservationService.listReservedSeat(trip.getVehicle(), trip, LocalDate.now());
            int availableSeats = totalSeat - seatReserved;

            loadAvailableSeatsMap.put(trip.getId(), seatsAvailable);
            availableSeatsMap.put(trip.getId(), availableSeats);
            loadReservedSeat.put(trip.getId(), listReservedSeat);
        }

        ModelAndView modelAndView = new ModelAndView("fragments/find_trip_by_destionation");
        modelAndView.addObject("foundTrips", foundTrips);
        if (foundTrips.isEmpty()) {
            modelAndView.addObject("isListEmpty", true);
        } else {
            modelAndView.addObject("isListEmpty", false);
        }
        modelAndView.addObject("loadAvailableSeatsMap", loadAvailableSeatsMap);
        modelAndView.addObject("availableSeatsMap", availableSeatsMap);
        modelAndView.addObject("listReservedSeat", loadReservedSeat);
        model.addAttribute("header", "Tất cả chuyến đến " + endCity.getName());
        model.addAttribute("currentPage", "Tìm chuyến");
        modelAndView.addObject("endCity", endCity.getName());
        modelAndView.addObject("startTime", LocalDate.now());
        modelAndView.addObject("endTime", endTime);
        return modelAndView;
    }

}
