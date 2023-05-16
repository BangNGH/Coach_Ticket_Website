package dacs.nguyenhuubang.bookingwebsiteV1.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "booking_detail")
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetails {

    @EmbeddedId
    private BookingDetailsId id;

    @MapsId("bookingId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    @JsonIgnore
    private Booking booking;

    @NotNull(message = "numberOfTickets cannot be null.")
    @Min(value = 1, message = "Số vé đặt trước phải lớn hơn 0")
    @Column
    private Integer numberOfTickets;

    @Column(name = "total_price")
    private Float totalPrice;
}
