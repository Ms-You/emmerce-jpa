package commerce.emmerce_jpa.repository;

import commerce.emmerce_jpa.domain.Order;
import commerce.emmerce_jpa.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findAllByOrder(Order order);
}
