package cz.ivosahlik.testcontainersystem.service;

import cz.ivosahlik.testcontainersystem.model.Order;
import cz.ivosahlik.testcontainersystem.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

	public Order createOrder(Order order) {
    	Order savedOrder = orderRepository.save(order);
    	log.info("Order {} saved to database:", savedOrder);
        return savedOrder;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(Long id, Order order) {
        Order existingOrder = getOrder(id);
        existingOrder.setStatus(order.getStatus());
        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        Order order = getOrder(id);
        orderRepository.delete(order);
    }
}
