package com.example.demo.security;
import com.example.demo.service.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UserService users;
    private final SecurityErrorHandler errors;
    public JwtAuthenticationFilter(JwtService jwt, UserService users, SecurityErrorHandler errors) {
        this.jwt = jwt; this.users = users; this.errors = errors;
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) { chain.doFilter(request, response); return; }
        try {
            var claims = jwt.validateToken(header.substring(7));
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = users.loadUserByUsername(claims.getSubject());
                if (!jwt.isTokenValid(claims, user)) throw new BadCredentialsException("JWT token is invalid");
                var authentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            errors.write(response, HttpStatus.UNAUTHORIZED, ex instanceof org.springframework.security.core.userdetails.UsernameNotFoundException
                ? "JWT user no longer exists" : ex.getMessage());
            return;
        }
        chain.doFilter(request, response);
    }
}
