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
package org.springframework.boot.actuate.context;


import WebApplicationType.NONE;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;


/**
 * Tests for {@link ShutdownEndpoint}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 */
public class ShutdownEndpointTests {
    @Test
    public void shutdown() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ShutdownEndpointTests.EndpointConfig.class);
        contextRunner.run(( context) -> {
            org.springframework.boot.actuate.context.EndpointConfig config = context.getBean(.class);
            ClassLoader previousTccl = Thread.currentThread().getContextClassLoader();
            Map<String, String> result;
            Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0], getClass().getClassLoader()));
            try {
                result = context.getBean(.class).shutdown();
            } finally {
                Thread.currentThread().setContextClassLoader(previousTccl);
            }
            assertThat(result.get("message")).startsWith("Shutting down");
            assertThat(((ConfigurableApplicationContext) (context)).isActive()).isTrue();
            assertThat(config.latch.await(10, TimeUnit.SECONDS)).isTrue();
            assertThat(config.threadContextClassLoader).isEqualTo(getClass().getClassLoader());
        });
    }

    @Test
    public void shutdownChild() throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ShutdownEndpointTests.EmptyConfig.class).child(ShutdownEndpointTests.EndpointConfig.class).web(NONE).run();
        CountDownLatch latch = context.getBean(ShutdownEndpointTests.EndpointConfig.class).latch;
        assertThat(context.getBean(ShutdownEndpoint.class).shutdown().get("message")).startsWith("Shutting down");
        assertThat(context.isActive()).isTrue();
        assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    public void shutdownParent() throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ShutdownEndpointTests.EndpointConfig.class).child(ShutdownEndpointTests.EmptyConfig.class).web(NONE).run();
        CountDownLatch parentLatch = context.getBean(ShutdownEndpointTests.EndpointConfig.class).latch;
        CountDownLatch childLatch = context.getBean(ShutdownEndpointTests.EmptyConfig.class).latch;
        assertThat(context.getBean(ShutdownEndpoint.class).shutdown().get("message")).startsWith("Shutting down");
        assertThat(context.isActive()).isTrue();
        assertThat(parentLatch.await(10, TimeUnit.SECONDS)).isTrue();
        assertThat(childLatch.await(10, TimeUnit.SECONDS)).isTrue();
    }

    @Configuration
    public static class EndpointConfig {
        private final CountDownLatch latch = new CountDownLatch(1);

        private volatile ClassLoader threadContextClassLoader;

        @Bean
        public ShutdownEndpoint endpoint() {
            ShutdownEndpoint endpoint = new ShutdownEndpoint();
            return endpoint;
        }

        @Bean
        public ApplicationListener<ContextClosedEvent> listener() {
            return ( event) -> {
                org.springframework.boot.actuate.context.EndpointConfig.this.threadContextClassLoader = Thread.currentThread().getContextClassLoader();
                org.springframework.boot.actuate.context.EndpointConfig.this.latch.countDown();
            };
        }
    }

    @Configuration
    public static class EmptyConfig {
        private final CountDownLatch latch = new CountDownLatch(1);

        @Bean
        public ApplicationListener<ContextClosedEvent> listener() {
            return ( event) -> org.springframework.boot.actuate.context.EmptyConfig.this.latch.countDown();
        }
    }
}

