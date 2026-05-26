package sg.edu.nus.secure_api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/hello")
    public String secureHello() {
        return "Hello, this is a protected API. You can only see this with a valid JWT.";
    }
}