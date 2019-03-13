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
package org.springframework.boot.actuate.endpoint.web;


import java.util.Collections;
import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link ServletEndpointRegistrar}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class ServletEndpointRegistrarTests {
    @Mock
    private ServletContext servletContext;

    @Mock
    private Dynamic dynamic;

    @Captor
    private ArgumentCaptor<Servlet> servlet;

    @Test
    public void createWhenServletEndpointsIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ServletEndpointRegistrar(null, null)).withMessageContaining("ServletEndpoints must not be null");
    }

    @Test
    public void onStartupShouldRegisterServlets() throws ServletException {
        assertBasePath(null, "/test/*");
    }

    @Test
    public void onStartupWhenHasBasePathShouldIncludeBasePath() throws ServletException {
        assertBasePath("/actuator", "/actuator/test/*");
    }

    @Test
    public void onStartupWhenHasEmptyBasePathShouldPrefixWithSlash() throws ServletException {
        assertBasePath("", "/test/*");
    }

    @Test
    public void onStartupWhenHasRootBasePathShouldNotAddDuplicateSlash() throws ServletException {
        assertBasePath("/", "/test/*");
    }

    @Test
    public void onStartupWhenHasInitParametersShouldRegisterInitParameters() throws Exception {
        ExposableServletEndpoint endpoint = mockEndpoint(new EndpointServlet(ServletEndpointRegistrarTests.TestServlet.class).withInitParameter("a", "b"));
        ServletEndpointRegistrar registrar = new ServletEndpointRegistrar("/actuator", Collections.singleton(endpoint));
        registrar.onStartup(this.servletContext);
        Mockito.verify(this.dynamic).setInitParameters(Collections.singletonMap("a", "b"));
    }

    @Test
    public void onStartupWhenHasLoadOnStartupShouldRegisterLoadOnStartup() throws Exception {
        ExposableServletEndpoint endpoint = mockEndpoint(new EndpointServlet(ServletEndpointRegistrarTests.TestServlet.class).withLoadOnStartup(7));
        ServletEndpointRegistrar registrar = new ServletEndpointRegistrar("/actuator", Collections.singleton(endpoint));
        registrar.onStartup(this.servletContext);
        Mockito.verify(this.dynamic).setLoadOnStartup(7);
    }

    @Test
    public void onStartupWhenHasNotLoadOnStartupShouldRegisterDefaultValue() throws Exception {
        ExposableServletEndpoint endpoint = mockEndpoint(new EndpointServlet(ServletEndpointRegistrarTests.TestServlet.class));
        ServletEndpointRegistrar registrar = new ServletEndpointRegistrar("/actuator", Collections.singleton(endpoint));
        registrar.onStartup(this.servletContext);
        Mockito.verify(this.dynamic).setLoadOnStartup((-1));
    }

    public static class TestServlet extends GenericServlet {
        @Override
        public void service(ServletRequest req, ServletResponse res) {
        }
    }
}

