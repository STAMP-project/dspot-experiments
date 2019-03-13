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
package org.springframework.scheduling.annotation;


import Ordered.HIGHEST_PRECEDENCE;
import Ordered.LOWEST_PRECEDENCE;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.aop.Advisor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;


/**
 * Tests use of @EnableAsync on @Configuration classes.
 *
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1
 */
public class EnableAsyncTests {
    @Test
    public void proxyingOccurs() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.AsyncConfig.class);
        ctx.refresh();
        EnableAsyncTests.AsyncBean asyncBean = ctx.getBean(EnableAsyncTests.AsyncBean.class);
        Assert.assertThat(AopUtils.isAopProxy(asyncBean), CoreMatchers.is(true));
        asyncBean.work();
        ctx.close();
    }

    @Test
    public void proxyingOccursWithMockitoStub() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.AsyncConfigWithMockito.class, EnableAsyncTests.AsyncBeanUser.class);
        ctx.refresh();
        EnableAsyncTests.AsyncBeanUser asyncBeanUser = ctx.getBean(EnableAsyncTests.AsyncBeanUser.class);
        EnableAsyncTests.AsyncBean asyncBean = asyncBeanUser.getAsyncBean();
        Assert.assertThat(AopUtils.isAopProxy(asyncBean), CoreMatchers.is(true));
        asyncBean.work();
        ctx.close();
    }

    @Test
    public void properExceptionForExistingProxyDependencyMismatch() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.AsyncConfig.class, EnableAsyncTests.AsyncBeanWithInterface.class, EnableAsyncTests.AsyncBeanUser.class);
        try {
            ctx.refresh();
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof BeanNotOfRequiredTypeException));
        }
        ctx.close();
    }

    @Test
    public void properExceptionForResolvedProxyDependencyMismatch() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.AsyncConfig.class, EnableAsyncTests.AsyncBeanUser.class, EnableAsyncTests.AsyncBeanWithInterface.class);
        try {
            ctx.refresh();
            Assert.fail("Should have thrown UnsatisfiedDependencyException");
        } catch (UnsatisfiedDependencyException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof BeanNotOfRequiredTypeException));
        }
        ctx.close();
    }

    @Test
    public void withAsyncBeanWithExecutorQualifiedByName() throws InterruptedException, ExecutionException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.AsyncWithExecutorQualifiedByNameConfig.class);
        ctx.refresh();
        EnableAsyncTests.AsyncBeanWithExecutorQualifiedByName asyncBean = ctx.getBean(EnableAsyncTests.AsyncBeanWithExecutorQualifiedByName.class);
        Future<Thread> workerThread0 = asyncBean.work0();
        Assert.assertThat(workerThread0.get().getName(), CoreMatchers.not(CoreMatchers.anyOf(Matchers.startsWith("e1-"), Matchers.startsWith("otherExecutor-"))));
        Future<Thread> workerThread = asyncBean.work();
        Assert.assertThat(workerThread.get().getName(), Matchers.startsWith("e1-"));
        Future<Thread> workerThread2 = asyncBean.work2();
        Assert.assertThat(workerThread2.get().getName(), Matchers.startsWith("otherExecutor-"));
        Future<Thread> workerThread3 = asyncBean.work3();
        Assert.assertThat(workerThread3.get().getName(), Matchers.startsWith("otherExecutor-"));
        ctx.close();
    }

    @Test
    public void asyncProcessorIsOrderedLowestPrecedenceByDefault() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.AsyncConfig.class);
        ctx.refresh();
        AsyncAnnotationBeanPostProcessor bpp = ctx.getBean(AsyncAnnotationBeanPostProcessor.class);
        Assert.assertThat(bpp.getOrder(), CoreMatchers.is(LOWEST_PRECEDENCE));
        ctx.close();
    }

    @Test
    public void orderAttributeIsPropagated() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.OrderedAsyncConfig.class);
        ctx.refresh();
        AsyncAnnotationBeanPostProcessor bpp = ctx.getBean(AsyncAnnotationBeanPostProcessor.class);
        Assert.assertThat(bpp.getOrder(), CoreMatchers.is(HIGHEST_PRECEDENCE));
        ctx.close();
    }

    @Test
    public void customAsyncAnnotationIsPropagated() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.CustomAsyncAnnotationConfig.class, EnableAsyncTests.CustomAsyncBean.class);
        ctx.refresh();
        Object bean = ctx.getBean(EnableAsyncTests.CustomAsyncBean.class);
        Assert.assertTrue(AopUtils.isAopProxy(bean));
        boolean isAsyncAdvised = false;
        for (Advisor advisor : getAdvisors()) {
            if (advisor instanceof AsyncAnnotationAdvisor) {
                isAsyncAdvised = true;
                break;
            }
        }
        Assert.assertTrue("bean was not async advised as expected", isAsyncAdvised);
        ctx.close();
    }

    /**
     * Fails with classpath errors on trying to classload AnnotationAsyncExecutionAspect
     */
    @Test(expected = BeanDefinitionStoreException.class)
    public void aspectModeAspectJAttemptsToRegisterAsyncAspect() {
        @SuppressWarnings("resource")
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.AspectJAsyncAnnotationConfig.class);
        ctx.refresh();
    }

    @Test
    public void customExecutorBean() throws InterruptedException {
        // Arrange
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.CustomExecutorBean.class);
        ctx.refresh();
        EnableAsyncTests.AsyncBean asyncBean = ctx.getBean(EnableAsyncTests.AsyncBean.class);
        // Act
        asyncBean.work();
        // Assert
        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> (asyncBean.getThreadOfExecution()) != null);
        Assert.assertThat(asyncBean.getThreadOfExecution().getName(), Matchers.startsWith("Custom-"));
        ctx.close();
    }

    @Test
    public void customExecutorConfig() {
        // Arrange
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.CustomExecutorConfig.class);
        ctx.refresh();
        EnableAsyncTests.AsyncBean asyncBean = ctx.getBean(EnableAsyncTests.AsyncBean.class);
        // Act
        asyncBean.work();
        // Assert
        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> (asyncBean.getThreadOfExecution()) != null);
        Assert.assertThat(asyncBean.getThreadOfExecution().getName(), Matchers.startsWith("Custom-"));
        ctx.close();
    }

    @Test
    public void customExecutorConfigWithThrowsException() {
        // Arrange
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.CustomExecutorConfig.class);
        ctx.refresh();
        EnableAsyncTests.AsyncBean asyncBean = ctx.getBean(EnableAsyncTests.AsyncBean.class);
        Method method = ReflectionUtils.findMethod(EnableAsyncTests.AsyncBean.class, "fail");
        TestableAsyncUncaughtExceptionHandler exceptionHandler = ((TestableAsyncUncaughtExceptionHandler) (ctx.getBean("exceptionHandler")));
        Assert.assertFalse("handler should not have been called yet", exceptionHandler.isCalled());
        // Act
        asyncBean.fail();
        // Assert
        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).pollInterval(10, TimeUnit.MILLISECONDS).untilAsserted(() -> exceptionHandler.assertCalledWith(method, .class));
        ctx.close();
    }

    @Test
    public void customExecutorBeanConfig() throws InterruptedException {
        // Arrange
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.CustomExecutorBeanConfig.class, EnableAsyncTests.ExecutorPostProcessor.class);
        ctx.refresh();
        EnableAsyncTests.AsyncBean asyncBean = ctx.getBean(EnableAsyncTests.AsyncBean.class);
        // Act
        asyncBean.work();
        // Assert
        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> (asyncBean.getThreadOfExecution()) != null);
        Assert.assertThat(asyncBean.getThreadOfExecution().getName(), Matchers.startsWith("Post-"));
        ctx.close();
    }

    @Test
    public void customExecutorBeanConfigWithThrowsException() {
        // Arrange
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(EnableAsyncTests.CustomExecutorBeanConfig.class, EnableAsyncTests.ExecutorPostProcessor.class);
        ctx.refresh();
        EnableAsyncTests.AsyncBean asyncBean = ctx.getBean(EnableAsyncTests.AsyncBean.class);
        TestableAsyncUncaughtExceptionHandler exceptionHandler = ((TestableAsyncUncaughtExceptionHandler) (ctx.getBean("exceptionHandler")));
        Assert.assertFalse("handler should not have been called yet", exceptionHandler.isCalled());
        Method method = ReflectionUtils.findMethod(EnableAsyncTests.AsyncBean.class, "fail");
        // Act
        asyncBean.fail();
        // Assert
        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).pollInterval(10, TimeUnit.MILLISECONDS).untilAsserted(() -> exceptionHandler.assertCalledWith(method, .class));
        ctx.close();
    }

    // SPR-14949
    @Test
    public void findOnInterfaceWithInterfaceProxy() throws InterruptedException {
        // Arrange
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableAsyncTests.Spr14949ConfigA.class);
        EnableAsyncTests.AsyncInterface asyncBean = ctx.getBean(EnableAsyncTests.AsyncInterface.class);
        // Act
        asyncBean.work();
        // Assert
        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> (asyncBean.getThreadOfExecution()) != null);
        Assert.assertThat(asyncBean.getThreadOfExecution().getName(), Matchers.startsWith("Custom-"));
        ctx.close();
    }

    // SPR-14949
    @Test
    public void findOnInterfaceWithCglibProxy() throws InterruptedException {
        // Arrange
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(EnableAsyncTests.Spr14949ConfigB.class);
        EnableAsyncTests.AsyncInterface asyncBean = ctx.getBean(EnableAsyncTests.AsyncInterface.class);
        // Act
        asyncBean.work();
        // Assert
        Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> (asyncBean.getThreadOfExecution()) != null);
        Assert.assertThat(asyncBean.getThreadOfExecution().getName(), Matchers.startsWith("Custom-"));
        ctx.close();
    }

    static class AsyncBeanWithExecutorQualifiedByName {
        @Async
        public Future<Thread> work0() {
            return new AsyncResult(Thread.currentThread());
        }

        @Async("e1")
        public Future<Thread> work() {
            return new AsyncResult(Thread.currentThread());
        }

        @Async("otherExecutor")
        public Future<Thread> work2() {
            return new AsyncResult(Thread.currentThread());
        }

        @Async("e2")
        public Future<Thread> work3() {
            return new AsyncResult(Thread.currentThread());
        }
    }

    static class AsyncBean {
        private Thread threadOfExecution;

        @Async
        public void work() {
            this.threadOfExecution = Thread.currentThread();
        }

        @Async
        public void fail() {
            throw new UnsupportedOperationException();
        }

        public Thread getThreadOfExecution() {
            return threadOfExecution;
        }
    }

    @Component("asyncBean")
    static class AsyncBeanWithInterface extends EnableAsyncTests.AsyncBean implements Runnable {
        @Override
        public void run() {
        }
    }

    static class AsyncBeanUser {
        private final EnableAsyncTests.AsyncBean asyncBean;

        public AsyncBeanUser(EnableAsyncTests.AsyncBean asyncBean) {
            this.asyncBean = asyncBean;
        }

        public EnableAsyncTests.AsyncBean getAsyncBean() {
            return asyncBean;
        }
    }

    @EnableAsync(annotation = EnableAsyncTests.CustomAsync.class)
    static class CustomAsyncAnnotationConfig {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CustomAsync {}

    static class CustomAsyncBean {
        @EnableAsyncTests.CustomAsync
        public void work() {
        }
    }

    @Configuration
    @EnableAsync(order = Ordered.HIGHEST_PRECEDENCE)
    static class OrderedAsyncConfig {
        @Bean
        public EnableAsyncTests.AsyncBean asyncBean() {
            return new EnableAsyncTests.AsyncBean();
        }
    }

    @Configuration
    @EnableAsync(mode = AdviceMode.ASPECTJ)
    static class AspectJAsyncAnnotationConfig {
        @Bean
        public EnableAsyncTests.AsyncBean asyncBean() {
            return new EnableAsyncTests.AsyncBean();
        }
    }

    @Configuration
    @EnableAsync
    static class AsyncConfig {
        @Bean
        public EnableAsyncTests.AsyncBean asyncBean() {
            return new EnableAsyncTests.AsyncBean();
        }
    }

    @Configuration
    @EnableAsync
    static class AsyncConfigWithMockito {
        @Bean
        @Lazy
        public EnableAsyncTests.AsyncBean asyncBean() {
            return Mockito.mock(EnableAsyncTests.AsyncBean.class);
        }
    }

    @Configuration
    @EnableAsync
    static class AsyncWithExecutorQualifiedByNameConfig {
        @Bean
        public EnableAsyncTests.AsyncBeanWithExecutorQualifiedByName asyncBean() {
            return new EnableAsyncTests.AsyncBeanWithExecutorQualifiedByName();
        }

        @Bean
        public Executor e1() {
            return new ThreadPoolTaskExecutor();
        }

        @Bean
        @Qualifier("e2")
        public Executor otherExecutor() {
            return new ThreadPoolTaskExecutor();
        }
    }

    @Configuration
    @EnableAsync
    static class CustomExecutorBean {
        @Bean
        public EnableAsyncTests.AsyncBean asyncBean() {
            return new EnableAsyncTests.AsyncBean();
        }

        @Bean
        public Executor taskExecutor() {
            return Executors.newSingleThreadExecutor(new CustomizableThreadFactory("Custom-"));
        }
    }

    @Configuration
    @EnableAsync
    static class CustomExecutorConfig implements AsyncConfigurer {
        @Bean
        public EnableAsyncTests.AsyncBean asyncBean() {
            return new EnableAsyncTests.AsyncBean();
        }

        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix("Custom-");
            executor.initialize();
            return executor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return exceptionHandler();
        }

        @Bean
        public AsyncUncaughtExceptionHandler exceptionHandler() {
            return new TestableAsyncUncaughtExceptionHandler();
        }
    }

    @Configuration
    @EnableAsync
    static class CustomExecutorBeanConfig implements AsyncConfigurer {
        @Bean
        public EnableAsyncTests.AsyncBean asyncBean() {
            return new EnableAsyncTests.AsyncBean();
        }

        @Override
        public Executor getAsyncExecutor() {
            return executor();
        }

        @Bean
        public ThreadPoolTaskExecutor executor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix("Custom-");
            executor.initialize();
            return executor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return exceptionHandler();
        }

        @Bean
        public AsyncUncaughtExceptionHandler exceptionHandler() {
            return new TestableAsyncUncaughtExceptionHandler();
        }
    }

    public static class ExecutorPostProcessor implements BeanPostProcessor {
        @Nullable
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof ThreadPoolTaskExecutor) {
                setThreadNamePrefix("Post-");
            }
            return bean;
        }
    }

    public interface AsyncInterface {
        @Async
        void work();

        Thread getThreadOfExecution();
    }

    public static class AsyncService implements EnableAsyncTests.AsyncInterface {
        private Thread threadOfExecution;

        @Override
        public void work() {
            this.threadOfExecution = Thread.currentThread();
        }

        @Override
        public Thread getThreadOfExecution() {
            return threadOfExecution;
        }
    }

    @Configuration
    @EnableAsync
    static class Spr14949ConfigA implements AsyncConfigurer {
        @Bean
        public EnableAsyncTests.AsyncInterface asyncBean() {
            return new EnableAsyncTests.AsyncService();
        }

        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix("Custom-");
            executor.initialize();
            return executor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return null;
        }
    }

    @Configuration
    @EnableAsync(proxyTargetClass = true)
    static class Spr14949ConfigB implements AsyncConfigurer {
        @Bean
        public EnableAsyncTests.AsyncInterface asyncBean() {
            return new EnableAsyncTests.AsyncService();
        }

        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix("Custom-");
            executor.initialize();
            return executor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return null;
        }
    }
}

