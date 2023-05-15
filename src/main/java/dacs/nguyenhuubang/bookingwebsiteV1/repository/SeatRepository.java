package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.SeatReservation;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Long countById(Long id);

    @Query("SELECT p FROM Seat p WHERE p.name LIKE %?1%")
    List<Seat> search(String keyword);

    List<Seat> findByVehicleId(int vehicleID);

}
