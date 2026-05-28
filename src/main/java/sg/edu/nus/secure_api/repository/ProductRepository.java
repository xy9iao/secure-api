package sg.edu.nus.secure_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.secure_api.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOwner(String owner);
}
