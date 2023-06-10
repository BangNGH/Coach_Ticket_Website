package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Long countById(Long id);

    @Query("SELECT p FROM Seat p WHERE CONCAT(p.name, ' ', p.vehicle.name, ' ', p.vehicle.licensePlates) LIKE %?1%")
    List<Seat> search(String keyword);

    List<Seat> findByVehicleId(int vehicleID);

}
