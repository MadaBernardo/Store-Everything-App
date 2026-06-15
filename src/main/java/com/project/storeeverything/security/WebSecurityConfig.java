package com.project.storeeverything.security;

import com.project.storeeverything.services.CustomUserDetailsService;
import com.project.storeeverything.services.InformationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration for StoreEverything.
 * Manages access control, authentication flows, and dynamic session flushing.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final InformationService informationService;

    /**
     * Constructor injection handling authentication dependencies and business services.
     */
    public WebSecurityConfig(CustomUserDetailsService userDetailsService,
                             PasswordEncoder passwordEncoder,
                             InformationService informationService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.informationService = informationService;
    }

    /**
     * Configures the main HTTP security filter chain wrapper.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                 //Disable CSRF to allow form submission without security tokens.
              //  .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests((requests) -> requests
                        // Publicly accessible routes open to any unauthenticated client web browser
                        .requestMatchers("/", "/register", "/login", "/error", "/public/share/**", "/css/**", "/js/**").permitAll()

                        // Administrative dashboard routes restricted strictly to database accounts with ADMIN roles
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Added 'ADMIN' to the shared panel so administrators can view this screen too
                        .requestMatchers("/information/shared-with-me").hasAnyRole("ADMIN", "USER", "FULL_USER")

                        // Information creation and lifecycle management blocks accessible by ADMIN and FULL_USER
                        .requestMatchers("/information/**", "/categories/**").hasAnyRole("ADMIN", "FULL_USER")

                        // General authenticated fallback access for the main dashboard layout space
                        .requestMatchers("/dashboard/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        // SESSION: Intercept the logout request to flush memory elements to MySQL before destroying the token
                        .addLogoutHandler((request, response, authentication) -> {
                            jakarta.servlet.http.HttpSession session = request.getSession(false);
                            if (session != null) {
                                System.out.println("LOGOUT HANDLER TRIGGERED: Committing transient session cards into persistent MySQL storage...");
                                informationService.flushSessionToDatabase(session);
                            }
                        })
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    /**
     * Links data provider beans to cryptographic encoders for user record retrievals.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService);
        authProvider.setPasswordEncoder(this.passwordEncoder);
        return authProvider;
    }
}