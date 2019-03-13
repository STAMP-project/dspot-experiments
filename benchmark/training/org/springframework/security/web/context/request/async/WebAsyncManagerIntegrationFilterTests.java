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
import java.util.concurrent.ThreadFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;
import org.springframework.web.context.request.async.WebAsyncManager;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class WebAsyncManagerIntegrationFilterTests {
    @Mock
    private SecurityContext securityContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AsyncWebRequest asyncWebRequest;

    private WebAsyncManager asyncManager;

    private WebAsyncManagerIntegrationFilterTests.JoinableThreadFactory threadFactory;

    private MockFilterChain filterChain;

    private WebAsyncManagerIntegrationFilter filter;

    @Test
    public void doFilterInternalRegistersSecurityContextCallableProcessor() throws Exception {
        SecurityContextHolder.setContext(securityContext);
        asyncManager.registerCallableInterceptors(new CallableProcessingInterceptorAdapter() {
            @Override
            public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
                assertThat(SecurityContextHolder.getContext()).isNotSameAs(securityContext);
            }
        });
        filter.doFilterInternal(request, response, filterChain);
        WebAsyncManagerIntegrationFilterTests.VerifyingCallable verifyingCallable = new WebAsyncManagerIntegrationFilterTests.VerifyingCallable();
        asyncManager.startCallableProcessing(verifyingCallable);
        threadFactory.join();
        assertThat(asyncManager.getConcurrentResult()).isSameAs(securityContext);
    }

    @Test
    public void doFilterInternalRegistersSecurityContextCallableProcessorContextUpdated() throws Exception {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        asyncManager.registerCallableInterceptors(new CallableProcessingInterceptorAdapter() {
            @Override
            public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
                assertThat(SecurityContextHolder.getContext()).isNotSameAs(securityContext);
            }
        });
        filter.doFilterInternal(request, response, filterChain);
        SecurityContextHolder.setContext(securityContext);
        WebAsyncManagerIntegrationFilterTests.VerifyingCallable verifyingCallable = new WebAsyncManagerIntegrationFilterTests.VerifyingCallable();
        asyncManager.startCallableProcessing(verifyingCallable);
        threadFactory.join();
        assertThat(asyncManager.getConcurrentResult()).isSameAs(securityContext);
    }

    private static final class JoinableThreadFactory implements ThreadFactory {
        private Thread t;

        public Thread newThread(Runnable r) {
            t = new Thread(r);
            return t;
        }

        public void join() throws InterruptedException {
            t.join();
        }
    }

    private class VerifyingCallable implements Callable<SecurityContext> {
        public SecurityContext call() throws Exception {
            return SecurityContextHolder.getContext();
        }
    }
}

