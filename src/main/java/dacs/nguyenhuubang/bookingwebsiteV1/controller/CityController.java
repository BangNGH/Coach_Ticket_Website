package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CityNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.CityRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.CityService;
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
@Controller
@RequestMapping("/admin/cities")
public class CityController {

    private final CityRepository cityRepository;
    private final CityService cityService;

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir) {
        int pageSize = 6;
        Page<City> page = cityService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<City> cities = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("cities", cities);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reserseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/pages/admin_crud_cities";
    }

    @GetMapping("")
    public String getCities(Model model){
        return findPaginated(1, model, "id", "asc");
    }


    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("pageTitle", "Thêm thành phố");
        model.addAttribute("city", new City());
        return "admin/pages/city_form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("city") City city, BindingResult bindingResult, RedirectAttributes re, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()) {
            return "admin/pages/city_form";
        }
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        city.setImage_path(fileName);
        City savedCity = cityService.save(city);
        String uploadDir = "./cities-images/" + savedCity.getId();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not save uploaded file " + e.getMessage());
        }
        re.addFlashAttribute("raMessage", "Lưu thành phố thành công.");
        return "redirect:/admin/cities";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try{
            City city = cityService.get(id);
            model.addAttribute("city", city);
            model.addAttribute("pageTitle", "Sửa thành phố (ID: " + id + ")");
            return "admin/pages/city_form";
        }catch (CityNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cities";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try{
            cityService.delete(id);
            ra.addFlashAttribute("raMessage", "Thành phố với (ID: " + id + ") đã bị xóa.");
        }catch (CityNotFoundException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }catch (CannotDeleteException e){
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/cities";
    }
}
