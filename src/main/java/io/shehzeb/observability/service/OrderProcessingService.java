package io.shehzeb.observability.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Example domain service demonstrating the three core Micrometer instrument types:
 * <ul>
 *   <li><b>Counter</b> — monotonic count of processed orders, tagged by outcome.</li>
 *   <li><b>Timer</b> — processing latency distribution (also exposed as a percentile histogram).</li>
 *   <li><b>Gauge</b> — current depth of the in-flight queue.</li>
 * </ul>
 */
@Service
public class OrderProcessingService {

    private final Counter processedCounter;
    private final Counter failedCounter;
    private final Timer processingTimer;
    private final AtomicInteger queueDepth = new AtomicInteger(0);

    public OrderProcessingService(MeterRegistry registry) {
        this.processedCounter = Counter.builder("orders.processed")
                .description("Total orders processed successfully")
                .tag("outcome", "success")
                .register(registry);
        this.failedCounter = Counter.builder("orders.processed")
                .description("Total orders that failed processing")
                .tag("outcome", "failure")
                .register(registry);
        this.processingTimer = Timer.builder("orders.processing.latency")
                .description("Order processing latency")
                .publishPercentileHistogram()
                .register(registry);
        registry.gauge("orders.queue.depth", queueDepth);
    }

    /**
     * Simulates processing an order: records latency on a timer, increments success/failure
     * counters, and reflects queue depth on a gauge.
     */
    @Timed(value = "orders.process.method", description = "Timing of the process() method")
    public boolean process(String orderId) {
        queueDepth.incrementAndGet();
        try {
            return processingTimer.record(() -> {
                sleepQuietly(ThreadLocalRandom.current().nextInt(5, 60));
                boolean success = ThreadLocalRandom.current().nextInt(100) < 90; // ~10% failures
                if (success) {
                    processedCounter.increment();
                } else {
                    failedCounter.increment();
                }
                return success;
            });
        } finally {
            queueDepth.decrementAndGet();
        }
    }

    public int currentQueueDepth() {
        return queueDepth.get();
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
