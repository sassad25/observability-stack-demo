# Observability Stack Demo

A Spring Boot service instrumented with **Micrometer** custom metrics, scraped by **Prometheus** and
visualized in a provisioned **Grafana** dashboard — a complete, runnable observability stack via
`docker compose up`.

> Author: **Muhammad Shehzeb Assad** — Senior Full Stack Java Developer
> Portfolio: https://github.com/sassad25

---

## What it demonstrates

- **Custom Micrometer instruments** on real business operations:
  - **Counter** `orders.processed` (tagged `outcome=success|failure`)
  - **Timer** `orders.processing.latency` with a percentile histogram
  - **Gauge** `orders.queue.depth` for in-flight work
  - **`@Timed`** method timing via a registered `TimedAspect`
- **Prometheus exposition** at `/actuator/prometheus`, scraped every 5s.
- **Grafana** auto-provisioned with a Prometheus datasource and a ready-made dashboard
  (throughput, latency percentiles, failure ratio, queue depth, JVM memory).
- **Common tags** (`application`) applied to every meter for multi-service dashboards.
- **SRE-friendly**: histogram buckets and SLA boundaries configured for latency SLOs.

## Run the whole stack

```bash
docker compose up --build
```

| Service    | URL                                            |
|------------|------------------------------------------------|
| App        | http://localhost:8086                          |
| Metrics    | http://localhost:8086/actuator/prometheus      |
| Prometheus | http://localhost:9090                          |
| Grafana    | http://localhost:3000  (admin / admin)         |

Generate some load, then watch the dashboard update:

```bash
# fire some orders
for /l %i in (1,1,200) do curl -s -X POST http://localhost:8086/api/v1/orders/process >NUL
```

(or on bash: `for i in $(seq 1 200); do curl -s -X POST localhost:8086/api/v1/orders/process >/dev/null; done`)

Open Grafana → dashboard **“Observability Stack Demo”**.

## Endpoints

| Method | Path                         | Description                              |
|--------|------------------------------|------------------------------------------|
| POST   | `/api/v1/orders/process`     | Process an order (emits metrics)         |
| GET    | `/api/v1/orders/queue-depth` | Current in-flight queue depth            |
| GET    | `/actuator/prometheus`       | Prometheus scrape endpoint               |
| GET    | `/actuator/health`           | Liveness/readiness                       |

## Testing

```bash
mvn clean verify
```

Tests assert that business operations populate the counter, timer, and gauge with the correct names
and tags (the same meters exported to Prometheus).

## Tech stack

Java 17 · Spring Boot 3.3 · Spring Boot Actuator · Micrometer + micrometer-registry-prometheus ·
Prometheus · Grafana · Docker Compose · JUnit 5.

## Layout

```
observability-stack-demo/
├── src/main/java/io/shehzeb/observability/
│   ├── service/OrderProcessingService.java   # counter + timer + gauge
│   ├── config/MetricsConfig.java             # TimedAspect + common tags + SLA
│   └── api/OrderController.java              # endpoints to generate load
├── monitoring/
│   ├── prometheus.yml                        # scrape config
│   └── grafana/provisioning/                 # datasource + dashboard
└── docker-compose.yml                        # app + prometheus + grafana
```

## License

MIT © Muhammad Shehzeb Assad
