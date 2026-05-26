package sg.edu.nus.secure_api.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String login(String username, String password) {
        if ("alice".equals(username) && "password123".equals(password)) {
            return jwtService.generateToken(username);
        }

        throw new RuntimeException("Invalid username or password");
    }
}