package dacs.nguyenhuubang.bookingwebsiteV1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "route")
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Tên tuyến đường không được để trống")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Thành phố xuất phát không được để trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("startCity")
    @JoinColumn(name = "start_city_id", referencedColumnName = "id", nullable = false)
    private City startCity;

    @NotNull(message = "Thành phố kết thúc không được để trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("endCity")
    @JoinColumn(name = "end_city_id", referencedColumnName = "id", nullable = false)
    private City endCity;

    @NotNull(message = "Khoảng cách không được để trống")
    @DecimalMin(value = "1.0", message = "Khoảng cách phải lớn hơn 0")
    @Column(name = "distance", nullable = false)
    private Float distance;

    @NotNull(message = "Thời gian chuyến đi không được để trống")
    @Min(value = 1, message = "Thời gian chuyến đi phải lớn hơn 0")
    @Column(name = "time_trip", nullable = false)
    private Integer time_trip;

    @Column(name = "image_path", nullable = true)
    private String image_path;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Trip> trips;

    @PreRemove
    private void checkForDependencies() {
        if (!trips.isEmpty()) {
            throw new CannotDeleteException("Cannot delete routes with associated trips");
        }
    }
    @Transient
    public String getRouteImagePath(){
        if (image_path == null){
            return null;
        }
        return "/route-images/" +id+"/"+image_path;
    }


}
