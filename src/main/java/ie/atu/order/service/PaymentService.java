package ie.atu.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ie.atu.order.payload.payment.PaymentRequest;

@FeignClient(name = "PAYMENT-SERVICE", url = "http://localhost:8082/payment")
public interface PaymentService {
    
    @PostMapping("")
    ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

}
