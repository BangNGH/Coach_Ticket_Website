package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Integer> {
    public Long countById(Integer id);

    @Query("SELECT p FROM Trip p WHERE CONCAT(p.route.name, ' ', p.vehicle.name,' ',p.startTime, ' ',p.price ) LIKE %?1%")
    List<Trip> search(String keyword);

        @Query("SELECT t FROM Trip t WHERE t.route.startCity = :startCity " +
                "AND t.route.endCity = :endCity ")
        List<Trip> findTripsByCitiesAndStartTime(@Param("startCity") City startCity,
                                                 @Param("endCity") City endCity);


}
