package dacs.nguyenhuubang.bookingwebsiteV1.event;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class SendEmailReminderEvent extends ApplicationEvent {
    private Booking booking;
    private String totalPrice;
    private String reservedSeatNames;
    private String numberofTicket;
    private String ticketCode;

    public SendEmailReminderEvent(Booking booking, String totalPrice, String reservedSeatNames, String numberofTicket, String ticketCode) {
        super(booking);
        this.booking = booking;
        this.totalPrice = totalPrice;
        this.reservedSeatNames = reservedSeatNames;
        this.numberofTicket = numberofTicket;
        this.ticketCode = ticketCode;

    }
}