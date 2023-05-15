package dacs.nguyenhuubang.bookingwebsiteV1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.CannotDeleteException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "city")
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Tên thành phố không được để trống")
    @Size(min = 3, max = 25, message = "Nhập tên thành phố hợp lệ")
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "startCity", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Route> startRoutes;

    @OneToMany(mappedBy = "endCity", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Route> endRoutes;

    @PreRemove
    public void checkRoutesBeforeDelete() {
        if (!startRoutes.isEmpty() || !endRoutes.isEmpty()) {
            throw new CannotDeleteException("Không thể xóa thành phố này vì nó có các tuyến đường liên quan.");

        }
    }
}
