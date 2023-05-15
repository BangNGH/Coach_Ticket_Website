package dacs.nguyenhuubang.bookingwebsiteV1.controller;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CityNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.UserNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.VehicleRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.security.UserService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.CityService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.VehiclesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/vehicles")
public class VehiclesController {
    private final VehicleRepository vehicleRepository;
    private final VehiclesService vehiclesService;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo" )int pageNo, Model model){
        int pageSize = 5;
        Page<Vehicle> page = vehiclesService.findPaginated(pageNo, pageSize);
        List<Vehicle> vehicles = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("vehicles", vehicles);
        return "admin/pages/admin_crud_vehicles";
    }

    @GetMapping("")
    public String getVehicles(Model model){
        return findPaginated(1, model);
    }


    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("pageTitle", "Create New");
        model.addAttribute("vehicle", new Vehicle());
        return "admin/pages/vehicle_form";
    }

    @PostMapping("/save")
    public String save(@Valid Vehicle vehicle, BindingResult bindingResult, RedirectAttributes ra, @RequestParam("file") MultipartFile multipartFile){
        if (bindingResult.hasErrors()) {
            return "admin/pages/vehicle_form";
        }
        try{
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            vehicle.setImage_path(fileName);
            Vehicle savedVehicle= vehiclesService.save(vehicle);
            String uploadDir = "./vehicle-images/" + savedVehicle.getId();
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
            ra.addFlashAttribute("raMessage", "The vehicle has been saved successfully.");
        }catch (DataIntegrityViolationException e){
            ra.addFlashAttribute("errorMessage","License Plates " + vehicle.getLicensePlates() + " already exists "+e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/admin/vehicles";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try{
            Vehicle vehicle = vehiclesService.get(id);
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("pageTitle", "Edit vehicle (ID: "+id+")");
            return "admin/pages/vehicle_form";
        }catch (VehicleNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/vehicles";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try{
            vehiclesService.delete(id);
            ra.addFlashAttribute("raMessage", "The vehicle (ID: "+id+") has been deleted");
        }catch (VehicleNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }catch (CannotDeleteException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

}
