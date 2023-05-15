package dacs.nguyenhuubang.bookingwebsiteV1.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/*@Configuration
@EnableWebSecurity
@EnableMethodSecurity*/
public class WebSecurityConfig {

	/*@Autowired
    private DataSource dataSource;
	
	@Autowired
	public UserDetailsService userDetailsService;
	

	 @Bean
	    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { 
	        return authenticationConfiguration.getAuthenticationManager();
	    }
	
	 @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	        authProvider.setUserDetailsService(userDetailsService);
	        authProvider.setPasswordEncoder(passwordEncoder());
	         
	        return authProvider;
	    }
*/
/*	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
            .password(passwordEncoder().encode("user1Pass"))
            .roles("USER")
            .build();
        UserDetails user2 = User.withUsername("user2")
            .password(passwordEncoder().encode("user2Pass"))
            .roles("USER")
            .build();
        UserDetails admin = User.withUsername("admin")
            .password(passwordEncoder().encode("adminPass"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user1, user2, admin);
    }
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.formLogin()
	      .loginPage("/auth-login.html")
	      .loginProcessingUrl("/j_spring_security_check")
	      .defaultSuccessUrl("/home",true)
	      .failureUrl("/auth-login.html?error=true");
	    return http.build();
	}*/
	/*@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf().disable()
	        .authorizeHttpRequests((requests) -> requests
	            .requestMatchers("/api/users/**", "/register", "/home", "/resources/**", "/static/**", "/css/**", "/js/**", "/img/**")
	            .permitAll().anyRequest().authenticated())
	        .formLogin((form) -> form.loginPage("/login").usernameParameter("username")
	            .passwordParameter("password").successForwardUrl("/home").failureUrl("/login?error=true").loginProcessingUrl("/j_spring_security_check").permitAll())
	        .logout((logout) -> logout.permitAll());
	   
	    SecurityFilterChain filterChain = http.build();
	    return filterChain;
	}

	
	
	@Bean
	 JdbcUserDetailsManager userDetailsManager() {
	    JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
	    userDetailsManager.setUsersByUsernameQuery("select username, password, enabled from user where username=?");
	    userDetailsManager.setAuthoritiesByUsernameQuery("SELECT user.username, role.name AS role\\r\\n\" + \r\n" + 
	    		"                    		\"FROM user\\r\\n\" + \r\n" + 
	    		"                    		\"JOIN user_role ON user.id = user_role.user_id\\r\\n\" + \r\n" + 
	    		"                    		\"JOIN role ON role.id = user_role.role_id\\r\\n\" + \r\n" + 
	    		"                    		\"WHERE username = ?");	 
	    return userDetailsManager;
	}*/
	
/*	@Bean
	   public UserDetailsManager authenticateUsers() {

	      UserDetails user = User.withUsername("username")
	        .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("password")).build();
	      JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
	      users.setAuthoritiesByUsernameQuery("select username, password, enabled from user where username=?");
	      users.setUsersByUsernameQuery("SELECT user.username, role.name AS role\\\\r\\\\n\\\" + \\r\\n\" + \r\n" + 
	      		"	    		\"                    		\\\"FROM user\\\\r\\\\n\\\" + \\r\\n\" + \r\n" + 
	      		"	    		\"                    		\\\"JOIN user_role ON user.id = user_role.user_id\\\\r\\\\n\\\" + \\r\\n\" + \r\n" + 
	      		"	    		\"                    		\\\"JOIN role ON role.id = user_role.role_id\\\\r\\\\n\\\" + \\r\\n\" + \r\n" + 
	      		"	    		\"                    		\\\"WHERE username = ?");
	      users.createUser(user);
	      return users;
	   }*/




}