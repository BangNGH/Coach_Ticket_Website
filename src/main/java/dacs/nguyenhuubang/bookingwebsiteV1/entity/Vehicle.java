package dacs.nguyenhuubang.bookingwebsiteV1.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "vehicle")
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Têm phương tiện không được để trống")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Biển số xe không được để trống")
    @NaturalId(mutable = true)
    @Column(name = "licensePlates", nullable = false, length = 50, unique = true)
    private String licensePlates;

    @NotNull(message = "Số ghế ngồi không được để trống")
    @Min(value = 4, message = "Số ghế ngồi phải lớn hơn 4")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "image_path", nullable = true)
    private String image_path;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Seat> seats;
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Trip> trips;

    @PreRemove
    private void checkForDependencies() {
        if (!seats.isEmpty()||!trips.isEmpty()) {
            throw new CannotDeleteException("Không thể xóa phương tiện này vì có liên quan khóa ngoại đến dữ liệu khác");
        }
    }

    @Transient
    public String getRouteImagePath(){
        if (image_path == null){
            return null;
        }
        return "/vehicle-images/" +id+"/"+image_path;
    }
}
