/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.web.socket.config;


import MessageBrokerBeanDefinitionParser.WEB_SOCKET_HANDLER_BEAN_NAME;
import MimeTypeUtils.APPLICATION_JSON;
import TransportType.WEBSOCKET;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.ContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.broker.DefaultSubscriptionRegistry;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.MultiServerUserRegistry;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationMessageHandler;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.simp.user.UserRegistryMessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.validation.Validator;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;
import org.springframework.web.socket.handler.TestWebSocketSession;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;
import org.springframework.web.socket.messaging.StompSubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;
import org.springframework.web.socket.sockjs.transport.handler.DefaultSockJsService;
import org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler;

import static MessageBrokerBeanDefinitionParser.MESSAGE_CONVERTER_BEAN_NAME;
import static MessageBrokerBeanDefinitionParser.MESSAGING_TEMPLATE_BEAN_NAME;


/**
 * Test fixture for {@link MessageBrokerBeanDefinitionParser}.
 * Also see test configuration files websocket-config-broker-*.xml.
 *
 * @author Brian Clozel
 * @author Artem Bilan
 * @author Rossen Stoyanchev
 */
public class MessageBrokerBeanDefinitionParserTests {
    private final GenericWebApplicationContext appContext = new GenericWebApplicationContext();

    @Test
    @SuppressWarnings("unchecked")
    public void simpleBroker() throws Exception {
        loadBeanDefinitions("websocket-config-broker-simple.xml");
        HandlerMapping hm = this.appContext.getBean(HandlerMapping.class);
        Assert.assertThat(hm, Matchers.instanceOf(SimpleUrlHandlerMapping.class));
        SimpleUrlHandlerMapping suhm = ((SimpleUrlHandlerMapping) (hm));
        Assert.assertThat(suhm.getUrlMap().keySet(), Matchers.hasSize(4));
        Assert.assertThat(suhm.getUrlMap().values(), Matchers.hasSize(4));
        HttpRequestHandler httpRequestHandler = ((HttpRequestHandler) (suhm.getUrlMap().get("/foo")));
        Assert.assertNotNull(httpRequestHandler);
        Assert.assertThat(httpRequestHandler, Matchers.instanceOf(WebSocketHttpRequestHandler.class));
        WebSocketHttpRequestHandler wsHttpRequestHandler = ((WebSocketHttpRequestHandler) (httpRequestHandler));
        HandshakeHandler handshakeHandler = wsHttpRequestHandler.getHandshakeHandler();
        Assert.assertNotNull(handshakeHandler);
        Assert.assertTrue((handshakeHandler instanceof TestHandshakeHandler));
        List<HandshakeInterceptor> interceptors = wsHttpRequestHandler.getHandshakeInterceptors();
        Assert.assertThat(interceptors, contains(instanceOf(FooTestInterceptor.class), instanceOf(BarTestInterceptor.class), instanceOf(OriginHandshakeInterceptor.class)));
        WebSocketSession session = new TestWebSocketSession("id");
        wsHttpRequestHandler.getWebSocketHandler().afterConnectionEstablished(session);
        Assert.assertEquals(true, session.getAttributes().get("decorated"));
        WebSocketHandler wsHandler = wsHttpRequestHandler.getWebSocketHandler();
        Assert.assertThat(wsHandler, Matchers.instanceOf(ExceptionWebSocketHandlerDecorator.class));
        wsHandler = getDelegate();
        Assert.assertThat(wsHandler, Matchers.instanceOf(LoggingWebSocketHandlerDecorator.class));
        wsHandler = getDelegate();
        Assert.assertThat(wsHandler, Matchers.instanceOf(TestWebSocketHandlerDecorator.class));
        wsHandler = getDelegate();
        Assert.assertThat(wsHandler, Matchers.instanceOf(SubProtocolWebSocketHandler.class));
        Assert.assertSame(wsHandler, this.appContext.getBean(WEB_SOCKET_HANDLER_BEAN_NAME));
        SubProtocolWebSocketHandler subProtocolWsHandler = ((SubProtocolWebSocketHandler) (wsHandler));
        Assert.assertEquals(Arrays.asList("v10.stomp", "v11.stomp", "v12.stomp"), subProtocolWsHandler.getSubProtocols());
        Assert.assertEquals((25 * 1000), subProtocolWsHandler.getSendTimeLimit());
        Assert.assertEquals((1024 * 1024), subProtocolWsHandler.getSendBufferSizeLimit());
        Assert.assertEquals((30 * 1000), subProtocolWsHandler.getTimeToFirstMessage());
        Map<String, SubProtocolHandler> handlerMap = subProtocolWsHandler.getProtocolHandlerMap();
        StompSubProtocolHandler stompHandler = ((StompSubProtocolHandler) (handlerMap.get("v12.stomp")));
        Assert.assertNotNull(stompHandler);
        Assert.assertEquals((128 * 1024), stompHandler.getMessageSizeLimit());
        Assert.assertNotNull(stompHandler.getErrorHandler());
        Assert.assertEquals(TestStompErrorHandler.class, stompHandler.getErrorHandler().getClass());
        Assert.assertNotNull(getPropertyValue("eventPublisher"));
        httpRequestHandler = ((HttpRequestHandler) (suhm.getUrlMap().get("/test/**")));
        Assert.assertNotNull(httpRequestHandler);
        Assert.assertThat(httpRequestHandler, Matchers.instanceOf(SockJsHttpRequestHandler.class));
        SockJsHttpRequestHandler sockJsHttpRequestHandler = ((SockJsHttpRequestHandler) (httpRequestHandler));
        wsHandler = unwrapWebSocketHandler(sockJsHttpRequestHandler.getWebSocketHandler());
        Assert.assertNotNull(wsHandler);
        Assert.assertThat(wsHandler, Matchers.instanceOf(SubProtocolWebSocketHandler.class));
        Assert.assertNotNull(sockJsHttpRequestHandler.getSockJsService());
        Assert.assertThat(sockJsHttpRequestHandler.getSockJsService(), Matchers.instanceOf(DefaultSockJsService.class));
        DefaultSockJsService defaultSockJsService = ((DefaultSockJsService) (sockJsHttpRequestHandler.getSockJsService()));
        WebSocketTransportHandler wsTransportHandler = ((WebSocketTransportHandler) (defaultSockJsService.getTransportHandlers().get(WEBSOCKET)));
        Assert.assertNotNull(wsTransportHandler.getHandshakeHandler());
        Assert.assertThat(wsTransportHandler.getHandshakeHandler(), Matchers.instanceOf(TestHandshakeHandler.class));
        Assert.assertFalse(defaultSockJsService.shouldSuppressCors());
        ThreadPoolTaskScheduler scheduler = ((ThreadPoolTaskScheduler) (defaultSockJsService.getTaskScheduler()));
        ScheduledThreadPoolExecutor executor = scheduler.getScheduledThreadPoolExecutor();
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), executor.getCorePoolSize());
        Assert.assertTrue(executor.getRemoveOnCancelPolicy());
        interceptors = defaultSockJsService.getHandshakeInterceptors();
        Assert.assertThat(interceptors, contains(instanceOf(FooTestInterceptor.class), instanceOf(BarTestInterceptor.class), instanceOf(OriginHandshakeInterceptor.class)));
        Assert.assertTrue(defaultSockJsService.getAllowedOrigins().contains("http://mydomain3.com"));
        Assert.assertTrue(defaultSockJsService.getAllowedOrigins().contains("http://mydomain4.com"));
        SimpUserRegistry userRegistry = this.appContext.getBean(SimpUserRegistry.class);
        Assert.assertNotNull(userRegistry);
        Assert.assertEquals(DefaultSimpUserRegistry.class, userRegistry.getClass());
        UserDestinationResolver userDestResolver = this.appContext.getBean(UserDestinationResolver.class);
        Assert.assertNotNull(userDestResolver);
        Assert.assertThat(userDestResolver, Matchers.instanceOf(DefaultUserDestinationResolver.class));
        DefaultUserDestinationResolver defaultUserDestResolver = ((DefaultUserDestinationResolver) (userDestResolver));
        Assert.assertEquals("/personal/", defaultUserDestResolver.getDestinationPrefix());
        UserDestinationMessageHandler userDestHandler = this.appContext.getBean(UserDestinationMessageHandler.class);
        Assert.assertNotNull(userDestHandler);
        SimpleBrokerMessageHandler brokerMessageHandler = this.appContext.getBean(SimpleBrokerMessageHandler.class);
        Assert.assertNotNull(brokerMessageHandler);
        Collection<String> prefixes = brokerMessageHandler.getDestinationPrefixes();
        Assert.assertEquals(Arrays.asList("/topic", "/queue"), new ArrayList<>(prefixes));
        DefaultSubscriptionRegistry registry = ((DefaultSubscriptionRegistry) (brokerMessageHandler.getSubscriptionRegistry()));
        Assert.assertEquals("my-selector", registry.getSelectorHeaderName());
        Assert.assertNotNull(brokerMessageHandler.getTaskScheduler());
        Assert.assertArrayEquals(new long[]{ 15000, 15000 }, brokerMessageHandler.getHeartbeatValue());
        Assert.assertTrue(brokerMessageHandler.isPreservePublishOrder());
        List<Class<? extends MessageHandler>> subscriberTypes = Arrays.asList(SimpAnnotationMethodMessageHandler.class, UserDestinationMessageHandler.class, SimpleBrokerMessageHandler.class);
        testChannel("clientInboundChannel", subscriberTypes, 2);
        testExecutor("clientInboundChannel", ((Runtime.getRuntime().availableProcessors()) * 2), Integer.MAX_VALUE, 60);
        subscriberTypes = Collections.singletonList(SubProtocolWebSocketHandler.class);
        testChannel("clientOutboundChannel", subscriberTypes, 2);
        testExecutor("clientOutboundChannel", ((Runtime.getRuntime().availableProcessors()) * 2), Integer.MAX_VALUE, 60);
        subscriberTypes = Arrays.asList(SimpleBrokerMessageHandler.class, UserDestinationMessageHandler.class);
        testChannel("brokerChannel", subscriberTypes, 1);
        try {
            this.appContext.getBean("brokerChannelExecutor", ThreadPoolTaskExecutor.class);
            Assert.fail("expected exception");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        Assert.assertNotNull(this.appContext.getBean("webSocketScopeConfigurer", CustomScopeConfigurer.class));
        DirectFieldAccessor accessor = new DirectFieldAccessor(registry);
        Object pathMatcher = accessor.getPropertyValue("pathMatcher");
        String pathSeparator = ((String) (new DirectFieldAccessor(pathMatcher).getPropertyValue("pathSeparator")));
        Assert.assertEquals(".", pathSeparator);
    }

    @Test
    public void stompBrokerRelay() {
        loadBeanDefinitions("websocket-config-broker-relay.xml");
        HandlerMapping hm = this.appContext.getBean(HandlerMapping.class);
        Assert.assertNotNull(hm);
        Assert.assertThat(hm, Matchers.instanceOf(SimpleUrlHandlerMapping.class));
        SimpleUrlHandlerMapping suhm = ((SimpleUrlHandlerMapping) (hm));
        Assert.assertThat(suhm.getUrlMap().keySet(), Matchers.hasSize(1));
        Assert.assertThat(suhm.getUrlMap().values(), Matchers.hasSize(1));
        Assert.assertEquals(2, suhm.getOrder());
        HttpRequestHandler httpRequestHandler = ((HttpRequestHandler) (suhm.getUrlMap().get("/foo/**")));
        Assert.assertNotNull(httpRequestHandler);
        Assert.assertThat(httpRequestHandler, Matchers.instanceOf(SockJsHttpRequestHandler.class));
        SockJsHttpRequestHandler sockJsHttpRequestHandler = ((SockJsHttpRequestHandler) (httpRequestHandler));
        WebSocketHandler wsHandler = unwrapWebSocketHandler(sockJsHttpRequestHandler.getWebSocketHandler());
        Assert.assertNotNull(wsHandler);
        Assert.assertThat(wsHandler, Matchers.instanceOf(SubProtocolWebSocketHandler.class));
        Assert.assertNotNull(sockJsHttpRequestHandler.getSockJsService());
        UserDestinationResolver userDestResolver = this.appContext.getBean(UserDestinationResolver.class);
        Assert.assertNotNull(userDestResolver);
        Assert.assertThat(userDestResolver, Matchers.instanceOf(DefaultUserDestinationResolver.class));
        DefaultUserDestinationResolver defaultUserDestResolver = ((DefaultUserDestinationResolver) (userDestResolver));
        Assert.assertEquals("/user/", defaultUserDestResolver.getDestinationPrefix());
        StompBrokerRelayMessageHandler messageBroker = this.appContext.getBean(StompBrokerRelayMessageHandler.class);
        Assert.assertNotNull(messageBroker);
        Assert.assertEquals("clientlogin", messageBroker.getClientLogin());
        Assert.assertEquals("clientpass", messageBroker.getClientPasscode());
        Assert.assertEquals("syslogin", messageBroker.getSystemLogin());
        Assert.assertEquals("syspass", messageBroker.getSystemPasscode());
        Assert.assertEquals("relayhost", messageBroker.getRelayHost());
        Assert.assertEquals(1234, messageBroker.getRelayPort());
        Assert.assertEquals("spring.io", messageBroker.getVirtualHost());
        Assert.assertEquals(5000, messageBroker.getSystemHeartbeatReceiveInterval());
        Assert.assertEquals(5000, messageBroker.getSystemHeartbeatSendInterval());
        Assert.assertThat(messageBroker.getDestinationPrefixes(), Matchers.containsInAnyOrder("/topic", "/queue"));
        Assert.assertTrue(messageBroker.isPreservePublishOrder());
        List<Class<? extends MessageHandler>> subscriberTypes = Arrays.asList(SimpAnnotationMethodMessageHandler.class, UserDestinationMessageHandler.class, StompBrokerRelayMessageHandler.class);
        testChannel("clientInboundChannel", subscriberTypes, 2);
        testExecutor("clientInboundChannel", ((Runtime.getRuntime().availableProcessors()) * 2), Integer.MAX_VALUE, 60);
        subscriberTypes = Collections.singletonList(SubProtocolWebSocketHandler.class);
        testChannel("clientOutboundChannel", subscriberTypes, 2);
        testExecutor("clientOutboundChannel", ((Runtime.getRuntime().availableProcessors()) * 2), Integer.MAX_VALUE, 60);
        subscriberTypes = Arrays.asList(StompBrokerRelayMessageHandler.class, UserDestinationMessageHandler.class);
        testChannel("brokerChannel", subscriberTypes, 1);
        try {
            this.appContext.getBean("brokerChannelExecutor", ThreadPoolTaskExecutor.class);
            Assert.fail("expected exception");
        } catch (NoSuchBeanDefinitionException ex) {
            // expected
        }
        String destination = "/topic/unresolved-user-destination";
        UserDestinationMessageHandler userDestHandler = this.appContext.getBean(UserDestinationMessageHandler.class);
        Assert.assertEquals(destination, userDestHandler.getBroadcastDestination());
        Assert.assertNotNull(messageBroker.getSystemSubscriptions());
        Assert.assertSame(userDestHandler, messageBroker.getSystemSubscriptions().get(destination));
        destination = "/topic/simp-user-registry";
        UserRegistryMessageHandler userRegistryHandler = this.appContext.getBean(UserRegistryMessageHandler.class);
        Assert.assertEquals(destination, userRegistryHandler.getBroadcastDestination());
        Assert.assertNotNull(messageBroker.getSystemSubscriptions());
        Assert.assertSame(userRegistryHandler, messageBroker.getSystemSubscriptions().get(destination));
        SimpUserRegistry userRegistry = this.appContext.getBean(SimpUserRegistry.class);
        Assert.assertEquals(MultiServerUserRegistry.class, userRegistry.getClass());
        String name = "webSocketMessageBrokerStats";
        WebSocketMessageBrokerStats stats = this.appContext.getBean(name, WebSocketMessageBrokerStats.class);
        String actual = stats.toString();
        String expected = "WebSocketSession\\[0 current WS\\(0\\)-HttpStream\\(0\\)-HttpPoll\\(0\\), " + ((((((((("0 total, 0 closed abnormally \\(0 connect failure, 0 send limit, 0 transport error\\)\\], " + "stompSubProtocol\\[processed CONNECT\\(0\\)-CONNECTED\\(0\\)-DISCONNECT\\(0\\)\\], ") + "stompBrokerRelay\\[0 sessions, relayhost:1234 \\(not available\\), ") + "processed CONNECT\\(0\\)-CONNECTED\\(0\\)-DISCONNECT\\(0\\)\\], ") + "inboundChannel\\[pool size = \\d, active threads = \\d, queued tasks = \\d, ") + "completed tasks = \\d\\], ") + "outboundChannel\\[pool size = \\d, active threads = \\d, queued tasks = \\d, ") + "completed tasks = \\d\\], ") + "sockJsScheduler\\[pool size = \\d, active threads = \\d, queued tasks = \\d, ") + "completed tasks = \\d\\]");
        Assert.assertTrue(((("\nExpected: " + (expected.replace("\\", ""))) + "\n  Actual: ") + actual), actual.matches(expected));
    }

    @Test
    public void annotationMethodMessageHandler() {
        loadBeanDefinitions("websocket-config-broker-simple.xml");
        SimpAnnotationMethodMessageHandler annotationMethodMessageHandler = this.appContext.getBean(SimpAnnotationMethodMessageHandler.class);
        Assert.assertNotNull(annotationMethodMessageHandler);
        MessageConverter messageConverter = annotationMethodMessageHandler.getMessageConverter();
        Assert.assertNotNull(messageConverter);
        Assert.assertTrue((messageConverter instanceof CompositeMessageConverter));
        String name = MESSAGE_CONVERTER_BEAN_NAME;
        CompositeMessageConverter compositeMessageConverter = this.appContext.getBean(name, CompositeMessageConverter.class);
        Assert.assertNotNull(compositeMessageConverter);
        name = MESSAGING_TEMPLATE_BEAN_NAME;
        SimpMessagingTemplate simpMessagingTemplate = this.appContext.getBean(name, SimpMessagingTemplate.class);
        Assert.assertNotNull(simpMessagingTemplate);
        Assert.assertEquals("/personal/", simpMessagingTemplate.getUserDestinationPrefix());
        List<MessageConverter> converters = compositeMessageConverter.getConverters();
        Assert.assertThat(converters.size(), Matchers.is(3));
        Assert.assertThat(converters.get(0), Matchers.instanceOf(StringMessageConverter.class));
        Assert.assertThat(converters.get(1), Matchers.instanceOf(ByteArrayMessageConverter.class));
        Assert.assertThat(converters.get(2), Matchers.instanceOf(MappingJackson2MessageConverter.class));
        ContentTypeResolver resolver = getContentTypeResolver();
        Assert.assertEquals(APPLICATION_JSON, getDefaultMimeType());
        DirectFieldAccessor handlerAccessor = new DirectFieldAccessor(annotationMethodMessageHandler);
        Object pathMatcher = handlerAccessor.getPropertyValue("pathMatcher");
        String pathSeparator = ((String) (new DirectFieldAccessor(pathMatcher).getPropertyValue("pathSeparator")));
        Assert.assertEquals(".", pathSeparator);
    }

    @Test
    public void customChannels() {
        loadBeanDefinitions("websocket-config-broker-customchannels.xml");
        SimpAnnotationMethodMessageHandler annotationMethodMessageHandler = this.appContext.getBean(SimpAnnotationMethodMessageHandler.class);
        Validator validator = annotationMethodMessageHandler.getValidator();
        Assert.assertNotNull(validator);
        Assert.assertSame(this.appContext.getBean("myValidator"), validator);
        Assert.assertThat(validator, Matchers.instanceOf(TestValidator.class));
        List<Class<? extends MessageHandler>> subscriberTypes = Arrays.asList(SimpAnnotationMethodMessageHandler.class, UserDestinationMessageHandler.class, SimpleBrokerMessageHandler.class);
        testChannel("clientInboundChannel", subscriberTypes, 3);
        testExecutor("clientInboundChannel", 100, 200, 600);
        subscriberTypes = Collections.singletonList(SubProtocolWebSocketHandler.class);
        testChannel("clientOutboundChannel", subscriberTypes, 3);
        testExecutor("clientOutboundChannel", 101, 201, 601);
        subscriberTypes = Arrays.asList(SimpleBrokerMessageHandler.class, UserDestinationMessageHandler.class);
        testChannel("brokerChannel", subscriberTypes, 1);
        testExecutor("brokerChannel", 102, 202, 602);
    }

    // SPR-11623
    @Test
    public void customChannelsWithDefaultExecutor() {
        loadBeanDefinitions("websocket-config-broker-customchannels-default-executor.xml");
        testExecutor("clientInboundChannel", ((Runtime.getRuntime().availableProcessors()) * 2), Integer.MAX_VALUE, 60);
        testExecutor("clientOutboundChannel", ((Runtime.getRuntime().availableProcessors()) * 2), Integer.MAX_VALUE, 60);
        Assert.assertFalse(this.appContext.containsBean("brokerChannelExecutor"));
    }

    @Test
    public void customArgumentAndReturnValueTypes() {
        loadBeanDefinitions("websocket-config-broker-custom-argument-and-return-value-types.xml");
        SimpAnnotationMethodMessageHandler handler = this.appContext.getBean(SimpAnnotationMethodMessageHandler.class);
        List<HandlerMethodArgumentResolver> customResolvers = handler.getCustomArgumentResolvers();
        Assert.assertEquals(2, customResolvers.size());
        Assert.assertTrue(handler.getArgumentResolvers().contains(customResolvers.get(0)));
        Assert.assertTrue(handler.getArgumentResolvers().contains(customResolvers.get(1)));
        List<HandlerMethodReturnValueHandler> customHandlers = handler.getCustomReturnValueHandlers();
        Assert.assertEquals(2, customHandlers.size());
        Assert.assertTrue(handler.getReturnValueHandlers().contains(customHandlers.get(0)));
        Assert.assertTrue(handler.getReturnValueHandlers().contains(customHandlers.get(1)));
    }

    @Test
    public void messageConverters() {
        loadBeanDefinitions("websocket-config-broker-converters.xml");
        CompositeMessageConverter compositeConverter = this.appContext.getBean(CompositeMessageConverter.class);
        Assert.assertNotNull(compositeConverter);
        Assert.assertEquals(4, compositeConverter.getConverters().size());
        Assert.assertEquals(StringMessageConverter.class, compositeConverter.getConverters().iterator().next().getClass());
    }

    @Test
    public void messageConvertersDefaultsOff() {
        loadBeanDefinitions("websocket-config-broker-converters-defaults-off.xml");
        CompositeMessageConverter compositeConverter = this.appContext.getBean(CompositeMessageConverter.class);
        Assert.assertNotNull(compositeConverter);
        Assert.assertEquals(1, compositeConverter.getConverters().size());
        Assert.assertEquals(StringMessageConverter.class, compositeConverter.getConverters().iterator().next().getClass());
    }
}

