package dacs.nguyenhuubang.bookingwebsiteV1.exception;

public class SeatHasBeenReseredException extends RuntimeException {

    public SeatHasBeenReseredException(String message) {
        super(message);
    }
}
