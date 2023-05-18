package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import java.util.List;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.UserNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.UserRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Controller
public class UserController {

	private final UserService userService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable(value = "pageNo" )int pageNo, Model model){
		int pageSize = 5;
		Page<UserEntity> page = userService.findPaginated(pageNo, pageSize);
		List<UserEntity> users = page.getContent();
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("users", users);
		return "admin/pages/admin_crud_users";
	}

	@GetMapping("")
	public String getUsers(Model model){
		return findPaginated(1, model);
	}

	@GetMapping("/new")
	public String showCreateForm(Model model){
		model.addAttribute("pageTitle", "Create New User");
		model.addAttribute("user", new UserEntity());
		return "admin/pages/user_form";
	}

	@PostMapping("/save")
	public String saveUser(@Valid @ModelAttribute("user") UserEntity user, BindingResult bindingResult, RedirectAttributes ra){
		if (bindingResult.hasErrors()) {
			return "admin/pages/user_form";
		}
		try {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userService.save(user);
			ra.addFlashAttribute("raMessage", "The user has been saved successfully.");
		}catch (DataIntegrityViolationException e){
			ra.addFlashAttribute("errorMessage","User with email " + user.getEmail() + " already exists "+e.getMessage());
		}
		return "redirect:/admin/users";
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
		try{
			UserEntity user = userService.get(id);
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User (ID: "+id+")");
			System.out.println(user.getPassword());
			return "admin/pages/user_form";
		}catch (UserNotFoundException e){
			ra.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/admin/users";
		}
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
		try{
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String currentUserName = authentication.getName();

			UserEntity deleteUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found"));
			if (deleteUser.getEmail().equals(currentUserName)) {
				ra.addFlashAttribute("errorMessage", "Cannot delete current user (ID: "+id+")");
			} else {
				userService.delete(id);
				ra.addFlashAttribute("errorMessage", "The User (ID: "+id+") has been deleted");
			}
		}catch (UserNotFoundException e){
			ra.addFlashAttribute("errorMessage", e.getMessage());
		}catch (CannotDeleteException e){
			ra.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/users";
	}


}