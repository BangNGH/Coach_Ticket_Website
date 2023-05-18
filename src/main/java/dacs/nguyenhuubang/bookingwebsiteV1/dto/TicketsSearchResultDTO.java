package dacs.nguyenhuubang.bookingwebsiteV1.dto;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.BookingDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketsSearchResultDTO {
    private List<Booking> bookings;
    private List<BookingDetails> bookingDetails;
}
