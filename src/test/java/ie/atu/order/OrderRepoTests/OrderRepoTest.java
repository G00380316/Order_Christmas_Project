package ie.atu.order.OrderRepoTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ie.atu.order.db.OrderRepo;
import ie.atu.order.exception.OrderServiceException;
import ie.atu.order.model.Order;


@SpringBootTest
public class OrderRepoTest {

    @Autowired
    private OrderRepo orderRepo;

    @Test
    public void shouldSaveAndRetrieveProduct() {

        Instant timeTest = Instant.now();
        
        // Given
        Order order = new Order(1L, 2L, 10, timeTest.truncatedTo(ChronoUnit.SECONDS), "SUCCESS", 100);
    

        // When
        Order savedOrder = orderRepo.save(order);

        // Then
        Optional<Order> retrievedOrder = orderRepo.findById(savedOrder.getId());
        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getId()).isEqualTo(1L);
        assertThat(retrievedOrder.get().getProductID()).isEqualTo(2L);
        assertThat(retrievedOrder.get().getQuantity()).isEqualTo(10);
        assertThat(retrievedOrder.get().getOrderDate()).isEqualTo(order.getOrderDate().truncatedTo(ChronoUnit.SECONDS));
        assertThat(retrievedOrder.get().getOrderStatus()).isEqualTo("SUCCESS");
        assertThat(retrievedOrder.get().getAmount()).isEqualTo(100);
    }

    @Test
    public void shouldThrowExceptionWhenProductIdNotFound() {
        // Given
        long nonExistentProductId = 999L;

        // When/Then
        assertThrows(OrderServiceException.class, () -> orderRepo.findById(nonExistentProductId)
                .orElseThrow(() -> new OrderServiceException("Product not found", "PRODUCT_NOT_FOUND",404)));
    }
}
