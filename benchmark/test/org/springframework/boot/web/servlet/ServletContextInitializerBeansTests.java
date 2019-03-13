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
package org.springframework.boot.web.servlet;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ServletContextInitializerBeans}.
 *
 * @author Andy Wilkinson
 */
public class ServletContextInitializerBeansTests {
    private ConfigurableApplicationContext context;

    @Test
    public void servletThatImplementsServletContextInitializerIsOnlyRegisteredOnce() {
        load(ServletContextInitializerBeansTests.ServletConfiguration.class);
        ServletContextInitializerBeans initializerBeans = new ServletContextInitializerBeans(this.context.getBeanFactory());
        assertThat(initializerBeans.size()).isEqualTo(1);
        assertThat(initializerBeans.iterator()).toIterable().hasOnlyElementsOfType(ServletContextInitializerBeansTests.TestServlet.class);
    }

    @Test
    public void filterThatImplementsServletContextInitializerIsOnlyRegisteredOnce() {
        load(ServletContextInitializerBeansTests.FilterConfiguration.class);
        ServletContextInitializerBeans initializerBeans = new ServletContextInitializerBeans(this.context.getBeanFactory());
        assertThat(initializerBeans.size()).isEqualTo(1);
        assertThat(initializerBeans.iterator()).toIterable().hasOnlyElementsOfType(ServletContextInitializerBeansTests.TestFilter.class);
    }

    @Test
    public void looksForInitializerBeansOfSpecifiedType() {
        load(ServletContextInitializerBeansTests.TestConfiguration.class);
        ServletContextInitializerBeans initializerBeans = new ServletContextInitializerBeans(this.context.getBeanFactory(), ServletContextInitializerBeansTests.TestServletContextInitializer.class);
        assertThat(initializerBeans.size()).isEqualTo(1);
        assertThat(initializerBeans.iterator()).toIterable().hasOnlyElementsOfType(ServletContextInitializerBeansTests.TestServletContextInitializer.class);
    }

    @Configuration
    static class ServletConfiguration {
        @Bean
        public ServletContextInitializerBeansTests.TestServlet testServlet() {
            return new ServletContextInitializerBeansTests.TestServlet();
        }
    }

    @Configuration
    static class FilterConfiguration {
        @Bean
        public ServletContextInitializerBeansTests.TestFilter testFilter() {
            return new ServletContextInitializerBeansTests.TestFilter();
        }
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public ServletContextInitializerBeansTests.TestServletContextInitializer testServletContextInitializer() {
            return new ServletContextInitializerBeansTests.TestServletContextInitializer();
        }

        @Bean
        public ServletContextInitializerBeansTests.OtherTestServletContextInitializer otherTestServletContextInitializer() {
            return new ServletContextInitializerBeansTests.OtherTestServletContextInitializer();
        }
    }

    static class TestServlet extends HttpServlet implements ServletContextInitializer {
        @Override
        public void onStartup(ServletContext servletContext) {
        }
    }

    static class TestFilter implements Filter , ServletContextInitializer {
        @Override
        public void onStartup(ServletContext servletContext) {
        }

        @Override
        public void init(FilterConfig filterConfig) {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        }

        @Override
        public void destroy() {
        }
    }

    static class TestServletContextInitializer implements ServletContextInitializer {
        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
        }
    }

    static class OtherTestServletContextInitializer implements ServletContextInitializer {
        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
        }
    }
}

