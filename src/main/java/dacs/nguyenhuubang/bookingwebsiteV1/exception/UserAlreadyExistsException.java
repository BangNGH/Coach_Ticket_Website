package dacs.nguyenhuubang.bookingwebsiteV1.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
