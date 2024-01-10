package ie.atu.order.OrderControllerTests;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;

import ie.atu.order.controller.OrderController;
import ie.atu.order.payload.order.OrderRequest;
import ie.atu.order.payload.order.OrderResponse;
import ie.atu.order.payload.payment.PaymentType;
import ie.atu.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
public class OrderControllerTestSuccess {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void placeOrder_shouldReturnOk() throws Exception {
        OrderRequest orderRequest = new OrderRequest(1L, 50L, 100L, PaymentType.PAYPAL);
        when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/order/placeorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk());
        }

        @Test
        public void getOrderDetails_shouldReturnOk() throws Exception {
        long orderId = 1L;

        OrderResponse.ProductDetails mockProductDetails = OrderResponse.ProductDetails.builder()
                .productName("Mock Product")
                .productID(123L)
                .quantity(2L)
                .price(50L)
                .build();

        OrderResponse.PaymentDetails mockPaymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentID(456L)
                .paymentType(PaymentType.CREDIT_CARD)
                .paymentStatus("SUCCESS")
                .paymentDate(Instant.now())
                .build();

        OrderResponse orderResponse = new OrderResponse(1, 50L, "1234", PaymentType.PAYPAL, "PLACED", Instant.now(),
                mockProductDetails, mockPaymentDetails);
        when(orderService.getOrderDetails(anyLong())).thenReturn(orderResponse);

        mockMvc.perform(get("/order/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderID").value(orderResponse.getOrderID()))
                .andExpect(jsonPath("$.amount").value(orderResponse.getAmount()))
                .andExpect(jsonPath("$.referenceNumber").value(orderResponse.getReferenceNumber()))
                .andExpect(jsonPath("$.paymentType").value(orderResponse.getPaymentType().toString()))
                .andExpect(jsonPath("$.orderStatus").value(orderResponse.getOrderStatus()))
                .andExpect(jsonPath("$.orderDate").exists())
                .andExpect(jsonPath("$.productDetails.productName").value(mockProductDetails.getProductName()))
                .andExpect(jsonPath("$.productDetails.productID").value(mockProductDetails.getProductID()))
                .andExpect(jsonPath("$.productDetails.quantity").value(mockProductDetails.getQuantity()))
                .andExpect(jsonPath("$.productDetails.price").value(mockProductDetails.getPrice()))
                .andExpect(jsonPath("$.paymentDetails.paymentID").value(mockPaymentDetails.getPaymentID()))
                .andExpect(
                        jsonPath("$.paymentDetails.paymentType").value(mockPaymentDetails.getPaymentType().toString()))
                .andExpect(jsonPath("$.paymentDetails.paymentStatus").value(mockPaymentDetails.getPaymentStatus()))
                .andExpect(jsonPath("$.paymentDetails.paymentDate").exists());
        }
}
