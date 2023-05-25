package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Integer> {

    public Long countById(Integer id);

    @Query("SELECT p FROM Route p WHERE CONCAT(p.name, ' ', p.startCity.name, ' ', p.endCity.name, ' ', p.distance) LIKE %?1%")
    List<Route> search(String keyword);

    @Query("SELECT p FROM Route p WHERE p.name = :routeName")
    Route getRouteByName(String routeName);
}