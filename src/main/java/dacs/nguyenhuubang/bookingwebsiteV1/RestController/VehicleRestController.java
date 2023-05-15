package dacs.nguyenhuubang.bookingwebsiteV1.RestController;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import dacs.nguyenhuubang.bookingwebsiteV1.service.VehiclesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleRestController {

    private final VehiclesService vehiclesService;

    @GetMapping("/search")
    @ResponseBody
    public List<Vehicle> searchUsers(@RequestParam("q") String q) {
        List<Vehicle> vehicles = vehiclesService.search(q);
        return vehicles;
    }

}
