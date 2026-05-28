package sg.edu.nus.secure_api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/hello")
    public String adminHello() {
        return "Hello, this is an ADMIN-only API. You can only see this with an ADMIN JWT.";
    }
}