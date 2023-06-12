package dacs.nguyenhuubang.bookingwebsiteV1.service;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Booking;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    public List<Booking> getBookings() {
        return (List<Booking>) bookingRepository.findAll();
    }

    public Booking save(Booking booking) {
        bookingRepository.save(booking);
        return booking;
    }

    public Booking get(Integer id) {
        Optional<Booking> result = bookingRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else
            throw new ResourceNotFoundException("Not found ticket with ID: " + id + "!");
    }

    public void delete(Integer id) {
        Long count = bookingRepository.countById(id);
        if (count == null || count == 0) {
            throw new ResourceNotFoundException("Could not find any ticket with ID " + id);
        }

        bookingRepository.deleteById(id);
    }

    public List<Booking> search(String keyword) {

        if (keyword != null) {
            return bookingRepository.search(keyword);
        }
        return bookingRepository.findAll();
    }

    public Page<Booking> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.bookingRepository.findAll(pageable);
    }

    //booking today
    public Page<Booking> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, LocalDate now) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.bookingRepository.findBookingToday(now, pageable);
    }

    public Page<Booking> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.bookingRepository.findAll(pageable);
    }

/*    public List<Booking> getBookedTripsByUserId(int id, Boolean isPaid) {
        return bookingRepository.getBookedTripsByUserId(id, isPaid);
    }*/

    public Page<Booking> findPage(int id, Boolean isPaid, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.bookingRepository.getBookedTripsByUserId(id, isPaid, pageable);
    }

    //phân trang show bill
    public Page<Booking> findPage(int id, Boolean isPaid, int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.bookingRepository.getBookedTripsByUserId(id, isPaid, pageable);
    }

    public Page<Booking> findPage(Boolean isPaid, int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.bookingRepository.getBills(isPaid, pageable);
    }

    //dùng cho search ajax bill
    public List<Booking> getBill(UserEntity user, Boolean isPaid) {
        return this.bookingRepository.getBill(user, isPaid);
    }

    public List<Booking> searchBookings(List<Booking> bookedTrip, String key) {
        List<Booking> foundBills = new ArrayList<>();
        for (Booking booking : bookedTrip) {
            if (booking.getBookingDetails().get(0).getTotalPrice().toString().contains(key) || booking.getTrip().getVehicle().getLicensePlates().toString().contains(key) || booking.getTrip().getVehicle().getName().toString().contains(key) || booking.getTrip().getStartTime().toString().contains(key) || booking.getTrip().getRoute().getName().toString().contains(key) || booking.getBookingDate().toString().contains(key) || booking.getBookingDetails().get(0).getId().getTicketCode().toString().contains(key)) {
                foundBills.add(booking);
            }
        }
        return foundBills;
    }
}
