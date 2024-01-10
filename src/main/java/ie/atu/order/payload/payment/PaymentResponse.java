package ie.atu.order.payload.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    
    private long paymentID;

    private String status;

    private PaymentType paymentType;

    private long amount;

    private Instant paymentDate;

    private long orderID;
}
