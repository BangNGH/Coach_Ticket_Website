package dacs.nguyenhuubang.bookingwebsiteV1.config;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Provider;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Xác định thông tin người dùng từ authentication
        DefaultOidcUser oauthUser = (DefaultOidcUser) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String fullName = oauthUser.getAttribute("name");

        // Kiểm tra vai trò của người dùng và gán quyền tương ứng
        List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("USER"));

        // Tạo đối tượng Authentication mới với quyền đã được gán
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

        // Thiết lập Authentication mới cho SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        Optional<UserEntity> user = userService.findbyEmail(email);
        //nếu user chưa tồn tại trước đó, thực hiện tạo mới, ngược lại thì update thông tin user
        if (user==null){
            userService.createNewUserAfterOauthLoginSuccess(email, fullName,Provider.GOOGLE);
        } else {
            userService.updateCustomerAfterOauthLoginSuccess(user.get(), fullName, Provider.GOOGLE);
        }
        // Tiếp tục xử lý thành công sau khi xác thực
        super.onAuthenticationSuccess(request, response, newAuthentication);
    }

}
