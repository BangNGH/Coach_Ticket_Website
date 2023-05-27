package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Route;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.RouteNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.CityService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin/routes")
@Controller
public class RouteController {
    private final RouteService routeService;
    private final CityService cityService;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {

        int pageSize = 6;
        Page<Route> page = routeService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Route> routes = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("routes", routes);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "admin/pages/admin_crud_routes";
    }

    @GetMapping("")
    public String getRoutes(Model model){
        return findPaginated(1, model, "id", "asc");
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "Create New");
        model.addAttribute("route", new Route());
        List<City> cities = cityService.getCities();
        model.addAttribute("cities", cities);
        if (cities.isEmpty()){
            model.addAttribute("message", "Danh sách rỗng hoặc các khóa ngoại đã bị xóa");
            return "error_message";
        }
        return "admin/pages/route_form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("route") Route route, BindingResult bindingResult, Model model, RedirectAttributes re, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()) {
            List<City> cities = cityService.getCities();
            model.addAttribute("cities", cities);
            return "admin/pages/route_form";
        }
        if (route.getEndCity().getName().equals(route.getStartCity().getName())){
            re.addFlashAttribute("errorMessage", "Start-city and End-city must be difference!");
            return "redirect:/admin/routes";
        }

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        route.setImage_path(fileName);
        Route savedRoute = routeService.save(route);
        String uploadDir = "./route-images/" + savedRoute.getId();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()){

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not save uploaded file " + e.getMessage());
        }
        re.addFlashAttribute("raMessage", "The route has been saved successfully.");
        return "redirect:/admin/routes";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Route route = routeService.get(id);
            model.addAttribute("route", route);
            model.addAttribute("pageTitle", "Edit Route (ID: " + id + ")");
            List<City> cities = cityService.getCities();
            model.addAttribute("cities", cities);
            return "admin/pages/route_form";
        } catch (RouteNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/routes";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            routeService.delete(id);
            ra.addFlashAttribute("raMessage", "The route (ID: " + id + ") has been deleted");
        } catch (RouteNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }catch (CannotDeleteException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/routes";
    }


}
