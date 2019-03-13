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
package org.springframework.test.context.event;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.event.annotation.PrepareTestInstance;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;


/**
 * Integration tests for {@link EventPublishingTestExecutionListener} and
 * accompanying {@link TestContextEvent} annotations.
 *
 * @author Frank Scheffler
 * @author Sam Brannen
 * @since 5.2
 */
public class EventPublishingTestExecutionListenerIntegrationTests {
    private static final String THREAD_NAME_PREFIX = "Test-";

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final TestContextManager testContextManager = new TestContextManager(EventPublishingTestExecutionListenerIntegrationTests.ExampleTestCase.class);

    private final TestContext testContext = testContextManager.getTestContext();

    private final TestExecutionListener listener = testContext.getApplicationContext().getBean(TestExecutionListener.class);

    private final Object testInstance = new EventPublishingTestExecutionListenerIntegrationTests.ExampleTestCase();

    private final Method testMethod = ReflectionUtils.findMethod(EventPublishingTestExecutionListenerIntegrationTests.ExampleTestCase.class, "traceableTest");

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void beforeTestClassAnnotation() throws Exception {
        testContextManager.beforeTestClass();
        Mockito.verify(listener, Mockito.only()).beforeTestClass(testContext);
    }

    @Test
    public void prepareTestInstanceAnnotation() throws Exception {
        testContextManager.prepareTestInstance(testInstance);
        Mockito.verify(listener, Mockito.only()).prepareTestInstance(testContext);
    }

    @Test
    public void beforeTestMethodAnnotation() throws Exception {
        testContextManager.beforeTestMethod(testInstance, testMethod);
        Mockito.verify(listener, Mockito.only()).beforeTestMethod(testContext);
    }

    /**
     * The {@code @BeforeTestMethod} condition in
     * {@link TestEventListenerConfiguration#beforeTestMethod(BeforeTestMethodEvent)}
     * only matches if the test method is annotated with {@code @Traceable}, and
     * {@link ExampleTestCase#standardTest()} is not.
     */
    @Test
    public void beforeTestMethodAnnotationWithFailingCondition() throws Exception {
        Method standardTest = ReflectionUtils.findMethod(EventPublishingTestExecutionListenerIntegrationTests.ExampleTestCase.class, "standardTest");
        testContextManager.beforeTestMethod(testInstance, standardTest);
        Mockito.verify(listener, Mockito.never()).beforeTestMethod(testContext);
    }

    /**
     * An exception thrown from an event listener executed in the current thread
     * should fail the test method.
     */
    @Test
    public void beforeTestMethodAnnotationWithFailingEventListener() throws Exception {
        Method method = ReflectionUtils.findMethod(EventPublishingTestExecutionListenerIntegrationTests.ExampleTestCase.class, "testWithFailingEventListener");
        exception.expect(RuntimeException.class);
        exception.expectMessage("Boom!");
        try {
            testContextManager.beforeTestMethod(testInstance, method);
        } finally {
            Mockito.verify(listener, Mockito.only()).beforeTestMethod(testContext);
        }
    }

    /**
     * An exception thrown from an event listener that is executed asynchronously
     * should not fail the test method.
     */
    @Test
    public void beforeTestMethodAnnotationWithFailingAsyncEventListener() throws Exception {
        EventPublishingTestExecutionListenerIntegrationTests.TrackingAsyncUncaughtExceptionHandler.asyncException = null;
        String methodName = "testWithFailingAsyncEventListener";
        Method method = ReflectionUtils.findMethod(EventPublishingTestExecutionListenerIntegrationTests.ExampleTestCase.class, methodName);
        testContextManager.beforeTestMethod(testInstance, method);
        Assert.assertThat(EventPublishingTestExecutionListenerIntegrationTests.countDownLatch.await(2, TimeUnit.SECONDS), CoreMatchers.equalTo(true));
        Mockito.verify(listener, Mockito.only()).beforeTestMethod(testContext);
        Assert.assertThat(EventPublishingTestExecutionListenerIntegrationTests.TrackingAsyncUncaughtExceptionHandler.asyncException.getMessage(), CoreMatchers.startsWith(((("Asynchronous exception for test method [" + methodName) + "] in thread [") + (EventPublishingTestExecutionListenerIntegrationTests.THREAD_NAME_PREFIX))));
    }

    @Test
    public void beforeTestExecutionAnnotation() throws Exception {
        testContextManager.beforeTestExecution(testInstance, testMethod);
        Mockito.verify(listener, Mockito.only()).beforeTestExecution(testContext);
    }

    @Test
    public void afterTestExecutionAnnotation() throws Exception {
        testContextManager.afterTestExecution(testInstance, testMethod, null);
        Mockito.verify(listener, Mockito.only()).afterTestExecution(testContext);
    }

    @Test
    public void afterTestMethodAnnotation() throws Exception {
        testContextManager.afterTestMethod(testInstance, testMethod, null);
        Mockito.verify(listener, Mockito.only()).afterTestMethod(testContext);
    }

    @Test
    public void afterTestClassAnnotation() throws Exception {
        testContextManager.afterTestClass();
        Mockito.verify(listener, Mockito.only()).afterTestClass(testContext);
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Traceable {}

    @RunWith(SpringRunner.class)
    @ContextConfiguration(classes = EventPublishingTestExecutionListenerIntegrationTests.TestEventListenerConfiguration.class)
    @TestExecutionListeners(EventPublishingTestExecutionListener.class)
    public static class ExampleTestCase {
        @EventPublishingTestExecutionListenerIntegrationTests.Traceable
        @Test
        public void traceableTest() {
            /* no-op */
        }

        @Test
        public void standardTest() {
            /* no-op */
        }

        @Test
        public void testWithFailingEventListener() {
            /* no-op */
        }

        @Test
        public void testWithFailingAsyncEventListener() {
            /* no-op */
        }
    }

    @Configuration
    @EnableAsync(proxyTargetClass = true)
    static class TestEventListenerConfiguration extends AsyncConfigurerSupport {
        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix(EventPublishingTestExecutionListenerIntegrationTests.THREAD_NAME_PREFIX);
            executor.initialize();
            return executor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return new EventPublishingTestExecutionListenerIntegrationTests.TrackingAsyncUncaughtExceptionHandler();
        }

        @Bean
        public TestExecutionListener listener() {
            return Mockito.mock(TestExecutionListener.class);
        }

        /**
         * The {@code @Async} test event listener method must reside in a separate
         * component since {@code @Async} is not supported on methods in
         * {@code @Configuration} classes.
         */
        @Bean
        EventPublishingTestExecutionListenerIntegrationTests.AsyncTestEventComponent asyncTestEventComponent() {
            return new EventPublishingTestExecutionListenerIntegrationTests.AsyncTestEventComponent(listener());
        }

        @BeforeTestClass("#root.event.source.testClass.name matches '.+TestCase'")
        public void beforeTestClass(BeforeTestClassEvent e) throws Exception {
            listener().beforeTestClass(e.getSource());
        }

        @PrepareTestInstance("#a0.testContext.testClass.name matches '.+TestCase'")
        public void prepareTestInstance(PrepareTestInstanceEvent e) throws Exception {
            listener().prepareTestInstance(e.getSource());
        }

        @BeforeTestMethod("#p0.testContext.testMethod.isAnnotationPresent(T(org.springframework.test.context.event.EventPublishingTestExecutionListenerIntegrationTests.Traceable))")
        public void beforeTestMethod(BeforeTestMethodEvent e) throws Exception {
            listener().beforeTestMethod(e.getSource());
        }

        @BeforeTestMethod("event.testContext.testMethod.name == 'testWithFailingEventListener'")
        public void beforeTestMethodWithFailure(BeforeTestMethodEvent event) throws Exception {
            listener().beforeTestMethod(event.getSource());
            throw new RuntimeException("Boom!");
        }

        @BeforeTestExecution
        public void beforeTestExecution(BeforeTestExecutionEvent e) throws Exception {
            listener().beforeTestExecution(e.getSource());
        }

        @AfterTestExecution
        public void afterTestExecution(AfterTestExecutionEvent e) throws Exception {
            listener().afterTestExecution(e.getSource());
        }

        @AfterTestMethod("event.testContext.testMethod.isAnnotationPresent(T(org.springframework.test.context.event.EventPublishingTestExecutionListenerIntegrationTests.Traceable))")
        public void afterTestMethod(AfterTestMethodEvent e) throws Exception {
            listener().afterTestMethod(e.getSource());
        }

        @AfterTestClass("#afterTestClassEvent.testContext.testClass.name matches '.+TestCase'")
        public void afterTestClass(AfterTestClassEvent afterTestClassEvent) throws Exception {
            listener().afterTestClass(afterTestClassEvent.getSource());
        }
    }

    /**
     * MUST be annotated with {@code @Component} due to a change in Spring 5.1 that
     * does not consider beans in a package starting with "org.springframework" to be
     * event listeners unless they are also components.
     *
     * @see org.springframework.context.event.EventListenerMethodProcessor#isSpringContainerClass
     */
    @Component
    static class AsyncTestEventComponent {
        final TestExecutionListener listener;

        AsyncTestEventComponent(TestExecutionListener listener) {
            this.listener = listener;
        }

        @BeforeTestMethod("event.testContext.testMethod.name == 'testWithFailingAsyncEventListener'")
        @Async
        public void beforeTestMethodWithAsyncFailure(BeforeTestMethodEvent event) throws Exception {
            this.listener.beforeTestMethod(event.getSource());
            throw new RuntimeException(String.format("Asynchronous exception for test method [%s] in thread [%s]", event.getTestContext().getTestMethod().getName(), Thread.currentThread().getName()));
        }
    }

    static class TrackingAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
        static volatile Throwable asyncException;

        @Override
        public void handleUncaughtException(Throwable exception, Method method, Object... params) {
            EventPublishingTestExecutionListenerIntegrationTests.TrackingAsyncUncaughtExceptionHandler.asyncException = exception;
            EventPublishingTestExecutionListenerIntegrationTests.countDownLatch.countDown();
        }
    }
}

