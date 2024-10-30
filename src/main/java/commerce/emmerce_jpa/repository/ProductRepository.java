package commerce.emmerce_jpa.repository;

import commerce.emmerce_jpa.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
