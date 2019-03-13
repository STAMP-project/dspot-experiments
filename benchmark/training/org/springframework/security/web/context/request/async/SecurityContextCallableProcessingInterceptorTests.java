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
package org.springframework.security.web.context.request.async;


import java.util.concurrent.Callable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.NativeWebRequest;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityContextCallableProcessingInterceptorTests {
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Callable<?> callable;

    @Mock
    private NativeWebRequest webRequest;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNull() {
        new SecurityContextCallableProcessingInterceptor(null);
    }

    @Test
    public void currentSecurityContext() throws Exception {
        SecurityContextCallableProcessingInterceptor interceptor = new SecurityContextCallableProcessingInterceptor();
        SecurityContextHolder.setContext(securityContext);
        interceptor.beforeConcurrentHandling(webRequest, callable);
        SecurityContextHolder.clearContext();
        interceptor.preProcess(webRequest, callable);
        assertThat(SecurityContextHolder.getContext()).isSameAs(securityContext);
        interceptor.postProcess(webRequest, callable, null);
        assertThat(SecurityContextHolder.getContext()).isNotSameAs(securityContext);
    }

    @Test
    public void specificSecurityContext() throws Exception {
        SecurityContextCallableProcessingInterceptor interceptor = new SecurityContextCallableProcessingInterceptor(securityContext);
        interceptor.preProcess(webRequest, callable);
        assertThat(SecurityContextHolder.getContext()).isSameAs(securityContext);
        interceptor.postProcess(webRequest, callable, null);
        assertThat(SecurityContextHolder.getContext()).isNotSameAs(securityContext);
    }
}

