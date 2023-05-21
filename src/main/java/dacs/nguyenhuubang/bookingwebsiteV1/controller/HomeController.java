package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatReservationRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TripRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.security.Security;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping(value = {"", "/", "/home"})
@Controller
public class HomeController {
    private final UserService userService;
    private final CityService cityService;
    private final TripService tripService;
    private final SeatReservationRepository seatReservationRepo;

    @RequestMapping(value = {"", "/"})
    public String home(Model model){
        List<City> cities = cityService.getCities();
        model.addAttribute("cities", cities);
        return "pages/home_page";
    }
    @PostMapping("/save-email")
    private String saveEmail(@ModelAttribute("user")UserEntity user, Model model){
        Optional<UserEntity> existsUser = userService.findbyEmail(user.getEmail());
        if (existsUser!=null)
        {
            model.addAttribute("errorMessage", "Email này đã đuợc đăng ký rồi");
            return "pages/fill_out_email";
        }
        user.setProvider(Provider.GITHUB);
        user.setRole("USER");
        user.setEnabled(true);
        userService.save(user);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        // Thiết lập Authentication mới cho SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        return "success_message";
    }
    @RequestMapping(value = {"/find"})
    public String getTrips(Model model, RedirectAttributes re, @RequestParam("startCity") City startCity,
                           @RequestParam("endCity") City endCity, @RequestParam("startTime") LocalDate startTime,
                           @RequestParam(value = "endTime", required = false) LocalDate endTime) {
        if (startCity==endCity){
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
            re.addFlashAttribute("errorMessage", "Không tìm thấy ghế ngồi");
            return "redirect:/";
        }
    }


}
