package dacs.nguyenhuubang.bookingwebsiteV1.controller.user;

import dacs.nguyenhuubang.bookingwebsiteV1.dto.EditUserInfoRequest;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Provider;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@RequestMapping("/users/manage-account")
@Controller
public class ManageAccountController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/edit/{username}")
    private String editUser(@PathVariable("username") String username, Model model, RedirectAttributes redirectAttributes) {
        try {
            UserEntity user = userService.findbyEmail(username).get();
            if (user.getProvider() != null) {
                if (user.getProvider() == Provider.GITHUB) {
                    model.addAttribute("user", user);
                    model.addAttribute("header", "Cung cấp email");
                    model.addAttribute("currentPage", "Chỉnh sửa tài khoản");
                    model.addAttribute("title", "Chỉnh sửa địa chỉ email của bạn.");
                    model.addAttribute("gbUserName", user.getEmail());
                    return "pages/fill_out_email";
                }
                redirectAttributes.addFlashAttribute("errorMessage", "Đã lấy thông tin từ tài khoản Google.");
                return "redirect:/";
            }
            EditUserInfoRequest userInfoRequest = new EditUserInfoRequest();
            userInfoRequest.setAddress(user.getAddress());
            userInfoRequest.setEmail(user.getEmail());
            userInfoRequest.setConfirmPassword("");
            userInfoRequest.setOldPassword("");
            userInfoRequest.setNewPassword("");
            userInfoRequest.setFullname(user.getFullname());
            model.addAttribute("userInfoRequest", userInfoRequest);
            model.addAttribute("header", "Chỉnh Sửa Tài Khoản");
            model.addAttribute("currentPage", "Chỉnh sửa");
            return "pages/edit_user";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }

    }

    @PostMapping("/save-user")
    private String editUser(@Valid @ModelAttribute("userInfoRequest") EditUserInfoRequest userInfoRequest, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                return "pages/edit_user";
            }
            UserEntity user = userService.findbyEmail(userInfoRequest.getEmail()).get();
            if (!passwordEncoder.matches(userInfoRequest.getOldPassword(), user.getPassword())) {
                model.addAttribute("errorMessage", "Sai mật khẩu");
                return "pages/edit_user";
            }
            if (!userInfoRequest.getConfirmPassword().isBlank() && !userInfoRequest.getNewPassword().isBlank()) {

                if (userInfoRequest.getConfirmPassword().equals(userInfoRequest.getNewPassword())) {
                    user.setPassword(passwordEncoder.encode(userInfoRequest.getNewPassword()));
                } else {
                    model.addAttribute("errorMessage", "Mật khẩu mới nhập không trùng khớp");
                    return "pages/edit_user";
                }
            }
            user.setPassword(user.getPassword());
            user.setAddress(userInfoRequest.getAddress());
            user.setEmail(userInfoRequest.getEmail());
            user.setFullname(userInfoRequest.getFullname());
            userService.save(user);
            model.addAttribute("successMessage", "Chỉnh sửa thông tin thành công");
            return "pages/edit_user";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }

    }
}
