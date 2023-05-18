package dacs.nguyenhuubang.bookingwebsiteV1.event.listener;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.event.RegistrationCompleteEvent;
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
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;
    private final JavaMailSender mailSender;
    private UserEntity theUser;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        theUser = event.getUser();
        String verificationToken = UUID.randomUUID().toString();
        userService.saveUserVerificationToken(theUser, verificationToken);
        String url = event.getApplicationUrl()+"/register/verifyEmail?token="+verificationToken;
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click the link below to verify your registration: {}", url);

    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "Booking coach website";
        String mailContent = "<html><head><style>" +
                "body { font-family: Arial, sans-serif; }" +
                "h2 { color: #325174; }" +
                "p { font-size: 16px; }" +
                "a { background-color: #bbbfcb; color: #FFFFFF; padding: 10px 15px; border-radius: 5px; text-decoration: none; }" +
                "</style></head>" +
                "<body>" +
                "<h2>Hi, " + theUser.getFullname() + "</h2>" +
                "<p>Thank you for registering with us. Please, follow the link below to complete your registration:</p>" +
                "<p><a href=\"" + url + "\">Verify your email to activate your account</a></p>" +
                "<p>Thank you,<br>Users Registration Portal Service</p>" +
                "</body></html>";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("nghbang1909@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
