package dacs.nguyenhuubang.bookingwebsiteV1.service;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Contact;
import dacs.nguyenhuubang.bookingwebsiteV1.exception.ResourceNotFoundException;
import dacs.nguyenhuubang.bookingwebsiteV1.repository.ContactRepo;
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
public class ContactService {
    private final ContactRepo contactRepo;

    public List<Contact> getCities() {
        return contactRepo.findAll();
    }

    public void save(Contact contact) {
        contactRepo.save(contact);
    }

    public Contact get(Long id) {
        Optional<Contact> result = contactRepo.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else
            throw new ResourceNotFoundException("Not found Contact with ID: " + id + "!");
    }

    public void delete(Long id) {
        Long count = contactRepo.countById(id);
        if (count == null || count == 0) {
            throw new ResourceNotFoundException("Could not find Contact city with ID " + id);
        }
        contactRepo.deleteById(id);
    }

    public List<Contact> search(String keyword) {

        if (keyword != null) {
            return contactRepo.search(keyword);
        }
        return contactRepo.findAll();

    }

    public Page<Contact> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.contactRepo.findAll(pageable);
    }

}
