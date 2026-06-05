package sg.edu.nus.secure_api.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import sg.edu.nus.secure_api.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_USERNAME = "authUsername";
    public static final String AUTH_ROLE = "authRole";
    public static final String AUTH_COOKIE_NAME = "authJwtToken";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!isProtectedPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        if (token == null) {
            handleUnauthorized(response);
            return;
        }

        Claims claims;
        try {
            claims = jwtService.authenticateToken(token);
        } catch (Exception e) {
            handleUnauthorized(response);
            return;
        }

        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        request.setAttribute(AUTH_USERNAME, username);
        request.setAttribute(AUTH_ROLE, role);

        filterChain.doFilter(request, response);
    }

    private boolean isProtectedPath(String path) {
        return path.equals("/products") || path.equals("/api/products");
    }

    private String resolveToken(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (path.equals("/products")) {
            return resolveTokenFromCookie(request);
        }

        if (path.equals("/api/products")) {
            return resolveTokenFromAuthorizationHeader(request);
        }

        return null;
    }

    private String resolveTokenFromAuthorizationHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private String resolveTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private void handleUnauthorized(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login-failure");
    }
}
