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
package org.springframework.cloud.netflix.hystrix;


import HttpStatus.OK;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Spencer Gibb
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HystrixOnlyApplication.class, webEnvironment = RANDOM_PORT, properties = { "management.endpoint.health.show-details=ALWAYS" })
@DirtiesContext
@ActiveProfiles("proxysecurity")
public class HystrixOnlyTests {
    private static final String BASE_PATH = new WebEndpointProperties().getBasePath();

    @LocalServerPort
    private int port;

    @Test
    public void testNormalExecution() {
        ResponseEntity<String> res = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/"), String.class);
        assertThat(res.getBody()).as("incorrect response").isEqualTo("Hello world");
    }

    @Test
    public void testFailureFallback() {
        ResponseEntity<String> res = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/fail"), String.class);
        assertThat(res.getBody()).as("incorrect fallback").isEqualTo("Fallback Hello world");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHystrixHealth() {
        Map map = getHealth();
        assertThat(map).containsKeys("details");
        Map details = ((Map) (map.get("details")));
        assertThat(details).containsKeys("hystrix");
        Map hystrix = ((Map) (details.get("hystrix")));
        assertThat(hystrix).containsEntry("status", "UP");
    }

    @Test
    public void testNoDiscoveryHealth() {
        Map<?, ?> map = getHealth();
        // There is explicitly no discovery, so there should be no discovery health key
        assertThat(map.containsKey("discovery")).as("Incorrect existing discovery health key").isFalse();
    }

    @Test
    public void testHystrixInnerMapMetrics() {
        // We have to hit any Hystrix command before Hystrix metrics to be populated
        String url = "http://localhost:" + (this.port);
        ResponseEntity<String> response = new TestRestTemplate().getForEntity(url, String.class);
        assertThat(response.getStatusCode()).as("bad response code").isEqualTo(OK);
        // Poller takes some time to realize for new metrics
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        Map<String, List<String>> map = ((Map<String, List<String>>) (getMetrics()));
        assertThat(map.get("names").contains("hystrix.latency.total")).as("There is no latencyTotal group key specified").isTrue();
        assertThat(map.get("names").contains("hystrix.latency.execution")).as("There is no latencyExecute group key specified").isTrue();
    }
}

