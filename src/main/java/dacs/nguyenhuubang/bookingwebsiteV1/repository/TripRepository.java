package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Integer> {
    Long countById(Integer id);

    @Query("SELECT p FROM Trip p WHERE CONCAT(p.route.name, ' ', p.vehicle.name,' ',p.startTime, ' ',p.price, ' ',p.vehicle.licensePlates ) LIKE %?1%")
    List<Trip> search(String keyword);

    @Query("SELECT t FROM Trip t WHERE t.route.startCity = :startCity " +
            "AND t.route.endCity = :endCity ")
    Page<Trip> findTripsByCitiesAndStartTime(@Param("startCity") City startCity,
                                             @Param("endCity") City endCity, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.route.startCity = :startCity " +
            "AND t.route.endCity = :endCity ")
    List<Trip> findTripsByCitiesAndStartTime(@Param("startCity") City startCity,
                                             @Param("endCity") City endCity);

    @Query("SELECT p FROM Trip p WHERE p.route.name = :routeName ORDER BY p.id LIMIT 1")
    Trip getTripByRouteName(String routeName);

    @Query("SELECT t FROM Trip t WHERE t.route.endCity = :endCity ")
    List<Trip> findTripsByDestination(@Param("endCity") City endCity);
}
