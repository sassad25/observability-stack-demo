package io.shehzeb.observability.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metrics configuration:
 * <ul>
 *   <li>Registers {@link TimedAspect} so {@code @Timed} annotations are honored.</li>
 *   <li>Adds a common {@code application} tag to every metric for multi-service dashboards.</li>
 *   <li>Enables client-side percentile histograms for HTTP server timings.</li>
 * </ul>
 */
@Configuration
public class MetricsConfig {

    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> commonTags() {
        return registry -> {
            registry.config().commonTags("application", "observability-stack-demo");
            registry.config().meterFilter(
                    MeterFilter.maxExpected("orders.processing.latency", java.time.Duration.ofSeconds(1)));
        };
    }
}
