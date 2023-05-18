package dacs.nguyenhuubang.bookingwebsiteV1.event;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class BookingCompleteEvent extends ApplicationEvent {
    private Booking booking;
    private String roundTripId;
    private String totalPrice;
    private String reservedSeatNames;
    private String numberofTicket;
    private String ticketCode;
    private String applicationUrl;

    public BookingCompleteEvent(Booking booking, String roundTripId, String totalPrice, String reservedSeatNames, String numberofTicket, String ticketCode, String applicationUrl) {
        super(booking);
        this.booking = booking;
        this.roundTripId = roundTripId;
        this.totalPrice = totalPrice;
        this.reservedSeatNames = reservedSeatNames;
        this.numberofTicket = numberofTicket;
        this.ticketCode = ticketCode;
        this.applicationUrl = applicationUrl;
    }
}