/**
 * Copyright 2017-2019 the original author or authors.
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


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "zuul.routes.eagerroute.service-id=eager", "zuul.ribbon.eager-load.enabled=true" })
@DirtiesContext
public class EagerLoadOfZuulConfigurationTests {
    @Test
    public void testEagerLoading() {
        // Child context FooConfig should have been eagerly instantiated..
        assertThat(EagerLoadOfZuulConfigurationTests.Foo.getInstanceCount()).isEqualTo(1);
    }

    @EnableAutoConfiguration
    @Configuration
    @EnableZuulProxy
    @RibbonClients(@RibbonClient(name = "eager", configuration = EagerLoadOfZuulConfigurationTests.FooConfig.class))
    static class TestConfig {}

    static class Foo {
        private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

        Foo() {
            EagerLoadOfZuulConfigurationTests.Foo.INSTANCE_COUNT.incrementAndGet();
        }

        public static int getInstanceCount() {
            return EagerLoadOfZuulConfigurationTests.Foo.INSTANCE_COUNT.get();
        }
    }

    static class FooConfig {
        @Bean
        public EagerLoadOfZuulConfigurationTests.Foo foo() {
            return new EagerLoadOfZuulConfigurationTests.Foo();
        }
    }
}

