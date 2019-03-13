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
package org.springframework.security.concurrent;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Abstract class for testing {@link DelegatingSecurityContextExecutorService} which
 * allows customization of how {@link DelegatingSecurityContextExecutorService} and its
 * mocks are created.
 *
 * @author Rob Winch
 * @since 3.2
 * @see CurrentDelegatingSecurityContextExecutorServiceTests
 * @see ExplicitDelegatingSecurityContextExecutorServiceTests
 */
public abstract class AbstractDelegatingSecurityContextExecutorServiceTests extends AbstractDelegatingSecurityContextExecutorTests {
    @Mock
    private Future<Object> expectedFutureObject;

    @Mock
    private Object resultArg;

    protected DelegatingSecurityContextExecutorService executor;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullDelegate() {
        new DelegatingSecurityContextExecutorService(null);
    }

    @Test
    public void shutdown() {
        executor.shutdown();
        Mockito.verify(delegate).shutdown();
    }

    @Test
    public void shutdownNow() {
        List<Runnable> result = executor.shutdownNow();
        Mockito.verify(delegate).shutdownNow();
        assertThat(result).isEqualTo(delegate.shutdownNow()).isNotNull();
    }

    @Test
    public void isShutdown() {
        boolean result = executor.isShutdown();
        Mockito.verify(delegate).isShutdown();
        assertThat(result).isEqualTo(delegate.isShutdown()).isNotNull();
    }

    @Test
    public void isTerminated() {
        boolean result = executor.isTerminated();
        Mockito.verify(delegate).isTerminated();
        assertThat(result).isEqualTo(delegate.isTerminated()).isNotNull();
    }

    @Test
    public void awaitTermination() throws InterruptedException {
        boolean result = executor.awaitTermination(1, TimeUnit.SECONDS);
        Mockito.verify(delegate).awaitTermination(1, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(delegate.awaitTermination(1, TimeUnit.SECONDS)).isNotNull();
    }

    @Test
    public void submitCallable() throws Exception {
        Mockito.when(delegate.submit(wrappedCallable)).thenReturn(expectedFutureObject);
        Future<Object> result = executor.submit(callable);
        Mockito.verify(delegate).submit(wrappedCallable);
        assertThat(result).isEqualTo(expectedFutureObject);
    }

    @Test
    public void submitRunnableWithResult() throws Exception {
        Mockito.when(delegate.submit(wrappedRunnable, resultArg)).thenReturn(expectedFutureObject);
        Future<Object> result = executor.submit(runnable, resultArg);
        Mockito.verify(delegate).submit(wrappedRunnable, resultArg);
        assertThat(result).isEqualTo(expectedFutureObject);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void submitRunnable() throws Exception {
        Mockito.when(((Future<Object>) (delegate.submit(wrappedRunnable)))).thenReturn(expectedFutureObject);
        Future<?> result = executor.submit(runnable);
        Mockito.verify(delegate).submit(wrappedRunnable);
        assertThat(result).isEqualTo(expectedFutureObject);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invokeAll() throws Exception {
        List<Future<Object>> exectedResult = Arrays.asList(expectedFutureObject);
        List<Callable<Object>> wrappedCallables = Arrays.asList(wrappedCallable);
        Mockito.when(delegate.invokeAll(wrappedCallables)).thenReturn(exectedResult);
        List<Future<Object>> result = executor.invokeAll(Arrays.asList(callable));
        Mockito.verify(delegate).invokeAll(wrappedCallables);
        assertThat(result).isEqualTo(exectedResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invokeAllTimeout() throws Exception {
        List<Future<Object>> exectedResult = Arrays.asList(expectedFutureObject);
        List<Callable<Object>> wrappedCallables = Arrays.asList(wrappedCallable);
        Mockito.when(delegate.invokeAll(wrappedCallables, 1, TimeUnit.SECONDS)).thenReturn(exectedResult);
        List<Future<Object>> result = executor.invokeAll(Arrays.asList(callable), 1, TimeUnit.SECONDS);
        Mockito.verify(delegate).invokeAll(wrappedCallables, 1, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(exectedResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invokeAny() throws Exception {
        List<Future<Object>> exectedResult = Arrays.asList(expectedFutureObject);
        List<Callable<Object>> wrappedCallables = Arrays.asList(wrappedCallable);
        Mockito.when(delegate.invokeAny(wrappedCallables)).thenReturn(exectedResult);
        Object result = executor.invokeAny(Arrays.asList(callable));
        Mockito.verify(delegate).invokeAny(wrappedCallables);
        assertThat(result).isEqualTo(exectedResult);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void invokeAnyTimeout() throws Exception {
        List<Future<Object>> exectedResult = Arrays.asList(expectedFutureObject);
        List<Callable<Object>> wrappedCallables = Arrays.asList(wrappedCallable);
        Mockito.when(delegate.invokeAny(wrappedCallables, 1, TimeUnit.SECONDS)).thenReturn(exectedResult);
        Object result = executor.invokeAny(Arrays.asList(callable), 1, TimeUnit.SECONDS);
        Mockito.verify(delegate).invokeAny(wrappedCallables, 1, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(exectedResult);
    }
}

