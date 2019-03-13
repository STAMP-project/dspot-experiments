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


import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Test fixture for {@link ServerEndpointRegistration}.
 *
 * @author Rossen Stoyanchev
 */
public class ServerEndpointRegistrationTests {
    @Test
    public void endpointPerConnection() throws Exception {
        @SuppressWarnings("resource")
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(ServerEndpointRegistrationTests.Config.class);
        ServerEndpointRegistration registration = new ServerEndpointRegistration("/path", ServerEndpointRegistrationTests.EchoEndpoint.class);
        registration.setBeanFactory(context.getBeanFactory());
        ServerEndpointRegistrationTests.EchoEndpoint endpoint = registration.getConfigurator().getEndpointInstance(ServerEndpointRegistrationTests.EchoEndpoint.class);
        Assert.assertNotNull(endpoint);
    }

    @Test
    public void endpointSingleton() throws Exception {
        ServerEndpointRegistrationTests.EchoEndpoint endpoint = new ServerEndpointRegistrationTests.EchoEndpoint(new ServerEndpointRegistrationTests.EchoService());
        ServerEndpointRegistration registration = new ServerEndpointRegistration("/path", endpoint);
        ServerEndpointRegistrationTests.EchoEndpoint actual = registration.getConfigurator().getEndpointInstance(ServerEndpointRegistrationTests.EchoEndpoint.class);
        Assert.assertSame(endpoint, actual);
    }

    @Configuration
    static class Config {
        @Bean
        public ServerEndpointRegistrationTests.EchoService echoService() {
            return new ServerEndpointRegistrationTests.EchoService();
        }
    }

    private static class EchoEndpoint extends Endpoint {
        @SuppressWarnings("unused")
        private final ServerEndpointRegistrationTests.EchoService service;

        @Autowired
        public EchoEndpoint(ServerEndpointRegistrationTests.EchoService service) {
            this.service = service;
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
        }
    }

    private static class EchoService {}
}

