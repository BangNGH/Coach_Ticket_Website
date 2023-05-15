package dacs.nguyenhuubang.bookingwebsiteV1.RestController;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CitiesRestController {

    private final CityService cityService;

    @GetMapping("/search")
    @ResponseBody
    public List<City> searchUsers(@RequestParam("q") String q) {
        List<City> cities = cityService.search(q);
        return cities;
    }

}
