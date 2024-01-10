package ie.atu.order.OrderServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import ie.atu.order.db.OrderRepo;
import ie.atu.order.model.Order;
import ie.atu.order.payload.order.OrderRequest;
import ie.atu.order.payload.payment.PaymentResponse;
import ie.atu.order.payload.payment.PaymentType;
import ie.atu.order.payload.product.ProductResponse;
import ie.atu.order.service.OrderService;
import ie.atu.order.service.PaymentService;
import ie.atu.order.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTestFails {

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Order order;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Failure Test: placeOrder should throw an exception when productService fails to reduce quantity")
    void placeOrder_shouldThrowExceptionWhenProductServiceFails() {
    // Arrange
    OrderRequest orderRequest = new OrderRequest(1L, 100L, 50L, PaymentType.PAYPAL);

    // Configure the orderRepo.save method to return an order
    Order savedOrder = new Order(1L, 1L, 50L, Instant.now(), "PLACED", 100L);
    given(orderRepo.save(any())).willReturn(savedOrder);

    // Configure productService.reduceQuantity to fail
    doThrow(new RuntimeException("Product quantity reduction failed"))
            .when(productService).reduceQuantity(anyLong(), anyLong());

    // Act & Assert
    assertThrows(Exception.class, () -> orderService.placeOrder(orderRequest));

    // Verify that orderRepo.save is invoked once
    verify(orderRepo, times(1)).save(any());
    // Verify that productService.reduceQuantity is invoked once
    verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
    // Verify that paymentService.doPayment is never invoked due to productService failure
    verify(paymentService, never()).doPayment(any());
    }


    @Test
    @DisplayName("Failure Test: getOrderDetails should throw an exception when order is not found")
    void getOrderDetails_shouldThrowExceptionWhenOrderNotFound() {
        // Arrange
        long orderId = 1L;

        when(orderRepo.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> orderService.getOrderDetails(orderId));
        verify(restTemplate, never()).getForObject(anyString(), eq(ProductResponse.class));
        verify(restTemplate, never()).getForObject(anyString(), eq(PaymentResponse.class));
    }

    @Test
    @DisplayName("Failure Test: getOrderDetails should throw an exception when product details are not found")
    void getOrderDetails_shouldThrowExceptionWhenProductDetailsNotFound() {
        // Arrange
        long orderId = 1L;

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(new Order()));

        when(restTemplate.getForObject(
                "http://localhost:8081/product/" + orderId,
                ProductResponse.class))
                .thenReturn(null);

        // Act & Assert
        assertThrows(Exception.class, () -> orderService.getOrderDetails(orderId));
        verify(restTemplate, never()).getForObject(anyString(), eq(PaymentResponse.class));
    }

    @Test
    @DisplayName("Failure Test: getOrderDetails should throw an exception when payment details are not found")
    void getOrderDetails_shouldThrowExceptionWhenPaymentDetailsNotFound() {
        // Arrange
        long orderId = 1L;

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(new Order()));

        when(restTemplate.getForObject(
                "http://localhost:8081/product/" + orderId,
                ProductResponse.class))
                .thenReturn(new ProductResponse());

        when(restTemplate.getForObject(
                "http://localhost:8082/payment/order/" + orderId,
                PaymentResponse.class))
                .thenReturn(null);

        // Act & Assert
        assertThrows(Exception.class, () -> orderService.getOrderDetails(orderId));
    }
}
