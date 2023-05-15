package dacs.nguyenhuubang.bookingwebsiteV1.repository;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    public Long countById(Integer id);

    @Query("SELECT p FROM Vehicle p WHERE CONCAT(p.name, ' ', p.capacity, ' ',p.licensePlates ) LIKE %?1%")
    public List<Vehicle> search(String keyword);

}
