package sg.edu.nus.secure_api.service;

import org.springframework.stereotype.Service;

import sg.edu.nus.secure_api.model.LoginResponse;
import sg.edu.nus.secure_api.model.Profile;
import sg.edu.nus.secure_api.repository.ProfileRepository;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final ProfileRepository profileRepository;

    public AuthService(JwtService jwtService, ProfileRepository profileRepository) {
        this.jwtService = jwtService;
        this.profileRepository = profileRepository;
    }

    public LoginResponse login(String username, String password) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!profile.getPassword().equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtService.generateToken(profile.getUsername(), profile.getRole());

        String message = "Profile found in database. Password verified. Role = "
                + profile.getRole()
                + ". JWT generated successfully.";

        return new LoginResponse(
                token,
                profile.getUsername(),
                profile.getRole(),
                message
        );
    }
}
