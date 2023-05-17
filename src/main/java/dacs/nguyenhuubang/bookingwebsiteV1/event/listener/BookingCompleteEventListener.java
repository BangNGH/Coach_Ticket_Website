package dacs.nguyenhuubang.bookingwebsiteV1.event.listener;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.event.BookingCompleteEvent;
import dacs.nguyenhuubang.bookingwebsiteV1.event.RegistrationCompleteEvent;
import dacs.nguyenhuubang.bookingwebsiteV1.security.UserService;
import dacs.nguyenhuubang.bookingwebsiteV1.service.BookingService;
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
public class BookingCompleteEventListener implements ApplicationListener<BookingCompleteEvent> {

    private final BookingService bookingService;
    private final JavaMailSender mailSender;
    private Booking theBooking;
    private String totalPrice;
    private String reservedSeats;
    private String numberOfTicket;
    private String ticketCode;

    @Override
    public void onApplicationEvent(BookingCompleteEvent event) {

        theBooking = event.getBooking();
        totalPrice = event.getTotalPrice();
        reservedSeats = event.getReservedSeatNames();
        numberOfTicket = event.getNumberofTicket();
        ticketCode = event.getTicketCode();

        String url = event.getApplicationUrl() + "/users/sendTicketCode";
        try {
            sendTicketCode(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click the link below: {}", url);
    }

    public void sendTicketCode(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Đặt Vé Thành Công";
        String senderName = "Booking coach website";

        StringBuilder mailContentBuilder = new StringBuilder();
        mailContentBuilder.append("<html>");
        mailContentBuilder.append("<body>");
        mailContentBuilder.append("<h2>Hi, "+ theBooking.getUser().getFullname()+ " </h2>");
        mailContentBuilder.append("<p>Cảm ơn bạn đã tin tưởng và đặt vé tại Voley Booking</p>");
        mailContentBuilder.append("<p>Số lượng vé: "+numberOfTicket+" </p>");
        mailContentBuilder.append("<p>Chổ ngồi: "+reservedSeats+" </p>");
        mailContentBuilder.append("<p>Mã vé: "+ticketCode+" </p>");
        mailContentBuilder.append("<p>Trạng thái: Đã thanh toán</p>");
        mailContentBuilder.append("<p>Vui lòng đến nhà xe trước thời gian khởi hành 15 phút. Khi lên xe quý khách vui lòng xuất trình email này cho nhân viên soát vé</p>");
        mailContentBuilder.append("<a href=\"" + url + "\">Xem chi tiết vé</a>");
        mailContentBuilder.append("</body>");
        mailContentBuilder.append("</html>");

        String mailContent = mailContentBuilder.toString();
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("nghbang1909@gmail.com", senderName);
        messageHelper.setTo(theBooking.getUser().getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }


}
