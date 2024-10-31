package commerce.emmerce_jpa.service;

import commerce.emmerce_jpa.config.SecurityUtil;
import commerce.emmerce_jpa.config.exception.ErrorCode;
import commerce.emmerce_jpa.config.exception.GlobalException;
import commerce.emmerce_jpa.domain.*;
import commerce.emmerce_jpa.dto.DeliveryDTO;
import commerce.emmerce_jpa.dto.OrderDTO;
import commerce.emmerce_jpa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;
    private final DeliveryRepository deliveryRepository;

    private Mono<Member> findCurrentMember() {
        return SecurityUtil.getCurrentMemberName()
                .flatMap(name -> Mono.fromCallable(() -> memberRepository.findByName(name))
                        .flatMap(optionalMember -> optionalMember
                                .map(Mono::just)
                                .orElseGet(() -> Mono.error(new GlobalException(ErrorCode.MEMBER_NOT_FOUND)))
                        )
                );
    }

    public Mono<OrderDTO.OrderCreateResp> startOrder(OrderDTO.OrderReq orderReq) {
        return findCurrentMember()
                .flatMap(member -> makeOrder(member, orderReq));
    }

    private Mono<OrderDTO.OrderCreateResp> makeOrder(Member member, OrderDTO.OrderReq orderReq) {
        return Mono.fromCallable(() -> {
                    Order savedOrder = orderRepository.save(Order.createOrder()
                            .orderDate(LocalDateTime.now())
                            .orderStatus(OrderStatus.ING)
                            .member(member)
                            .build());
                    log.info("생성된 order_id: {}", savedOrder.getOrderId());
                    return savedOrder;
                })
                .flatMap(savedOrder -> saveProductsForOrder(savedOrder, orderReq.getOrderProductList())
                        .then(Mono.fromCallable(() -> orderProductRepository.findAllByOrder(savedOrder))
                                .flatMapMany(orderProducts -> Flux.fromIterable(orderProducts)
                                        .flatMap(orderProduct -> createDeliveryForOrder(orderReq.getDeliveryReq(), orderProduct.getOrderProductId()))
                                )
                                .then())
                        .then(Mono.just(new OrderDTO.OrderCreateResp(savedOrder.getOrderId())))
                );
    }

    private Mono<Void> saveProductsForOrder(Order order, List<OrderDTO.OrderProductReq> orderProductReqList) {
        return Flux.fromIterable(orderProductReqList)
                .concatMap(orderProductReq ->
                        Mono.fromCallable(() -> productRepository.findById(orderProductReq.getProductId())
                                        .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."))) // Optional 처리
                                .flatMap(product -> {
                                    if (product.getStockQuantity() < orderProductReq.getTotalCount()) {
                                        return Mono.error(new RuntimeException("'" + product.getName() + "' 상품의 재고가 부족합니다."));
                                    } else {
                                        OrderProduct orderProduct = OrderProduct.builder()
                                                .totalPrice(product.getDiscountPrice() * orderProductReq.getTotalCount())
                                                .totalCount(orderProductReq.getTotalCount())
                                                .order(order)
                                                .product(product)
                                                .build();
                                        return Mono.fromCallable(() -> orderProductRepository.save(orderProduct)); // save 호출을 Mono로 감쌉니다.
                                    }
                                })
                )
                .then();
    }


    private Mono<Void> createDeliveryForOrder(DeliveryDTO.DeliveryReq deliveryReq, Long orderProductId) {
        return Mono.fromCallable(() -> orderProductRepository.findById(orderProductId)
                        .orElseThrow(() -> new GlobalException(ErrorCode.ORDER_PRODUCT_NOT_FOUND)))
                .flatMap(orderProduct ->
                        Mono.fromCallable(() -> deliveryRepository.save(Delivery.createDelivery()
                                .name(deliveryReq.getName())
                                .tel(deliveryReq.getTel())
                                .email(deliveryReq.getEmail())
                                .city(deliveryReq.getCity())
                                .street(deliveryReq.getStreet())
                                .zipcode(deliveryReq.getZipcode())
                                .deliveryStatus(DeliveryStatus.READY)
                                .orderProduct(orderProduct)
                                .build())
                        ).then()
                );
    }

}
