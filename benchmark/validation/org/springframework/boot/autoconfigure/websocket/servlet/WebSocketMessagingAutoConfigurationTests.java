/**
 * Copyright 2012-2018 the original author or authors.
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


import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.sockjs.client.SockJsClient;


/**
 * Tests for {@link WebSocketMessagingAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class WebSocketMessagingAutoConfigurationTests {
    private AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext();

    private SockJsClient sockJsClient;

    @Test
    public void basicMessagingWithJsonResponse() throws Throwable {
        Object result = performStompSubscription("/app/json");
        assertThat(new String(((byte[]) (result)))).isEqualTo(String.format("{%n  \"foo\" : 5,%n  \"bar\" : \"baz\"%n}"));
    }

    @Test
    public void basicMessagingWithStringResponse() throws Throwable {
        Object result = performStompSubscription("/app/string");
        assertThat(new String(((byte[]) (result)))).isEqualTo("string data");
    }

    @Test
    public void customizedConverterTypesMatchDefaultConverterTypes() {
        List<MessageConverter> customizedConverters = getCustomizedConverters();
        List<MessageConverter> defaultConverters = getDefaultConverters();
        assertThat(customizedConverters.size()).isEqualTo(defaultConverters.size());
        Iterator<MessageConverter> customizedIterator = customizedConverters.iterator();
        Iterator<MessageConverter> defaultIterator = defaultConverters.iterator();
        while (customizedIterator.hasNext()) {
            assertThat(customizedIterator.next()).isInstanceOf(defaultIterator.next().getClass());
        } 
    }

    @Configuration
    @EnableWebSocket
    @EnableConfigurationProperties
    @EnableWebSocketMessageBroker
    @ImportAutoConfiguration({ JacksonAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class, WebSocketMessagingAutoConfiguration.class, DispatcherServletAutoConfiguration.class })
    static class WebSocketMessagingConfiguration implements WebSocketMessageBrokerConfigurer {
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/messaging").withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.setApplicationDestinationPrefixes("/app");
        }

        @Bean
        public WebSocketMessagingAutoConfigurationTests.MessagingController messagingController() {
            return new WebSocketMessagingAutoConfigurationTests.MessagingController();
        }

        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public TomcatWebSocketServletWebServerCustomizer tomcatCustomizer() {
            return new TomcatWebSocketServletWebServerCustomizer();
        }
    }

    @Controller
    static class MessagingController {
        @SubscribeMapping("/json")
        WebSocketMessagingAutoConfigurationTests.Data json() {
            return new WebSocketMessagingAutoConfigurationTests.Data(5, "baz");
        }

        @SubscribeMapping("/string")
        String string() {
            return "string data";
        }
    }

    static class Data {
        private int foo;

        private String bar;

        Data(int foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public int getFoo() {
            return this.foo;
        }

        public String getBar() {
            return this.bar;
        }
    }
}

