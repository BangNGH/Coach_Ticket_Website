package dacs.nguyenhuubang.bookingwebsiteV1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "shutte_bus")
@NoArgsConstructor
@AllArgsConstructor
public class ShuttleBus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên liên hệ không được để trống")
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @NotBlank(message = "Vui lòng điền địa chỉ đón")
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone", nullable = false)
    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Size(min = 10, max = 13, message = "Vui lòng điền số điện thoại hợp lệ")
    private String phone;

    @NotNull(message = "Thông tin đặt chỗ không được để trống")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;


}
