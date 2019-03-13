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
package org.springframework.scheduling.annotation;


import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.concurrent.ListenableFuture;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 */
public class AsyncAnnotationBeanPostProcessorTests {
    @Test
    public void proxyCreated() {
        ConfigurableApplicationContext context = initContext(new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        Object target = context.getBean("target");
        Assert.assertTrue(AopUtils.isAopProxy(target));
        context.close();
    }

    @Test
    public void invokedAsynchronously() {
        ConfigurableApplicationContext context = initContext(new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        testBean.test();
        Thread mainThread = Thread.currentThread();
        testBean.await(3000);
        Thread asyncThread = testBean.getThread();
        Assert.assertNotSame(mainThread, asyncThread);
        context.close();
    }

    @Test
    public void invokedAsynchronouslyOnProxyTarget() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerBeanDefinition("postProcessor", new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        AsyncAnnotationBeanPostProcessorTests.TestBean tb = new AsyncAnnotationBeanPostProcessorTests.TestBean();
        ProxyFactory pf = new ProxyFactory(AsyncAnnotationBeanPostProcessorTests.ITestBean.class, ((MethodInterceptor) (( invocation) -> invocation.getMethod().invoke(tb, invocation.getArguments()))));
        context.registerBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class, () -> ((org.springframework.scheduling.annotation.ITestBean) (pf.getProxy())));
        context.refresh();
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        testBean.test();
        Thread mainThread = Thread.currentThread();
        testBean.await(3000);
        Thread asyncThread = testBean.getThread();
        Assert.assertNotSame(mainThread, asyncThread);
        context.close();
    }

    @Test
    public void threadNamePrefix() {
        BeanDefinition processorDefinition = new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("testExecutor");
        executor.afterPropertiesSet();
        processorDefinition.getPropertyValues().add("executor", executor);
        ConfigurableApplicationContext context = initContext(processorDefinition);
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        testBean.test();
        testBean.await(3000);
        Thread asyncThread = testBean.getThread();
        Assert.assertTrue(asyncThread.getName().startsWith("testExecutor"));
        context.close();
    }

    @Test
    public void taskExecutorByBeanType() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinition processorDefinition = new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class);
        context.registerBeanDefinition("postProcessor", processorDefinition);
        BeanDefinition executorDefinition = new RootBeanDefinition(ThreadPoolTaskExecutor.class);
        executorDefinition.getPropertyValues().add("threadNamePrefix", "testExecutor");
        context.registerBeanDefinition("myExecutor", executorDefinition);
        BeanDefinition targetDefinition = new RootBeanDefinition(AsyncAnnotationBeanPostProcessorTests.TestBean.class);
        context.registerBeanDefinition("target", targetDefinition);
        context.refresh();
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        testBean.test();
        testBean.await(3000);
        Thread asyncThread = testBean.getThread();
        Assert.assertTrue(asyncThread.getName().startsWith("testExecutor"));
        context.close();
    }

    @Test
    public void taskExecutorByBeanName() {
        StaticApplicationContext context = new StaticApplicationContext();
        BeanDefinition processorDefinition = new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class);
        context.registerBeanDefinition("postProcessor", processorDefinition);
        BeanDefinition executorDefinition = new RootBeanDefinition(ThreadPoolTaskExecutor.class);
        executorDefinition.getPropertyValues().add("threadNamePrefix", "testExecutor");
        context.registerBeanDefinition("myExecutor", executorDefinition);
        BeanDefinition executorDefinition2 = new RootBeanDefinition(ThreadPoolTaskExecutor.class);
        executorDefinition2.getPropertyValues().add("threadNamePrefix", "testExecutor2");
        context.registerBeanDefinition("taskExecutor", executorDefinition2);
        BeanDefinition targetDefinition = new RootBeanDefinition(AsyncAnnotationBeanPostProcessorTests.TestBean.class);
        context.registerBeanDefinition("target", targetDefinition);
        context.refresh();
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        testBean.test();
        testBean.await(3000);
        Thread asyncThread = testBean.getThread();
        Assert.assertTrue(asyncThread.getName().startsWith("testExecutor2"));
        context.close();
    }

    @Test
    public void configuredThroughNamespace() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.load(new ClassPathResource("taskNamespaceTests.xml", getClass()));
        context.refresh();
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        testBean.test();
        testBean.await(3000);
        Thread asyncThread = testBean.getThread();
        Assert.assertTrue(asyncThread.getName().startsWith("testExecutor"));
        TestableAsyncUncaughtExceptionHandler exceptionHandler = context.getBean("exceptionHandler", TestableAsyncUncaughtExceptionHandler.class);
        Assert.assertFalse("handler should not have been called yet", exceptionHandler.isCalled());
        testBean.failWithVoid();
        exceptionHandler.await(3000);
        Method m = ReflectionUtils.findMethod(AsyncAnnotationBeanPostProcessorTests.TestBean.class, "failWithVoid");
        exceptionHandler.assertCalledWith(m, UnsupportedOperationException.class);
        context.close();
    }

    @Test
    @SuppressWarnings("resource")
    public void handleExceptionWithFuture() {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AsyncAnnotationBeanPostProcessorTests.ConfigWithExceptionHandler.class);
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        TestableAsyncUncaughtExceptionHandler exceptionHandler = context.getBean("exceptionHandler", TestableAsyncUncaughtExceptionHandler.class);
        Assert.assertFalse("handler should not have been called yet", exceptionHandler.isCalled());
        Future<Object> result = testBean.failWithFuture();
        assertFutureWithException(result, exceptionHandler);
    }

    @Test
    @SuppressWarnings("resource")
    public void handleExceptionWithListenableFuture() {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AsyncAnnotationBeanPostProcessorTests.ConfigWithExceptionHandler.class);
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        TestableAsyncUncaughtExceptionHandler exceptionHandler = context.getBean("exceptionHandler", TestableAsyncUncaughtExceptionHandler.class);
        Assert.assertFalse("handler should not have been called yet", exceptionHandler.isCalled());
        Future<Object> result = testBean.failWithListenableFuture();
        assertFutureWithException(result, exceptionHandler);
    }

    @Test
    public void handleExceptionWithCustomExceptionHandler() {
        Method m = ReflectionUtils.findMethod(AsyncAnnotationBeanPostProcessorTests.TestBean.class, "failWithVoid");
        TestableAsyncUncaughtExceptionHandler exceptionHandler = new TestableAsyncUncaughtExceptionHandler();
        BeanDefinition processorDefinition = new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class);
        processorDefinition.getPropertyValues().add("exceptionHandler", exceptionHandler);
        ConfigurableApplicationContext context = initContext(processorDefinition);
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        Assert.assertFalse("Handler should not have been called", exceptionHandler.isCalled());
        testBean.failWithVoid();
        exceptionHandler.await(3000);
        exceptionHandler.assertCalledWith(m, UnsupportedOperationException.class);
    }

    @Test
    public void exceptionHandlerThrowsUnexpectedException() {
        Method m = ReflectionUtils.findMethod(AsyncAnnotationBeanPostProcessorTests.TestBean.class, "failWithVoid");
        TestableAsyncUncaughtExceptionHandler exceptionHandler = new TestableAsyncUncaughtExceptionHandler(true);
        BeanDefinition processorDefinition = new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class);
        processorDefinition.getPropertyValues().add("exceptionHandler", exceptionHandler);
        processorDefinition.getPropertyValues().add("executor", new AsyncAnnotationBeanPostProcessorTests.DirectExecutor());
        ConfigurableApplicationContext context = initContext(processorDefinition);
        AsyncAnnotationBeanPostProcessorTests.ITestBean testBean = context.getBean("target", AsyncAnnotationBeanPostProcessorTests.ITestBean.class);
        Assert.assertFalse("Handler should not have been called", exceptionHandler.isCalled());
        try {
            testBean.failWithVoid();
            exceptionHandler.assertCalledWith(m, UnsupportedOperationException.class);
        } catch (Exception e) {
            Assert.fail("No unexpected exception should have been received");
        }
    }

    private interface ITestBean {
        Thread getThread();

        @Async
        void test();

        Future<Object> failWithFuture();

        ListenableFuture<Object> failWithListenableFuture();

        void failWithVoid();

        void await(long timeout);
    }

    public static class TestBean implements AsyncAnnotationBeanPostProcessorTests.ITestBean {
        private Thread thread;

        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public Thread getThread() {
            return this.thread;
        }

        @Override
        @Async
        public void test() {
            this.thread = Thread.currentThread();
            this.latch.countDown();
        }

        @Async
        @Override
        public Future<Object> failWithFuture() {
            throw new UnsupportedOperationException("failWithFuture");
        }

        @Async
        @Override
        public ListenableFuture<Object> failWithListenableFuture() {
            throw new UnsupportedOperationException("failWithListenableFuture");
        }

        @Async
        @Override
        public void failWithVoid() {
            throw new UnsupportedOperationException("failWithVoid");
        }

        @Override
        public void await(long timeout) {
            try {
                this.latch.await(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static class DirectExecutor implements Executor {
        @Override
        public void execute(Runnable r) {
            r.run();
        }
    }

    @Configuration
    @EnableAsync
    static class ConfigWithExceptionHandler extends AsyncConfigurerSupport {
        @Bean
        public AsyncAnnotationBeanPostProcessorTests.ITestBean target() {
            return new AsyncAnnotationBeanPostProcessorTests.TestBean();
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return exceptionHandler();
        }

        @Bean
        public TestableAsyncUncaughtExceptionHandler exceptionHandler() {
            return new TestableAsyncUncaughtExceptionHandler();
        }
    }
}

