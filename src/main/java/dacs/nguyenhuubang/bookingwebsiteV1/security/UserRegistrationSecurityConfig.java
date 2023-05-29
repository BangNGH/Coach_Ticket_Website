package dacs.nguyenhuubang.bookingwebsiteV1.security;

import dacs.nguyenhuubang.bookingwebsiteV1.config.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class UserRegistrationSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/register/**", "/oauth/**", "/login/**", "/home/**", "/resources/**", "/static/**", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/users/**")
                .hasAnyAuthority("USER", "ADMIN")
                .requestMatchers("/home/**").permitAll()
                .requestMatchers("/admin/**")
                .hasAnyAuthority("ADMIN").anyRequest().permitAll()
                .and()
                .oauth2Login().loginPage("/login").userInfoEndpoint().userService(oauthUserService).and().successHandler(oAuth2LoginSuccessHandler)
                .and()
                .formLogin().loginPage("/login").successHandler(successHandler()).failureUrl("/login?error=true")
                .and()
                .rememberMe().key("uniqueAndSecret").tokenValiditySeconds(86400)
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .build();
    }

    @Autowired
    private CustomOAuth2UserService oauthUserService;
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    response.sendRedirect("/admin");
                    return;
                }
            }
            response.sendRedirect("/home");
        };
    }










}
