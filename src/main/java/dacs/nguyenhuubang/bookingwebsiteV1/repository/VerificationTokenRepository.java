package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

//@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    @Query("SELECT p FROM VerificationToken p WHERE p.token = ?1 ")
    VerificationToken findByToken(String token);
}
