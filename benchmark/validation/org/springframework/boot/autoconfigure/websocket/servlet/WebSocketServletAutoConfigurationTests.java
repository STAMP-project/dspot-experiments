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
package org.springframework.boot.autoconfigure.websocket.servlet;


import WebSocketServletAutoConfiguration.JettyWebSocketConfiguration;
import WebSocketServletAutoConfiguration.TomcatWebSocketConfiguration;
import org.junit.Test;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link WebSocketServletAutoConfiguration}
 *
 * @author Andy Wilkinson
 */
public class WebSocketServletAutoConfigurationTests {
    private AnnotationConfigServletWebServerApplicationContext context;

    @Test
    public void tomcatServerContainerIsAvailableFromTheServletContext() {
        serverContainerIsAvailableFromTheServletContext(WebSocketServletAutoConfigurationTests.TomcatConfiguration.class, TomcatWebSocketConfiguration.class);
    }

    @Test
    public void jettyServerContainerIsAvailableFromTheServletContext() {
        serverContainerIsAvailableFromTheServletContext(WebSocketServletAutoConfigurationTests.JettyConfiguration.class, JettyWebSocketConfiguration.class);
    }

    @Configuration
    static class CommonConfiguration {
        @Bean
        public WebServerFactoryCustomizerBeanPostProcessor ServletWebServerCustomizerBeanPostProcessor() {
            return new WebServerFactoryCustomizerBeanPostProcessor();
        }
    }

    @Configuration
    static class TomcatConfiguration extends WebSocketServletAutoConfigurationTests.CommonConfiguration {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
            factory.setPort(0);
            return factory;
        }
    }

    @Configuration
    static class JettyConfiguration extends WebSocketServletAutoConfigurationTests.CommonConfiguration {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            JettyServletWebServerFactory JettyServletWebServerFactory = new JettyServletWebServerFactory();
            JettyServletWebServerFactory.setPort(0);
            return JettyServletWebServerFactory;
        }
    }
}

