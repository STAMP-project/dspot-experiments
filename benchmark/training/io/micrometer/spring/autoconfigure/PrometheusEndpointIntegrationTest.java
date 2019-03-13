/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring.autoconfigure;


import HttpHeaders.ACCEPT;
import HttpMethod.GET;
import HttpStatus.OK;
import MediaType.TEXT_PLAIN_VALUE;
import SpringBootTest.WebEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = PrometheusEndpointIntegrationTest.MetricsApp.class, properties = { "management.security.enabled=false", "management.metrics.export.prometheus.enabled=true" })
public class PrometheusEndpointIntegrationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void producesTextPlain() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCEPT, TEXT_PLAIN_VALUE);
        HttpEntity<Void> request = new HttpEntity(headers);
        ResponseEntity<String> result = testRestTemplate.exchange("/prometheus", GET, request, String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
    }

    @SpringBootApplication(scanBasePackages = "ignore")
    static class MetricsApp {}
}

