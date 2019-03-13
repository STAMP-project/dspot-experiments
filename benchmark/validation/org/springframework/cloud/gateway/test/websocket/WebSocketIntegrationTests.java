/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.test.websocket;


import CloseStatus.GOING_AWAY;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.gateway.filter.WebsocketRoutingFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.test.PermitAllSecurityConfiguration;
import org.springframework.cloud.gateway.test.support.HttpServer;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.core.publisher.ReplayProcessor;


/**
 * Original is here
 * {@see https://github.com/spring-projects/spring-framework/blob/master/spring-webflux/src/test/java/org/springframework/web/reactive/socket/WebSocketIntegrationTests.java}
 * Integration tests with server-side {@link WebSocketHandler}s.
 *
 * @author Rossen Stoyanchev
 */
public class WebSocketIntegrationTests {
    private static final Log logger = LogFactory.getLog(WebSocketIntegrationTests.class);

    protected int serverPort;

    private WebSocketClient client;

    private HttpServer server;

    private ConfigurableApplicationContext gatewayContext;

    private int gatewayPort;

    @Test
    public void echo() throws Exception {
        int count = 100;
        Flux<String> input = Flux.range(1, count).map(( index) -> "msg-" + index);
        ReplayProcessor<Object> output = ReplayProcessor.create(count);
        client.execute(getUrl("/echo"), ( session) -> {
            logger.debug("Starting to send messages");
            return session.send(input.doOnNext(( s) -> logger.debug(("outbound " + s))).map(( s) -> session.textMessage(s))).thenMany(session.receive().take(count).map(WebSocketMessage::getPayloadAsText)).subscribeWith(output).doOnNext(( s) -> logger.debug(("inbound " + s))).then().doOnSuccessOrError(( aVoid, ex) -> logger.debug(("Done with " + (ex != null ? ex.getMessage() : "success"))));
        }).block(Duration.ofMillis(5000));
        assertThat(output.collectList().block(Duration.ofMillis(5000))).isEqualTo(input.collectList().block(Duration.ofMillis(5000)));
    }

    @Test
    public void echoForHttp() throws Exception {
        int count = 100;
        Flux<String> input = Flux.range(1, count).map(( index) -> "msg-" + index);
        ReplayProcessor<Object> output = ReplayProcessor.create(count);
        client.execute(getHttpUrl("/echoForHttp"), ( session) -> {
            logger.debug("Starting to send messages");
            return session.send(input.doOnNext(( s) -> logger.debug(("outbound " + s))).map(( s) -> session.textMessage(s))).thenMany(session.receive().take(count).map(WebSocketMessage::getPayloadAsText)).subscribeWith(output).doOnNext(( s) -> logger.debug(("inbound " + s))).then().doOnSuccessOrError(( aVoid, ex) -> logger.debug(("Done with " + (ex != null ? ex.getMessage() : "success"))));
        }).block(Duration.ofMillis(5000));
        assertThat(output.collectList().block(Duration.ofMillis(5000))).isEqualTo(input.collectList().block(Duration.ofMillis(5000)));
    }

    @Test
    public void subProtocol() throws Exception {
        String protocol = "echo-v1";
        String protocol2 = "echo-v2";
        AtomicReference<HandshakeInfo> infoRef = new AtomicReference<>();
        MonoProcessor<Object> output = MonoProcessor.create();
        client.execute(getUrl("/sub-protocol"), new WebSocketHandler() {
            @Override
            public List<String> getSubProtocols() {
                return Arrays.asList(protocol, protocol2);
            }

            @Override
            public Mono<Void> handle(WebSocketSession session) {
                infoRef.set(session.getHandshakeInfo());
                return session.receive().map(WebSocketMessage::getPayloadAsText).subscribeWith(output).then();
            }
        }).block(Duration.ofMillis(5000));
        HandshakeInfo info = infoRef.get();
        assertThat(info.getHeaders().getFirst("Upgrade")).isEqualToIgnoringCase("websocket");
        assertThat(info.getHeaders().getFirst("Sec-WebSocket-Protocol")).isEqualTo(protocol);
        assertThat(info.getSubProtocol()).as("Wrong protocol accepted").isEqualTo(protocol);
        assertThat(output.block(Duration.ofSeconds(5))).as("Wrong protocol detected on the server side").isEqualTo(protocol);
    }

    @Test
    public void customHeader() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("my-header", "my-value");
        MonoProcessor<Object> output = MonoProcessor.create();
        client.execute(getUrl("/custom-header"), headers, ( session) -> session.receive().map(WebSocketMessage::getPayloadAsText).subscribeWith(output).then()).block(Duration.ofMillis(5000));
        assertThat(output.block(Duration.ofMillis(5000))).isEqualTo("my-header:my-value");
    }

    @Test
    public void sessionClosing() throws Exception {
        this.client.execute(getUrl("/close"), ( session) -> {
            logger.debug("Starting..");
            return session.receive().doOnNext(( s) -> logger.debug(("inbound " + s))).then().doFinally(( signalType) -> {
                logger.debug(("Completed with: " + signalType));
            });
        }).block(Duration.ofMillis(5000));
    }

    @Configuration
    static class WebSocketTestConfig {
        @Bean
        public DispatcherHandler webHandler() {
            return new DispatcherHandler();
        }

        @Bean
        public WebSocketHandlerAdapter handlerAdapter() {
            return new WebSocketHandlerAdapter(webSocketService());
        }

        @Bean
        public WebSocketService webSocketService() {
            return new org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService(getUpgradeStrategy());
        }

        protected RequestUpgradeStrategy getUpgradeStrategy() {
            return new ReactorNettyRequestUpgradeStrategy();
        }

        @Bean
        public HandlerMapping handlerMapping() {
            Map<String, WebSocketHandler> map = new HashMap<>();
            map.put("/echo", new WebSocketIntegrationTests.EchoWebSocketHandler());
            map.put("/echoForHttp", new WebSocketIntegrationTests.EchoWebSocketHandler());
            map.put("/sub-protocol", new WebSocketIntegrationTests.SubProtocolWebSocketHandler());
            map.put("/custom-header", new WebSocketIntegrationTests.CustomHeaderHandler());
            map.put("/close", new WebSocketIntegrationTests.SessionClosingHandler());
            SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
            mapping.setUrlMap(map);
            return mapping;
        }
    }

    private static class EchoWebSocketHandler implements WebSocketHandler {
        @Override
        public Mono<Void> handle(WebSocketSession session) {
            // Use retain() for Reactor Netty
            return session.send(session.receive().doOnNext(WebSocketMessage::retain));
        }
    }

    private static class SubProtocolWebSocketHandler implements WebSocketHandler {
        @Override
        public List<String> getSubProtocols() {
            return Arrays.asList("echo-v1", "echo-v2");
        }

        @Override
        public Mono<Void> handle(WebSocketSession session) {
            String protocol = session.getHandshakeInfo().getSubProtocol();
            if (!(StringUtils.hasText(protocol))) {
                return Mono.error(new IllegalStateException("Missing protocol"));
            }
            List<String> protocols = session.getHandshakeInfo().getHeaders().get(WebsocketRoutingFilter.SEC_WEBSOCKET_PROTOCOL);
            assertThat(protocols).contains("echo-v1,echo-v2");
            WebSocketMessage message = session.textMessage(protocol);
            return WebSocketIntegrationTests.doSend(session, Mono.just(message));
        }
    }

    private static class CustomHeaderHandler implements WebSocketHandler {
        @Override
        public Mono<Void> handle(WebSocketSession session) {
            HttpHeaders headers = session.getHandshakeInfo().getHeaders();
            if (!(headers.containsKey("my-header"))) {
                return Mono.error(new IllegalStateException("Missing my-header"));
            }
            String payload = "my-header:" + (headers.getFirst("my-header"));
            WebSocketMessage message = session.textMessage(payload);
            return WebSocketIntegrationTests.doSend(session, Mono.just(message));
        }
    }

    private static class SessionClosingHandler implements WebSocketHandler {
        @Override
        public Mono<Void> handle(WebSocketSession session) {
            return Flux.never().mergeWith(session.close(GOING_AWAY)).then();
        }
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(PermitAllSecurityConfiguration.class)
    @RibbonClient(name = "wsservice", configuration = WebSocketIntegrationTests.LocalRibbonClientConfiguration.class)
    protected static class GatewayConfig {
        @Bean
        public RouteLocator wsRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route(( r) -> r.path("/echoForHttp").uri("lb://wsservice")).route(( r) -> r.alwaysTrue().uri("lb:ws://wsservice")).build();
        }
    }

    public static class LocalRibbonClientConfiguration {
        @Value("${ws.server.port}")
        private int wsPort;

        @Bean
        public ServerList<Server> ribbonServerList() {
            return new org.springframework.cloud.netflix.ribbon.StaticServerList(new Server("localhost", this.wsPort));
        }
    }
}

