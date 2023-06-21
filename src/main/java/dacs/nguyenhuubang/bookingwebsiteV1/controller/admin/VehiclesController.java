package dacs.nguyenhuubang.bookingwebsiteV1.controller.admin;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.VehicleNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.VehiclesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
@Controller
@RequestMapping("/admin/vehicles")
public class VehiclesController {
    private final VehiclesService vehiclesService;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 5;
        Page<Vehicle> page = vehiclesService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Vehicle> vehicles = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("vehicles", vehicles);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "admin/pages/admin_crud_vehicles";
    }

    @GetMapping("")
    public String getVehicles(Model model){
        return findPaginated(1, model, "id", "asc");
    }


    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("pageTitle", "Thêm mới xe");
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
            ra.addFlashAttribute("raMessage", "Xe đã được lưu thành công.");
        }catch (DataIntegrityViolationException e){
            ra.addFlashAttribute("errorMessage", "Biển số xe " + vehicle.getLicensePlates() + " đã được đăng ký " + e.getMessage());
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
            model.addAttribute("pageTitle", "Sửa xe với (ID: " + id + ")");
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
            ra.addFlashAttribute("raMessage", "Xe với (ID: " + id + ") đã bị xóa.");
        }catch (VehicleNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }catch (CannotDeleteException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

}
