package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.SeatReservation;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Vehicle;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
    public Long countById(Long id);

    @Query("SELECT p FROM SeatReservation p WHERE CONCAT( p.booking.user.email, ' ', p.seat.name, ' ', p.booking.trip.vehicle.licensePlates, ' ',p.booking.trip.vehicle.name, ' ',p.booking.trip.startTime, ' ',p.booking.booking_date) LIKE %?1%")
    List<SeatReservation> search(String keyword);

    @Query("SELECT COUNT(sr) FROM SeatReservation sr WHERE sr.booking.trip = ?1 AND sr.booking.booking_date = ?2")
    Integer checkAvailableSeat(Trip trip, LocalDate bookingDate);

    @Query("SELECT CASE WHEN COUNT(sr) > 0 THEN TRUE ELSE FALSE END FROM SeatReservation sr WHERE sr.booking.trip = ?1 AND sr.booking.booking_date = ?2 AND sr.seat = ?3")
    Boolean seatIsReserved(Trip trip, LocalDate bookingDate, Seat seat);

    @Query("SELECT s FROM Seat s WHERE s.vehicle = ?1 AND s NOT IN " +
            "(SELECT sr.seat FROM SeatReservation sr WHERE sr.booking.trip = ?2 AND sr.booking.booking_date = ?3)")
    List<Seat> listAvailableSeat(Vehicle vehicle, Trip trip, LocalDate bookingDate);


}
/*    @Modifying
    @Transactional
    @Query("UPDATE SeatReservation seat_reservation set seat_reservation.seatsAvailable = (seat_reservation.seatsAvailable + 1) where seat_reservation.booking.trip.id = ?1 and seat_reservation.booking.booking_date = ?2")
    void updateSeatReservation(int trip_id, LocalDate booking_date);*/


