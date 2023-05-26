package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.ShuttleBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransitionRepository extends JpaRepository<ShuttleBus, Integer> {
    public Integer countById(Integer id);

    @Query("SELECT p FROM ShuttleBus p WHERE CONCAT(p.name, ' ', p.address,' ',p.phone) LIKE %?1%")
    public List<ShuttleBus> search(String keyword);

    @Query("SELECT p FROM ShuttleBus p WHERE p.booking.id =:id")
    ShuttleBus findByBookingId(int id);
}
