package sg.edu.nus.secure_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import sg.edu.nus.secure_api.security.JwtAuthenticationFilter;

@Controller
public class ProductPageController {

    private final ProductController productController;

    public ProductPageController(ProductController productController) {
        this.productController = productController;
    }

    @GetMapping("/products")
    public String productsPage(
            @RequestAttribute(JwtAuthenticationFilter.AUTH_USERNAME) String username,
            @RequestAttribute(JwtAuthenticationFilter.AUTH_ROLE) String role,
            Model model
    ) {
        model.addAttribute("username", username);
        model.addAttribute("role", role);
        model.addAttribute("products", productController.getProducts(username, role));

        return "products";
    }
}
