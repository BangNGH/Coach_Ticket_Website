package dacs.nguyenhuubang.bookingwebsiteV1.exception;

public class VehicleNotFoundException extends RuntimeException{
    public VehicleNotFoundException(String message) {
        super(message);
    }
}