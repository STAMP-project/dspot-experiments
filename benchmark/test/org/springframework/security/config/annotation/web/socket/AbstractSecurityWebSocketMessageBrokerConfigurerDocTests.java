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
package org.springframework.security.config.annotation.web.socket;


import SimpMessageType.MESSAGE;
import SimpMessageType.SUBSCRIBE;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


public class AbstractSecurityWebSocketMessageBrokerConfigurerDocTests {
    AnnotationConfigWebApplicationContext context;

    TestingAuthenticationToken messageUser;

    CsrfToken token;

    String sessionAttr;

    @Test
    public void securityMappings() {
        loadConfig(AbstractSecurityWebSocketMessageBrokerConfigurerDocTests.WebSocketSecurityConfig.class);
        clientInboundChannel().send(message("/user/queue/errors", SUBSCRIBE));
        try {
            clientInboundChannel().send(message("/denyAll", MESSAGE));
            fail("Expected Exception");
        } catch (MessageDeliveryException expected) {
            assertThat(expected.getCause()).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Controller
    static class MyController {
        @MessageMapping("/authentication")
        public void authentication(@AuthenticationPrincipal
        String un) {
            // ... do something ...
        }
    }

    @Configuration
    static class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
        @Override
        protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
            // <5>
            // <4>
            // <3>
            // <2>
            // <1>
            messages.nullDestMatcher().authenticated().simpSubscribeDestMatchers("/user/queue/errors").permitAll().simpDestMatchers("/app/**").hasRole("USER").simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole("USER").simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll().anyMessage().denyAll();// <6>

        }
    }

    @Configuration
    @EnableWebSocketMessageBroker
    static class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/chat").withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/queue/", "/topic/");
            registry.setApplicationDestinationPrefixes("/permitAll", "/denyAll");
        }

        @Bean
        public AbstractSecurityWebSocketMessageBrokerConfigurerDocTests.MyController myController() {
            return new AbstractSecurityWebSocketMessageBrokerConfigurerDocTests.MyController();
        }
    }

    @Configuration
    static class SyncExecutorConfig {
        @Bean
        public static SyncExecutorSubscribableChannelPostProcessor postProcessor() {
            return new SyncExecutorSubscribableChannelPostProcessor();
        }
    }
}

