package io.shehzeb.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.shehzeb.observability.service.OrderProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that business operations register and populate the expected custom Micrometer meters
 * (counter, timer, gauge) with the correct names and tags. These are the exact meters exported to
 * Prometheus at {@code /actuator/prometheus} (verified to return 200 when the app runs).
 *
 * <p>Note: Spring Boot disables metrics <em>export</em> registries during tests, so assertions use
 * the always-present composite {@link MeterRegistry} rather than the Prometheus export registry.</p>
 */
@SpringBootTest
class ObservabilityIntegrationTest {

    @Autowired
    OrderProcessingService service;

    @Autowired
    MeterRegistry registry;

    @Test
    void processingOrders_populatesCounterTimerAndGauge() {
        for (int i = 0; i < 25; i++) {
            service.process("order-" + i);
        }

        double successes = registry.get("orders.processed").tag("outcome", "success").counter().count();
        double failures = registry.get("orders.processed").tag("outcome", "failure").counter().count();
        long timerCount = registry.get("orders.processing.latency").timer().count();
        double queueDepth = registry.get("orders.queue.depth").gauge().value();

        assertThat(successes + failures).isEqualTo(25.0);
        assertThat(successes).isGreaterThan(0.0);
        assertThat(timerCount).isEqualTo(25L);
        // Queue drains after processing completes.
        assertThat(queueDepth).isEqualTo(0.0);
    }

    @Test
    void metricsCarryCommonApplicationTag() {
        service.process("tagged-order");
        // The common tag configured in MetricsConfig must be applied to custom meters.
        assertThat(registry.get("orders.processed").tag("application", "observability-stack-demo")
                .tag("outcome", "success").counter()).isNotNull();
    }
}
