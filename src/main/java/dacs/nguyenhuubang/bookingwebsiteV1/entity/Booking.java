package dacs.nguyenhuubang.bookingwebsiteV1.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Getter
@Setter
@Table(name = "booking")
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("trip")
    @JoinColumn(name = "trip_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Trip is required")
    private Trip trip;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("user")
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    @NotNull
    @Column(name = "isPaid")
    private Boolean isPaid=false;

    @NotNull
    @Column(name = "booking_date", nullable = false)
    private LocalDate booking_date;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SeatReservation> seatReservations;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BookingDetails> bookingDetails;

    @PreRemove
    private void checkForDependencies() {
        if (!seatReservations.isEmpty()||!bookingDetails.isEmpty()) {
            throw new CannotDeleteException("Cannot delete Booking with associated SeatReservation");
        }
    }

}