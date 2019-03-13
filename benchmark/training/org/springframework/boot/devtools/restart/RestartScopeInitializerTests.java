/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.devtools.restart;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * Tests for {@link RestartScopeInitializer}.
 *
 * @author Phillip Webb
 */
public class RestartScopeInitializerTests {
    private static AtomicInteger createCount;

    private static AtomicInteger refreshCount;

    @Test
    public void restartScope() {
        RestartScopeInitializerTests.createCount = new AtomicInteger();
        RestartScopeInitializerTests.refreshCount = new AtomicInteger();
        ConfigurableApplicationContext context = runApplication();
        context.close();
        context = runApplication();
        context.close();
        assertThat(RestartScopeInitializerTests.createCount.get()).isEqualTo(1);
        assertThat(RestartScopeInitializerTests.refreshCount.get()).isEqualTo(2);
    }

    @Configuration
    public static class Config {
        @Bean
        @RestartScope
        public RestartScopeInitializerTests.ScopeTestBean scopeTestBean() {
            return new RestartScopeInitializerTests.ScopeTestBean();
        }
    }

    public static class ScopeTestBean implements ApplicationListener<ContextRefreshedEvent> {
        public ScopeTestBean() {
            RestartScopeInitializerTests.createCount.incrementAndGet();
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            RestartScopeInitializerTests.refreshCount.incrementAndGet();
        }
    }
}

