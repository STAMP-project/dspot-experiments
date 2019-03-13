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
package org.springframework.context.event;


import AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;
import RootBeanDefinition.SCOPE_PROTOTYPE;
import TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.BeanThatBroadcasts;
import org.springframework.context.BeanThatListens;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Unit and integration tests for the ApplicationContext event support.
 *
 * @author Alef Arendsen
 * @author Rick Evans
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 */
public class ApplicationContextEventTests extends AbstractApplicationEventListenerTests {
    @Test
    public void multicastSimpleEvent() {
        multicastEvent(true, ApplicationListener.class, new ContextRefreshedEvent(new StaticApplicationContext()), null);
        multicastEvent(true, ApplicationListener.class, new ContextClosedEvent(new StaticApplicationContext()), null);
    }

    @Test
    public void multicastGenericEvent() {
        multicastEvent(true, AbstractApplicationEventListenerTests.StringEventListener.class, createGenericTestEvent("test"), getGenericApplicationEventType("stringEvent"));
    }

    @Test
    public void multicastGenericEventWrongType() {
        multicastEvent(false, AbstractApplicationEventListenerTests.StringEventListener.class, createGenericTestEvent(123L), getGenericApplicationEventType("longEvent"));
    }

    @Test
    public void multicastGenericEventWildcardSubType() {
        multicastEvent(false, AbstractApplicationEventListenerTests.StringEventListener.class, createGenericTestEvent("test"), getGenericApplicationEventType("wildcardEvent"));
    }

    @Test
    public void multicastConcreteTypeGenericListener() {
        multicastEvent(true, AbstractApplicationEventListenerTests.StringEventListener.class, new AbstractApplicationEventListenerTests.StringEvent(this, "test"), null);
    }

    @Test
    public void multicastConcreteWrongTypeGenericListener() {
        multicastEvent(false, AbstractApplicationEventListenerTests.StringEventListener.class, new AbstractApplicationEventListenerTests.LongEvent(this, 123L), null);
    }

    @Test
    public void multicastSmartGenericTypeGenericListener() {
        multicastEvent(true, AbstractApplicationEventListenerTests.StringEventListener.class, new AbstractApplicationEventListenerTests.SmartGenericTestEvent(this, "test"), null);
    }

    @Test
    public void multicastSmartGenericWrongTypeGenericListener() {
        multicastEvent(false, AbstractApplicationEventListenerTests.StringEventListener.class, new AbstractApplicationEventListenerTests.SmartGenericTestEvent(this, 123L), null);
    }

    @Test
    public void simpleApplicationEventMulticasterWithTaskExecutor() {
        @SuppressWarnings("unchecked")
        ApplicationListener<ApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        ApplicationEvent evt = new ContextClosedEvent(new StaticApplicationContext());
        SimpleApplicationEventMulticaster smc = new SimpleApplicationEventMulticaster();
        smc.setTaskExecutor(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
                command.run();
            }
        });
        smc.addApplicationListener(listener);
        smc.multicastEvent(evt);
        Mockito.verify(listener, Mockito.times(2)).onApplicationEvent(evt);
    }

    @Test
    public void simpleApplicationEventMulticasterWithException() {
        @SuppressWarnings("unchecked")
        ApplicationListener<ApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        ApplicationEvent evt = new ContextClosedEvent(new StaticApplicationContext());
        SimpleApplicationEventMulticaster smc = new SimpleApplicationEventMulticaster();
        smc.addApplicationListener(listener);
        RuntimeException thrown = new RuntimeException();
        BDDMockito.willThrow(thrown).given(listener).onApplicationEvent(evt);
        try {
            smc.multicastEvent(evt);
            Assert.fail("Should have thrown RuntimeException");
        } catch (RuntimeException ex) {
            Assert.assertSame(thrown, ex);
        }
    }

    @Test
    public void simpleApplicationEventMulticasterWithErrorHandler() {
        @SuppressWarnings("unchecked")
        ApplicationListener<ApplicationEvent> listener = Mockito.mock(ApplicationListener.class);
        ApplicationEvent evt = new ContextClosedEvent(new StaticApplicationContext());
        SimpleApplicationEventMulticaster smc = new SimpleApplicationEventMulticaster();
        smc.setErrorHandler(LOG_AND_SUPPRESS_ERROR_HANDLER);
        smc.addApplicationListener(listener);
        BDDMockito.willThrow(new RuntimeException()).given(listener).onApplicationEvent(evt);
        smc.multicastEvent(evt);
    }

    @Test
    public void orderedListeners() {
        ApplicationContextEventTests.MyOrderedListener1 listener1 = new ApplicationContextEventTests.MyOrderedListener1();
        ApplicationContextEventTests.MyOrderedListener2 listener2 = new ApplicationContextEventTests.MyOrderedListener2(listener1);
        SimpleApplicationEventMulticaster smc = new SimpleApplicationEventMulticaster();
        smc.addApplicationListener(listener2);
        smc.addApplicationListener(listener1);
        smc.multicastEvent(new ApplicationContextEventTests.MyEvent(this));
        smc.multicastEvent(new ApplicationContextEventTests.MyOtherEvent(this));
        Assert.assertEquals(2, listener1.seenEvents.size());
    }

    @Test
    public void orderedListenersWithAnnotation() {
        ApplicationContextEventTests.MyOrderedListener3 listener1 = new ApplicationContextEventTests.MyOrderedListener3();
        ApplicationContextEventTests.MyOrderedListener4 listener2 = new ApplicationContextEventTests.MyOrderedListener4(listener1);
        SimpleApplicationEventMulticaster smc = new SimpleApplicationEventMulticaster();
        smc.addApplicationListener(listener2);
        smc.addApplicationListener(listener1);
        smc.multicastEvent(new ApplicationContextEventTests.MyEvent(this));
        smc.multicastEvent(new ApplicationContextEventTests.MyOtherEvent(this));
        Assert.assertEquals(2, listener1.seenEvents.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void proxiedListeners() {
        ApplicationContextEventTests.MyOrderedListener1 listener1 = new ApplicationContextEventTests.MyOrderedListener1();
        ApplicationContextEventTests.MyOrderedListener2 listener2 = new ApplicationContextEventTests.MyOrderedListener2(listener1);
        ApplicationListener<ApplicationEvent> proxy1 = ((ApplicationListener<ApplicationEvent>) (new ProxyFactory(listener1).getProxy()));
        ApplicationListener<ApplicationEvent> proxy2 = ((ApplicationListener<ApplicationEvent>) (new ProxyFactory(listener2).getProxy()));
        SimpleApplicationEventMulticaster smc = new SimpleApplicationEventMulticaster();
        smc.addApplicationListener(proxy1);
        smc.addApplicationListener(proxy2);
        smc.multicastEvent(new ApplicationContextEventTests.MyEvent(this));
        smc.multicastEvent(new ApplicationContextEventTests.MyOtherEvent(this));
        Assert.assertEquals(2, listener1.seenEvents.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void proxiedListenersMixedWithTargetListeners() {
        ApplicationContextEventTests.MyOrderedListener1 listener1 = new ApplicationContextEventTests.MyOrderedListener1();
        ApplicationContextEventTests.MyOrderedListener2 listener2 = new ApplicationContextEventTests.MyOrderedListener2(listener1);
        ApplicationListener<ApplicationEvent> proxy1 = ((ApplicationListener<ApplicationEvent>) (new ProxyFactory(listener1).getProxy()));
        ApplicationListener<ApplicationEvent> proxy2 = ((ApplicationListener<ApplicationEvent>) (new ProxyFactory(listener2).getProxy()));
        SimpleApplicationEventMulticaster smc = new SimpleApplicationEventMulticaster();
        smc.addApplicationListener(listener1);
        smc.addApplicationListener(listener2);
        smc.addApplicationListener(proxy1);
        smc.addApplicationListener(proxy2);
        smc.multicastEvent(new ApplicationContextEventTests.MyEvent(this));
        smc.multicastEvent(new ApplicationContextEventTests.MyOtherEvent(this));
        Assert.assertEquals(2, listener1.seenEvents.size());
    }

    @Test
    public void testEventPublicationInterceptor() throws Throwable {
        MethodInvocation invocation = Mockito.mock(MethodInvocation.class);
        ApplicationContext ctx = Mockito.mock(ApplicationContext.class);
        EventPublicationInterceptor interceptor = new EventPublicationInterceptor();
        interceptor.setApplicationEventClass(ApplicationContextEventTests.MyEvent.class);
        interceptor.setApplicationEventPublisher(ctx);
        interceptor.afterPropertiesSet();
        BDDMockito.given(invocation.proceed()).willReturn(new Object());
        BDDMockito.given(invocation.getThis()).willReturn(new Object());
        interceptor.invoke(invocation);
        Mockito.verify(ctx).publishEvent(ArgumentMatchers.isA(ApplicationContextEventTests.MyEvent.class));
    }

    @Test
    public void listenersInApplicationContext() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerBeanDefinition("listener1", new RootBeanDefinition(ApplicationContextEventTests.MyOrderedListener1.class));
        RootBeanDefinition listener2 = new RootBeanDefinition(ApplicationContextEventTests.MyOrderedListener2.class);
        listener2.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference("listener1"));
        listener2.setLazyInit(true);
        context.registerBeanDefinition("listener2", listener2);
        context.refresh();
        Assert.assertFalse(context.getDefaultListableBeanFactory().containsSingleton("listener2"));
        ApplicationContextEventTests.MyOrderedListener1 listener1 = context.getBean("listener1", ApplicationContextEventTests.MyOrderedListener1.class);
        ApplicationContextEventTests.MyOtherEvent event1 = new ApplicationContextEventTests.MyOtherEvent(context);
        context.publishEvent(event1);
        Assert.assertFalse(context.getDefaultListableBeanFactory().containsSingleton("listener2"));
        ApplicationContextEventTests.MyEvent event2 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event2);
        Assert.assertTrue(context.getDefaultListableBeanFactory().containsSingleton("listener2"));
        ApplicationContextEventTests.MyEvent event3 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event3);
        ApplicationContextEventTests.MyOtherEvent event4 = new ApplicationContextEventTests.MyOtherEvent(context);
        context.publishEvent(event4);
        Assert.assertTrue(listener1.seenEvents.contains(event1));
        Assert.assertTrue(listener1.seenEvents.contains(event2));
        Assert.assertTrue(listener1.seenEvents.contains(event3));
        Assert.assertTrue(listener1.seenEvents.contains(event4));
        listener1.seenEvents.clear();
        context.publishEvent(event1);
        context.publishEvent(event2);
        context.publishEvent(event3);
        context.publishEvent(event4);
        Assert.assertTrue(listener1.seenEvents.contains(event1));
        Assert.assertTrue(listener1.seenEvents.contains(event2));
        Assert.assertTrue(listener1.seenEvents.contains(event3));
        Assert.assertTrue(listener1.seenEvents.contains(event4));
        AbstractApplicationEventMulticaster multicaster = context.getBean(AbstractApplicationEventMulticaster.class);
        Assert.assertEquals(2, multicaster.retrieverCache.size());
        context.close();
    }

    @Test
    public void listenersInApplicationContextWithPayloadEvents() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerBeanDefinition("listener", new RootBeanDefinition(ApplicationContextEventTests.MyPayloadListener.class));
        context.refresh();
        ApplicationContextEventTests.MyPayloadListener listener = context.getBean("listener", ApplicationContextEventTests.MyPayloadListener.class);
        context.publishEvent("event1");
        context.publishEvent("event2");
        context.publishEvent("event3");
        context.publishEvent("event4");
        Assert.assertTrue(listener.seenPayloads.contains("event1"));
        Assert.assertTrue(listener.seenPayloads.contains("event2"));
        Assert.assertTrue(listener.seenPayloads.contains("event3"));
        Assert.assertTrue(listener.seenPayloads.contains("event4"));
        AbstractApplicationEventMulticaster multicaster = context.getBean(AbstractApplicationEventMulticaster.class);
        Assert.assertEquals(2, multicaster.retrieverCache.size());
        context.close();
    }

    @Test
    public void listenersInApplicationContextWithNestedChild() {
        StaticApplicationContext context = new StaticApplicationContext();
        RootBeanDefinition nestedChild = new RootBeanDefinition(StaticApplicationContext.class);
        nestedChild.getPropertyValues().add("parent", context);
        nestedChild.setInitMethodName("refresh");
        context.registerBeanDefinition("nestedChild", nestedChild);
        RootBeanDefinition listener1Def = new RootBeanDefinition(ApplicationContextEventTests.MyOrderedListener1.class);
        listener1Def.setDependsOn("nestedChild");
        context.registerBeanDefinition("listener1", listener1Def);
        context.refresh();
        ApplicationContextEventTests.MyOrderedListener1 listener1 = context.getBean("listener1", ApplicationContextEventTests.MyOrderedListener1.class);
        ApplicationContextEventTests.MyEvent event1 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event1);
        Assert.assertTrue(listener1.seenEvents.contains(event1));
        SimpleApplicationEventMulticaster multicaster = context.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, SimpleApplicationEventMulticaster.class);
        Assert.assertFalse(multicaster.getApplicationListeners().isEmpty());
        context.close();
        Assert.assertTrue(multicaster.getApplicationListeners().isEmpty());
    }

    @Test
    public void nonSingletonListenerInApplicationContext() {
        StaticApplicationContext context = new StaticApplicationContext();
        RootBeanDefinition listener = new RootBeanDefinition(ApplicationContextEventTests.MyNonSingletonListener.class);
        listener.setScope(SCOPE_PROTOTYPE);
        context.registerBeanDefinition("listener", listener);
        context.refresh();
        ApplicationContextEventTests.MyEvent event1 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event1);
        ApplicationContextEventTests.MyOtherEvent event2 = new ApplicationContextEventTests.MyOtherEvent(context);
        context.publishEvent(event2);
        ApplicationContextEventTests.MyEvent event3 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event3);
        ApplicationContextEventTests.MyOtherEvent event4 = new ApplicationContextEventTests.MyOtherEvent(context);
        context.publishEvent(event4);
        Assert.assertTrue(ApplicationContextEventTests.MyNonSingletonListener.seenEvents.contains(event1));
        Assert.assertTrue(ApplicationContextEventTests.MyNonSingletonListener.seenEvents.contains(event2));
        Assert.assertTrue(ApplicationContextEventTests.MyNonSingletonListener.seenEvents.contains(event3));
        Assert.assertTrue(ApplicationContextEventTests.MyNonSingletonListener.seenEvents.contains(event4));
        ApplicationContextEventTests.MyNonSingletonListener.seenEvents.clear();
        context.close();
    }

    @Test
    public void listenerAndBroadcasterWithCircularReference() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerBeanDefinition("broadcaster", new RootBeanDefinition(BeanThatBroadcasts.class));
        RootBeanDefinition listenerDef = new RootBeanDefinition(BeanThatListens.class);
        listenerDef.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference("broadcaster"));
        context.registerBeanDefinition("listener", listenerDef);
        context.refresh();
        BeanThatBroadcasts broadcaster = context.getBean("broadcaster", BeanThatBroadcasts.class);
        context.publishEvent(new ApplicationContextEventTests.MyEvent(context));
        Assert.assertEquals("The event was not received by the listener", 2, broadcaster.receivedCount);
        context.close();
    }

    @Test
    public void innerBeanAsListener() {
        StaticApplicationContext context = new StaticApplicationContext();
        RootBeanDefinition listenerDef = new RootBeanDefinition(TestBean.class);
        listenerDef.getPropertyValues().add("friends", new RootBeanDefinition(BeanThatListens.class));
        context.registerBeanDefinition("listener", listenerDef);
        context.refresh();
        context.publishEvent(new ApplicationContextEventTests.MyEvent(this));
        context.publishEvent(new ApplicationContextEventTests.MyEvent(this));
        TestBean listener = context.getBean(TestBean.class);
        Assert.assertEquals(3, ((BeanThatListens) (listener.getFriends().iterator().next())).getEventCount());
        context.close();
    }

    @Test
    public void anonymousClassAsListener() {
        final Set<ApplicationContextEventTests.MyEvent> seenEvents = new HashSet<>();
        StaticApplicationContext context = new StaticApplicationContext();
        context.addApplicationListener(new ApplicationListener<ApplicationContextEventTests.MyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationContextEventTests.MyEvent event) {
                seenEvents.add(event);
            }
        });
        context.refresh();
        ApplicationContextEventTests.MyEvent event1 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event1);
        context.publishEvent(new ApplicationContextEventTests.MyOtherEvent(context));
        ApplicationContextEventTests.MyEvent event2 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event2);
        Assert.assertSame(2, seenEvents.size());
        Assert.assertTrue(seenEvents.contains(event1));
        Assert.assertTrue(seenEvents.contains(event2));
        context.close();
    }

    @Test
    public void lambdaAsListener() {
        final Set<ApplicationContextEventTests.MyEvent> seenEvents = new HashSet<>();
        StaticApplicationContext context = new StaticApplicationContext();
        ApplicationListener<ApplicationContextEventTests.MyEvent> listener = seenEvents::add;
        context.addApplicationListener(listener);
        context.refresh();
        ApplicationContextEventTests.MyEvent event1 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event1);
        context.publishEvent(new ApplicationContextEventTests.MyOtherEvent(context));
        ApplicationContextEventTests.MyEvent event2 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event2);
        Assert.assertSame(2, seenEvents.size());
        Assert.assertTrue(seenEvents.contains(event1));
        Assert.assertTrue(seenEvents.contains(event2));
        context.close();
    }

    @Test
    public void lambdaAsListenerWithErrorHandler() {
        final Set<ApplicationContextEventTests.MyEvent> seenEvents = new HashSet<>();
        StaticApplicationContext context = new StaticApplicationContext();
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setErrorHandler(ReflectionUtils::rethrowRuntimeException);
        context.getBeanFactory().registerSingleton(StaticApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME, multicaster);
        ApplicationListener<ApplicationContextEventTests.MyEvent> listener = seenEvents::add;
        context.addApplicationListener(listener);
        context.refresh();
        ApplicationContextEventTests.MyEvent event1 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event1);
        context.publishEvent(new ApplicationContextEventTests.MyOtherEvent(context));
        ApplicationContextEventTests.MyEvent event2 = new ApplicationContextEventTests.MyEvent(context);
        context.publishEvent(event2);
        Assert.assertSame(2, seenEvents.size());
        Assert.assertTrue(seenEvents.contains(event1));
        Assert.assertTrue(seenEvents.contains(event2));
        context.close();
    }

    @Test
    public void lambdaAsListenerWithJava8StyleClassCastMessage() {
        StaticApplicationContext context = new StaticApplicationContext();
        ApplicationListener<ApplicationEvent> listener = ( event) -> {
            throw new ClassCastException(event.getClass().getName());
        };
        context.addApplicationListener(listener);
        context.refresh();
        context.publishEvent(new ApplicationContextEventTests.MyEvent(context));
        context.close();
    }

    @Test
    public void lambdaAsListenerWithJava9StyleClassCastMessage() {
        StaticApplicationContext context = new StaticApplicationContext();
        ApplicationListener<ApplicationEvent> listener = ( event) -> {
            throw new ClassCastException(("spring.context/" + (event.getClass().getName())));
        };
        context.addApplicationListener(listener);
        context.refresh();
        context.publishEvent(new ApplicationContextEventTests.MyEvent(context));
        context.close();
    }

    @Test
    public void beanPostProcessorPublishesEvents() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("listener", new RootBeanDefinition(BeanThatListens.class));
        context.registerBeanDefinition("messageSource", new RootBeanDefinition(StaticMessageSource.class));
        context.registerBeanDefinition("postProcessor", new RootBeanDefinition(ApplicationContextEventTests.EventPublishingBeanPostProcessor.class));
        context.refresh();
        context.publishEvent(new ApplicationContextEventTests.MyEvent(this));
        BeanThatListens listener = context.getBean(BeanThatListens.class);
        Assert.assertEquals(4, listener.getEventCount());
        context.close();
    }

    @SuppressWarnings("serial")
    public static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }

    @SuppressWarnings("serial")
    public static class MyOtherEvent extends ApplicationEvent {
        public MyOtherEvent(Object source) {
            super(source);
        }
    }

    public static class MyOrderedListener1 implements ApplicationListener<ApplicationEvent> , Ordered {
        public final List<ApplicationEvent> seenEvents = new LinkedList<>();

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            this.seenEvents.add(event);
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }

    public interface MyOrderedListenerIfc<E extends ApplicationEvent> extends ApplicationListener<E> , Ordered {}

    public abstract static class MyOrderedListenerBase implements ApplicationContextEventTests.MyOrderedListenerIfc<ApplicationContextEventTests.MyEvent> {
        @Override
        public int getOrder() {
            return 1;
        }
    }

    public static class MyOrderedListener2 extends ApplicationContextEventTests.MyOrderedListenerBase {
        private final ApplicationContextEventTests.MyOrderedListener1 otherListener;

        public MyOrderedListener2(ApplicationContextEventTests.MyOrderedListener1 otherListener) {
            this.otherListener = otherListener;
        }

        @Override
        public void onApplicationEvent(ApplicationContextEventTests.MyEvent event) {
            Assert.assertTrue(this.otherListener.seenEvents.contains(event));
        }
    }

    public static class MyPayloadListener implements ApplicationListener<PayloadApplicationEvent> {
        public final Set<Object> seenPayloads = new HashSet<>();

        @Override
        public void onApplicationEvent(PayloadApplicationEvent event) {
            this.seenPayloads.add(event.getPayload());
        }
    }

    public static class MyNonSingletonListener implements ApplicationListener<ApplicationEvent> {
        public static final Set<ApplicationEvent> seenEvents = new HashSet<>();

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            ApplicationContextEventTests.MyNonSingletonListener.seenEvents.add(event);
        }
    }

    @Order(5)
    public static class MyOrderedListener3 implements ApplicationListener<ApplicationEvent> {
        public final Set<ApplicationEvent> seenEvents = new HashSet<>();

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            this.seenEvents.add(event);
        }
    }

    @Order(50)
    public static class MyOrderedListener4 implements ApplicationListener<ApplicationContextEventTests.MyEvent> {
        private final ApplicationContextEventTests.MyOrderedListener3 otherListener;

        public MyOrderedListener4(ApplicationContextEventTests.MyOrderedListener3 otherListener) {
            this.otherListener = otherListener;
        }

        @Override
        public void onApplicationEvent(ApplicationContextEventTests.MyEvent event) {
            Assert.assertTrue(this.otherListener.seenEvents.contains(event));
        }
    }

    public static class EventPublishingBeanPostProcessor implements BeanPostProcessor , ApplicationContextAware {
        private ApplicationContext applicationContext;

        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            this.applicationContext.publishEvent(new ApplicationContextEventTests.MyEvent(this));
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }
}

