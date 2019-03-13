/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.web.reactive.context;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.reactive.context.config.ExampleReactiveWebServerApplicationConfiguration;
import org.springframework.boot.web.reactive.server.MockReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.http.server.reactive.HttpHandler;


/**
 * Tests for {@link AnnotationConfigReactiveWebServerApplicationContext}.
 *
 * @author Phillip Webb
 */
public class AnnotationConfigReactiveWebServerApplicationContextTests {
    private AnnotationConfigReactiveWebServerApplicationContext context;

    @Test
    public void createFromScan() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext(ExampleReactiveWebServerApplicationConfiguration.class.getPackage().getName());
        verifyContext();
    }

    @Test
    public void createFromConfigClass() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext(ExampleReactiveWebServerApplicationConfiguration.class);
        verifyContext();
    }

    @Test
    public void registerAndRefresh() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext();
        this.context.register(ExampleReactiveWebServerApplicationConfiguration.class);
        this.context.refresh();
        verifyContext();
    }

    @Test
    public void multipleRegistersAndRefresh() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext();
        this.context.register(AnnotationConfigReactiveWebServerApplicationContextTests.WebServerConfiguration.class);
        this.context.register(AnnotationConfigReactiveWebServerApplicationContextTests.HttpHandlerConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBeansOfType(AnnotationConfigReactiveWebServerApplicationContextTests.WebServerConfiguration.class)).hasSize(1);
        assertThat(this.context.getBeansOfType(AnnotationConfigReactiveWebServerApplicationContextTests.HttpHandlerConfiguration.class)).hasSize(1);
    }

    @Test
    public void scanAndRefresh() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext();
        this.context.scan(ExampleReactiveWebServerApplicationConfiguration.class.getPackage().getName());
        this.context.refresh();
        verifyContext();
    }

    @Test
    public void httpHandlerInitialization() {
        // gh-14666
        this.context = new AnnotationConfigReactiveWebServerApplicationContext(AnnotationConfigReactiveWebServerApplicationContextTests.InitializationTestConfig.class);
        verifyContext();
    }

    @Test
    public void registerBean() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext();
        this.context.register(ExampleReactiveWebServerApplicationConfiguration.class);
        this.context.registerBean(AnnotationConfigReactiveWebServerApplicationContextTests.TestBean.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigReactiveWebServerApplicationContextTests.TestBean")).isTrue();
        assertThat(this.context.getBean(AnnotationConfigReactiveWebServerApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithLazy() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext();
        this.context.register(ExampleReactiveWebServerApplicationConfiguration.class);
        this.context.registerBean(AnnotationConfigReactiveWebServerApplicationContextTests.TestBean.class, Lazy.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigReactiveWebServerApplicationContextTests.TestBean")).isFalse();
        assertThat(this.context.getBean(AnnotationConfigReactiveWebServerApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithSupplier() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext();
        this.context.register(ExampleReactiveWebServerApplicationConfiguration.class);
        this.context.registerBean(AnnotationConfigReactiveWebServerApplicationContextTests.TestBean.class, AnnotationConfigReactiveWebServerApplicationContextTests.TestBean::new);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigReactiveWebServerApplicationContextTests.TestBean")).isTrue();
        assertThat(this.context.getBean(AnnotationConfigReactiveWebServerApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithSupplierAndLazy() {
        this.context = new AnnotationConfigReactiveWebServerApplicationContext();
        this.context.register(ExampleReactiveWebServerApplicationConfiguration.class);
        this.context.registerBean(AnnotationConfigReactiveWebServerApplicationContextTests.TestBean.class, AnnotationConfigReactiveWebServerApplicationContextTests.TestBean::new, Lazy.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigReactiveWebServerApplicationContextTests.TestBean")).isFalse();
        assertThat(this.context.getBean(AnnotationConfigReactiveWebServerApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Configuration
    public static class WebServerConfiguration {
        @Bean
        public ReactiveWebServerFactory webServerFactory() {
            return new MockReactiveWebServerFactory();
        }
    }

    @Configuration
    public static class HttpHandlerConfiguration {
        @Bean
        public HttpHandler httpHandler() {
            return Mockito.mock(HttpHandler.class);
        }
    }

    @Configuration
    public static class InitializationTestConfig {
        private static boolean addedListener;

        @Bean
        public ReactiveWebServerFactory webServerFactory() {
            return new MockReactiveWebServerFactory();
        }

        @Bean
        public HttpHandler httpHandler() {
            if (!(AnnotationConfigReactiveWebServerApplicationContextTests.InitializationTestConfig.addedListener)) {
                throw new RuntimeException("Handlers should be added after listeners, we're being initialized too early!");
            }
            return Mockito.mock(HttpHandler.class);
        }

        @Bean
        public AnnotationConfigReactiveWebServerApplicationContextTests.InitializationTestConfig.Listener listener() {
            return new AnnotationConfigReactiveWebServerApplicationContextTests.InitializationTestConfig.Listener();
        }

        @Bean
        public ApplicationEventMulticaster applicationEventMulticaster() {
            return new SimpleApplicationEventMulticaster() {
                @Override
                public void addApplicationListenerBean(String listenerBeanName) {
                    super.addApplicationListenerBean(listenerBeanName);
                    if ("listener".equals(listenerBeanName)) {
                        AnnotationConfigReactiveWebServerApplicationContextTests.InitializationTestConfig.addedListener = true;
                    }
                }
            };
        }

        private static class Listener implements ApplicationListener<ContextRefreshedEvent> {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) {
            }
        }
    }

    private static class TestBean {}
}

