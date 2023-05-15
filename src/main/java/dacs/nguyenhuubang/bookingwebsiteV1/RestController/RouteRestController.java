package dacs.nguyenhuubang.bookingwebsiteV1.RestController;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Route;
import dacs.nguyenhuubang.bookingwebsiteV1.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteRestController {

    private final RouteService routeService;

    @GetMapping("/search")
    @ResponseBody
    public List<Route> searchUsers(@RequestParam("q") String q) {
        List<Route> routes = routeService.search(q);
        return routes;
    }

}
