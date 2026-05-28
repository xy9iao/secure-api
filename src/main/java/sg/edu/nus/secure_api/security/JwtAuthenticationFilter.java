package sg.edu.nus.secure_api.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import sg.edu.nus.secure_api.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_USERNAME = "authUsername";
    public static final String AUTH_ROLE = "authRole";

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

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: JWT token is missing or invalid");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: JWT token is missing or invalid");
            return;
        }

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        if (isAdminPath(path) && !"ADMIN".equals(role)) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden: ADMIN role is required");
            return;
        }

        request.setAttribute(AUTH_USERNAME, username);
        request.setAttribute(AUTH_ROLE, role);

        filterChain.doFilter(request, response);
    }

    private boolean isProtectedPath(String path) {
        return path.startsWith("/api/secure/")
                || path.startsWith("/api/admin/")
                || path.startsWith("/api/products");
    }

    private boolean isAdminPath(String path) {
        return path.startsWith("/api/admin/");
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
}
