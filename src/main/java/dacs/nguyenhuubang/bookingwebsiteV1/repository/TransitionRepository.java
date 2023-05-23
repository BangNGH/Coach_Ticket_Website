package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.ShuttleBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransitionRepository extends JpaRepository<ShuttleBus, Integer> {
    public Integer countById(Integer id);

    @Query("SELECT p FROM ShuttleBus p WHERE CONCAT(p.name, ' ', p.address,' ',p.phone) LIKE %?1%")
    public List<ShuttleBus> search(String keyword);

    @Query("SELECT p FROM ShuttleBus p WHERE p.booking.id =:id")
    ShuttleBus findByBookingId(int id);
}
