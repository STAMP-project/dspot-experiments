/**
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.metrics.export.prometheus;


import TextFormat.CONTENT_TYPE_004;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.actuate.endpoint.web.test.WebEndpointRunners;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Tests for {@link PrometheusScrapeEndpoint}.
 *
 * @author Jon Schneider
 */
@RunWith(WebEndpointRunners.class)
public class PrometheusScrapeEndpointIntegrationTests {
    private static WebTestClient client;

    @Test
    public void scrapeHasContentTypeText004() {
        PrometheusScrapeEndpointIntegrationTests.client.get().uri("/actuator/prometheus").exchange().expectStatus().isOk().expectHeader().contentType(MediaType.parseMediaType(CONTENT_TYPE_004));
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public PrometheusScrapeEndpoint prometheusScrapeEndpoint(CollectorRegistry collectorRegistry) {
            return new PrometheusScrapeEndpoint(collectorRegistry);
        }

        @Bean
        public CollectorRegistry collectorRegistry() {
            return new CollectorRegistry(true);
        }

        @Bean
        public MeterRegistry registry(CollectorRegistry registry) {
            return new io.micrometer.prometheus.PrometheusMeterRegistry(( k) -> null, registry, Clock.SYSTEM);
        }
    }
}

