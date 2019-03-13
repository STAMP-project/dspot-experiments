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


import RootBeanDefinition.SCOPE_PROTOTYPE;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 */
@SuppressWarnings("resource")
public class AsyncExecutionTests {
    private static String originalThreadName;

    private static int listenerCalled = 0;

    private static int listenerConstructed = 0;

    @Test
    public void asyncMethods() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncMethodBean.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.refresh();
        AsyncExecutionTests.AsyncMethodBean asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncMethodBean.class);
        asyncTest.doNothing(5);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
        ListenableFuture<String> listenableFuture = asyncTest.returnSomethingListenable(20);
        Assert.assertEquals("20", listenableFuture.get());
        CompletableFuture<String> completableFuture = asyncTest.returnSomethingCompletable(20);
        Assert.assertEquals("20", completableFuture.get());
        try {
            asyncTest.returnSomething(0).get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
        try {
            asyncTest.returnSomething((-1)).get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IOException));
        }
        try {
            asyncTest.returnSomethingListenable(0).get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
        try {
            asyncTest.returnSomethingListenable((-1)).get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IOException));
        }
        try {
            asyncTest.returnSomethingCompletable(0).get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void asyncMethodsThroughInterface() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.SimpleAsyncMethodBean.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.refresh();
        AsyncExecutionTests.SimpleInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.SimpleInterface.class);
        asyncTest.doNothing(5);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void asyncMethodsWithQualifier() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncMethodWithQualifierBean.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.registerBeanDefinition("e0", new RootBeanDefinition(ThreadPoolTaskExecutor.class));
        context.registerBeanDefinition("e1", new RootBeanDefinition(ThreadPoolTaskExecutor.class));
        context.registerBeanDefinition("e2", new RootBeanDefinition(ThreadPoolTaskExecutor.class));
        context.refresh();
        AsyncExecutionTests.AsyncMethodWithQualifierBean asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncMethodWithQualifierBean.class);
        asyncTest.doNothing(5);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
        Future<String> future2 = asyncTest.returnSomething2(30);
        Assert.assertEquals("30", future2.get());
    }

    @Test
    public void asyncMethodsWithQualifierThroughInterface() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.SimpleAsyncMethodWithQualifierBean.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.registerBeanDefinition("e0", new RootBeanDefinition(ThreadPoolTaskExecutor.class));
        context.registerBeanDefinition("e1", new RootBeanDefinition(ThreadPoolTaskExecutor.class));
        context.registerBeanDefinition("e2", new RootBeanDefinition(ThreadPoolTaskExecutor.class));
        context.refresh();
        AsyncExecutionTests.SimpleInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.SimpleInterface.class);
        asyncTest.doNothing(5);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
        Future<String> future2 = asyncTest.returnSomething2(30);
        Assert.assertEquals("30", future2.get());
    }

    @Test
    public void asyncClass() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncClassBean.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.refresh();
        AsyncExecutionTests.AsyncClassBean asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncClassBean.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
        ListenableFuture<String> listenableFuture = asyncTest.returnSomethingListenable(20);
        Assert.assertEquals("20", listenableFuture.get());
        CompletableFuture<String> completableFuture = asyncTest.returnSomethingCompletable(20);
        Assert.assertEquals("20", completableFuture.get());
        try {
            asyncTest.returnSomething(0).get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
        try {
            asyncTest.returnSomethingListenable(0).get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
        try {
            asyncTest.returnSomethingCompletable(0).get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void asyncClassWithPostProcessor() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncClassBean.class));
        context.registerBeanDefinition("asyncProcessor", new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        context.refresh();
        AsyncExecutionTests.AsyncClassBean asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncClassBean.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void asyncClassWithInterface() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncClassBeanWithInterface.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.refresh();
        AsyncExecutionTests.RegularInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.RegularInterface.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void asyncClassWithInterfaceAndPostProcessor() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncClassBeanWithInterface.class));
        context.registerBeanDefinition("asyncProcessor", new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        context.refresh();
        AsyncExecutionTests.RegularInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.RegularInterface.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void asyncInterface() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncInterfaceBean.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.refresh();
        AsyncExecutionTests.AsyncInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncInterface.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void asyncInterfaceWithPostProcessor() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncInterfaceBean.class));
        context.registerBeanDefinition("asyncProcessor", new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        context.refresh();
        AsyncExecutionTests.AsyncInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncInterface.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void dynamicAsyncInterface() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.DynamicAsyncInterfaceBean.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.refresh();
        AsyncExecutionTests.AsyncInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncInterface.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void dynamicAsyncInterfaceWithPostProcessor() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.DynamicAsyncInterfaceBean.class));
        context.registerBeanDefinition("asyncProcessor", new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        context.refresh();
        AsyncExecutionTests.AsyncInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncInterface.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void asyncMethodsInInterface() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncMethodsInterfaceBean.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        context.refresh();
        AsyncExecutionTests.AsyncMethodsInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncMethodsInterface.class);
        asyncTest.doNothing(5);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void asyncMethodsInInterfaceWithPostProcessor() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncMethodsInterfaceBean.class));
        context.registerBeanDefinition("asyncProcessor", new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        context.refresh();
        AsyncExecutionTests.AsyncMethodsInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncMethodsInterface.class);
        asyncTest.doNothing(5);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void dynamicAsyncMethodsInInterfaceWithPostProcessor() throws Exception {
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.DynamicAsyncMethodsInterfaceBean.class));
        context.registerBeanDefinition("asyncProcessor", new RootBeanDefinition(AsyncAnnotationBeanPostProcessor.class));
        context.refresh();
        AsyncExecutionTests.AsyncMethodsInterface asyncTest = context.getBean("asyncTest", AsyncExecutionTests.AsyncMethodsInterface.class);
        asyncTest.doSomething(10);
        Future<String> future = asyncTest.returnSomething(20);
        Assert.assertEquals("20", future.get());
    }

    @Test
    public void asyncMethodListener() throws Exception {
        // Arrange
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        AsyncExecutionTests.listenerCalled = 0;
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncMethodListener.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        // Act
        context.refresh();
        // Assert
        Awaitility.await().atMost(1, TimeUnit.SECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> (AsyncExecutionTests.listenerCalled) == 1);
        context.close();
    }

    @Test
    public void asyncClassListener() throws Exception {
        // Arrange
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        AsyncExecutionTests.listenerCalled = 0;
        AsyncExecutionTests.listenerConstructed = 0;
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("asyncTest", new RootBeanDefinition(AsyncExecutionTests.AsyncClassListener.class));
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        // Act
        context.refresh();
        context.close();
        // Assert
        Awaitility.await().atMost(1, TimeUnit.SECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> (AsyncExecutionTests.listenerCalled) == 2);
        Assert.assertEquals(1, AsyncExecutionTests.listenerConstructed);
    }

    @Test
    public void asyncPrototypeClassListener() throws Exception {
        // Arrange
        AsyncExecutionTests.originalThreadName = Thread.currentThread().getName();
        AsyncExecutionTests.listenerCalled = 0;
        AsyncExecutionTests.listenerConstructed = 0;
        GenericApplicationContext context = new GenericApplicationContext();
        RootBeanDefinition listenerDef = new RootBeanDefinition(AsyncExecutionTests.AsyncClassListener.class);
        listenerDef.setScope(SCOPE_PROTOTYPE);
        context.registerBeanDefinition("asyncTest", listenerDef);
        context.registerBeanDefinition("autoProxyCreator", new RootBeanDefinition(DefaultAdvisorAutoProxyCreator.class));
        context.registerBeanDefinition("asyncAdvisor", new RootBeanDefinition(AsyncAnnotationAdvisor.class));
        // Act
        context.refresh();
        context.close();
        // Assert
        Awaitility.await().atMost(1, TimeUnit.SECONDS).pollInterval(10, TimeUnit.MILLISECONDS).until(() -> (AsyncExecutionTests.listenerCalled) == 2);
        Assert.assertEquals(2, AsyncExecutionTests.listenerConstructed);
    }

    public interface SimpleInterface {
        void doNothing(int i);

        void doSomething(int i);

        Future<String> returnSomething(int i);

        Future<String> returnSomething2(int i);
    }

    public static class AsyncMethodBean {
        public void doNothing(int i) {
            Assert.assertTrue(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName));
        }

        @Async
        public void doSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
        }

        @Async
        public Future<String> returnSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            if (i == 0) {
                throw new IllegalArgumentException();
            } else
                if (i < 0) {
                    return AsyncResult.forExecutionException(new IOException());
                }

            return AsyncResult.forValue(Integer.toString(i));
        }

        @Async
        public ListenableFuture<String> returnSomethingListenable(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            if (i == 0) {
                throw new IllegalArgumentException();
            } else
                if (i < 0) {
                    return AsyncResult.forExecutionException(new IOException());
                }

            return new AsyncResult(Integer.toString(i));
        }

        @Async
        public CompletableFuture<String> returnSomethingCompletable(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            if (i == 0) {
                throw new IllegalArgumentException();
            }
            return CompletableFuture.completedFuture(Integer.toString(i));
        }
    }

    public static class SimpleAsyncMethodBean extends AsyncExecutionTests.AsyncMethodBean implements AsyncExecutionTests.SimpleInterface {
        @Override
        public Future<String> returnSomething2(int i) {
            throw new UnsupportedOperationException();
        }
    }

    @Async("e0")
    public static class AsyncMethodWithQualifierBean {
        public void doNothing(int i) {
            Assert.assertTrue(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName));
        }

        @Async("e1")
        public void doSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            Assert.assertTrue(Thread.currentThread().getName().startsWith("e1-"));
        }

        @AsyncExecutionTests.MyAsync
        public Future<String> returnSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            Assert.assertTrue(Thread.currentThread().getName().startsWith("e2-"));
            return new AsyncResult(Integer.toString(i));
        }

        public Future<String> returnSomething2(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            Assert.assertTrue(Thread.currentThread().getName().startsWith("e0-"));
            return new AsyncResult(Integer.toString(i));
        }
    }

    public static class SimpleAsyncMethodWithQualifierBean extends AsyncExecutionTests.AsyncMethodWithQualifierBean implements AsyncExecutionTests.SimpleInterface {}

    @Async("e2")
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyAsync {}

    @Async
    @SuppressWarnings("serial")
    public static class AsyncClassBean implements Serializable , DisposableBean {
        public void doSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
        }

        public Future<String> returnSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            if (i == 0) {
                throw new IllegalArgumentException();
            }
            return new AsyncResult(Integer.toString(i));
        }

        public ListenableFuture<String> returnSomethingListenable(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            if (i == 0) {
                throw new IllegalArgumentException();
            }
            return new AsyncResult(Integer.toString(i));
        }

        @Async
        public CompletableFuture<String> returnSomethingCompletable(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            if (i == 0) {
                throw new IllegalArgumentException();
            }
            return CompletableFuture.completedFuture(Integer.toString(i));
        }

        @Override
        public void destroy() {
        }
    }

    public interface RegularInterface {
        void doSomething(int i);

        Future<String> returnSomething(int i);
    }

    @Async
    public static class AsyncClassBeanWithInterface implements AsyncExecutionTests.RegularInterface {
        public void doSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
        }

        public Future<String> returnSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            return new AsyncResult(Integer.toString(i));
        }
    }

    @Async
    public interface AsyncInterface {
        void doSomething(int i);

        Future<String> returnSomething(int i);
    }

    public static class AsyncInterfaceBean implements AsyncExecutionTests.AsyncInterface {
        @Override
        public void doSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
        }

        @Override
        public Future<String> returnSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            return new AsyncResult(Integer.toString(i));
        }
    }

    public static class DynamicAsyncInterfaceBean implements FactoryBean<AsyncExecutionTests.AsyncInterface> {
        private final AsyncExecutionTests.AsyncInterface proxy;

        public DynamicAsyncInterfaceBean() {
            ProxyFactory pf = new ProxyFactory(new HashMap());
            DefaultIntroductionAdvisor advisor = new DefaultIntroductionAdvisor(new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
                    if (Future.class.equals(invocation.getMethod().getReturnType())) {
                        return new AsyncResult(invocation.getArguments()[0].toString());
                    }
                    return null;
                }
            });
            advisor.addInterface(AsyncExecutionTests.AsyncInterface.class);
            pf.addAdvisor(advisor);
            this.proxy = ((AsyncExecutionTests.AsyncInterface) (pf.getProxy()));
        }

        @Override
        public AsyncExecutionTests.AsyncInterface getObject() {
            return this.proxy;
        }

        @Override
        public Class<?> getObjectType() {
            return this.proxy.getClass();
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public interface AsyncMethodsInterface {
        void doNothing(int i);

        @Async
        void doSomething(int i);

        @Async
        Future<String> returnSomething(int i);
    }

    public static class AsyncMethodsInterfaceBean implements AsyncExecutionTests.AsyncMethodsInterface {
        @Override
        public void doNothing(int i) {
            Assert.assertTrue(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName));
        }

        @Override
        public void doSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
        }

        @Override
        public Future<String> returnSomething(int i) {
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
            return new AsyncResult(Integer.toString(i));
        }
    }

    public static class DynamicAsyncMethodsInterfaceBean implements FactoryBean<AsyncExecutionTests.AsyncMethodsInterface> {
        private final AsyncExecutionTests.AsyncMethodsInterface proxy;

        public DynamicAsyncMethodsInterfaceBean() {
            ProxyFactory pf = new ProxyFactory(new HashMap());
            DefaultIntroductionAdvisor advisor = new DefaultIntroductionAdvisor(new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
                    if (Future.class.equals(invocation.getMethod().getReturnType())) {
                        return new AsyncResult(invocation.getArguments()[0].toString());
                    }
                    return null;
                }
            });
            advisor.addInterface(AsyncExecutionTests.AsyncMethodsInterface.class);
            pf.addAdvisor(advisor);
            this.proxy = ((AsyncExecutionTests.AsyncMethodsInterface) (pf.getProxy()));
        }

        @Override
        public AsyncExecutionTests.AsyncMethodsInterface getObject() {
            return this.proxy;
        }

        @Override
        public Class<?> getObjectType() {
            return this.proxy.getClass();
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class AsyncMethodListener implements ApplicationListener<ApplicationEvent> {
        @Override
        @Async
        public void onApplicationEvent(ApplicationEvent event) {
            (AsyncExecutionTests.listenerCalled)++;
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
        }
    }

    @Async
    public static class AsyncClassListener implements ApplicationListener<ApplicationEvent> {
        public AsyncClassListener() {
            (AsyncExecutionTests.listenerConstructed)++;
        }

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            (AsyncExecutionTests.listenerCalled)++;
            Assert.assertTrue((!(Thread.currentThread().getName().equals(AsyncExecutionTests.originalThreadName))));
        }
    }
}

