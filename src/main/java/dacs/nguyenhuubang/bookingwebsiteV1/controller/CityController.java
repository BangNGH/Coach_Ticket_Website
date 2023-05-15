package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import java.util.List;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CityNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.CityRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/cities")
public class CityController {

	private final CityRepository cityRepository;
	private final CityService cityService;

	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable(value = "pageNo" )int pageNo, Model model){
		int pageSize = 5;
		Page<City> page = cityService.findPaginated(pageNo, pageSize);
		List<City> cities = page.getContent();
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("cities", cities);
		return "admin/pages/admin_crud_cities";
	}

	@GetMapping("")
	public String getCities(Model model){
/*		List<City> cities = cityService.getCities();
		model.addAttribute("cities", cities);*/
		return findPaginated(1, model);
	}


	@GetMapping("/new")
	public String showCreateForm(Model model){
		model.addAttribute("pageTitle", "Create New City");
		model.addAttribute("city", new City());
		return "admin/pages/city_form";
	}

	@PostMapping("/save")
	public String save(@Valid City city, BindingResult bindingResult, RedirectAttributes re){
		if (bindingResult.hasErrors()) {
			return "admin/pages/city_form";
		}
		City existingCity = cityRepository.findById(city.getId()).orElse(null);
		if (existingCity != null) {
			// Update the existing city
			existingCity.setName(city.getName());
			cityService.save(existingCity);
		} else {
			// Save the new city
			cityService.save(city);
		}
		re.addFlashAttribute("raMessage", "The city has been saved successfully.");
		return "redirect:/admin/cities";
	}


	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
		try{
			City city = cityService.get(id);
			model.addAttribute("city", city);
			model.addAttribute("pageTitle", "Edit User (ID: "+id+")");
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
			ra.addFlashAttribute("raMessage", "The City (ID: "+id+") has been deleted");
		}catch (CityNotFoundException e){
			ra.addFlashAttribute("errorMessage", e.getMessage());
		}catch (CannotDeleteException e){
			ra.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/cities";
	}


/*	@GetMapping("/search-city")
	public String search(Model model) {
		List<City> cites = cityRepository.findAll();
		model.addAttribute("cites", cites);
		return "city/SearchCity";
	}*/


}
