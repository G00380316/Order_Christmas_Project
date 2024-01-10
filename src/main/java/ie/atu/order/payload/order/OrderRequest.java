package ie.atu.order.payload.order;

import ie.atu.order.payload.payment.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    
    private long productID;

    private long totalAmount;

    private long quantity;
    
    private PaymentType paymentType;

}
