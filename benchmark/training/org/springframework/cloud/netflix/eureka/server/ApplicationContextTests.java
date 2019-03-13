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
package org.springframework.cloud.netflix.eureka.server;


import HttpMethod.GET;
import HttpStatus.OK;
import MediaType.APPLICATION_JSON;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationContextTests.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "spring.application.name=eureka", "server.servlet.context-path=/context", "management.security.enabled=false", "management.endpoints.web.exposure.include=*" })
public class ApplicationContextTests {
    private static final String BASE_PATH = new WebEndpointProperties().getBasePath();

    @LocalServerPort
    private int port = 0;

    @Test
    public void catalogLoads() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/context/eureka/apps"), Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void dashboardLoads() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/context/"), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        String body = entity.getBody();
        // System.err.println(body);
        assertThat(body.contains("eureka/js")).isTrue();
        assertThat(body.contains("eureka/css")).isTrue();
        // The "DS Replicas"
        assertThat(body.contains("<a href=\"http://localhost:8761/eureka/\">localhost</a>")).isTrue();
    }

    @Test
    public void cssAvailable() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/context/eureka/css/wro.css"), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void jsAvailable() {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/context/eureka/js/wro.js"), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void adminLoads() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(APPLICATION_JSON));
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate().exchange((((("http://localhost:" + (this.port)) + "/context") + (ApplicationContextTests.BASE_PATH)) + "/env"), GET, new org.springframework.http.HttpEntity("parameters", headers), Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableEurekaServer
    protected static class Application {}
}

