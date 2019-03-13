/**
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.boot.admin.client.registration;


import de.codecentric.boot.admin.client.config.InstanceProperties;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.mock.web.MockServletContext;


public class ServletApplicationFactoryTest {
    private InstanceProperties instance = new InstanceProperties();

    private ServerProperties server = new ServerProperties();

    private ManagementServerProperties management = new ManagementServerProperties();

    private MockServletContext servletContext = new MockServletContext();

    private PathMappedEndpoints pathMappedEndpoints = Mockito.mock(PathMappedEndpoints.class);

    private WebEndpointProperties webEndpoint = new WebEndpointProperties();

    private DispatcherServletPath dispatcherServletPath = Mockito.mock(DispatcherServletPath.class);

    private ServletApplicationFactory factory = new ServletApplicationFactory(instance, management, server, servletContext, pathMappedEndpoints, webEndpoint, Collections::emptyMap, dispatcherServletPath);

    @Test
    public void test_contextPath_mgmtPath() {
        servletContext.setContextPath("app");
        webEndpoint.setBasePath("/admin");
        Mockito.when(pathMappedEndpoints.getPath(EndpointId.of("health"))).thenReturn("/admin/health");
        publishApplicationReadyEvent(factory, 8080, null);
        Application app = factory.createApplication();
        assertThat(app.getManagementUrl()).isEqualTo((("http://" + (getHostname())) + ":8080/app/admin"));
        assertThat(app.getHealthUrl()).isEqualTo((("http://" + (getHostname())) + ":8080/app/admin/health"));
        assertThat(app.getServiceUrl()).isEqualTo((("http://" + (getHostname())) + ":8080/app"));
    }

    @Test
    public void test_contextPath_mgmtPortPath() {
        servletContext.setContextPath("app");
        webEndpoint.setBasePath("/admin");
        Mockito.when(pathMappedEndpoints.getPath(EndpointId.of("health"))).thenReturn("/admin/health");
        publishApplicationReadyEvent(factory, 8080, 8081);
        Application app = factory.createApplication();
        assertThat(app.getManagementUrl()).isEqualTo((("http://" + (getHostname())) + ":8081/admin"));
        assertThat(app.getHealthUrl()).isEqualTo((("http://" + (getHostname())) + ":8081/admin/health"));
        assertThat(app.getServiceUrl()).isEqualTo((("http://" + (getHostname())) + ":8080/app"));
    }

    @Test
    public void test_contextPath() {
        servletContext.setContextPath("app");
        Mockito.when(pathMappedEndpoints.getPath(EndpointId.of("health"))).thenReturn("/actuator/health");
        publishApplicationReadyEvent(factory, 80, null);
        Application app = factory.createApplication();
        assertThat(app.getManagementUrl()).isEqualTo((("http://" + (getHostname())) + ":80/app/actuator"));
        assertThat(app.getHealthUrl()).isEqualTo((("http://" + (getHostname())) + ":80/app/actuator/health"));
        assertThat(app.getServiceUrl()).isEqualTo((("http://" + (getHostname())) + ":80/app"));
    }

    @Test
    public void test_servletPath() {
        Mockito.when(dispatcherServletPath.getPrefix()).thenReturn("app");
        servletContext.setContextPath("srv");
        Mockito.when(pathMappedEndpoints.getPath(EndpointId.of("health"))).thenReturn("/actuator/health");
        publishApplicationReadyEvent(factory, 80, null);
        Application app = factory.createApplication();
        assertThat(app.getManagementUrl()).isEqualTo((("http://" + (getHostname())) + ":80/srv/app/actuator"));
        assertThat(app.getHealthUrl()).isEqualTo((("http://" + (getHostname())) + ":80/srv/app/actuator/health"));
        assertThat(app.getServiceUrl()).isEqualTo((("http://" + (getHostname())) + ":80/srv"));
    }

    private static class TestWebServerInitializedEvent extends WebServerInitializedEvent {
        private final WebServer server = Mockito.mock(WebServer.class);

        private final WebServerApplicationContext context = Mockito.mock(WebServerApplicationContext.class);

        private TestWebServerInitializedEvent(String name, int port) {
            super(Mockito.mock(WebServer.class));
            Mockito.when(server.getPort()).thenReturn(port);
            Mockito.when(context.getServerNamespace()).thenReturn(name);
        }

        @Override
        public WebServerApplicationContext getApplicationContext() {
            return context;
        }

        @Override
        public WebServer getWebServer() {
            return this.server;
        }
    }
}

