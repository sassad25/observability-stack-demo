package io.shehzeb.observability.api;

import io.shehzeb.observability.service.OrderProcessingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Endpoints to generate load so the metrics have something to show. In a real system these metrics
 * would come from genuine business operations.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderProcessingService service;

    public OrderController(OrderProcessingService service) {
        this.service = service;
    }

    @PostMapping("/process")
    public Map<String, Object> process() {
        String orderId = UUID.randomUUID().toString();
        boolean success = service.process(orderId);
        return Map.of("orderId", orderId, "success", success);
    }

    @GetMapping("/queue-depth")
    public Map<String, Integer> queueDepth() {
        return Map.of("depth", service.currentQueueDepth());
    }
}
