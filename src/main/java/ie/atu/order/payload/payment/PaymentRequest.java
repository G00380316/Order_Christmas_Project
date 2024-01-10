package ie.atu.order.payload.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    
    private long orderID;

    private String referenceNumber;

    private PaymentType paymentType;

    private long amount;
}