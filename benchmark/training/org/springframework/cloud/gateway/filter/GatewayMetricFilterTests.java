/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.filter;


import HttpHeaders.HOST;
import HttpMethod.GET;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class GatewayMetricFilterTests extends BaseWebClientTests {
    private static final String REQUEST_METRICS_NAME = "gateway.requests";

    @Autowired
    private MeterRegistry meterRegistry;

    @Value("${test.uri}")
    private String testUri;

    @Test
    public void gatewayRequestsMeterFilterHasTags() {
        testClient.get().uri("/headers").header(HOST, "www.metricshappypath.org").exchange().expectStatus().isOk().returnResult(String.class).consumeWith(( result) -> {
            assertMetricsContainsTag("outcome", HttpStatus.Series.SUCCESSFUL.name());
            assertMetricsContainsTag("status", HttpStatus.OK.name());
            assertMetricsContainsTag("routeId", "test_metrics_happy_path");
            assertMetricsContainsTag("routeUri", "lb://testservice");
        });
    }

    @Test
    public void gatewayRequestsMeterFilterHasTagsForBadTargetUri() {
        testClient.get().uri("/badtargeturi").exchange().expectStatus().is5xxServerError().returnResult(String.class).consumeWith(( result) -> {
            assertMetricsContainsTag("outcome", HttpStatus.Series.SERVER_ERROR.name());
            assertMetricsContainsTag("status", HttpStatus.INTERNAL_SERVER_ERROR.name());
            assertMetricsContainsTag("routeId", "default_path_to_httpbin");
            assertMetricsContainsTag("routeUri", testUri);
        });
    }

    @Test
    public void hasMetricsForSetStatusFilter() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HOST, "www.setcustomstatusmetrics.org");
        // cannot use netty client since we cannot read custom http status
        ResponseEntity<String> response = new TestRestTemplate().exchange(((baseUri) + "/headers"), GET, new org.springframework.http.HttpEntity(headers), String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(432);
        assertMetricsContainsTag("outcome", "CUSTOM");
        assertMetricsContainsTag("status", "432");
        assertMetricsContainsTag("routeId", "test_custom_http_status_metrics");
        assertMetricsContainsTag("routeUri", testUri);
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @RestController
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class CustomConfig {
        @Value("${test.uri}")
        protected String testUri;

        @Bean
        public RouteLocator myRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route("test_metrics_happy_path", ( r) -> r.host("*.metricshappypath.org").uri(testUri)).route("test_custom_http_status_metrics", ( r) -> r.host("*.setcustomstatusmetrics.org").filters(( f) -> f.setStatus(432)).uri(testUri)).build();
        }

        @RequestMapping("/httpbin/badtargeturi")
        public String exception() {
            throw new RuntimeException("an error");
        }
    }
}

