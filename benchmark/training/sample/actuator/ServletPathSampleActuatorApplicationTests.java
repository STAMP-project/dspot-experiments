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


import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
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
 * Integration tests for endpoints configuration.
 *
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "spring.mvc.servlet.path=/spring" })
public class ServletPathSampleActuatorApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testErrorPath() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = this.restTemplate.withBasicAuth("user", getPassword()).getForEntity("/spring/error", Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = entity.getBody();
        assertThat(body.get("error")).isEqualTo("None");
        assertThat(body.get("status")).isEqualTo(999);
    }

    @Test
    public void testHealth() {
        ResponseEntity<String> entity = this.restTemplate.withBasicAuth("user", getPassword()).getForEntity("/spring/actuator/health", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    public void testHomeIsSecure() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = this.restTemplate.getForEntity("/spring/", Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(UNAUTHORIZED);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = entity.getBody();
        assertThat(body.get("error")).isEqualTo("Unauthorized");
        assertThat(entity.getHeaders()).doesNotContainKey("Set-Cookie");
    }
}

