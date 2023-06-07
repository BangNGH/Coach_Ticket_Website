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

/*    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Xác Nhận Tài Khoản";
        String senderName = "Nhà xe Travelista";
        String mailContent = "<html><head><style>" +
                "body { font-family: Arial, sans-serif; }" +
                "h2 { color: #325174; }" +
                "p { font-size: 16px; }" +
                "a { background-color: #bbbfcb; color: #FFFFFF; padding: 10px 15px; border-radius: 5px; text-decoration: none; }" +
                "</style></head>" +
                "<body>" +
                "<h2>Hi, " + theUser.getFullname() + "</h2>" +
                "<p>Cám ơn bạn đã đăng ký tại khoản tại Travelista. Để xác nhận việc đăng ký tài khoản của bạn, vui lòng ấn vòng đường link bên dưới:</p>" +
                "<p><a href=\"" + url + "\">Xác nhận đăng ký</a></p>" +
                "<p>Xin cám ơn,<br>Nhà xe Travelista</p>" +
                "</body></html>";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("nghbang1909@gmail.com", senderName);
        System.out.println("Sending email to" + theUser.getEmail() + "...");
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    } */
public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
    String subject = "Xác Nhận Tài Khoản";
    String senderName = "Nhà xe Travelista";
    StringBuilder mailContentBuilder = new StringBuilder();
    mailContentBuilder.append("<html>");
    mailContentBuilder.append("<head>");
    mailContentBuilder.append("<style>");
    mailContentBuilder.append("body { font-family: Arial, sans-serif; }");
    mailContentBuilder.append("h2 { color: #383429; }");
    mailContentBuilder.append(".container { background-color: #ffffff;margin: 20px; padding: 20px; border: 3px solid #ffffff; }");
    mailContentBuilder.append("p { margin-bottom: 10px; }");
    mailContentBuilder.append("a { color: #28a745; }");
    mailContentBuilder.append("</style>");
    mailContentBuilder.append("</head>");
    mailContentBuilder.append("<body style=\"background-color: #e1dada;padding:20px\">");
    mailContentBuilder.append("<div class=\"container\">");
    mailContentBuilder.append("<h2>Xin chào, " + theUser.getFullname() + "</h2>");
    mailContentBuilder.append("<p>Cám ơn bạn đã đăng ký tại khoản tại Travelista. Để xác nhận việc đăng ký tài khoản của bạn, vui lòng ấn vòng đường link bên dưới:</p>");
    mailContentBuilder.append("<p>Hãy <a href=\"" + url + "\">xác nhận đăng ký tài khoản</a> của bạn, đường dẫn này sẽ hết hạn trong vòng <strong>15 phút</strong></p>");
    mailContentBuilder.append("<p>Xin cám ơn,<br>Nhà xe Travelista</p>");
    mailContentBuilder.append("</div>");
    mailContentBuilder.append("</body>");
    mailContentBuilder.append("</html>");

    String mailContent = mailContentBuilder.toString();
    MimeMessage message = mailSender.createMimeMessage();
    var messageHelper = new MimeMessageHelper(message);
    messageHelper.setFrom("nghbang1909@gmail.com", senderName);
    System.out.println("Sending email to" + theUser.getEmail() + "...");
    messageHelper.setTo(theUser.getEmail());
    messageHelper.setSubject(subject);
    messageHelper.setText(mailContent, true);
    mailSender.send(message);
}
}
