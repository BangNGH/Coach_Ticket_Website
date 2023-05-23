package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    public Long countById(Integer id);

    @Query("SELECT p FROM Booking p WHERE CONCAT(p.trip.route.name, ' ', p.user.fullname,' ',p.bookingDate,' ', p.trip.startTime,' ',p.isPaid, ' ', p.trip.vehicle.name) LIKE %?1%")
    List<Booking> search(String keyword);

    /*    @Query("SELECT p FROM Booking p WHERE p.user.id =?1 AND p.isPaid = ?2")
        List<Booking> getBookedTripsByUserId(int id, Boolean isPaid);*/
    @Query("SELECT p FROM Booking p WHERE p.user.id = ?1 AND p.isPaid = ?2")
    Page<Booking> getBookedTripsByUserId(int id, Boolean isPaid, Pageable pageable);

    @Query("SELECT p FROM Booking p WHERE p.isPaid = ?1")
    Page<Booking> getBills(Boolean isPaid, Pageable pageable);
}
