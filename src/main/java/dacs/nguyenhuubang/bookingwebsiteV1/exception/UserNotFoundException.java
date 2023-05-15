package dacs.nguyenhuubang.bookingwebsiteV1.exception;

public class UserNotFoundException extends  RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
