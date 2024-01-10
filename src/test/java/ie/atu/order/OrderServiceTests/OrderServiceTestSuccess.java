package ie.atu.order.OrderServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ie.atu.order.db.OrderRepo;
import ie.atu.order.model.Order;
import ie.atu.order.payload.order.OrderRequest;
import ie.atu.order.payload.order.OrderResponse;
import ie.atu.order.payload.payment.PaymentResponse;
import ie.atu.order.payload.payment.PaymentType;
import ie.atu.order.payload.product.ProductResponse;
import ie.atu.order.service.OrderService;
import ie.atu.order.service.PaymentService;
import ie.atu.order.service.ProductService;



@ExtendWith(MockitoExtension.class)
public class OrderServiceTestSuccess {

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
    void placeOrder_shouldReturnOrderId() {
    
        Order order = new Order(1L, 1L, 10, Instant.now().truncatedTo(ChronoUnit.SECONDS), "SUCCESS", 100L);
        OrderRequest orderRequest = new OrderRequest(1L, 100L, 50L, PaymentType.PAYPAL);
        

        given(orderRepo.save(any())).willReturn(order);

        // Mocking the productService.reduceQuantity method
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        when(paymentService.doPayment(any())).thenReturn(null);

        // Act
        long orderId = orderService.placeOrder(orderRequest);

        // Assert
        assertEquals(1L, orderId);
        verify(orderRepo, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any());
    }

    @Test
    void getOrderDetails_shouldReturnOrderResponse() {
        // Arrange
        long orderId = 1L;

        // Mocking order details
        Order order = new Order();
        order.setId(orderId);
        order.setAmount(50L);
        order.setOrderStatus("PLACED");
        order.setOrderDate(Instant.now());

        when(orderRepo.findById(orderId)).thenReturn(java.util.Optional.of(order));

        // Mocking product details
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductName("Sample Product");
        productResponse.setProductId(123L);

        when(restTemplate.getForObject(
                "http://localhost:8081/product/" + order.getProductID(),
                ProductResponse.class))
                .thenReturn(productResponse);

        // Mocking payment details
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentID(456L);
        paymentResponse.setStatus("SUCCESS");
        paymentResponse.setPaymentDate(Instant.now());

        when(restTemplate.getForObject(
                "http://localhost:8082/payment/order/" + order.getId(),
                PaymentResponse.class))
                .thenReturn(paymentResponse);

        // Act
        OrderResponse orderResponse = orderService.getOrderDetails(orderId);

        // Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderID());
        assertEquals(order.getAmount(), orderResponse.getAmount());
        assertEquals(order.getOrderStatus(), orderResponse.getOrderStatus());
        assertEquals(order.getOrderDate(), orderResponse.getOrderDate());
        assertEquals(productResponse.getProductName(), orderResponse.getProductDetails().getProductName());
        assertEquals(productResponse.getProductId(), orderResponse.getProductDetails().getProductID());
        assertEquals(paymentResponse.getPaymentID(), orderResponse.getPaymentDetails().getPaymentID());
        assertEquals(paymentResponse.getStatus(), orderResponse.getPaymentDetails().getPaymentStatus());
        assertEquals(paymentResponse.getPaymentDate(), orderResponse.getPaymentDetails().getPaymentDate());
    }
}
