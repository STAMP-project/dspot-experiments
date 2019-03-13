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
package sample.actuator.ui;


import HttpMethod.GET;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import MediaType.TEXT_HTML;
import java.util.Arrays;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Basic integration tests for demo application.
 *
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleActuatorUiApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHome() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        ResponseEntity<String> entity = this.restTemplate.withBasicAuth("user", getPassword()).exchange("/", GET, new org.springframework.http.HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).contains("<title>Hello");
    }

    @Test
    public void testCss() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/css/bootstrap.min.css", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        assertThat(entity.getBody()).contains("body");
    }

    @Test
    public void testMetrics() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = this.restTemplate.getForEntity("/actuator/metrics", Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    public void testError() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        ResponseEntity<String> entity = this.restTemplate.withBasicAuth("user", getPassword()).exchange("/error", GET, new org.springframework.http.HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(entity.getBody()).contains("<html>").contains("<body>").contains("Please contact the operator with the above information");
    }
}

