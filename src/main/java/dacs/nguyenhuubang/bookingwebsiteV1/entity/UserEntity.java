package dacs.nguyenhuubang.bookingwebsiteV1.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @NotBlank(message = "Fullname is required")
    @Column(name = "fullname", nullable = false, length = 45)
    private String fullname;

    @NotBlank(message = "Email is required")
  //  @Email(message = "Email is not valid")
    @NaturalId(mutable = true)
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

   // @NotBlank(message = "Password is required")
 //   @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = true)
    private String password;

    //@NotBlank(message = "Address is required")
    @Column(name = "address", nullable = true)
    private String address;

    @Column
    private String role;

    @Column
    @JsonProperty("isEnabled")
    private boolean isEnabled = false;
    @Enumerated(EnumType.STRING)
    @Column
    private Provider provider;

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
