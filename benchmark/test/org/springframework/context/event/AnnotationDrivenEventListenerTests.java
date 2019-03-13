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
package org.springframework.context.event;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.test.AbstractIdentifiable;
import org.springframework.context.event.test.AnotherTestEvent;
import org.springframework.context.event.test.EventCollector;
import org.springframework.context.event.test.GenericEventPojo;
import org.springframework.context.event.test.Identifiable;
import org.springframework.context.event.test.TestEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;


/**
 *
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 */
public class AnnotationDrivenEventListenerTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private ConfigurableApplicationContext context;

    private EventCollector eventCollector;

    private CountDownLatch countDownLatch;// 1 call by default


    @Test
    public void simpleEventJavaConfig() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class);
        TestEvent event = new TestEvent(this, "test");
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(listener, event);
        this.eventCollector.assertTotalEventsCount(1);
        this.eventCollector.clear();
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(listener, event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void simpleEventXmlConfig() {
        this.context = new ClassPathXmlApplicationContext("org/springframework/context/event/simple-event-configuration.xml");
        TestEvent event = new TestEvent(this, "test");
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector = getEventCollector(this.context);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(listener, event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void metaAnnotationIsDiscovered() {
        load(AnnotationDrivenEventListenerTests.MetaAnnotationListenerTestBean.class);
        AnnotationDrivenEventListenerTests.MetaAnnotationListenerTestBean bean = this.context.getBean(AnnotationDrivenEventListenerTests.MetaAnnotationListenerTestBean.class);
        this.eventCollector.assertNoEventReceived(bean);
        TestEvent event = new TestEvent();
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(bean, event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void contextEventsAreReceived() {
        load(AnnotationDrivenEventListenerTests.ContextEventListener.class);
        AnnotationDrivenEventListenerTests.ContextEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.ContextEventListener.class);
        List<Object> events = this.eventCollector.getEvents(listener);
        Assert.assertEquals("Wrong number of initial context events", 1, events.size());
        Assert.assertEquals(ContextRefreshedEvent.class, events.get(0).getClass());
        this.context.stop();
        List<Object> eventsAfterStop = this.eventCollector.getEvents(listener);
        Assert.assertEquals("Wrong number of context events on shutdown", 2, eventsAfterStop.size());
        Assert.assertEquals(ContextStoppedEvent.class, eventsAfterStop.get(1).getClass());
        this.eventCollector.assertTotalEventsCount(2);
    }

    @Test
    public void methodSignatureNoEvent() {
        @SuppressWarnings("resource")
        AnnotationConfigApplicationContext failingContext = new AnnotationConfigApplicationContext();
        failingContext.register(AnnotationDrivenEventListenerTests.BasicConfiguration.class, AnnotationDrivenEventListenerTests.InvalidMethodSignatureEventListener.class);
        this.thrown.expect(BeanInitializationException.class);
        this.thrown.expectMessage(AnnotationDrivenEventListenerTests.InvalidMethodSignatureEventListener.class.getName());
        this.thrown.expectMessage("cannotBeCalled");
        failingContext.refresh();
    }

    @Test
    public void simpleReply() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class, AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnotherTestEvent event = new AnotherTestEvent(this, "dummy");
        AnnotationDrivenEventListenerTests.ReplyEventListener replyEventListener = this.context.getBean(AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertNoEventReceived(replyEventListener);
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(replyEventListener, event);
        this.eventCollector.assertEvent(listener, new TestEvent(replyEventListener, event.getId(), "dummy"));// reply

        this.eventCollector.assertTotalEventsCount(2);
    }

    @Test
    public void nullReplyIgnored() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class, AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnotherTestEvent event = new AnotherTestEvent(this, null);// No response

        AnnotationDrivenEventListenerTests.ReplyEventListener replyEventListener = this.context.getBean(AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertNoEventReceived(replyEventListener);
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(replyEventListener, event);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void arrayReply() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class, AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnotherTestEvent event = new AnotherTestEvent(this, new String[]{ "first", "second" });
        AnnotationDrivenEventListenerTests.ReplyEventListener replyEventListener = this.context.getBean(AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertNoEventReceived(replyEventListener);
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(replyEventListener, event);
        this.eventCollector.assertEvent(listener, "first", "second");// reply

        this.eventCollector.assertTotalEventsCount(3);
    }

    @Test
    public void collectionReply() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class, AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        Set<Object> replies = new LinkedHashSet<>();
        replies.add("first");
        replies.add(4L);
        replies.add("third");
        AnotherTestEvent event = new AnotherTestEvent(this, replies);
        AnnotationDrivenEventListenerTests.ReplyEventListener replyEventListener = this.context.getBean(AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertNoEventReceived(replyEventListener);
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(replyEventListener, event);
        this.eventCollector.assertEvent(listener, "first", "third");// reply (no listener for 4L)

        this.eventCollector.assertTotalEventsCount(3);
    }

    @Test
    public void collectionReplyNullValue() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class, AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnotherTestEvent event = new AnotherTestEvent(this, Arrays.asList(null, "test"));
        AnnotationDrivenEventListenerTests.ReplyEventListener replyEventListener = this.context.getBean(AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertNoEventReceived(replyEventListener);
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(replyEventListener, event);
        this.eventCollector.assertEvent(listener, "test");
        this.eventCollector.assertTotalEventsCount(2);
    }

    @Test
    public void eventListenerWorksWithSimpleInterfaceProxy() throws Exception {
        load(AnnotationDrivenEventListenerTests.ScopedProxyTestBean.class);
        AnnotationDrivenEventListenerTests.SimpleService proxy = this.context.getBean(AnnotationDrivenEventListenerTests.SimpleService.class);
        Assert.assertTrue("bean should be a proxy", (proxy instanceof Advised));
        this.eventCollector.assertNoEventReceived(proxy.getId());
        this.context.publishEvent(new ContextRefreshedEvent(this.context));
        this.eventCollector.assertNoEventReceived(proxy.getId());
        TestEvent event = new TestEvent();
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(proxy.getId(), event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void eventListenerWorksWithAnnotatedInterfaceProxy() throws Exception {
        load(AnnotationDrivenEventListenerTests.AnnotatedProxyTestBean.class);
        AnnotationDrivenEventListenerTests.AnnotatedSimpleService proxy = this.context.getBean(AnnotationDrivenEventListenerTests.AnnotatedSimpleService.class);
        Assert.assertTrue("bean should be a proxy", (proxy instanceof Advised));
        this.eventCollector.assertNoEventReceived(proxy.getId());
        this.context.publishEvent(new ContextRefreshedEvent(this.context));
        this.eventCollector.assertNoEventReceived(proxy.getId());
        TestEvent event = new TestEvent();
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(proxy.getId(), event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void eventListenerWorksWithCglibProxy() throws Exception {
        load(AnnotationDrivenEventListenerTests.CglibProxyTestBean.class);
        AnnotationDrivenEventListenerTests.CglibProxyTestBean proxy = this.context.getBean(AnnotationDrivenEventListenerTests.CglibProxyTestBean.class);
        Assert.assertTrue("bean should be a cglib proxy", AopUtils.isCglibProxy(proxy));
        this.eventCollector.assertNoEventReceived(proxy.getId());
        this.context.publishEvent(new ContextRefreshedEvent(this.context));
        this.eventCollector.assertNoEventReceived(proxy.getId());
        TestEvent event = new TestEvent();
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(proxy.getId(), event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void privateMethodOnCglibProxyFails() throws Exception {
        try {
            load(AnnotationDrivenEventListenerTests.CglibProxyWithPrivateMethod.class);
            Assert.fail("Should have thrown BeanInitializationException");
        } catch (BeanInitializationException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void eventListenerWorksWithCustomScope() throws Exception {
        load(AnnotationDrivenEventListenerTests.CustomScopeTestBean.class);
        AnnotationDrivenEventListenerTests.CustomScope customScope = new AnnotationDrivenEventListenerTests.CustomScope();
        this.context.getBeanFactory().registerScope("custom", customScope);
        AnnotationDrivenEventListenerTests.CustomScopeTestBean proxy = this.context.getBean(AnnotationDrivenEventListenerTests.CustomScopeTestBean.class);
        Assert.assertTrue("bean should be a cglib proxy", AopUtils.isCglibProxy(proxy));
        this.eventCollector.assertNoEventReceived(proxy.getId());
        this.context.publishEvent(new ContextRefreshedEvent(this.context));
        this.eventCollector.assertNoEventReceived(proxy.getId());
        customScope.active = false;
        this.context.publishEvent(new ContextRefreshedEvent(this.context));
        customScope.active = true;
        this.eventCollector.assertNoEventReceived(proxy.getId());
        TestEvent event = new TestEvent();
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(proxy.getId(), event);
        this.eventCollector.assertTotalEventsCount(1);
        try {
            customScope.active = false;
            this.context.publishEvent(new TestEvent());
            Assert.fail("Should have thrown IllegalStateException");
        } catch (BeanCreationException ex) {
            // expected
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void asyncProcessingApplied() throws InterruptedException {
        loadAsync(AnnotationDrivenEventListenerTests.AsyncEventListener.class);
        String threadName = Thread.currentThread().getName();
        AnotherTestEvent event = new AnotherTestEvent(this, threadName);
        AnnotationDrivenEventListenerTests.AsyncEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.AsyncEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent(event);
        this.countDownLatch.await(2, TimeUnit.SECONDS);
        this.eventCollector.assertEvent(listener, event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void asyncProcessingAppliedWithInterfaceProxy() throws InterruptedException {
        doLoad(AnnotationDrivenEventListenerTests.AsyncConfigurationWithInterfaces.class, AnnotationDrivenEventListenerTests.SimpleProxyTestBean.class);
        String threadName = Thread.currentThread().getName();
        AnotherTestEvent event = new AnotherTestEvent(this, threadName);
        AnnotationDrivenEventListenerTests.SimpleService listener = this.context.getBean(AnnotationDrivenEventListenerTests.SimpleService.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent(event);
        this.countDownLatch.await(2, TimeUnit.SECONDS);
        this.eventCollector.assertEvent(listener, event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void asyncProcessingAppliedWithScopedProxy() throws InterruptedException {
        doLoad(AnnotationDrivenEventListenerTests.AsyncConfigurationWithInterfaces.class, AnnotationDrivenEventListenerTests.ScopedProxyTestBean.class);
        String threadName = Thread.currentThread().getName();
        AnotherTestEvent event = new AnotherTestEvent(this, threadName);
        AnnotationDrivenEventListenerTests.SimpleService listener = this.context.getBean(AnnotationDrivenEventListenerTests.SimpleService.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent(event);
        this.countDownLatch.await(2, TimeUnit.SECONDS);
        this.eventCollector.assertEvent(listener, event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void exceptionPropagated() {
        load(AnnotationDrivenEventListenerTests.ExceptionEventListener.class);
        TestEvent event = new TestEvent(this, "fail");
        AnnotationDrivenEventListenerTests.ExceptionEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.ExceptionEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        try {
            this.context.publishEvent(event);
            Assert.fail("An exception should have thrown");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Wrong exception", "Test exception", e.getMessage());
            this.eventCollector.assertEvent(listener, event);
            this.eventCollector.assertTotalEventsCount(1);
        }
    }

    @Test
    public void exceptionNotPropagatedWithAsync() throws InterruptedException {
        loadAsync(AnnotationDrivenEventListenerTests.ExceptionEventListener.class);
        AnotherTestEvent event = new AnotherTestEvent(this, "fail");
        AnnotationDrivenEventListenerTests.ExceptionEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.ExceptionEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent(event);
        this.countDownLatch.await(2, TimeUnit.SECONDS);
        this.eventCollector.assertEvent(listener, event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void listenerWithSimplePayload() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class);
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent("test");
        this.eventCollector.assertEvent(listener, "test");
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void listenerWithNonMatchingPayload() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class);
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent(123L);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertTotalEventsCount(0);
    }

    @Test
    public void replyWithPayload() {
        load(AnnotationDrivenEventListenerTests.TestEventListener.class, AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnotherTestEvent event = new AnotherTestEvent(this, "String");
        AnnotationDrivenEventListenerTests.ReplyEventListener replyEventListener = this.context.getBean(AnnotationDrivenEventListenerTests.ReplyEventListener.class);
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.TestEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertNoEventReceived(replyEventListener);
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(replyEventListener, event);
        this.eventCollector.assertEvent(listener, "String");// reply

        this.eventCollector.assertTotalEventsCount(2);
    }

    @Test
    public void listenerWithGenericApplicationEvent() {
        load(AnnotationDrivenEventListenerTests.GenericEventListener.class);
        AnnotationDrivenEventListenerTests.GenericEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.GenericEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent("TEST");
        this.eventCollector.assertEvent(listener, "TEST");
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void listenerWithResolvableTypeEvent() {
        load(AnnotationDrivenEventListenerTests.ResolvableTypeEventListener.class);
        AnnotationDrivenEventListenerTests.ResolvableTypeEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.ResolvableTypeEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        GenericEventPojo<String> event = new GenericEventPojo<>("TEST");
        this.context.publishEvent(event);
        this.eventCollector.assertEvent(listener, event);
        this.eventCollector.assertTotalEventsCount(1);
    }

    @Test
    public void listenerWithResolvableTypeEventWrongGeneric() {
        load(AnnotationDrivenEventListenerTests.ResolvableTypeEventListener.class);
        AnnotationDrivenEventListenerTests.ResolvableTypeEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.ResolvableTypeEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        GenericEventPojo<Long> event = new GenericEventPojo<>(123L);
        this.context.publishEvent(event);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertTotalEventsCount(0);
    }

    @Test
    public void conditionMatch() {
        validateConditionMatch(AnnotationDrivenEventListenerTests.ConditionalEventListener.class);
    }

    @Test
    public void conditionMatchWithProxy() {
        validateConditionMatch(AnnotationDrivenEventListenerTests.ConditionalEventListener.class, MethodValidationPostProcessor.class);
    }

    @Test
    public void conditionDoesNotMatch() {
        long maxLong = Long.MAX_VALUE;
        load(AnnotationDrivenEventListenerTests.ConditionalEventListener.class);
        TestEvent event = new TestEvent(this, "KO");
        AnnotationDrivenEventListenerTests.TestEventListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.ConditionalEventListener.class);
        this.eventCollector.assertNoEventReceived(listener);
        this.context.publishEvent(event);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertTotalEventsCount(0);
        this.context.publishEvent("KO");
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertTotalEventsCount(0);
        this.context.publishEvent(maxLong);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertTotalEventsCount(0);
        this.context.publishEvent(24.0);
        this.eventCollector.assertNoEventReceived(listener);
        this.eventCollector.assertTotalEventsCount(0);
    }

    @Test
    public void orderedListeners() {
        load(AnnotationDrivenEventListenerTests.OrderedTestListener.class);
        AnnotationDrivenEventListenerTests.OrderedTestListener listener = this.context.getBean(AnnotationDrivenEventListenerTests.OrderedTestListener.class);
        Assert.assertTrue(listener.order.isEmpty());
        this.context.publishEvent("whatever");
        Assert.assertThat(listener.order, contains("first", "second", "third"));
    }

    @Configuration
    static class BasicConfiguration {
        @Bean
        public EventCollector eventCollector() {
            return new EventCollector();
        }

        @Bean
        public CountDownLatch testCountDownLatch() {
            return new CountDownLatch(1);
        }

        @Bean
        public AnnotationDrivenEventListenerTests.BasicConfiguration.TestConditionEvaluator conditionEvaluator() {
            return new AnnotationDrivenEventListenerTests.BasicConfiguration.TestConditionEvaluator();
        }

        static class TestConditionEvaluator {
            public boolean valid(Double ratio) {
                return new Double(42).equals(ratio);
            }
        }
    }

    abstract static class AbstractTestEventListener extends AbstractIdentifiable {
        @Autowired
        private EventCollector eventCollector;

        protected void collectEvent(Object content) {
            this.eventCollector.addEvent(this, content);
        }
    }

    @Component
    static class TestEventListener extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @EventListener
        public void handle(TestEvent event) {
            collectEvent(event);
        }

        @EventListener
        public void handleString(String content) {
            collectEvent(content);
        }
    }

    @EventListener
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FooListener {}

    @Component
    static class MetaAnnotationListenerTestBean extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @AnnotationDrivenEventListenerTests.FooListener
        public void handleIt(TestEvent event) {
            collectEvent(event);
        }
    }

    @Component
    static class ContextEventListener extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @EventListener
        public void handleContextEvent(ApplicationContextEvent event) {
            collectEvent(event);
        }
    }

    @Component
    static class InvalidMethodSignatureEventListener {
        @EventListener
        public void cannotBeCalled(String s, Integer what) {
        }
    }

    @Component
    static class ReplyEventListener extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @EventListener
        public Object handle(AnotherTestEvent event) {
            collectEvent(event);
            if ((event.content) == null) {
                return null;
            } else
                if ((event.content) instanceof String) {
                    String s = ((String) (event.content));
                    if (s.equals("String")) {
                        return event.content;
                    } else {
                        return new TestEvent(this, event.getId(), s);
                    }
                }

            return event.content;
        }
    }

    @Component
    static class ExceptionEventListener extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @Autowired
        private CountDownLatch countDownLatch;

        @EventListener
        public void handle(TestEvent event) {
            collectEvent(event);
            if ("fail".equals(event.msg)) {
                throw new IllegalStateException("Test exception");
            }
        }

        @EventListener
        @Async
        public void handleAsync(AnotherTestEvent event) {
            collectEvent(event);
            if ("fail".equals(event.content)) {
                this.countDownLatch.countDown();
                throw new IllegalStateException("Test exception");
            }
        }
    }

    @Component
    static class AsyncEventListener extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @Autowired
        private CountDownLatch countDownLatch;

        @EventListener
        @Async
        public void handleAsync(AnotherTestEvent event) {
            Assert.assertNotEquals(event.content, Thread.currentThread().getName());
            collectEvent(event);
            this.countDownLatch.countDown();
        }
    }

    @Configuration
    @Import(AnnotationDrivenEventListenerTests.BasicConfiguration.class)
    @EnableAsync(proxyTargetClass = true)
    static class AsyncConfiguration {}

    @Configuration
    @Import(AnnotationDrivenEventListenerTests.BasicConfiguration.class)
    @EnableAsync(proxyTargetClass = false)
    static class AsyncConfigurationWithInterfaces {}

    interface SimpleService extends Identifiable {
        void handleIt(TestEvent event);

        void handleAsync(AnotherTestEvent event);
    }

    @Component
    static class SimpleProxyTestBean extends AbstractIdentifiable implements AnnotationDrivenEventListenerTests.SimpleService {
        @Autowired
        private EventCollector eventCollector;

        @Autowired
        private CountDownLatch countDownLatch;

        @EventListener
        @Override
        public void handleIt(TestEvent event) {
            this.eventCollector.addEvent(this, event);
        }

        @EventListener
        @Async
        public void handleAsync(AnotherTestEvent event) {
            Assert.assertNotEquals(event.content, Thread.currentThread().getName());
            this.eventCollector.addEvent(this, event);
            this.countDownLatch.countDown();
        }
    }

    @Component
    @Scope(proxyMode = ScopedProxyMode.INTERFACES)
    static class ScopedProxyTestBean extends AbstractIdentifiable implements AnnotationDrivenEventListenerTests.SimpleService {
        @Autowired
        private EventCollector eventCollector;

        @Autowired
        private CountDownLatch countDownLatch;

        @EventListener
        @Override
        public void handleIt(TestEvent event) {
            this.eventCollector.addEvent(this, event);
        }

        @EventListener
        @Async
        public void handleAsync(AnotherTestEvent event) {
            Assert.assertNotEquals(event.content, Thread.currentThread().getName());
            this.eventCollector.addEvent(this, event);
            this.countDownLatch.countDown();
        }
    }

    interface AnnotatedSimpleService extends Identifiable {
        @EventListener
        void handleIt(TestEvent event);
    }

    @Component
    @Scope(proxyMode = ScopedProxyMode.INTERFACES)
    static class AnnotatedProxyTestBean extends AbstractIdentifiable implements AnnotationDrivenEventListenerTests.AnnotatedSimpleService {
        @Autowired
        private EventCollector eventCollector;

        @Override
        public void handleIt(TestEvent event) {
            this.eventCollector.addEvent(this, event);
        }
    }

    @Component
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    static class CglibProxyTestBean extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @EventListener
        public void handleIt(TestEvent event) {
            collectEvent(event);
        }
    }

    @Component
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    static class CglibProxyWithPrivateMethod extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @EventListener
        private void handleIt(TestEvent event) {
            collectEvent(event);
        }
    }

    @Component
    @Scope(scopeName = "custom", proxyMode = ScopedProxyMode.TARGET_CLASS)
    static class CustomScopeTestBean extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @EventListener
        public void handleIt(TestEvent event) {
            collectEvent(event);
        }
    }

    @Component
    static class GenericEventListener extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @EventListener
        public void handleString(PayloadApplicationEvent<String> event) {
            collectEvent(event.getPayload());
        }
    }

    @Component
    static class ResolvableTypeEventListener extends AnnotationDrivenEventListenerTests.AbstractTestEventListener {
        @EventListener
        public void handleString(GenericEventPojo<String> value) {
            collectEvent(value);
        }
    }

    @EventListener
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConditionalEvent {
        @AliasFor(annotation = EventListener.class, attribute = "condition")
        String value();
    }

    interface ConditionalEventInterface extends Identifiable {
        void handle(TestEvent event);

        void handleString(String payload);

        void handleTimestamp(Long timestamp);

        void handleRatio(Double ratio);
    }

    @Component
    @Validated
    static class ConditionalEventListener extends AnnotationDrivenEventListenerTests.TestEventListener implements AnnotationDrivenEventListenerTests.ConditionalEventInterface {
        @EventListener(condition = "'OK'.equals(#root.event.msg)")
        @Override
        public void handle(TestEvent event) {
            super.handle(event);
        }

        @Override
        @EventListener(condition = "#payload.startsWith('OK')")
        public void handleString(String payload) {
            super.handleString(payload);
        }

        @AnnotationDrivenEventListenerTests.ConditionalEvent("#root.event.timestamp > #p0")
        public void handleTimestamp(Long timestamp) {
            collectEvent(timestamp);
        }

        @AnnotationDrivenEventListenerTests.ConditionalEvent("@conditionEvaluator.valid(#p0)")
        public void handleRatio(Double ratio) {
            collectEvent(ratio);
        }
    }

    @Configuration
    static class OrderedTestListener extends AnnotationDrivenEventListenerTests.TestEventListener {
        public final List<String> order = new ArrayList<>();

        @EventListener
        @Order(50)
        public void handleThird(String payload) {
            this.order.add("third");
        }

        @EventListener
        @Order(-50)
        public void handleFirst(String payload) {
            this.order.add("first");
        }

        @EventListener
        public void handleSecond(String payload) {
            this.order.add("second");
        }
    }

    static class EventOnPostConstruct {
        @Autowired
        ApplicationEventPublisher publisher;

        @PostConstruct
        public void init() {
            this.publisher.publishEvent("earlyEvent");
        }
    }

    private static class CustomScope implements org.springframework.beans.factory.config.Scope {
        public boolean active = true;

        private Object instance = null;

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            org.springframework.util.Assert.state(this.active, "Not active");
            if ((this.instance) == null) {
                this.instance = objectFactory.getObject();
            }
            return this.instance;
        }

        @Override
        public Object remove(String name) {
            return null;
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
        }

        @Override
        public Object resolveContextualObject(String key) {
            return null;
        }

        @Override
        public String getConversationId() {
            return null;
        }
    }
}

