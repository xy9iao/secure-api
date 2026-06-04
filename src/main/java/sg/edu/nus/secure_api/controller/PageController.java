package sg.edu.nus.secure_api.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import sg.edu.nus.secure_api.model.LoginResponse;
import sg.edu.nus.secure_api.model.Product;
import sg.edu.nus.secure_api.repository.ProductRepository;
import sg.edu.nus.secure_api.security.JwtAuthenticationFilter;
import sg.edu.nus.secure_api.service.AuthService;

@Controller
public class PageController {

    private final AuthService authService;
    private final ProductRepository productRepository;

    public PageController(AuthService authService, ProductRepository productRepository) {
        this.authService = authService;
        this.productRepository = productRepository;
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
            LoginResponse loginResponse = authService.login(username, password);
            response.addHeader(HttpHeaders.SET_COOKIE, buildLoginCookie(loginResponse.getToken()).toString());
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            return "redirect:/login-failure?message=Invalid+username+or+password";
        } catch (RuntimeException e) {
            return "redirect:/login-failure?message=Login+failed";
        }
    }

    @GetMapping("/products")
    public String products(
            @RequestAttribute(JwtAuthenticationFilter.AUTH_USERNAME) String username,
            @RequestAttribute(JwtAuthenticationFilter.AUTH_ROLE) String role,
            Model model
    ) {
        List<Product> products = "ADMIN".equals(role)
                ? productRepository.findAll()
                : productRepository.findByOwner(username);

        model.addAttribute("username", username);
        model.addAttribute("role", role);
        model.addAttribute("products", products);

        return "products";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildLogoutCookie().toString());
        return "redirect:/";
    }

    @GetMapping("/login-failure")
    public String loginFailure(
            @RequestParam(defaultValue = "Invalid username or password.") String message,
            Model model
    ) {
        model.addAttribute("message", message);
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
