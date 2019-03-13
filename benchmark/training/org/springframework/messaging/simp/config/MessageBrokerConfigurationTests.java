/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.messaging.simp.config;


import MimeTypeUtils.APPLICATION_JSON;
import SimpMessageType.MESSAGE;
import StompCommand.SEND;
import StompCommand.SUBSCRIBE;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.ContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.broker.DefaultSubscriptionRegistry;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.MultiServerUserRegistry;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationMessageHandler;
import org.springframework.messaging.simp.user.UserRegistryMessageHandler;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;


/**
 * Test fixture for {@link AbstractMessageBrokerConfiguration}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Sebastien Deleuze
 */
public class MessageBrokerConfigurationTests {
    @Test
    public void clientInboundChannel() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.SimpleBrokerConfig.class);
        MessageBrokerConfigurationTests.TestChannel channel = context.getBean("clientInboundChannel", MessageBrokerConfigurationTests.TestChannel.class);
        Set<MessageHandler> handlers = getSubscribers();
        Assert.assertEquals(3, handlers.size());
        Assert.assertTrue(handlers.contains(context.getBean(SimpAnnotationMethodMessageHandler.class)));
        Assert.assertTrue(handlers.contains(context.getBean(UserDestinationMessageHandler.class)));
        Assert.assertTrue(handlers.contains(context.getBean(SimpleBrokerMessageHandler.class)));
    }

    @Test
    public void clientInboundChannelWithBrokerRelay() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.BrokerRelayConfig.class);
        MessageBrokerConfigurationTests.TestChannel channel = context.getBean("clientInboundChannel", MessageBrokerConfigurationTests.TestChannel.class);
        Set<MessageHandler> handlers = getSubscribers();
        Assert.assertEquals(3, handlers.size());
        Assert.assertTrue(handlers.contains(context.getBean(SimpAnnotationMethodMessageHandler.class)));
        Assert.assertTrue(handlers.contains(context.getBean(UserDestinationMessageHandler.class)));
        Assert.assertTrue(handlers.contains(context.getBean(StompBrokerRelayMessageHandler.class)));
    }

    @Test
    public void clientInboundChannelCustomized() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.CustomConfig.class);
        AbstractSubscribableChannel channel = context.getBean("clientInboundChannel", AbstractSubscribableChannel.class);
        Assert.assertEquals(3, channel.getInterceptors().size());
        MessageBrokerConfigurationTests.CustomThreadPoolTaskExecutor taskExecutor = context.getBean("clientInboundChannelExecutor", MessageBrokerConfigurationTests.CustomThreadPoolTaskExecutor.class);
        Assert.assertEquals(11, getCorePoolSize());
        Assert.assertEquals(12, getMaxPoolSize());
        Assert.assertEquals(13, getKeepAliveSeconds());
    }

    @Test
    public void clientOutboundChannelUsedByAnnotatedMethod() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.SimpleBrokerConfig.class);
        MessageBrokerConfigurationTests.TestChannel channel = context.getBean("clientOutboundChannel", MessageBrokerConfigurationTests.TestChannel.class);
        SimpAnnotationMethodMessageHandler messageHandler = context.getBean(SimpAnnotationMethodMessageHandler.class);
        StompHeaderAccessor headers = StompHeaderAccessor.create(SUBSCRIBE);
        headers.setSessionId("sess1");
        headers.setSessionAttributes(new ConcurrentHashMap());
        headers.setSubscriptionId("subs1");
        headers.setDestination("/foo");
        Message<?> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();
        messageHandler.handleMessage(message);
        message = channel.messages.get(0);
        headers = StompHeaderAccessor.wrap(message);
        Assert.assertEquals(MESSAGE, headers.getMessageType());
        Assert.assertEquals("/foo", headers.getDestination());
        Assert.assertEquals("bar", new String(((byte[]) (message.getPayload()))));
    }

    @Test
    public void clientOutboundChannelUsedBySimpleBroker() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.SimpleBrokerConfig.class);
        MessageBrokerConfigurationTests.TestChannel outboundChannel = context.getBean("clientOutboundChannel", MessageBrokerConfigurationTests.TestChannel.class);
        SimpleBrokerMessageHandler broker = context.getBean(SimpleBrokerMessageHandler.class);
        StompHeaderAccessor headers = StompHeaderAccessor.create(SUBSCRIBE);
        headers.setSessionId("sess1");
        headers.setSubscriptionId("subs1");
        headers.setDestination("/foo");
        Message<?> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        // subscribe
        broker.handleMessage(createConnectMessage("sess1", new long[]{ 0, 0 }));
        broker.handleMessage(message);
        headers = StompHeaderAccessor.create(SEND);
        headers.setSessionId("sess1");
        headers.setDestination("/foo");
        message = MessageBuilder.createMessage("bar".getBytes(), headers.getMessageHeaders());
        // message
        broker.handleMessage(message);
        message = outboundChannel.messages.get(1);
        headers = StompHeaderAccessor.wrap(message);
        Assert.assertEquals(MESSAGE, headers.getMessageType());
        Assert.assertEquals("/foo", headers.getDestination());
        Assert.assertEquals("bar", new String(((byte[]) (message.getPayload()))));
    }

    @Test
    public void clientOutboundChannelCustomized() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.CustomConfig.class);
        AbstractSubscribableChannel channel = context.getBean("clientOutboundChannel", AbstractSubscribableChannel.class);
        Assert.assertEquals(4, channel.getInterceptors().size());
        ThreadPoolTaskExecutor taskExecutor = context.getBean("clientOutboundChannelExecutor", ThreadPoolTaskExecutor.class);
        Assert.assertEquals(21, taskExecutor.getCorePoolSize());
        Assert.assertEquals(22, taskExecutor.getMaxPoolSize());
        Assert.assertEquals(23, taskExecutor.getKeepAliveSeconds());
        SimpleBrokerMessageHandler broker = context.getBean("simpleBrokerMessageHandler", SimpleBrokerMessageHandler.class);
        Assert.assertTrue(broker.isPreservePublishOrder());
    }

    @Test
    public void brokerChannel() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.SimpleBrokerConfig.class);
        MessageBrokerConfigurationTests.TestChannel channel = context.getBean("brokerChannel", MessageBrokerConfigurationTests.TestChannel.class);
        Set<MessageHandler> handlers = getSubscribers();
        Assert.assertEquals(2, handlers.size());
        Assert.assertTrue(handlers.contains(context.getBean(UserDestinationMessageHandler.class)));
        Assert.assertTrue(handlers.contains(context.getBean(SimpleBrokerMessageHandler.class)));
        Assert.assertNull(getExecutor());
    }

    @Test
    public void brokerChannelWithBrokerRelay() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.BrokerRelayConfig.class);
        MessageBrokerConfigurationTests.TestChannel channel = context.getBean("brokerChannel", MessageBrokerConfigurationTests.TestChannel.class);
        Set<MessageHandler> handlers = getSubscribers();
        Assert.assertEquals(2, handlers.size());
        Assert.assertTrue(handlers.contains(context.getBean(UserDestinationMessageHandler.class)));
        Assert.assertTrue(handlers.contains(context.getBean(StompBrokerRelayMessageHandler.class)));
    }

    @Test
    public void brokerChannelUsedByAnnotatedMethod() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.SimpleBrokerConfig.class);
        MessageBrokerConfigurationTests.TestChannel channel = context.getBean("brokerChannel", MessageBrokerConfigurationTests.TestChannel.class);
        SimpAnnotationMethodMessageHandler messageHandler = context.getBean(SimpAnnotationMethodMessageHandler.class);
        StompHeaderAccessor headers = StompHeaderAccessor.create(SEND);
        headers.setSessionId("sess1");
        headers.setSessionAttributes(new ConcurrentHashMap());
        headers.setDestination("/foo");
        Message<?> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        messageHandler.handleMessage(message);
        message = channel.messages.get(0);
        headers = StompHeaderAccessor.wrap(message);
        Assert.assertEquals(MESSAGE, headers.getMessageType());
        Assert.assertEquals("/bar", headers.getDestination());
        Assert.assertEquals("bar", new String(((byte[]) (message.getPayload()))));
    }

    @Test
    public void brokerChannelCustomized() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.CustomConfig.class);
        AbstractSubscribableChannel channel = context.getBean("brokerChannel", AbstractSubscribableChannel.class);
        Assert.assertEquals(4, channel.getInterceptors().size());
        ThreadPoolTaskExecutor taskExecutor = context.getBean("brokerChannelExecutor", ThreadPoolTaskExecutor.class);
        Assert.assertEquals(31, taskExecutor.getCorePoolSize());
        Assert.assertEquals(32, taskExecutor.getMaxPoolSize());
        Assert.assertEquals(33, taskExecutor.getKeepAliveSeconds());
    }

    @Test
    public void configureMessageConvertersDefault() {
        AbstractMessageBrokerConfiguration config = new MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig();
        CompositeMessageConverter compositeConverter = config.brokerMessageConverter();
        List<MessageConverter> converters = compositeConverter.getConverters();
        Assert.assertThat(converters.size(), Matchers.is(3));
        Assert.assertThat(converters.get(0), Matchers.instanceOf(StringMessageConverter.class));
        Assert.assertThat(converters.get(1), Matchers.instanceOf(ByteArrayMessageConverter.class));
        Assert.assertThat(converters.get(2), Matchers.instanceOf(MappingJackson2MessageConverter.class));
        ContentTypeResolver resolver = getContentTypeResolver();
        Assert.assertEquals(APPLICATION_JSON, getDefaultMimeType());
    }

    @Test
    public void threadPoolSizeDefault() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.DefaultConfig.class);
        String name = "clientInboundChannelExecutor";
        ThreadPoolTaskExecutor executor = context.getBean(name, ThreadPoolTaskExecutor.class);
        Assert.assertEquals(((Runtime.getRuntime().availableProcessors()) * 2), executor.getCorePoolSize());
        // No way to verify queue capacity
        name = "clientOutboundChannelExecutor";
        executor = context.getBean(name, ThreadPoolTaskExecutor.class);
        Assert.assertEquals(((Runtime.getRuntime().availableProcessors()) * 2), executor.getCorePoolSize());
        name = "brokerChannelExecutor";
        executor = context.getBean(name, ThreadPoolTaskExecutor.class);
        Assert.assertEquals(0, executor.getCorePoolSize());
        Assert.assertEquals(1, executor.getMaxPoolSize());
    }

    @Test
    public void configureMessageConvertersCustom() {
        final MessageConverter testConverter = Mockito.mock(MessageConverter.class);
        AbstractMessageBrokerConfiguration config = new MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig() {
            @Override
            protected boolean configureMessageConverters(List<MessageConverter> messageConverters) {
                messageConverters.add(testConverter);
                return false;
            }
        };
        CompositeMessageConverter compositeConverter = config.brokerMessageConverter();
        Assert.assertThat(compositeConverter.getConverters().size(), Matchers.is(1));
        Iterator<MessageConverter> iterator = compositeConverter.getConverters().iterator();
        Assert.assertThat(iterator.next(), Matchers.is(testConverter));
    }

    @Test
    public void configureMessageConvertersCustomAndDefault() {
        final MessageConverter testConverter = Mockito.mock(MessageConverter.class);
        AbstractMessageBrokerConfiguration config = new MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig() {
            @Override
            protected boolean configureMessageConverters(List<MessageConverter> messageConverters) {
                messageConverters.add(testConverter);
                return true;
            }
        };
        CompositeMessageConverter compositeConverter = config.brokerMessageConverter();
        Assert.assertThat(compositeConverter.getConverters().size(), Matchers.is(4));
        Iterator<MessageConverter> iterator = compositeConverter.getConverters().iterator();
        Assert.assertThat(iterator.next(), Matchers.is(testConverter));
        Assert.assertThat(iterator.next(), Matchers.instanceOf(StringMessageConverter.class));
        Assert.assertThat(iterator.next(), Matchers.instanceOf(ByteArrayMessageConverter.class));
        Assert.assertThat(iterator.next(), Matchers.instanceOf(MappingJackson2MessageConverter.class));
    }

    @Test
    public void customArgumentAndReturnValueTypes() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.CustomConfig.class);
        SimpAnnotationMethodMessageHandler handler = context.getBean(SimpAnnotationMethodMessageHandler.class);
        List<HandlerMethodArgumentResolver> customResolvers = handler.getCustomArgumentResolvers();
        Assert.assertEquals(1, customResolvers.size());
        Assert.assertTrue(handler.getArgumentResolvers().contains(customResolvers.get(0)));
        List<HandlerMethodReturnValueHandler> customHandlers = handler.getCustomReturnValueHandlers();
        Assert.assertEquals(1, customHandlers.size());
        Assert.assertTrue(handler.getReturnValueHandlers().contains(customHandlers.get(0)));
    }

    @Test
    public void simpValidatorDefault() {
        AbstractMessageBrokerConfiguration config = new MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig() {};
        config.setApplicationContext(new StaticApplicationContext());
        Assert.assertThat(config.simpValidator(), Matchers.notNullValue());
        Assert.assertThat(config.simpValidator(), Matchers.instanceOf(OptionalValidatorFactoryBean.class));
    }

    @Test
    public void simpValidatorCustom() {
        final Validator validator = Mockito.mock(Validator.class);
        AbstractMessageBrokerConfiguration config = new MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig() {
            @Override
            public Validator getValidator() {
                return validator;
            }
        };
        Assert.assertSame(validator, config.simpValidator());
    }

    @Test
    public void simpValidatorMvc() {
        StaticApplicationContext appCxt = new StaticApplicationContext();
        appCxt.registerSingleton("mvcValidator", MessageBrokerConfigurationTests.TestValidator.class);
        AbstractMessageBrokerConfiguration config = new MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig() {};
        config.setApplicationContext(appCxt);
        Assert.assertThat(config.simpValidator(), Matchers.notNullValue());
        Assert.assertThat(config.simpValidator(), Matchers.instanceOf(MessageBrokerConfigurationTests.TestValidator.class));
    }

    @Test
    public void simpValidatorInjected() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.SimpleBrokerConfig.class);
        SimpAnnotationMethodMessageHandler messageHandler = context.getBean(SimpAnnotationMethodMessageHandler.class);
        Assert.assertThat(messageHandler.getValidator(), Matchers.notNullValue(Validator.class));
    }

    @Test
    public void customPathMatcher() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.CustomConfig.class);
        SimpleBrokerMessageHandler broker = context.getBean(SimpleBrokerMessageHandler.class);
        DefaultSubscriptionRegistry registry = ((DefaultSubscriptionRegistry) (broker.getSubscriptionRegistry()));
        Assert.assertEquals("a.a", registry.getPathMatcher().combine("a", "a"));
        PathMatcher pathMatcher = context.getBean(SimpAnnotationMethodMessageHandler.class).getPathMatcher();
        Assert.assertEquals("a.a", pathMatcher.combine("a", "a"));
        DefaultUserDestinationResolver resolver = context.getBean(DefaultUserDestinationResolver.class);
        Assert.assertNotNull(resolver);
        Assert.assertEquals(false, resolver.isRemoveLeadingSlash());
    }

    @Test
    public void customCacheLimit() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.CustomConfig.class);
        SimpleBrokerMessageHandler broker = context.getBean(SimpleBrokerMessageHandler.class);
        DefaultSubscriptionRegistry registry = ((DefaultSubscriptionRegistry) (broker.getSubscriptionRegistry()));
        Assert.assertEquals(8192, registry.getCacheLimit());
    }

    @Test
    public void customUserRegistryOrder() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.CustomConfig.class);
        SimpUserRegistry registry = context.getBean(SimpUserRegistry.class);
        Assert.assertTrue((registry instanceof MessageBrokerConfigurationTests.TestUserRegistry));
        Assert.assertEquals(99, ((MessageBrokerConfigurationTests.TestUserRegistry) (registry)).getOrder());
    }

    @Test
    public void userBroadcasts() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.BrokerRelayConfig.class);
        SimpUserRegistry userRegistry = context.getBean(SimpUserRegistry.class);
        Assert.assertEquals(MultiServerUserRegistry.class, userRegistry.getClass());
        UserDestinationMessageHandler handler1 = context.getBean(UserDestinationMessageHandler.class);
        Assert.assertEquals("/topic/unresolved-user-destination", handler1.getBroadcastDestination());
        UserRegistryMessageHandler handler2 = context.getBean(UserRegistryMessageHandler.class);
        Assert.assertEquals("/topic/simp-user-registry", handler2.getBroadcastDestination());
        StompBrokerRelayMessageHandler relay = context.getBean(StompBrokerRelayMessageHandler.class);
        Assert.assertNotNull(relay.getSystemSubscriptions());
        Assert.assertEquals(2, relay.getSystemSubscriptions().size());
        Assert.assertSame(handler1, relay.getSystemSubscriptions().get("/topic/unresolved-user-destination"));
        Assert.assertSame(handler2, relay.getSystemSubscriptions().get("/topic/simp-user-registry"));
    }

    @Test
    public void userBroadcastsDisabledWithSimpleBroker() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.SimpleBrokerConfig.class);
        SimpUserRegistry registry = context.getBean(SimpUserRegistry.class);
        Assert.assertNotNull(registry);
        Assert.assertNotEquals(MultiServerUserRegistry.class, registry.getClass());
        UserDestinationMessageHandler handler = context.getBean(UserDestinationMessageHandler.class);
        Assert.assertNull(handler.getBroadcastDestination());
        Object nullBean = context.getBean("userRegistryMessageHandler");
        Assert.assertTrue(nullBean.equals(null));
    }

    // SPR-16275
    @Test
    public void dotSeparatorWithBrokerSlashConvention() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.DotSeparatorWithSlashBrokerConventionConfig.class);
        testDotSeparator(context, true);
    }

    // SPR-16275
    @Test
    public void dotSeparatorWithBrokerDotConvention() {
        ApplicationContext context = loadConfig(MessageBrokerConfigurationTests.DotSeparatorWithDotBrokerConventionConfig.class);
        testDotSeparator(context, false);
    }

    @SuppressWarnings("unused")
    @Controller
    static class TestController {
        @SubscribeMapping("/foo")
        public String handleSubscribe() {
            return "bar";
        }

        @MessageMapping("/foo")
        @SendTo("/bar")
        public String handleMessage() {
            return "bar";
        }
    }

    static class BaseTestMessageBrokerConfig extends AbstractMessageBrokerConfiguration {
        @Override
        protected SimpUserRegistry createLocalUserRegistry(@Nullable
        Integer order) {
            MessageBrokerConfigurationTests.TestUserRegistry registry = new MessageBrokerConfigurationTests.TestUserRegistry();
            if (order != null) {
                registry.setOrder(order);
            }
            return registry;
        }
    }

    @SuppressWarnings("unused")
    @Configuration
    static class SimpleBrokerConfig extends MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig {
        @Bean
        public MessageBrokerConfigurationTests.TestController subscriptionController() {
            return new MessageBrokerConfigurationTests.TestController();
        }

        @Override
        @Bean
        public AbstractSubscribableChannel clientInboundChannel() {
            return new MessageBrokerConfigurationTests.TestChannel();
        }

        @Override
        @Bean
        public AbstractSubscribableChannel clientOutboundChannel() {
            return new MessageBrokerConfigurationTests.TestChannel();
        }

        @Override
        @Bean
        public AbstractSubscribableChannel brokerChannel() {
            return new MessageBrokerConfigurationTests.TestChannel();
        }
    }

    @Configuration
    static class BrokerRelayConfig extends MessageBrokerConfigurationTests.SimpleBrokerConfig {
        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableStompBrokerRelay("/topic", "/queue").setAutoStartup(true).setUserDestinationBroadcast("/topic/unresolved-user-destination").setUserRegistryBroadcast("/topic/simp-user-registry");
        }
    }

    @Configuration
    static class DefaultConfig extends MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig {}

    @Configuration
    static class CustomConfig extends MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig {
        private ChannelInterceptor interceptor = new ChannelInterceptor() {};

        @Override
        protected void configureClientInboundChannel(ChannelRegistration registration) {
            registration.interceptors(this.interceptor);
            registration.taskExecutor(new MessageBrokerConfigurationTests.CustomThreadPoolTaskExecutor()).corePoolSize(11).maxPoolSize(12).keepAliveSeconds(13).queueCapacity(14);
        }

        @Override
        protected void configureClientOutboundChannel(ChannelRegistration registration) {
            registration.interceptors(this.interceptor, this.interceptor);
            registration.taskExecutor().corePoolSize(21).maxPoolSize(22).keepAliveSeconds(23).queueCapacity(24);
        }

        @Override
        protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(Mockito.mock(HandlerMethodArgumentResolver.class));
        }

        @Override
        protected void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
            returnValueHandlers.add(Mockito.mock(HandlerMethodReturnValueHandler.class));
        }

        @Override
        protected void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.configureBrokerChannel().interceptors(this.interceptor, this.interceptor, this.interceptor);
            registry.configureBrokerChannel().taskExecutor().corePoolSize(31).maxPoolSize(32).keepAliveSeconds(33).queueCapacity(34);
            registry.setPathMatcher(new AntPathMatcher(".")).enableSimpleBroker("/topic", "/queue");
            registry.setCacheLimit(8192);
            registry.setPreservePublishOrder(true);
            registry.setUserRegistryOrder(99);
        }
    }

    @Configuration
    abstract static class BaseDotSeparatorConfig extends MessageBrokerConfigurationTests.BaseTestMessageBrokerConfig {
        @Override
        protected void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.setPathMatcher(new AntPathMatcher("."));
        }

        @Override
        @Bean
        public AbstractSubscribableChannel clientInboundChannel() {
            // synchronous
            return new ExecutorSubscribableChannel(null);
        }

        @Override
        @Bean
        public AbstractSubscribableChannel clientOutboundChannel() {
            return new MessageBrokerConfigurationTests.TestChannel();
        }

        @Override
        @Bean
        public AbstractSubscribableChannel brokerChannel() {
            // synchronous
            return new ExecutorSubscribableChannel(null);
        }
    }

    @Configuration
    static class DotSeparatorWithSlashBrokerConventionConfig extends MessageBrokerConfigurationTests.BaseDotSeparatorConfig {
        // RabbitMQ-style broker convention for STOMP destinations
        @Override
        protected void configureMessageBroker(MessageBrokerRegistry registry) {
            super.configureMessageBroker(registry);
            registry.enableSimpleBroker("/topic", "/queue");
        }
    }

    @Configuration
    static class DotSeparatorWithDotBrokerConventionConfig extends MessageBrokerConfigurationTests.BaseDotSeparatorConfig {
        // Artemis-style broker convention for STOMP destinations
        @Override
        protected void configureMessageBroker(MessageBrokerRegistry registry) {
            super.configureMessageBroker(registry);
            registry.enableSimpleBroker("topic.", "queue.");
        }
    }

    private static class TestChannel extends ExecutorSubscribableChannel {
        private final List<Message<?>> messages = new ArrayList<>();

        @Override
        public boolean sendInternal(Message<?> message, long timeout) {
            this.messages.add(message);
            return true;
        }
    }

    private static class TestUserRegistry implements Ordered , SimpUserRegistry {
        private Integer order;

        public void setOrder(int order) {
            this.order = order;
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        @Override
        public SimpUser getUser(String userName) {
            return null;
        }

        @Override
        public Set<SimpUser> getUsers() {
            return null;
        }

        @Override
        public int getUserCount() {
            return 0;
        }

        @Override
        public Set<SimpSubscription> findSubscriptions(SimpSubscriptionMatcher matcher) {
            return null;
        }
    }

    private static class TestValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return false;
        }

        @Override
        public void validate(@Nullable
        Object target, Errors errors) {
        }
    }

    @SuppressWarnings("serial")
    private static class CustomThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {}
}

