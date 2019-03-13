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
package org.springframework.boot.devtools.restart;


import RestartInitializer.NONE;
import java.net.URL;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link OnInitializedRestarterCondition}.
 *
 * @author Phillip Webb
 */
public class OnInitializedRestarterConditionTests {
    private static Object wait = new Object();

    @Test
    public void noInstance() {
        Restarter.clearInstance();
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(OnInitializedRestarterConditionTests.Config.class);
        assertThat(context.containsBean("bean")).isFalse();
        context.close();
    }

    @Test
    public void noInitialization() {
        Restarter.initialize(new String[0], false, NONE);
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(OnInitializedRestarterConditionTests.Config.class);
        assertThat(context.containsBean("bean")).isFalse();
        context.close();
    }

    @Test
    public void initialized() throws Exception {
        Thread thread = new Thread(OnInitializedRestarterConditionTests.TestInitialized::main);
        thread.start();
        synchronized(OnInitializedRestarterConditionTests.wait) {
            OnInitializedRestarterConditionTests.wait.wait();
        }
    }

    public static class TestInitialized {
        public static void main(String... args) {
            RestartInitializer initializer = Mockito.mock(RestartInitializer.class);
            BDDMockito.given(initializer.getInitialUrls(ArgumentMatchers.any(Thread.class))).willReturn(new URL[0]);
            Restarter.initialize(new String[0], false, initializer);
            ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(OnInitializedRestarterConditionTests.Config.class);
            assertThat(context.containsBean("bean")).isTrue();
            context.close();
            synchronized(OnInitializedRestarterConditionTests.wait) {
                OnInitializedRestarterConditionTests.wait.notify();
            }
        }
    }

    @Configuration
    public static class Config {
        @Bean
        @ConditionalOnInitializedRestarter
        public String bean() {
            return "bean";
        }
    }
}

