package sg.edu.nus.secure_api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import sg.edu.nus.secure_api.security.JwtAuthenticationFilter;
import sg.edu.nus.secure_api.service.AuthService;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String loginPage() {
        return "index";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response
    ) {
        try {
            String token = authService.login(username, password);
            response.addHeader(HttpHeaders.SET_COOKIE, buildLoginCookie(token).toString());
            return "redirect:/products";
        } catch (RuntimeException e) {
            return "redirect:/login-failure";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildLogoutCookie().toString());
        return "redirect:/";
    }

    @GetMapping("/login-failure")
    public String loginFailure() {
        return "login-failure";
    }

    private ResponseCookie buildLoginCookie(String token) {
        return ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .path("/")
                .maxAge(30 * 60)
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie buildLogoutCookie() {
        return ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
