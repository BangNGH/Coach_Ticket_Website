package dacs.nguyenhuubang.bookingwebsiteV1.service;


import dacs.nguyenhuubang.bookingwebsiteV1.entity.City;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.Seat;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;


    public List<Seat> getList() {
        return (List<Seat>)seatRepository.findAll();
    }

    public void save(Seat seat) {
        seatRepository.save(seat);
    }

    public Seat get(Long id){
        Optional<Seat> result = seatRepository.findById(id);
        if (result.isPresent()){
            return result.get();
        }
        else
            throw new ResourceNotFoundException("Not found seat with ID: "+id+"!");
    }

    public void delete(Long id) {
        Long count = seatRepository.countById(id);
        if (count == null || count == 0) {
            throw new ResourceNotFoundException("Not found seat with ID: "+id+"!");
        }
        seatRepository.deleteById(id);
    }

    public List<Seat> search(String keyword) {

        if (keyword != null) {
            return seatRepository.search(keyword);
        }
        return seatRepository.findAll();

    }

    public Page<Seat> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.seatRepository.findAll(pageable);
    }

    public Page<Seat> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.seatRepository.findAll(pageable);
    }
}