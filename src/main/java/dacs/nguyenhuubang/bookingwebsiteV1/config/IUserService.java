package dacs.nguyenhuubang.bookingwebsiteV1.config;

import java.util.List;
import java.util.Optional;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.registration.RegistrationRequest;
import dacs.nguyenhuubang.bookingwebsiteV1.registration.token.VerificationToken;

public interface IUserService{
	List<UserEntity> getUsers();
	UserEntity registerUser(RegistrationRequest request);

	Optional<UserEntity> findbyEmail(String email);


    void saveUserVerificationToken(UserEntity user, String verificationToken);

	String validateToken(String theToken);

	VerificationToken generateNewVerificationToken(String oldToken);

    void save(UserEntity user);
}