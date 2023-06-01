package dacs.nguyenhuubang.bookingwebsiteV1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Vui lòng điền tên")
    @Column(name = "fullname", nullable = false, length = 45)
    private String fullname;

    @NotBlank(message = "Vui lòng điền email")
  //  @Email(message = "Email is not valid")
    @NaturalId(mutable = true)
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "address", nullable = false, unique = true)
    private String address;

    @Column
    private String role;

    @Column
    @JsonProperty("isEnabled")
    private boolean isEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column
    private Provider provider;

    @Column(name = "reset_password_token", nullable = true)
    private String resetPasswordToken;

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Booking> bookings;

    @PreRemove
    private void checkForDependencies() {
        if (!bookings.isEmpty()) {
            throw new CannotDeleteException("Không thể xóa user này vì có liên quan khóa ngoại đến bảng khác");
        }
    }

}
