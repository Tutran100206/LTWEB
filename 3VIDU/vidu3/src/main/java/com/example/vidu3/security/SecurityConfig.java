package com.example.vidu3.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration @EnableMethodSecurity(proxyTargetClass=true)
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean SecurityFilterChain filterChain(HttpSecurity http, CustomUserDetailsService users, PasswordEncoder encoder) throws Exception {
        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        return http.authenticationProvider(provider)
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll()
                .requestMatchers("/", "/login", "/css/**", "/images/**", "/error", "/access-denied", "/register", "/verify-otp", "/resend-register-otp", "/forgot-password", "/reset-password").permitAll()
                .requestMatchers("/users/**", "/dashboard/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").usernameParameter("login").passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true).failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").invalidateHttpSession(true).deleteCookies("VIDU3SESSION"))
            .exceptionHandling(errors -> errors.accessDeniedPage("/access-denied"))
            .build();
    }
}
