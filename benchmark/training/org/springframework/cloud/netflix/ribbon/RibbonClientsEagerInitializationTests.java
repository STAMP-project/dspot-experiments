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
package org.springframework.cloud.netflix.ribbon;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Biju Kunjummen
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "ribbon.eager-load.enabled=true", "ribbon.eager-load.clients=testspec1,testspec2" })
@DirtiesContext
public class RibbonClientsEagerInitializationTests {
    @Test
    public void contextsShouldBeInitialized() {
        assertThat(RibbonClientsEagerInitializationTests.Foo1.getInstanceCount()).isEqualTo(2);
    }

    static class FooConfig {
        @Bean
        public RibbonClientsEagerInitializationTests.Foo1 foo() {
            return new RibbonClientsEagerInitializationTests.Foo1();
        }
    }

    @Configuration
    @EnableAutoConfiguration
    @RibbonClients({ @RibbonClient(name = "testspec1", configuration = RibbonClientsEagerInitializationTests.FooConfig.class), @RibbonClient(name = "testspec2", configuration = RibbonClientsEagerInitializationTests.FooConfig.class), @RibbonClient(name = "testspec3", configuration = RibbonClientsEagerInitializationTests.FooConfig.class) })
    static class RibbonConfig {}

    static class Foo1 {
        private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

        Foo1() {
            RibbonClientsEagerInitializationTests.Foo1.INSTANCE_COUNT.incrementAndGet();
        }

        public static int getInstanceCount() {
            return RibbonClientsEagerInitializationTests.Foo1.INSTANCE_COUNT.get();
        }
    }
}

