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
package org.springframework.boot.web.servlet.context;


import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.config.ExampleServletWebServerApplicationConfiguration;
import org.springframework.boot.web.servlet.mock.MockServlet;
import org.springframework.boot.web.servlet.server.MockServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Tests for {@link AnnotationConfigServletWebServerApplicationContext}.
 *
 * @author Phillip Webb
 */
public class AnnotationConfigServletWebServerApplicationContextTests {
    private AnnotationConfigServletWebServerApplicationContext context;

    @Test
    public void createFromScan() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(ExampleServletWebServerApplicationConfiguration.class.getPackage().getName());
        verifyContext();
    }

    @Test
    public void sessionScopeAvailable() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(ExampleServletWebServerApplicationConfiguration.class, AnnotationConfigServletWebServerApplicationContextTests.SessionScopedComponent.class);
        verifyContext();
    }

    @Test
    public void sessionScopeAvailableToServlet() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(ExampleServletWebServerApplicationConfiguration.class, AnnotationConfigServletWebServerApplicationContextTests.ExampleServletWithAutowired.class, AnnotationConfigServletWebServerApplicationContextTests.SessionScopedComponent.class);
        Servlet servlet = this.context.getBean(AnnotationConfigServletWebServerApplicationContextTests.ExampleServletWithAutowired.class);
        assertThat(servlet).isNotNull();
    }

    @Test
    public void createFromConfigClass() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(ExampleServletWebServerApplicationConfiguration.class);
        verifyContext();
    }

    @Test
    public void registerAndRefresh() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        this.context.register(ExampleServletWebServerApplicationConfiguration.class);
        this.context.refresh();
        verifyContext();
    }

    @Test
    public void multipleRegistersAndRefresh() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        this.context.register(AnnotationConfigServletWebServerApplicationContextTests.WebServerConfiguration.class);
        this.context.register(AnnotationConfigServletWebServerApplicationContextTests.ServletContextAwareConfiguration.class);
        this.context.refresh();
        assertThat(this.context.getBeansOfType(Servlet.class)).hasSize(1);
        assertThat(this.context.getBeansOfType(ServletWebServerFactory.class)).hasSize(1);
    }

    @Test
    public void scanAndRefresh() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        this.context.scan(ExampleServletWebServerApplicationConfiguration.class.getPackage().getName());
        this.context.refresh();
        verifyContext();
    }

    @Test
    public void createAndInitializeCyclic() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(AnnotationConfigServletWebServerApplicationContextTests.ServletContextAwareEmbeddedConfiguration.class);
        verifyContext();
        // You can't initialize the application context and inject the servlet context
        // because of a cycle - we'd like this to be not null but it never will be
        assertThat(this.context.getBean(AnnotationConfigServletWebServerApplicationContextTests.ServletContextAwareEmbeddedConfiguration.class).getServletContext()).isNull();
    }

    @Test
    public void createAndInitializeWithParent() {
        AnnotationConfigServletWebServerApplicationContext parent = new AnnotationConfigServletWebServerApplicationContext(AnnotationConfigServletWebServerApplicationContextTests.WebServerConfiguration.class);
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        this.context.register(AnnotationConfigServletWebServerApplicationContextTests.WebServerConfiguration.class, AnnotationConfigServletWebServerApplicationContextTests.ServletContextAwareConfiguration.class);
        this.context.setParent(parent);
        this.context.refresh();
        verifyContext();
        assertThat(this.context.getBean(AnnotationConfigServletWebServerApplicationContextTests.ServletContextAwareConfiguration.class).getServletContext()).isNotNull();
    }

    @Test
    public void registerBean() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        this.context.register(ExampleServletWebServerApplicationConfiguration.class);
        this.context.registerBean(AnnotationConfigServletWebServerApplicationContextTests.TestBean.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigServletWebServerApplicationContextTests.TestBean")).isTrue();
        assertThat(this.context.getBean(AnnotationConfigServletWebServerApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithLazy() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        this.context.register(ExampleServletWebServerApplicationConfiguration.class);
        this.context.registerBean(AnnotationConfigServletWebServerApplicationContextTests.TestBean.class, Lazy.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigServletWebServerApplicationContextTests.TestBean")).isFalse();
        assertThat(this.context.getBean(AnnotationConfigServletWebServerApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithSupplier() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        this.context.register(ExampleServletWebServerApplicationConfiguration.class);
        this.context.registerBean(AnnotationConfigServletWebServerApplicationContextTests.TestBean.class, AnnotationConfigServletWebServerApplicationContextTests.TestBean::new);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigServletWebServerApplicationContextTests.TestBean")).isTrue();
        assertThat(this.context.getBean(AnnotationConfigServletWebServerApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Test
    public void registerBeanWithSupplierAndLazy() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        this.context.register(ExampleServletWebServerApplicationConfiguration.class);
        this.context.registerBean(AnnotationConfigServletWebServerApplicationContextTests.TestBean.class, AnnotationConfigServletWebServerApplicationContextTests.TestBean::new, Lazy.class);
        this.context.refresh();
        assertThat(this.context.getBeanFactory().containsSingleton("annotationConfigServletWebServerApplicationContextTests.TestBean")).isFalse();
        assertThat(this.context.getBean(AnnotationConfigServletWebServerApplicationContextTests.TestBean.class)).isNotNull();
    }

    @Component
    @SuppressWarnings("serial")
    protected static class ExampleServletWithAutowired extends GenericServlet {
        @Autowired
        private AnnotationConfigServletWebServerApplicationContextTests.SessionScopedComponent component;

        @Override
        public void service(ServletRequest req, ServletResponse res) {
            assertThat(this.component).isNotNull();
        }
    }

    @Component
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    protected static class SessionScopedComponent {}

    @Configuration
    @EnableWebMvc
    public static class ServletContextAwareEmbeddedConfiguration implements ServletContextAware {
        private ServletContext servletContext;

        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new MockServletWebServerFactory();
        }

        @Bean
        public Servlet servlet() {
            return new MockServlet();
        }

        @Override
        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        public ServletContext getServletContext() {
            return this.servletContext;
        }
    }

    @Configuration
    public static class WebServerConfiguration {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new MockServletWebServerFactory();
        }
    }

    @Configuration
    @EnableWebMvc
    public static class ServletContextAwareConfiguration implements ServletContextAware {
        private ServletContext servletContext;

        @Bean
        public Servlet servlet() {
            return new MockServlet();
        }

        @Override
        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        public ServletContext getServletContext() {
            return this.servletContext;
        }
    }

    private static class TestBean {}
}

