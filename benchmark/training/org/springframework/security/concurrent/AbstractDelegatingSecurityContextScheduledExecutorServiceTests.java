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


import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Abstract class for testing {@link DelegatingSecurityContextScheduledExecutorService}
 * which allows customization of how
 * {@link DelegatingSecurityContextScheduledExecutorService} and its mocks are created.
 *
 * @author Rob Winch
 * @since 3.2
 * @see CurrentDelegatingSecurityContextScheduledExecutorServiceTests
 * @see ExplicitDelegatingSecurityContextScheduledExecutorServiceTests
 */
public abstract class AbstractDelegatingSecurityContextScheduledExecutorServiceTests extends AbstractDelegatingSecurityContextExecutorServiceTests {
    @Mock
    private ScheduledFuture<Object> expectedResult;

    private DelegatingSecurityContextScheduledExecutorService executor;

    @Test
    @SuppressWarnings("unchecked")
    public void scheduleRunnable() {
        Mockito.when(((ScheduledFuture<Object>) (delegate.schedule(wrappedRunnable, 1, TimeUnit.SECONDS)))).thenReturn(expectedResult);
        ScheduledFuture<?> result = executor.schedule(runnable, 1, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(expectedResult);
        Mockito.verify(delegate).schedule(wrappedRunnable, 1, TimeUnit.SECONDS);
    }

    @Test
    public void scheduleCallable() {
        Mockito.when(((ScheduledFuture<Object>) (delegate.schedule(wrappedCallable, 1, TimeUnit.SECONDS)))).thenReturn(expectedResult);
        ScheduledFuture<Object> result = executor.schedule(callable, 1, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(expectedResult);
        Mockito.verify(delegate).schedule(wrappedCallable, 1, TimeUnit.SECONDS);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void scheduleAtFixedRate() {
        Mockito.when(((ScheduledFuture<Object>) (delegate.scheduleAtFixedRate(wrappedRunnable, 1, 2, TimeUnit.SECONDS)))).thenReturn(expectedResult);
        ScheduledFuture<?> result = executor.scheduleAtFixedRate(runnable, 1, 2, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(expectedResult);
        Mockito.verify(delegate).scheduleAtFixedRate(wrappedRunnable, 1, 2, TimeUnit.SECONDS);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void scheduleWithFixedDelay() {
        Mockito.when(((ScheduledFuture<Object>) (delegate.scheduleWithFixedDelay(wrappedRunnable, 1, 2, TimeUnit.SECONDS)))).thenReturn(expectedResult);
        ScheduledFuture<?> result = executor.scheduleWithFixedDelay(runnable, 1, 2, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(expectedResult);
        Mockito.verify(delegate).scheduleWithFixedDelay(wrappedRunnable, 1, 2, TimeUnit.SECONDS);
    }
}

