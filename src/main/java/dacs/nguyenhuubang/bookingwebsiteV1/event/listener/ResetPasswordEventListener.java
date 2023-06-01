package dacs.nguyenhuubang.bookingwebsiteV1.event.listener;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.event.ResetPasswordEvent;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetPasswordEventListener implements ApplicationListener<ResetPasswordEvent> {

    private final UserService userService;
    private final JavaMailSender mailSender;
    private UserEntity user;
    private String email;

    @Override
    public void onApplicationEvent(ResetPasswordEvent event) {
        email = event.getUser().getEmail();
        user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.updateResetPassword(token, user);
        String resetUrl = event.getApplicationUrl() + "/reset-password?token=" + token;
        String url = event.getApplicationUrl();
        try {
            sendVerificationEmail(url, resetUrl);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationEmail(String url, String resetUrl) throws MessagingException, UnsupportedEncodingException {
        String subject = "Khôi Phục Mật Khẩu";
        String senderName = "Nhà xe Travelista";
        String mailContent = "<html><head><style>" +
                "body { font-family: Arial, sans-serif; }" +
                "h2 { color: #325174; }" +
                "p { font-size: 16px; }" +
                "a { background-color: #bbbfcb; color: #FFFFFF; padding: 10px 15px; border-radius: 5px; text-decoration: none; }" +
                "</style></head>" +
                "<body>" +
                "<h2>Xin chào bạn</h2>" +
                "<p>Chúng tôi nhận thấy rằng bạn đã yêu cầu khôi phục mật khẩu tại <a href=\"" + url + "\">Travelista</a>Vui lòng ấn vào đường dẫn bên dưới để khôi phục mật khẩu của bạn.</p>" +
                "<p>Hãy <a href=\"" + resetUrl + "\">khôi phục mật khẩu</a> và vui lòng không chia sẻ đường dẫn này cho bất cứ ai.</p>" +
                "<p>Xin cám ơn,<br>Nhà xe Travelista</p>" +
                "</body></html>";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("nghbang1909@gmail.com", senderName);
        System.out.println("Sending email to" + email + "...");
        messageHelper.setTo(email);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
