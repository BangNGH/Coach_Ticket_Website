package dacs.nguyenhuubang.bookingwebsiteV1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contacts")
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên liên hệ không được để trống")
    @Size(max = 100, message = "Tên liên hệ không được vượt quá 100 ký tự")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 100, message = "Tiêu đề không được vượt quá 100 ký tự")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 1000, message = "Nội dung không được vượt quá 1000 ký tự")
    @Column(name = "content", nullable = false)
    private String content;

}

