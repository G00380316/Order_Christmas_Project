package ie.atu.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.atu.order.payload.order.OrderRequest;
import ie.atu.order.payload.order.OrderResponse;
import ie.atu.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/order")
@Log4j2
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/placeorder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {

        log.info("Placing Order method is called");

        log.info("orderRequest: {} being proccessed", orderRequest.toString());

        if (orderRequest.getTotalAmount() != 0
                && orderRequest.getQuantity() != 0
                && orderRequest.getTotalAmount() >= 0 && orderRequest.getQuantity() > 0) {
            try {
                long orderId = orderService.placeOrder(orderRequest);
                log.info("Order processed Id: {}", orderId);
                return new ResponseEntity<>(orderId, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long orderId) {

        log.info("Getting Order Details method was called");

        OrderResponse orderResponse = orderService.getOrderDetails(orderId);

        try {
            log.info("orderResponse : " + orderResponse.toString());
            return new ResponseEntity<>(orderResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while processing order details", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}