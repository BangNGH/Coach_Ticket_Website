package dacs.nguyenhuubang.bookingwebsiteV1.config;

import dacs.nguyenhuubang.bookingwebsiteV1.entity.Provider;
import dacs.nguyenhuubang.bookingwebsiteV1.entity.UserEntity;
import dacs.nguyenhuubang.bookingwebsiteV1.security.CustomOAuth2User;
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
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getEmail();
        String fullName = oauthUser.getFullName();
        String loginName = oauthUser.getLogin();
        Optional<UserEntity> user = null;
        //Trường hợp đăng nhập bằng github
        if (email == null)
            user = userService.findbyEmail(loginName);
        else //Trường hợp đăng nhập bằng google
            user = userService.findbyEmail(email);

        // Kiểm tra vai trò của người dùng và gán quyền tương ứng
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        // Tạo đối tượng Authentication mới với quyền đã được gán
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(oauthUser, null, authorities);
        /*Authentication newAuthentication;
        if (email==null) //Trường hợp đăng nhập bằng github{
            newAuthentication = new UsernamePasswordAuthenticationToken(loginName, null, authorities);
       else  newAuthentication = new UsernamePasswordAuthenticationToken(email, null, authorities);*/

        // Thiết lập Authentication mới cho SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        //nếu user chưa tồn tại trước đó, thực hiện tạo mới, ngược lại thì update thông tin user
        if (user == null) {
            if (email == null)
                userService.createNewUserAfterOauthLoginSuccess(loginName, fullName, Provider.GITHUB);
            else userService.createNewUserAfterOauthLoginSuccess(email, fullName, Provider.GOOGLE);
        } else {
            if (email == null)
                userService.updateCustomerAfterOauthLoginSuccess(user.get(), fullName, Provider.GITHUB);
            else userService.updateCustomerAfterOauthLoginSuccess(user.get(), fullName, Provider.GOOGLE);
        }

        // Tiếp tục xử lý thành công sau khi xác thực
        super.onAuthenticationSuccess(request, response, newAuthentication);
    }

}
