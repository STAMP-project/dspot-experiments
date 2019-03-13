/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.web.socket.server.standard;


import javax.websocket.server.ServerEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


public class SpringConfiguratorTests {
    private MockServletContext servletContext;

    private ContextLoader contextLoader;

    private AnnotationConfigWebApplicationContext webAppContext;

    private SpringConfigurator configurator;

    @Test
    public void getEndpointPerConnection() throws Exception {
        SpringConfiguratorTests.PerConnectionEchoEndpoint endpoint = this.configurator.getEndpointInstance(SpringConfiguratorTests.PerConnectionEchoEndpoint.class);
        Assert.assertNotNull(endpoint);
    }

    @Test
    public void getEndpointSingletonByType() throws Exception {
        SpringConfiguratorTests.EchoEndpoint expected = this.webAppContext.getBean(SpringConfiguratorTests.EchoEndpoint.class);
        SpringConfiguratorTests.EchoEndpoint actual = this.configurator.getEndpointInstance(SpringConfiguratorTests.EchoEndpoint.class);
        Assert.assertSame(expected, actual);
    }

    @Test
    public void getEndpointSingletonByComponentName() throws Exception {
        SpringConfiguratorTests.ComponentEchoEndpoint expected = this.webAppContext.getBean(SpringConfiguratorTests.ComponentEchoEndpoint.class);
        SpringConfiguratorTests.ComponentEchoEndpoint actual = this.configurator.getEndpointInstance(SpringConfiguratorTests.ComponentEchoEndpoint.class);
        Assert.assertSame(expected, actual);
    }

    @Configuration
    @ComponentScan(basePackageClasses = SpringConfiguratorTests.class)
    static class Config {
        @Bean
        public SpringConfiguratorTests.EchoEndpoint javaConfigEndpoint() {
            return new SpringConfiguratorTests.EchoEndpoint(echoService());
        }

        @Bean
        public SpringConfiguratorTests.EchoService echoService() {
            return new SpringConfiguratorTests.EchoService();
        }
    }

    @ServerEndpoint("/echo")
    private static class EchoEndpoint {
        @SuppressWarnings("unused")
        private final SpringConfiguratorTests.EchoService service;

        @Autowired
        public EchoEndpoint(SpringConfiguratorTests.EchoService service) {
            this.service = service;
        }
    }

    @Component("myComponentEchoEndpoint")
    @ServerEndpoint("/echo")
    private static class ComponentEchoEndpoint {
        @SuppressWarnings("unused")
        private final SpringConfiguratorTests.EchoService service;

        @Autowired
        public ComponentEchoEndpoint(SpringConfiguratorTests.EchoService service) {
            this.service = service;
        }
    }

    @ServerEndpoint("/echo")
    private static class PerConnectionEchoEndpoint {
        @SuppressWarnings("unused")
        private final SpringConfiguratorTests.EchoService service;

        @Autowired
        public PerConnectionEchoEndpoint(SpringConfiguratorTests.EchoService service) {
            this.service = service;
        }
    }

    private static class EchoService {}
}

