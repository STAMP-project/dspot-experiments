/**
 * Copyright 2002-2015 the original author or authors.
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


import ServletRegistration.Dynamic;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * Test case for {@link AbstractDispatcherServletInitializer}.
 *
 * @author Arjen Poutsma
 */
public class DispatcherServletInitializerTests {
    private static final String SERVLET_NAME = "myservlet";

    private static final String ROLE_NAME = "role";

    private static final String SERVLET_MAPPING = "/myservlet";

    private final MockServletContext servletContext = new DispatcherServletInitializerTests.MyMockServletContext();

    private final AbstractDispatcherServletInitializer initializer = new DispatcherServletInitializerTests.MyDispatcherServletInitializer();

    private final Map<String, Servlet> servlets = new LinkedHashMap<>(2);

    private final Map<String, MockServletRegistration> registrations = new LinkedHashMap<>(2);

    @Test
    public void register() throws ServletException {
        initializer.onStartup(servletContext);
        Assert.assertEquals(1, servlets.size());
        Assert.assertNotNull(servlets.get(DispatcherServletInitializerTests.SERVLET_NAME));
        DispatcherServlet servlet = ((DispatcherServlet) (servlets.get(DispatcherServletInitializerTests.SERVLET_NAME)));
        Assert.assertEquals(DispatcherServletInitializerTests.MyDispatcherServlet.class, servlet.getClass());
        WebApplicationContext servletContext = servlet.getWebApplicationContext();
        Assert.assertTrue(servletContext.containsBean("bean"));
        Assert.assertTrue(((servletContext.getBean("bean")) instanceof DispatcherServletInitializerTests.MyBean));
        Assert.assertEquals(1, registrations.size());
        Assert.assertNotNull(registrations.get(DispatcherServletInitializerTests.SERVLET_NAME));
        MockServletRegistration registration = registrations.get(DispatcherServletInitializerTests.SERVLET_NAME);
        Assert.assertEquals(Collections.singleton(DispatcherServletInitializerTests.SERVLET_MAPPING), registration.getMappings());
        Assert.assertEquals(1, registration.getLoadOnStartup());
        Assert.assertEquals(DispatcherServletInitializerTests.ROLE_NAME, registration.getRunAsRole());
    }

    private class MyMockServletContext extends MockServletContext {
        @Override
        public Dynamic addServlet(String servletName, Servlet servlet) {
            servlets.put(servletName, servlet);
            MockServletRegistration registration = new MockServletRegistration();
            registrations.put(servletName, registration);
            return registration;
        }
    }

    private static class MyDispatcherServletInitializer extends AbstractDispatcherServletInitializer {
        @Override
        protected String getServletName() {
            return DispatcherServletInitializerTests.SERVLET_NAME;
        }

        @Override
        protected DispatcherServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
            return new DispatcherServletInitializerTests.MyDispatcherServlet(servletAppContext);
        }

        @Override
        protected WebApplicationContext createServletApplicationContext() {
            StaticWebApplicationContext servletContext = new StaticWebApplicationContext();
            servletContext.registerSingleton("bean", DispatcherServletInitializerTests.MyBean.class);
            return servletContext;
        }

        @Override
        protected String[] getServletMappings() {
            return new String[]{ DispatcherServletInitializerTests.SERVLET_MAPPING };
        }

        @Override
        protected void customizeRegistration(ServletRegistration.Dynamic registration) {
            registration.setRunAsRole("role");
        }

        @Override
        protected WebApplicationContext createRootApplicationContext() {
            return null;
        }
    }

    private static class MyBean {}

    @SuppressWarnings("serial")
    private static class MyDispatcherServlet extends DispatcherServlet {
        public MyDispatcherServlet(WebApplicationContext webApplicationContext) {
            super(webApplicationContext);
        }
    }
}

