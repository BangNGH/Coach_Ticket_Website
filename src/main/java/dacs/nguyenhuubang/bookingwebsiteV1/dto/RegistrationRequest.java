package dacs.nguyenhuubang.bookingwebsiteV1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(String role,
                                  @NotBlank(message = "Password không được bỏ trống!") @Size(min = 6, max = 20, message = "Mật khẩu phải nằm trong khoảng 6 -> 20 ký tự") String password,
                                  @NotBlank(message = "Họ tên không được bỏ trống!") @Size(max = 45, message = "Họ và tên phải duới 45 ký tự") String fullname,
                                  @NotBlank(message = "Số điện thoại không được để trống!") String address,
                                  @NotBlank(message = "Email không được bỏ trống!") @Email(message = "Email must be valid") String email
){

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String role() {
        return role;
    }
}
