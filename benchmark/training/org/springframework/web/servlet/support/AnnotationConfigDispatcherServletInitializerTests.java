/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.web.servlet.support;


import DispatcherType.ASYNC;
import DispatcherType.FORWARD;
import DispatcherType.INCLUDE;
import DispatcherType.REQUEST;
import java.util.Collections;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * Test case for {@link AbstractAnnotationConfigDispatcherServletInitializer}.
 *
 * @author Arjen Poutsma
 */
public class AnnotationConfigDispatcherServletInitializerTests {
    private static final String SERVLET_NAME = "myservlet";

    private static final String ROLE_NAME = "role";

    private static final String SERVLET_MAPPING = "/myservlet";

    private AbstractDispatcherServletInitializer initializer;

    private MockServletContext servletContext;

    private Map<String, Servlet> servlets;

    private Map<String, MockServletRegistration> servletRegistrations;

    private Map<String, Filter> filters;

    private Map<String, MockFilterRegistration> filterRegistrations;

    @Test
    public void register() throws ServletException {
        initializer.onStartup(servletContext);
        Assert.assertEquals(1, servlets.size());
        Assert.assertNotNull(servlets.get(AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME));
        DispatcherServlet servlet = ((DispatcherServlet) (servlets.get(AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME)));
        WebApplicationContext wac = servlet.getWebApplicationContext();
        refresh();
        Assert.assertTrue(wac.containsBean("bean"));
        Assert.assertTrue(((wac.getBean("bean")) instanceof AnnotationConfigDispatcherServletInitializerTests.MyBean));
        Assert.assertEquals(1, servletRegistrations.size());
        Assert.assertNotNull(servletRegistrations.get(AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME));
        MockServletRegistration servletRegistration = servletRegistrations.get(AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME);
        Assert.assertEquals(Collections.singleton(AnnotationConfigDispatcherServletInitializerTests.SERVLET_MAPPING), servletRegistration.getMappings());
        Assert.assertEquals(1, servletRegistration.getLoadOnStartup());
        Assert.assertEquals(AnnotationConfigDispatcherServletInitializerTests.ROLE_NAME, servletRegistration.getRunAsRole());
        Assert.assertTrue(servletRegistration.isAsyncSupported());
        Assert.assertEquals(4, filterRegistrations.size());
        Assert.assertNotNull(filterRegistrations.get("hiddenHttpMethodFilter"));
        Assert.assertNotNull(filterRegistrations.get("delegatingFilterProxy"));
        Assert.assertNotNull(filterRegistrations.get("delegatingFilterProxy#0"));
        Assert.assertNotNull(filterRegistrations.get("delegatingFilterProxy#1"));
        for (MockFilterRegistration filterRegistration : filterRegistrations.values()) {
            Assert.assertTrue(filterRegistration.isAsyncSupported());
            EnumSet<DispatcherType> enumSet = EnumSet.of(REQUEST, FORWARD, INCLUDE, ASYNC);
            Assert.assertEquals(enumSet, filterRegistration.getMappings().get(AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME));
        }
    }

    @Test
    public void asyncSupportedFalse() throws ServletException {
        initializer = new AnnotationConfigDispatcherServletInitializerTests.MyAnnotationConfigDispatcherServletInitializer() {
            @Override
            protected boolean isAsyncSupported() {
                return false;
            }
        };
        initializer.onStartup(servletContext);
        MockServletRegistration servletRegistration = servletRegistrations.get(AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME);
        Assert.assertFalse(servletRegistration.isAsyncSupported());
        for (MockFilterRegistration filterRegistration : filterRegistrations.values()) {
            Assert.assertFalse(filterRegistration.isAsyncSupported());
            Assert.assertEquals(EnumSet.of(REQUEST, FORWARD, INCLUDE), filterRegistration.getMappings().get(AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME));
        }
    }

    // SPR-11357
    @Test
    public void rootContextOnly() throws ServletException {
        initializer = new AnnotationConfigDispatcherServletInitializerTests.MyAnnotationConfigDispatcherServletInitializer() {
            @Override
            protected Class<?>[] getRootConfigClasses() {
                return new Class<?>[]{ AnnotationConfigDispatcherServletInitializerTests.MyConfiguration.class };
            }

            @Override
            protected Class<?>[] getServletConfigClasses() {
                return null;
            }
        };
        initializer.onStartup(servletContext);
        DispatcherServlet servlet = ((DispatcherServlet) (servlets.get(AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME)));
        servlet.init(new org.springframework.mock.web.test.MockServletConfig(this.servletContext));
        WebApplicationContext wac = servlet.getWebApplicationContext();
        refresh();
        Assert.assertTrue(wac.containsBean("bean"));
        Assert.assertTrue(((wac.getBean("bean")) instanceof AnnotationConfigDispatcherServletInitializerTests.MyBean));
    }

    @Test
    public void noFilters() throws ServletException {
        initializer = new AnnotationConfigDispatcherServletInitializerTests.MyAnnotationConfigDispatcherServletInitializer() {
            @Override
            protected Filter[] getServletFilters() {
                return null;
            }
        };
        initializer.onStartup(servletContext);
        Assert.assertEquals(0, filterRegistrations.size());
    }

    private class MyMockServletContext extends MockServletContext {
        @Override
        public <T extends EventListener> void addListener(T t) {
            if (t instanceof ServletContextListener) {
                ((ServletContextListener) (t)).contextInitialized(new ServletContextEvent(this));
            }
        }

        @Override
        public Dynamic addServlet(String servletName, Servlet servlet) {
            if (servlets.containsKey(servletName)) {
                return null;
            }
            servlets.put(servletName, servlet);
            MockServletRegistration registration = new MockServletRegistration();
            servletRegistrations.put(servletName, registration);
            return registration;
        }

        @Override
        public Dynamic addFilter(String filterName, Filter filter) {
            if (filters.containsKey(filterName)) {
                return null;
            }
            filters.put(filterName, filter);
            MockFilterRegistration registration = new MockFilterRegistration();
            filterRegistrations.put(filterName, registration);
            return registration;
        }
    }

    private static class MyAnnotationConfigDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
        @Override
        protected String getServletName() {
            return AnnotationConfigDispatcherServletInitializerTests.SERVLET_NAME;
        }

        @Override
        protected Class<?>[] getServletConfigClasses() {
            return new Class<?>[]{ AnnotationConfigDispatcherServletInitializerTests.MyConfiguration.class };
        }

        @Override
        protected String[] getServletMappings() {
            return new String[]{ "/myservlet" };
        }

        @Override
        protected Filter[] getServletFilters() {
            return new Filter[]{ new HiddenHttpMethodFilter(), new DelegatingFilterProxy("a"), new DelegatingFilterProxy("b"), new DelegatingFilterProxy("c") };
        }

        @Override
        protected void customizeRegistration(ServletRegistration.Dynamic registration) {
            registration.setRunAsRole("role");
        }

        @Override
        protected Class<?>[] getRootConfigClasses() {
            return null;
        }
    }

    public static class MyBean {}

    @Configuration
    public static class MyConfiguration {
        @Bean
        public AnnotationConfigDispatcherServletInitializerTests.MyBean bean() {
            return new AnnotationConfigDispatcherServletInitializerTests.MyBean();
        }
    }
}

