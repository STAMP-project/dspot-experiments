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
package org.springframework.cloud.netflix.zuul.filters.route;


import HttpStatus.OK;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "zuul.routes.lazyroute.service-id=lazy", "zuul.routes.lazyroute.path=/lazy/**", "zuul.ribbon.eager-load.enabled=false" })
@DirtiesContext
public class LazyLoadOfZuulConfigurationTests {
    @LocalServerPort
    protected int port;

    @Test
    public void testLazyLoading() {
        // Child context FooConfig should be lazily created..
        assertThat(LazyLoadOfZuulConfigurationTests.Foo.getInstanceCount()).isEqualTo(0);
        String uri = String.format("http://localhost:%d/lazy/sample", this.port);
        ResponseEntity<String> result = new TestRestTemplate().getForEntity(uri, String.class);
        assertThat(result.getStatusCode()).isEqualTo(OK);
        // the instance should be available now..
        assertThat(LazyLoadOfZuulConfigurationTests.Foo.getInstanceCount()).isEqualTo(1);
        assertThat(result.getBody()).isEqualTo("sample");
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @EnableZuulProxy
    @RestController
    @RibbonClient(name = "lazy", configuration = LazyLoadOfZuulConfigurationTests.FooConfig.class)
    @Import(NoSecurityConfiguration.class)
    static class TestConfig {
        @RequestMapping("/sample")
        public String sampleEndpoint() {
            return "sample";
        }
    }

    static class Foo {
        private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

        Foo() {
            LazyLoadOfZuulConfigurationTests.Foo.INSTANCE_COUNT.incrementAndGet();
        }

        public static int getInstanceCount() {
            return LazyLoadOfZuulConfigurationTests.Foo.INSTANCE_COUNT.get();
        }
    }

    static class FooConfig {
        @Bean
        public LazyLoadOfZuulConfigurationTests.Foo foo() {
            return new LazyLoadOfZuulConfigurationTests.Foo();
        }

        @LocalServerPort
        private int port;

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server("localhost", this.port));
        }
    }
}

