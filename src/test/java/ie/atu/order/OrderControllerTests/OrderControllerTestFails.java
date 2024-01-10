package ie.atu.order.OrderControllerTests;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ie.atu.order.controller.OrderController;
import ie.atu.order.exception.OrderServiceException;
import ie.atu.order.payload.order.OrderRequest;
import ie.atu.order.payload.payment.PaymentType;
import ie.atu.order.service.OrderService;

@WebMvcTest(OrderController.class)
public class OrderControllerTestFails {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void placeOrder_shouldReturnBadRequest() throws Exception {
                OrderRequest orderRequest = new OrderRequest(1, 50L, -100L, PaymentType.PAYPAL);
                when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(1L);

                mockMvc.perform(post("/order/placeorder")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void getOrderDetails_shouldReturnisNotFound() throws Exception {
                long orderId = 1L;

                when(orderService.getOrderDetails(anyLong()))
                                .thenReturn(null)
                                .thenThrow(new OrderServiceException("Error Code:", "ORDER_NOT_FOUND", 404));

                mockMvc.perform(get("/order/{orderId}", orderId))
                                .andExpect(status().isNotFound());
        }
}
