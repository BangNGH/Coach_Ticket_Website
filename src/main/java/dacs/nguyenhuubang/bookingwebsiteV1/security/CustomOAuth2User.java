package dacs.nguyenhuubang.bookingwebsiteV1.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
public class CustomOAuth2User implements OAuth2User {
    private OAuth2User oauth2User;
    private List<GrantedAuthority> authorities =
            AuthorityUtils.createAuthorityList("USER");

    public CustomOAuth2User(OAuth2User oauth2User) {

        this.oauth2User = oauth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oauth2User.getAttribute("name");
    }

    public String getEmail() {
        return oauth2User.getAttribute("email");
    }
    public String getLogin() {
        return oauth2User.getAttribute("login");
    }
}