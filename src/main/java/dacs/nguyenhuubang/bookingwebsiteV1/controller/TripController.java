package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Route;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.RouteService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.TripService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.VehiclesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin/trips")
@Controller
public class TripController {
    private final RouteService routeService;
    private final VehiclesService vehiclesService;
    private final TripService tripService;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 10;
        Page<Trip> page = tripService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Trip> trips = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("trips", trips);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "admin/pages/admin_crud_trips";
    }

    @GetMapping("")
    public String getTrips(Model model){
        return findPaginated(1, model, "id", "asc");
    }

    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("pageTitle", "Thêm chuyến");
        List<Route> routes = routeService.getRoutes();
        List<Vehicle> vehicles = vehiclesService.getList();
        if (routes.isEmpty() || vehicles.isEmpty()){
            model.addAttribute("message", "Các khóa ngoại liên quan đã bị xóa");
            return "error_message";
        }
        model.addAttribute("trip", new Trip());
        model.addAttribute("routes", routes);
        model.addAttribute("vehicles", vehicles);
        return "admin/pages/trip_form";
    }
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("trip") Trip trip, BindingResult bindingResult, Model model, RedirectAttributes re) throws IOException {
        if (bindingResult.hasErrors()) {
            List<Route> routes = routeService.getRoutes();
            model.addAttribute("routes", routes);
            List<Vehicle> vehicles = vehiclesService.getList();
            model.addAttribute("vehicles", vehicles);
            return "admin/pages/trip_form";
        }
        tripService.save(trip);
        re.addFlashAttribute("raMessage", "Lưu thành công chuyến đi.");
        return "redirect:/admin/trips";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try{
            Trip trip = tripService.get(id);
            model.addAttribute("trip", trip);
            model.addAttribute("pageTitle", "Sửa chuyến (ID: " + id + ")");
            List<Route> routes = routeService.getRoutes();
            model.addAttribute("routes", routes);
            List<Vehicle> vehicles = vehiclesService.getList();
            model.addAttribute("vehicles", vehicles);
            return "admin/pages/trip_form";
        }catch (ResourceNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/trips";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try{
            tripService.delete(id);
            ra.addFlashAttribute("raMessage", "Chuyến đi (ID: " + id + ") đã bị xóa");
        }catch (ResourceNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }catch (CannotDeleteException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/trips";
    }

}
