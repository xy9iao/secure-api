package sg.edu.nus.secure_api.service;

import org.springframework.stereotype.Service;

import sg.edu.nus.secure_api.model.LoginResponse;
import sg.edu.nus.secure_api.model.User;
import sg.edu.nus.secure_api.repository.UserRepository;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        String message = "User found in database. Password verified. Role = "
                + user.getRole()
                + ". JWT generated successfully.";

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getRole(),
                message
        );
    }
}