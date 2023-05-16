package dacs.nguyenhuubang.bookingwebsiteV1.service;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.*;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.SeatHasBeenReseredException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatReservationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SeatReservationService {
    private final SeatReservationRepository seatReservationRepository;

    public List<SeatReservation> getList() {
        return (List<SeatReservation>) seatReservationRepository.findAll();
    }

    public List<Seat> getReservedSeat(Booking booking) {
        return seatReservationRepository.reservedSeat(booking);
    }

    public void save(SeatReservation seatReservation, Integer id) {

        if (id != null) {
            SeatReservation updatedReservation = get(Long.valueOf(id));
            if (updatedReservation.getBooking() != seatReservation.getBooking()){
                int totalSeat = seatReservation.getBooking().getTrip().getVehicle().getCapacity();
                int availableSeat = seatReservationRepository.checkAvailableSeat(seatReservation.getBooking().getTrip(), seatReservation.getBooking().getBooking_date());
                if (totalSeat == availableSeat) {
                    throw new SeatHasBeenReseredException("Chuyến xe này đã hết ghế!Vui lòng tìm chuyến khác");
                }
                if (seatReservationRepository.seatIsReserved(seatReservation.getBooking().getTrip(), seatReservation.getBooking().getBooking_date(), seatReservation.getSeat())) {
                    throw new SeatHasBeenReseredException("Seat " + seatReservation.getSeat().getName() + " Has Been Reserved");
                } else {
                    if (availableSeat == 0) {
                        availableSeat = totalSeat - 1;
                    } else {
                        availableSeat = totalSeat - availableSeat-1;
                    }
                    updatedReservation.setSeatsAvailable(availableSeat);
                    updatedReservation.setBooking(seatReservation.getBooking());
                    updatedReservation.setSeat(seatReservation.getSeat());
                    seatReservationRepository.save(updatedReservation);
                }

            }else {
                updatedReservation.setSeatsAvailable(updatedReservation.getSeatsAvailable());
                if (updatedReservation.getSeat()==seatReservation.getSeat()) {
                    seatReservationRepository.save(updatedReservation);
                } else {
                    if (seatReservationRepository.seatIsReserved(seatReservation.getBooking().getTrip(), seatReservation.getBooking().getBooking_date(), seatReservation.getSeat())) {
                        throw new SeatHasBeenReseredException("Seat " + seatReservation.getSeat().getName() + " Has Been Reserved");
                    } else {
                        updatedReservation.setSeat(seatReservation.getSeat());
                        seatReservationRepository.save(updatedReservation);
                    }
                }
            }
        } else {

            int totalSeat = seatReservation.getBooking().getTrip().getVehicle().getCapacity();
            int availableSeat = seatReservationRepository.checkAvailableSeat(seatReservation.getBooking().getTrip(), seatReservation.getBooking().getBooking_date());
            if (totalSeat == availableSeat) {
                throw new SeatHasBeenReseredException("Chuyến xe này đã hết ghế!Vui lòng tìm chuyến khác");
            }
            if (seatReservationRepository.seatIsReserved(seatReservation.getBooking().getTrip(), seatReservation.getBooking().getBooking_date(), seatReservation.getSeat())) {
                throw new SeatHasBeenReseredException("Seat " + seatReservation.getSeat().getName() + " Has Been Reserved");
            } else {
                if (availableSeat == 0) {
                    availableSeat = totalSeat - 1;
                } else {
                    availableSeat = totalSeat - availableSeat -1;
                }
                seatReservation.setSeatsAvailable(availableSeat);
                seatReservationRepository.save(seatReservation);
            }
        }
    }

    public SeatReservation get(Long id) {
        Optional<SeatReservation> result = seatReservationRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else
            throw new ResourceNotFoundException("Not found seat with ID: " + id + "!");
    }

    public void delete(Long id) {
        Long count = seatReservationRepository.countById(id);
        if (count == null || count == 0) {
            throw new ResourceNotFoundException("Not found seat-resered with ID: " + id + "!");
        }
        seatReservationRepository.deleteById(id);

    }

    public List<SeatReservation> search(String keyword) {

        if (keyword != null) {
            return seatReservationRepository.search(keyword);
        }
        return seatReservationRepository.findAll();
    }

    public Page<SeatReservation> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.seatReservationRepository.findAll(pageable);
    }
}