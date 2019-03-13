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
package org.springframework.cloud.netflix.zuul;


import HttpMethod.GET;
import HttpStatus.OK;
import RoutesEndpoint.RouteDetails;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.netflix.zuul.test.NoSecurityConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 *
 * @author Ryan Baxter
 * @author Gregor Zurowski
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, value = { "zuul.routes.sslservice.url=https://localhost:8443", "management.security.enabled=false", "management.endpoints.web.exposure.include=*" })
@DirtiesContext
public class RoutesEndpointIntegrationTests {
    private static final String BASE_PATH = new WebEndpointProperties().getBasePath();

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoutesEndpointIntegrationTests.SimpleZuulProxyApplication.RoutesRefreshListener refreshListener;

    @Test
    @SuppressWarnings("unchecked")
    public void getRoutesTest() {
        ResponseEntity<Map> entity = restTemplate.getForEntity(((RoutesEndpointIntegrationTests.BASE_PATH) + "/routes"), Map.class);
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(OK);
        Map<String, String> routes = entity.getBody();
        assertThat(routes.get("/sslservice/**")).isEqualTo("https://localhost:8443");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void postRoutesTest() {
        ResponseEntity<Map> entity = restTemplate.postForEntity(((RoutesEndpointIntegrationTests.BASE_PATH) + "/routes"), null, Map.class);
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(OK);
        Map<String, String> routes = entity.getBody();
        assertThat(routes.get("/sslservice/**")).isEqualTo("https://localhost:8443");
        assertThat(refreshListener.wasCalled()).isTrue();
    }

    @Test
    public void getRouteDetailsTest() {
        ResponseEntity<Map<String, RoutesEndpoint.RouteDetails>> responseEntity = restTemplate.exchange(((RoutesEndpointIntegrationTests.BASE_PATH) + "/routes/details"), GET, null, new org.springframework.core.ParameterizedTypeReference<Map<String, RoutesEndpoint.RouteDetails>>() {});
        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        RoutesEndpoint.RouteDetails details = responseEntity.getBody().get("/sslservice/**");
        assertThat(details.getPath()).isEqualTo("/**");
        assertThat(details.getFullPath()).isEqualTo("/sslservice/**");
        assertThat(details.getLocation()).isEqualTo("https://localhost:8443");
        assertThat(details.getPrefix()).isEqualTo("/sslservice");
        assertThat(details.isPrefixStripped()).isTrue();
    }

    @Configuration
    @EnableAutoConfiguration
    @RestController
    @EnableZuulProxy
    @Import(NoSecurityConfiguration.class)
    static class SimpleZuulProxyApplication {
        @Component
        static class RoutesRefreshListener implements ApplicationListener<RoutesRefreshedEvent> {
            private boolean called = false;

            @Override
            public void onApplicationEvent(RoutesRefreshedEvent routesRefreshedEvent) {
                called = true;
            }

            public boolean wasCalled() {
                return called;
            }
        }
    }
}

