package sg.edu.nus.secure_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.secure_api.model.Product;
import sg.edu.nus.secure_api.security.JwtAuthenticationFilter;
import sg.edu.nus.secure_api.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getProducts(
            @RequestAttribute(JwtAuthenticationFilter.AUTH_USERNAME) String username,
            @RequestAttribute(JwtAuthenticationFilter.AUTH_ROLE) String role
    ) {
        return productService.findProductsFor(username, role);
    }
}
