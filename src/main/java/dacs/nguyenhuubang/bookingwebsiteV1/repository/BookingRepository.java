package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    public Long countById(Integer id);

    @Query("SELECT p FROM Booking p WHERE CONCAT(p.trip.route.name, ' ', p.user.email,' ',p.bookingDate,' ', p.trip.startTime,' ',p.isPaid,' ',p.id, ' ', p.trip.vehicle.name) LIKE %?1%")
    List<Booking> search(String keyword);

    /*    @Query("SELECT p FROM Booking p WHERE p.user.id =?1 AND p.isPaid = ?2")
        List<Booking> getBookedTripsByUserId(int id, Boolean isPaid);*/
    @Query("SELECT p FROM Booking p WHERE p.user.id = ?1 AND p.isPaid = ?2")
    Page<Booking> getBookedTripsByUserId(int id, Boolean isPaid, Pageable pageable);


    //dùng cho show bill và phân trang
    @Query("SELECT p FROM Booking p WHERE p.isPaid = ?1")
    Page<Booking> getBills(Boolean isPaid, Pageable pageable);

    //booking today
    @Query("SELECT p FROM Booking p WHERE p.bookingDate = ?1")
    Page<Booking> findBookingToday(LocalDate now, Pageable pageable);


    //dùng cho search ajax bill
    @Query("SELECT p FROM Booking p WHERE p.user = ?1 AND p.isPaid = ?2")
    List<Booking> getBill(UserEntity user, Boolean isPaid);

    @Query("DELETE FROM Booking b WHERE b.isPaid = false AND b.bookingDate = :currentDate AND (TIMEDIFF(:currentTime, b.trip.startTime) <= '01:30:00')")
    @Modifying
    void cancelAllUnpaidTickets(@Param("currentDate") LocalDate currentDate, @Param("currentTime") LocalTime currentTime);

}
