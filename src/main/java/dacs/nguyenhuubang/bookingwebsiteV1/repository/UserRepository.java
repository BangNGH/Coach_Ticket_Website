package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query("SELECT u FROM UserEntity u WHERE u.email= :username")
    public UserEntity getUserByUsername(@Param("username") String username);

    public Long countById(Integer id);

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT p FROM UserEntity p WHERE CONCAT(p.fullname, ' ', p.email, ' ', p.address) LIKE %?1%")
    public List<UserEntity> search(String keyword);

    @Query("SELECT u FROM UserEntity u WHERE u.address= :loginName")
    Optional<UserEntity> findByGithubUserName(String loginName);

    public UserEntity findByResetPasswordToken(String token);


    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationToken u WHERE u.user.id = :id")
    void deleteTokenByUserId(@Param("id") Integer id);

}