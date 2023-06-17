package dacs.nguyenhuubang.bookingwebsiteV1.RestController;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.ShuttleBus;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TransitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transit")
@RequiredArgsConstructor
public class TransitRestController {

    private final TransitionService transitionService;

    @GetMapping("/search")
    @ResponseBody
    public List<ShuttleBus> searchTransit(@RequestParam("q") String q) {
        List<ShuttleBus> cities = transitionService.search(q);
        return cities;
    }

    @GetMapping("/search/transits-today")
    @ResponseBody
    public List<ShuttleBus> searchTransitToday(@RequestParam("q") String q) {
        List<ShuttleBus> cities = transitionService.search(q).stream().filter(i -> i.getBooking().getBookingDate().equals(LocalDate.now())).toList();
        ;
        System.out.println(cities);
        return cities;
    }
}
