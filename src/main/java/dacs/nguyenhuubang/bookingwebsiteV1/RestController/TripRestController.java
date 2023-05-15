package dacs.nguyenhuubang.bookingwebsiteV1.RestController;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripRestController {

    private final TripService tripService;

    @GetMapping("/search")
    @ResponseBody
    public List<Trip> searchUsers(@RequestParam("q") String q) {
        List<Trip> trips = tripService.search(q);
        return trips;
    }
}
