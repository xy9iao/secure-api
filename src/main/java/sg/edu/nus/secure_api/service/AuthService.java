package sg.edu.nus.secure_api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sg.edu.nus.secure_api.model.Profile;
import sg.edu.nus.secure_api.repository.ProfileRepository;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final ProfileRepository profileRepository;
    private final Path jwtOutputFile;
    private final int jwtOutputMaxEntries;

    public AuthService(
            JwtService jwtService,
            ProfileRepository profileRepository,
            @Value("${jwt.output-file:generated-jwts.txt}") String jwtOutputFile,
            @Value("${jwt.output-max-entries:5}") int jwtOutputMaxEntries
    ) {
        this.jwtService = jwtService;
        this.profileRepository = profileRepository;
        this.jwtOutputFile = Path.of(jwtOutputFile);
        this.jwtOutputMaxEntries = Math.max(1, jwtOutputMaxEntries);
    }

    public String login(String username, String password) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!profile.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtService.generateToken(profile.getUsername(), profile.getRole());
        saveTokenToLocalFile(profile.getUsername(), token);

        return token;
    }

    private void saveTokenToLocalFile(String username, String token) {
        try {
            Path parent = jwtOutputFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();
            if (Files.exists(jwtOutputFile)) {
                lines.addAll(Files.readAllLines(jwtOutputFile));
            }

            lines.add(LocalDateTime.now() + " | " + username + " | " + token);

            while (lines.size() > jwtOutputMaxEntries) {
                lines.remove(0);
            }

            Files.writeString(
                    jwtOutputFile,
                    String.join(System.lineSeparator(), lines) + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("JWT generated but could not be written to local file", e);
        }
    }
}
