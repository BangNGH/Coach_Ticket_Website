package dacs.nguyenhuubang.bookingwebsiteV1.controller;

import dacs.nguyenhuubang.bookingwebsiteV1.dto.RegistrationRequest;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.event.RegistrationCompleteEvent;
import dacs.nguyenhuubang.bookingwebsiteV1.event.listener.RegistrationCompleteEventListener;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.UserAlreadyExistsException;
import dacs.nguyenhuubang.bookingwebsiteV1.registration.token.VerificationToken;
import dacs.nguyenhuubang.bookingwebsiteV1.registration.token.VerificationTokenRepository;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository tokenRepository;
    private final RegistrationCompleteEventListener eventListener;
    private final HttpServletRequest servletRequest;

    @GetMapping("")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest("USER","", "", "", ""));
        return "auth-register";
    }

    @PostMapping("/process_register")
    public String processRegister(@Valid @ModelAttribute("registrationRequest") RegistrationRequest registrationRequest, BindingResult bindingResult, final HttpServletRequest request, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth-register";
        }
        try {
            registrationRequest = new RegistrationRequest("USER", registrationRequest.getPassword(),
                    registrationRequest.getFullname(), registrationRequest.getAddress(),
                    registrationRequest.getEmail());
            UserEntity user = userService.registerUser(registrationRequest);
            //publish registration event
            publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
            model.addAttribute("email", user.getEmail());
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth-register";
        }
        return "registerResult";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token, Model model) {

        String url = applicationUrl(servletRequest)+"/register/resend-verification-token?token="+token;
        VerificationToken theToken = tokenRepository.findByToken(token);
        if (theToken.getUser().isEnabled()) {
            model.addAttribute("title", "Thông báo!");
            model.addAttribute("message", "Tài khoản của bạn đã được kích hoạt, xin hãy đăng nhập.");
        } else {
            String verificationResult = userService.validateToken(token);
            if (verificationResult.equalsIgnoreCase("Valid")) {
                model.addAttribute("title", "Thành công.");
                model.addAttribute("message", "Xác nhận tài khoản thành công. Bạn đã có thể đăng nhập");
            } else {
                model.addAttribute("title", "Lỗi");
                model.addAttribute("message", "Đường dẫn này đã hết hạn, hãy<a href=\"" + url + "\">lấy đường dẫn mới </a>để kích hoạt tài khoản của bạn!");
            }
        }
        return "registerVerifyResult";
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @GetMapping("/resend-verification-token")
    public String resendVerificationToken(@RequestParam("token") String oldToken, final HttpServletRequest request, Model model) throws MessagingException, UnsupportedEncodingException {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        UserEntity theUser = verificationToken.getUser();
        resendVerificationTokenEmail(theUser, applicationUrl(request), verificationToken);
        model.addAttribute("message", "Đường dẫn mới đã được gửi đến email của bạn và sẽ hết hạn trong vòng 15 phút, hãy kiểm tra đường dẫn để kích hoạt tài khoản.");
        return "registerVerifyResult";
    }

    private void resendVerificationTokenEmail(UserEntity theUser, String applicationUrl, VerificationToken verificationToken) throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl+"/register/verifyEmail?token="+verificationToken.getToken();
        eventListener.sendVerificationEmail(url);
    }
}
