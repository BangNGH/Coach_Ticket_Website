package dacs.nguyenhuubang.bookingwebsiteV1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserInfoRequest {
    @NotBlank(message = "Họ tên không được bỏ trống!") @Size(max = 45, message = "Họ và tên phải duới 45 ký tự") String fullname;
    @NotBlank(message = "Số điện thoại không được để trống!") String address;
    @NotBlank(message = "Email không được bỏ trống!") @Email(message = "Nhập email hợp lệ") String email;
    String newPassword;
    String confirmPassword;
    String oldPassword;
}
