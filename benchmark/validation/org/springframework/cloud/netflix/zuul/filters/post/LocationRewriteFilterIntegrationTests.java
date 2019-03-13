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
package org.springframework.cloud.netflix.zuul.filters.post;


import HttpStatus.FOUND;
import SpringBootTest.WebEnvironment;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.test.NoSecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 *
 *
 * @author Biju Kunjummen
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = { "zuul.routes.aservice.path:/service/**", "zuul.routes.aservice.strip-prefix:true", "eureka.client.enabled:false" })
@DirtiesContext
public class LocationRewriteFilterIntegrationTests {
    @LocalServerPort
    private int port;

    @Test
    public void testWithRedirectPrefixStripped() {
        String url = ("http://localhost:" + (port)) + "/service/redirectingUri";
        ResponseEntity<String> response = new TestRestTemplate().getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
        List<String> locationHeaders = response.getHeaders().get("Location");
        assertThat(locationHeaders).hasSize(1);
        String locationHeader = locationHeaders.get(0);
        assertThat(locationHeader).withFailMessage("Location should have prefix").isEqualTo(String.format("http://localhost:%d/service/redirectedUri", port));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableZuulProxy
    @Controller
    @RibbonClient(name = "aservice", configuration = LocationRewriteFilterIntegrationTests.RibbonConfig.class)
    @Import(NoSecurityConfiguration.class)
    protected static class Config {
        @RequestMapping("/redirectingUri")
        public String redirect1() {
            return "redirect:/redirectedUri";
        }

        @Bean
        public LocationRewriteFilter locationRewriteFilter() {
            return new LocationRewriteFilter();
        }
    }

    public static class RibbonConfig {
        @LocalServerPort
        private int port;

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server("localhost", this.port));
        }
    }
}

