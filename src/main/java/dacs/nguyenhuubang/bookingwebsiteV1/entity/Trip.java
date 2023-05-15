package dacs.nguyenhuubang.bookingwebsiteV1.entity;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Setter
@Getter
@Table(name = "trip")
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Thời gian khởi hành không được bỏ trống")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "Giá tiền không được bỏ trống")
    @Positive
    @Column(name = "price", nullable = false)
    private Float price;

    @NotNull(message = "Tuyến đi không được bỏ trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("route")
    @JoinColumn(name = "route_id", referencedColumnName = "id", nullable = false)
    private Route route;

    @NotNull(message = "Loại xe không được bỏ trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("vehicle")
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Booking> bookings;

    @PreRemove
    void checkForDependencies() {
        if (!bookings.isEmpty()) {
            throw new CannotDeleteException("Không thể xóa chuyến này vì có liên quan khóa ngoại đến bảng khác");
        }
    }

}