package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {
    public Long countById(Integer id);

    @Query("SELECT p FROM City p WHERE p.name LIKE %?1%")
    public List<City> search(String keyword);

    @Query("SELECT p FROM City p WHERE p.name = :cityName")
    City findCityByName(String cityName);


}
