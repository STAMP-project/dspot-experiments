/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.endpoint.invoker.cache;


import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.InvocationContext;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvoker;


/**
 * Tests for {@link CachingOperationInvoker}.
 *
 * @author Stephane Nicoll
 */
public class CachingOperationInvokerTests {
    @Test
    public void createInstanceWithTtlSetToZero() {
        assertThatIllegalArgumentException().isThrownBy(() -> new CachingOperationInvoker(mock(.class), 0)).withMessageContaining("TimeToLive");
    }

    @Test
    public void cacheInTtlRangeWithNoParameter() {
        assertCacheIsUsed(Collections.emptyMap());
    }

    @Test
    public void cacheInTtlWithNullParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("first", null);
        parameters.put("second", null);
        assertCacheIsUsed(parameters);
    }

    @Test
    public void targetAlwaysInvokedWithParameters() {
        OperationInvoker target = Mockito.mock(OperationInvoker.class);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", "value");
        parameters.put("something", null);
        InvocationContext context = new InvocationContext(Mockito.mock(SecurityContext.class), parameters);
        BDDMockito.given(target.invoke(context)).willReturn(new Object());
        CachingOperationInvoker invoker = new CachingOperationInvoker(target, 500L);
        invoker.invoke(context);
        invoker.invoke(context);
        invoker.invoke(context);
        Mockito.verify(target, Mockito.times(3)).invoke(context);
    }

    @Test
    public void targetAlwaysInvokedWithPrincipal() {
        OperationInvoker target = Mockito.mock(OperationInvoker.class);
        Map<String, Object> parameters = new HashMap<>();
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        BDDMockito.given(securityContext.getPrincipal()).willReturn(Mockito.mock(Principal.class));
        InvocationContext context = new InvocationContext(securityContext, parameters);
        BDDMockito.given(target.invoke(context)).willReturn(new Object());
        CachingOperationInvoker invoker = new CachingOperationInvoker(target, 500L);
        invoker.invoke(context);
        invoker.invoke(context);
        invoker.invoke(context);
        Mockito.verify(target, Mockito.times(3)).invoke(context);
    }

    @Test
    public void targetInvokedWhenCacheExpires() throws InterruptedException {
        OperationInvoker target = Mockito.mock(OperationInvoker.class);
        Map<String, Object> parameters = new HashMap<>();
        InvocationContext context = new InvocationContext(Mockito.mock(SecurityContext.class), parameters);
        BDDMockito.given(target.invoke(context)).willReturn(new Object());
        CachingOperationInvoker invoker = new CachingOperationInvoker(target, 50L);
        invoker.invoke(context);
        Thread.sleep(55);
        invoker.invoke(context);
        Mockito.verify(target, Mockito.times(2)).invoke(context);
    }
}

