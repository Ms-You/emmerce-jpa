package commerce.emmerce_jpa.repository;

import commerce.emmerce_jpa.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
