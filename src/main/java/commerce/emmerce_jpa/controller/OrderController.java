package commerce.emmerce_jpa.controller;

import commerce.emmerce_jpa.dto.OrderDTO;
import commerce.emmerce_jpa.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("/order")
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Mono<OrderDTO.OrderCreateResp> orderProducts(@RequestBody OrderDTO.OrderReq orderReq) {
        return orderService.startOrder(orderReq);
    }

}
