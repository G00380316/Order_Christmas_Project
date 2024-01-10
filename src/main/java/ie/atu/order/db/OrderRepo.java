package ie.atu.order.db;

import org.springframework.data.jpa.repository.JpaRepository;

import ie.atu.order.model.Order;

public interface OrderRepo extends JpaRepository<Order,Long>{
    
}
