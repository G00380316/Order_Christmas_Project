package ie.atu.order.payload.order;

import java.time.Instant;

import ie.atu.order.payload.payment.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private long orderID;

    private long amount;

    private String referenceNumber;

    private PaymentType paymentType;

    private String orderStatus;

    private Instant orderDate;

    private ProductDetails productDetails;

    private PaymentDetails paymentDetails;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductDetails {

        private String productName;

        private long productID;

        private long quantity;

        private long price;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDetails {

        private long paymentID;

        private PaymentType paymentType;

        private String paymentStatus;

        private Instant paymentDate;
    }
}
