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
package org.springframework.cloud.gateway.sample;


import GatewaySampleApplication.HELLO_FROM_FAKE_ACTUATOR_METRICS_GATEWAY_REQUESTS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cloud.test.ClassPathExclusions;
import org.springframework.cloud.test.ModifiedClassPathRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;


@RunWith(ModifiedClassPathRunner.class)
@ClassPathExclusions({ "micrometer-*.jar", "spring-boot-actuator-*.jar", "spring-boot-actuator-autoconfigure-*.jar" })
@DirtiesContext
public class GatewaySampleApplicationWithoutMetricsTests {
    protected static int port;

    protected WebTestClient webClient;

    protected String baseUri;

    @Test
    public void actuatorMetrics() {
        init(GatewaySampleApplicationTests.TestConfig.class);
        webClient.get().uri("/get").exchange().expectStatus().isOk();
        webClient.get().uri((("http://localhost:" + (GatewaySampleApplicationWithoutMetricsTests.port)) + "/actuator/metrics/gateway.requests")).exchange().expectStatus().isOk().expectBody(String.class).isEqualTo(HELLO_FROM_FAKE_ACTUATOR_METRICS_GATEWAY_REQUESTS);
    }
}

