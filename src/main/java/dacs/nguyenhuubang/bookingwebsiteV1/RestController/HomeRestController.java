package dacs.nguyenhuubang.bookingwebsiteV1.RestController;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.CityRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TripRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.CityService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeRestController {
    private final BookingService bookingService;
    private final CityService cityService;
    private final TripService tripService;
    private final TripRepository tripRepository;
    private final CityRepository cityRepository;

/*    @PostMapping("/find")
    public List<Trip> findTrips(@RequestParam(value = "round-trip", required = false) Boolean roundTrip,@RequestParam("startCity") int startCityId,
                                @RequestParam("endCity") int endCityId,
                                @RequestParam("startTime") LocalDate startTime,
                                @RequestParam(value = "endTime", required = false) LocalDate endTime) {

        System.out.println(roundTrip);
        // Thực hiện tìm kiếm chuyến xe trong TripRepository dựa trên các tham số đầu vào
        // Trả về danh sách các chuyến xe tương ứng
        City startCity = cityRepository.findById(startCityId).get(); // Tạo đối tượng City từ startCityId
        City endCity = cityRepository.findById(endCityId).get(); // Tạo đối tượng City từ endCityId

        if (roundTrip != null && roundTrip) {
            // Trường hợp có ngày về (round-trip)
            return tripRepository.findTripsByCitiesAndTime(startCity, endCity, startTime, endTime);
        } else {
            // Trường hợp không có ngày về (one-way)
            return tripRepository.findTripsByCitiesAndStartTime(startCity, endCity, startTime);
        }
    }*/

}
