package io.shehzeb.observability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Observability Stack Demo.
 *
 * <p>A Spring Boot service instrumented with Micrometer custom metrics (counters, timers, gauges)
 * exposed at {@code /actuator/prometheus}. The accompanying docker-compose stack scrapes these with
 * Prometheus and visualizes them in a provisioned Grafana dashboard.</p>
 */
@SpringBootApplication
public class ObservabilityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityDemoApplication.class, args);
    }
}
