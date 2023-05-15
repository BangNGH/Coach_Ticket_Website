package dacs.nguyenhuubang.bookingwebsiteV1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	public Long countById(Integer id);
	Optional<UserEntity> findByEmail(String email);

	@Query("SELECT p FROM UserEntity p WHERE CONCAT(p.fullname, ' ', p.email) LIKE %?1%")
	public List<UserEntity> search(String keyword);


}