package dacs.nguyenhuubang.bookingwebsiteV1.service;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Trip;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;

    public List<Trip> getTrips() {
        return (List<Trip>)tripRepository.findAll();
    }

    public Trip save(Trip trip) {
        tripRepository.save(trip);
        return trip;
    }

    public Trip get(Integer id){
        Optional<Trip> result = tripRepository.findById(id);
        if (result.isPresent()){
            return result.get();
        }
        else
            throw new ResourceNotFoundException("Could not find any trip with ID: "+id+"!");
    }

    public void delete(Integer id) {
        Long count = tripRepository.countById(id);
        if (count == null || count == 0) {
            throw new ResourceNotFoundException("Could not find any trip with ID " + id);
        }
        tripRepository.deleteById(id);
    }

    public List<Trip> search(String keyword) {

        if (keyword != null) {
            return tripRepository.search(keyword);
        }
        return tripRepository.findAll();
    }

    public Page<Trip> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.tripRepository.findAll(pageable);
    }

    public Page<Trip> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.tripRepository.findAll(pageable);
    }

    public List<Trip> findTripsByCitiesAndStartTime(City startCity, City endCity) {
        return tripRepository.findTripsByCitiesAndStartTime(startCity, endCity);
    }

    public List<Trip> findTripsByDestination(City endCity) {
        return tripRepository.findTripsByDestination(endCity);
    }

    public Trip getTripByRouteName(String routeName) {
        return tripRepository.getTripByRouteName(routeName);
    }

    //phân trang & sort tìm kiếm chuyến
    public Page<Trip> findPaginated(int pageNo, int pageSize, String sortField, String sortDir, City startCity, City endCity) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.tripRepository.findTripsByCitiesAndStartTime(startCity, endCity, pageable);
    }
}
