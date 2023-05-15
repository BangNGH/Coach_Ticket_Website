package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    public Long countById(Integer id);

    @Query("SELECT p FROM Booking p WHERE CONCAT(p.trip.route.name, ' ', p.user.fullname) LIKE %?1%")
    List<Booking> search(String keyword);
}
