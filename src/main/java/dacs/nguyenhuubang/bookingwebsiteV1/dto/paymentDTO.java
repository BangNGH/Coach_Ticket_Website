package dacs.nguyenhuubang.bookingwebsiteV1.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class paymentDTO implements Serializable {
    private String status;
    private String message;
    private String URL;
}
