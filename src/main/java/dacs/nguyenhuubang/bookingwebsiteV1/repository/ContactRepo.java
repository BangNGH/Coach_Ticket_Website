package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepo extends JpaRepository<Contact, Long> {
    public Long countById(Long id);

    //Hàm CONCAT() trong MySQL được dùng để nối hai hoặc nhiều chuỗi với nhau:
    @Query("SELECT p FROM Contact p WHERE CONCAT(p.title,' ', p.email) LIKE %?1%")
    public List<Contact> search(String keyword);
}