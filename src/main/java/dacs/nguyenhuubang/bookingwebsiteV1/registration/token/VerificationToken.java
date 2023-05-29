package dacs.nguyenhuubang.bookingwebsiteV1.registration.token;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "verification_token")
@NoArgsConstructor

public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expirationTime", nullable = false)
    private Date expirationTime;

    private static final int EXPIRATION_TIME =15;
    public VerificationToken(String token, UserEntity user) {
        super();
        this.token = token;
        this.user = user;
        this.expirationTime = this.getTokenExpirationTime();
    }

    public VerificationToken(String token) {
        super();
        this.token = token;
        this.expirationTime = this.getTokenExpirationTime();
    }

    // tính toán và trả về thời gian hết hạn của token
    public Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);//để thêm một khoảng thời gian (EXPIRATION_TIME) vào thời gian hiện tại.
        return new Date(calendar.getTime().getTime());
    }
}
