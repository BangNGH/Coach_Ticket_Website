package dacs.nguyenhuubang.bookingwebsiteV1.service;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.ShuttleBus;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.TransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransitionService {
    private final TransitionRepository transitionRepository;

    public List<ShuttleBus> getCities() {
        return (List<ShuttleBus>) transitionRepository.findAll();
    }

    public ShuttleBus save(ShuttleBus shuttleBus) {
        try {
            ShuttleBus exists = transitionRepository.findByBookingId(shuttleBus.getBooking().getId());
            if (exists == null) {
                transitionRepository.save(shuttleBus);
            } else {
                exists.setName(shuttleBus.getName());
                exists.setPhone(shuttleBus.getPhone());
                exists.setAddress(shuttleBus.getAddress());
                transitionRepository.save(exists);
            }
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Không tìm thấy bất kỳ chuyến trung chuyển nào với ID: " + shuttleBus.getId() + "!");
        }

        return shuttleBus;
    }

    public ShuttleBus get(Integer id) {
        Optional<ShuttleBus> result = transitionRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else
            throw new ResourceNotFoundException("Không tìm thấy bất kỳ chuyến trung chuyển nào với ID: " + id + "!");
    }

    public void delete(Integer id) {
        Integer count = transitionRepository.countById(id);
        System.out.println(count);
        if (count == null || count == 0) {
            System.out.println("ERROR");
            throw new ResourceNotFoundException("Không tìm thấy bất kỳ chuyến trung chuyển nào với ID " + id);
        }

        transitionRepository.delete(get(id));
    }

    public List<ShuttleBus> search(String keyword) {

        if (keyword != null) {
            return transitionRepository.search(keyword);
        }
        return transitionRepository.findAll();

    }

    public Page<ShuttleBus> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.transitionRepository.findAll(pageable);
    }

    public Page<ShuttleBus> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.transitionRepository.findAll(pageable);
    }

    //transit today
    public Page<ShuttleBus> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, LocalDate now) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.transitionRepository.findTransitToday(now, pageable);
    }

    public ShuttleBus findByBookingId(Integer id) {
        return this.transitionRepository.findByBookingId(id);
    }
}
