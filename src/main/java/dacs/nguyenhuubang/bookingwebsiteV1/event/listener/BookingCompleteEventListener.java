package dacs.nguyenhuubang.bookingwebsiteV1.event.listener;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.SeatReservation;
import dacs.nguyenhuubang.bookingwebsiteV1.event.BookingCompleteEvent;
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
import java.text.NumberFormat;
import java.util.Locale;

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
    private String roundTripId;

    @Override
    public void onApplicationEvent(BookingCompleteEvent event) {

        theBooking = event.getBooking();
        totalPrice = event.getTotalPrice();
        reservedSeats = event.getReservedSeatNames();
        numberOfTicket = event.getNumberofTicket();
        ticketCode = event.getTicketCode();
        roundTripId = event.getRoundTripId();
        String url = event.getApplicationUrl()+"&sentEmail=1";
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
        mailContentBuilder.append("<head>");
        mailContentBuilder.append("<style>");
        mailContentBuilder.append("body { font-family: Arial, sans-serif; }");
        mailContentBuilder.append("h2 { color: #1a6f2d; }");
        mailContentBuilder.append(".container { margin: 20px; padding: 20px; border: 3px solid #1a6f2d; }");
        mailContentBuilder.append("p { margin-bottom: 10px; }");
        mailContentBuilder.append("a { color: #28a745; }");
        mailContentBuilder.append("</style>");
        mailContentBuilder.append("</head>");
        mailContentBuilder.append("<body>");
        mailContentBuilder.append("<div class=\"container\">");
        mailContentBuilder.append("<h2>Hi, " + theBooking.getUser().getFullname() + "</h2>");
        mailContentBuilder.append("<p>Cảm ơn bạn đã tin tưởng và đặt vé tại Voley Booking</p>");
        mailContentBuilder.append("<p><strong>Thông tin vé lượt đi:</strong></p>");
        mailContentBuilder.append("<p><strong>Tuyến:</strong> " + theBooking.getTrip().getRoute().getName() + "</p>");
        mailContentBuilder.append("<p><strong>Số lượng vé lượt đi:</strong> " + numberOfTicket + "</p>");
        mailContentBuilder.append("<p><strong>Chỗ ngồi lượt đi:</strong> " + reservedSeats + "</p>");
        mailContentBuilder.append("<p><strong>Mã vé lượt đi:</strong> " + ticketCode + "</p>");

        if (!roundTripId.isBlank()) {
            Booking booking = bookingService.get(Integer.valueOf(roundTripId));
            String send_reservedSeatNames = ""; // Chuỗi để lưu tên các ghế đã đặt
            for (SeatReservation seatReservation : booking.getSeatReservations()) {
                String seatName = seatReservation.getSeat().getName();
                send_reservedSeatNames += seatName + ", "; // Thêm tên của ghế vào chuỗi
            }
            send_reservedSeatNames = send_reservedSeatNames.substring(0, send_reservedSeatNames.length() - 2);
            mailContentBuilder.append("<p><strong>Thông tin vé lượt về:</strong></p>");
            mailContentBuilder.append("<p><strong>Số lượng vé lượt về:</strong> " + booking.getBookingDetails().get(0).getNumberOfTickets() + "</p>");
            mailContentBuilder.append("<p><strong>Chỗ ngồi lượt về:</strong> " + send_reservedSeatNames + "</p>");
            mailContentBuilder.append("<p><strong>Mã vé lượt về:</strong> " + booking.getBookingDetails().get(0).getId().getTicketCode() + "</p>");
        }

        mailContentBuilder.append("<p><strong>Trạng thái:</strong> Đã thanh toán</p>");

        Locale localeVN = new Locale("vi", "VN");
        NumberFormat vn = NumberFormat.getInstance(localeVN);
        String str2 = vn.format(Double.parseDouble(totalPrice));
        mailContentBuilder.append("<p><strong>Tổng tiền:</strong> " + str2 + "đ</p>");
        mailContentBuilder.append("<p>Vui lòng đến nhà xe trước thời gian khởi hành <strong>15 phút</strong>. Khi lên xe, quý khách vui lòng xuất trình email này cho nhân viên soát vé.</p>");
        mailContentBuilder.append("<p><strong>Địa chỉ:</strong> Hutech, khu công nghệ cao, Quận 9, TP.HCM</p>");
        mailContentBuilder.append("<p><a href=\"" + url + "\">Xem chi tiết vé</a></p>");
        mailContentBuilder.append("</div>");
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