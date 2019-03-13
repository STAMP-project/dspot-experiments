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
package org.springframework.cloud.netflix.zuul.filters.discovery;


import HttpMethod.GET;
import HttpStatus.OK;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.RoutesEndpoint;
import org.springframework.cloud.netflix.zuul.test.NoSecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 *
 * @author St?phane Leroy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.application.name=regex-test-application", "spring.jmx.enabled=false", "eureka.client.enabled=false" })
@DirtiesContext
public class PatternServiceRouteMapperIntegrationTests {
    protected static final String SERVICE_ID = "domain-service-v1";

    @LocalServerPort
    private int port;

    @Autowired
    private DiscoveryClientRouteLocator routes;

    @Autowired
    private RoutesEndpoint endpoint;

    @Test
    public void getRegexMappedService() {
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/v1/domain/service/get/1"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Get 1");
    }

    @Test
    public void getStaticRoute() {
        this.routes.addRoute("/self/**", ("http://localhost:" + (this.port)));
        this.endpoint.reset();
        ResponseEntity<String> result = new TestRestTemplate().exchange((("http://localhost:" + (this.port)) + "/self/get/1"), GET, new org.springframework.http.HttpEntity(((Void) (null))), String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertThat(result.getBody()).isEqualTo("Get 1");
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @RestController
    @EnableZuulProxy
    @RibbonClient(value = PatternServiceRouteMapperIntegrationTests.SERVICE_ID, configuration = PatternServiceRouteMapperIntegrationTests.SimpleRibbonClientConfiguration.class)
    @Import(NoSecurityConfiguration.class)
    protected static class SampleCustomZuulProxyApplication {
        @Bean
        public DiscoveryClient discoveryClient() {
            DiscoveryClient discoveryClient = Mockito.mock(DiscoveryClient.class);
            List<String> services = new ArrayList<>();
            services.add(PatternServiceRouteMapperIntegrationTests.SERVICE_ID);
            Mockito.when(discoveryClient.getServices()).thenReturn(services);
            return discoveryClient;
        }

        @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
        public String get(@PathVariable
        String id) {
            return "Get " + id;
        }

        @Bean
        public PatternServiceRouteMapper serviceRouteMapper() {
            return new PatternServiceRouteMapper("(?<domain>^.+)-(?<name>.+)-(?<version>v.+$)", "${version}/${domain}/${name}");
        }
    }

    protected static class SimpleRibbonClientConfiguration {
        @LocalServerPort
        private int port = 0;

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server("localhost", this.port));
        }
    }
}

