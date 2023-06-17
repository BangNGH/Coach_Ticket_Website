package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.ShuttleBus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransitionRepository extends JpaRepository<ShuttleBus, Integer> {
    Integer countById(Integer id);

    @Query("SELECT p FROM ShuttleBus p WHERE CONCAT(p.name, ' ', p.address,' ',p.phone,' ',p.booking.trip.route.name, ' ', p.booking.bookingDate, ' ', p.booking.trip.startTime) LIKE %?1%")
    List<ShuttleBus> search(String keyword);

    @Query("SELECT p FROM ShuttleBus p WHERE p.booking.id =:id")
    ShuttleBus findByBookingId(int id);

    @Query("SELECT p FROM ShuttleBus p WHERE p.booking.bookingDate = ?1")
    Page<ShuttleBus> findTransitToday(LocalDate now, Pageable pageable);

}
