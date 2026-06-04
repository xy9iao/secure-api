package sg.edu.nus.secure_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.secure_api.model.Product;
import sg.edu.nus.secure_api.repository.ProductRepository;
import sg.edu.nus.secure_api.security.JwtAuthenticationFilter;

@RestController
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/api/products")
    public List<Product> productsApi(
            @RequestAttribute(JwtAuthenticationFilter.AUTH_USERNAME) String username,
            @RequestAttribute(JwtAuthenticationFilter.AUTH_ROLE) String role
    ) {
        return getProducts(username, role);
    }

    public List<Product> getProducts(String username, String role) {
        if ("ADMIN".equals(role)) {
            return productRepository.findAll();
        }

        return productRepository.findByOwner(username);
    }
}
