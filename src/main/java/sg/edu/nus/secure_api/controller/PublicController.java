package sg.edu.nus.secure_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    @GetMapping("/api/public/hello")
    public String publicHello() {
        return "Hello, this is a public API. No JWT token is required.";
    }
}