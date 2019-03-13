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
package org.springframework.boot.actuate.autoconfigure.web.reactive;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;


/**
 * Tests for {@link ReactiveManagementContextFactory}.
 *
 * @author Madhura Bhave
 */
public class ReactiveManagementContextFactoryTests {
    private ReactiveManagementContextFactory factory = new ReactiveManagementContextFactory();

    private AnnotationConfigReactiveWebServerApplicationContext parent = new AnnotationConfigReactiveWebServerApplicationContext();

    @Test
    public void createManagementContextShouldCreateChildContextWithConfigClasses() {
        this.parent.register(ReactiveManagementContextFactoryTests.ParentConfiguration.class);
        this.parent.refresh();
        AnnotationConfigReactiveWebServerApplicationContext childContext = ((AnnotationConfigReactiveWebServerApplicationContext) (this.factory.createManagementContext(this.parent, ReactiveManagementContextFactoryTests.TestConfiguration1.class, ReactiveManagementContextFactoryTests.TestConfiguration2.class)));
        childContext.refresh();
        assertThat(childContext.getBean(ReactiveManagementContextFactoryTests.TestConfiguration1.class)).isNotNull();
        assertThat(childContext.getBean(ReactiveManagementContextFactoryTests.TestConfiguration2.class)).isNotNull();
        assertThat(childContext.getBean(ReactiveWebServerFactoryAutoConfiguration.class)).isNotNull();
        childContext.close();
        this.parent.close();
    }

    @Configuration
    static class ParentConfiguration {
        @Bean
        public ReactiveWebServerFactory reactiveWebServerFactory() {
            return Mockito.mock(ReactiveWebServerFactory.class);
        }

        @Bean
        public HttpHandler httpHandler(ApplicationContext applicationContext) {
            return Mockito.mock(HttpHandler.class);
        }
    }

    @Configuration
    static class TestConfiguration1 {
        @Bean
        public HttpHandler httpHandler(ApplicationContext applicationContext) {
            return Mockito.mock(HttpHandler.class);
        }
    }

    @Configuration
    static class TestConfiguration2 {}
}

