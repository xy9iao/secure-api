package sg.edu.nus.secure_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sg.edu.nus.secure_api.model.Product;
import sg.edu.nus.secure_api.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findProductsFor(String username, String role) {
        if ("ADMIN".equals(role)) {
            return productRepository.findAll();
        }

        return productRepository.findByOwner(username);
    }
}
