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


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 *
 *
 * @author Rob Winch
 * @since 3.2
 */
@RunWith(MockitoJUnitRunner.class)
public class DelegatingSecurityContextCallableTests {
    @Mock
    private Callable<Object> delegate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Object callableResult;

    private Callable<Object> callable;

    private ExecutorService executor;

    private SecurityContext originalSecurityContext;

    // --- constructor ---
    @Test(expected = IllegalArgumentException.class)
    public void constructorNullDelegate() {
        new DelegatingSecurityContextCallable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullDelegateNonNullSecurityContext() {
        new DelegatingSecurityContextCallable(null, securityContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullDelegateAndSecurityContext() {
        new DelegatingSecurityContextCallable(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullSecurityContext() {
        new DelegatingSecurityContextCallable(delegate, null);
    }

    // --- call ---
    @Test
    public void call() throws Exception {
        callable = new DelegatingSecurityContextCallable(delegate, securityContext);
        assertWrapped(callable);
    }

    @Test
    public void callDefaultSecurityContext() throws Exception {
        SecurityContextHolder.setContext(securityContext);
        callable = new DelegatingSecurityContextCallable(delegate);
        SecurityContextHolder.clearContext();// ensure callable is what sets up the

        // SecurityContextHolder
        assertWrapped(callable);
    }

    // SEC-3031
    @Test
    public void callOnSameThread() throws Exception {
        originalSecurityContext = securityContext;
        SecurityContextHolder.setContext(originalSecurityContext);
        callable = new DelegatingSecurityContextCallable(delegate, securityContext);
        assertWrapped(callable.call());
    }

    // --- create ---
    @Test(expected = IllegalArgumentException.class)
    public void createNullDelegate() {
        DelegatingSecurityContextCallable.create(null, securityContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullDelegateAndSecurityContext() {
        DelegatingSecurityContextRunnable.create(null, null);
    }

    @Test
    public void createNullSecurityContext() throws Exception {
        SecurityContextHolder.setContext(securityContext);
        callable = DelegatingSecurityContextCallable.create(delegate, null);
        SecurityContextHolder.clearContext();// ensure callable is what sets up the

        // SecurityContextHolder
        assertWrapped(callable);
    }

    @Test
    public void create() throws Exception {
        callable = DelegatingSecurityContextCallable.create(delegate, securityContext);
        assertWrapped(callable);
    }

    // --- toString
    // SEC-2682
    @Test
    public void toStringDelegates() {
        callable = new DelegatingSecurityContextCallable(delegate, securityContext);
        assertThat(callable.toString()).isEqualTo(delegate.toString());
    }
}

