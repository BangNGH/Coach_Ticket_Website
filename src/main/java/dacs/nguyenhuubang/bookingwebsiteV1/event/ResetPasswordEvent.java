package dacs.nguyenhuubang.bookingwebsiteV1.event;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ResetPasswordEvent extends ApplicationEvent {
    private UserEntity user;
    private String applicationUrl;

    public ResetPasswordEvent(UserEntity user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
