package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Provider;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.event.ResetPasswordEvent;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.UserNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class ForgotPassword {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final HttpServletRequest servletRequest;

    @GetMapping("/login")
    public String login(Model model) {
        String url = applicationUrl(servletRequest) + "/home";
        model.addAttribute("url", url);
        return "auth-login";
    }

    @PostMapping("/process-forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            Optional<UserEntity> foundUser = userService.findbyEmail(email);
            if (foundUser == null) {
                model.addAttribute("error", "Không tìm thấy người dùng với email: " + email);
                return "auth-forgot-password";
            }
            if (foundUser.get().getProvider() != null) {
                if (foundUser.get().getProvider() == Provider.GITHUB) {
                    model.addAttribute("error", "Bạn đã đăng nhập bằng tài khoản Github với email này.");
                    return "auth-forgot-password";
                }
                if (foundUser.get().getProvider() == Provider.GOOGLE) {
                    model.addAttribute("error", "Bạn đã đăng nhập bằng tài khoản Google với email này.");
                    return "auth-forgot-password";
                }
            }
            publisher.publishEvent(new ResetPasswordEvent(foundUser.get(), applicationUrl(servletRequest)));
            model.addAttribute("success", "Gửi yêu cầu thành công, hãy kiểm tra email của bạn.");
        } catch (UserNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "auth-forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {

        UserEntity user = userService.getResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("error", "Có lỗi xảy ra hoặc đường dẫn đã hết hạn.");
            return "auth-forgot-password";
        }
        model.addAttribute("token", token);
        return "reset_password_form";
    }

    @PostMapping("/change-password")
    public String changePassword(final HttpServletRequest request, Model model, RedirectAttributes re) {
        try {
            String newPassword = request.getParameter("newPassword");
            String token = request.getParameter("token");
            String confirmPassword = request.getParameter("confirmPassword");
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Mật khẩu xác nhận không trùng khớp!");
                return "reset_password_form";
            }
            UserEntity foundUser = userService.getResetPasswordToken(token);
            userService.updatePassword(foundUser, newPassword);
            re.addFlashAttribute("success", "Đổi mật khẩu thành công.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "reset_password_form";
        }
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
