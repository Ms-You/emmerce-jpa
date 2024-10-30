package commerce.emmerce_jpa.repository;

import commerce.emmerce_jpa.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
