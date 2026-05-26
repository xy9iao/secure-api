package sg.edu.nus.secure_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sg.edu.nus.secure_api.model.LoginRequest;
import sg.edu.nus.secure_api.model.LoginResponse;
import sg.edu.nus.secure_api.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}