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
package org.springframework.boot.web.servlet;


import javax.servlet.Filter;
import org.junit.Test;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.mock.MockFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Integration tests for {@link Filter} registration.
 *
 * @author Andy Wilkinson
 */
public class FilterRegistrationIntegrationTests {
    private AnnotationConfigServletWebServerApplicationContext context;

    @Test
    public void normalFiltersAreRegistered() {
        load(FilterRegistrationIntegrationTests.FilterConfiguration.class);
        assertThat(this.context.getServletContext().getFilterRegistrations()).hasSize(1);
    }

    @Test
    public void scopedTargetFiltersAreNotRegistered() {
        load(FilterRegistrationIntegrationTests.ScopedTargetFilterConfiguration.class);
        assertThat(this.context.getServletContext().getFilterRegistrations()).isEmpty();
    }

    @Configuration
    static class ContainerConfiguration {
        @Bean
        public TomcatServletWebServerFactory webServerFactory() {
            return new TomcatServletWebServerFactory(0);
        }
    }

    @Configuration
    static class ScopedTargetFilterConfiguration {
        @Bean(name = "scopedTarget.myFilter")
        public Filter myFilter() {
            return new MockFilter();
        }
    }

    @Configuration
    static class FilterConfiguration {
        @Bean
        public Filter myFilter() {
            return new MockFilter();
        }
    }
}

