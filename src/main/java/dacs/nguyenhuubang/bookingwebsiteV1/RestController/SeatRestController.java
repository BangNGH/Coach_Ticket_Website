package dacs.nguyenhuubang.bookingwebsiteV1.RestController;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatRestController {

    private final SeatService seatService;

    @GetMapping("/search")
    @ResponseBody
    public List<Seat> searchUsers(@RequestParam("q") String q) {
        List<Seat> seats = seatService.search(q);
        return seats;
    }
}
