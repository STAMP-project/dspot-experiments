/**
 * Copyright 2012-2018 the original author or authors.
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
package sample.actuator;


import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration tests for switching off management endpoints.
 *
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "management.server.port=-1" })
public class NoManagementSampleActuatorApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHome() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = this.restTemplate.withBasicAuth("user", getPassword()).getForEntity("/", Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = entity.getBody();
        assertThat(body.get("message")).isEqualTo("Hello Phil");
    }

    @Test
    public void testMetricsNotAvailable() {
        testHome();// makes sure some requests have been made

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = this.restTemplate.withBasicAuth("user", getPassword()).getForEntity("/metrics", Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}

