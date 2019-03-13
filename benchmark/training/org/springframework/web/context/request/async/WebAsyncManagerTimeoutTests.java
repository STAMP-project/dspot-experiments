/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.web.context.request.async;


import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.servlet.AsyncEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.mock.web.test.MockAsyncContext;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.context.request.NativeWebRequest;


/**
 * {@link WebAsyncManager} tests where container-triggered timeout/completion
 * events are simulated.
 *
 * @author Rossen Stoyanchev
 */
public class WebAsyncManagerTimeoutTests {
    private static final AsyncEvent ASYNC_EVENT = null;

    private WebAsyncManager asyncManager;

    private StandardServletAsyncWebRequest asyncWebRequest;

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    @Test
    public void startCallableProcessingTimeoutAndComplete() throws Exception {
        WebAsyncManagerTimeoutTests.StubCallable callable = new WebAsyncManagerTimeoutTests.StubCallable();
        CallableProcessingInterceptor interceptor = Mockito.mock(CallableProcessingInterceptor.class);
        BDDMockito.given(interceptor.handleTimeout(this.asyncWebRequest, callable)).willReturn(CallableProcessingInterceptor.RESULT_NONE);
        this.asyncManager.registerCallableInterceptor("interceptor", interceptor);
        this.asyncManager.startCallableProcessing(callable);
        this.asyncWebRequest.onTimeout(WebAsyncManagerTimeoutTests.ASYNC_EVENT);
        this.asyncWebRequest.onComplete(WebAsyncManagerTimeoutTests.ASYNC_EVENT);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(AsyncRequestTimeoutException.class, this.asyncManager.getConcurrentResult().getClass());
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, callable);
        Mockito.verify(interceptor).afterCompletion(this.asyncWebRequest, callable);
    }

    @Test
    public void startCallableProcessingTimeoutAndResumeThroughCallback() throws Exception {
        WebAsyncManagerTimeoutTests.StubCallable callable = new WebAsyncManagerTimeoutTests.StubCallable();
        WebAsyncTask<Object> webAsyncTask = new WebAsyncTask(callable);
        webAsyncTask.onTimeout(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 7;
            }
        });
        this.asyncManager.startCallableProcessing(webAsyncTask);
        this.asyncWebRequest.onTimeout(WebAsyncManagerTimeoutTests.ASYNC_EVENT);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(7, this.asyncManager.getConcurrentResult());
        Assert.assertEquals("/test", ((MockAsyncContext) (this.servletRequest.getAsyncContext())).getDispatchedPath());
    }

    @Test
    public void startCallableProcessingTimeoutAndResumeThroughInterceptor() throws Exception {
        WebAsyncManagerTimeoutTests.StubCallable callable = new WebAsyncManagerTimeoutTests.StubCallable();
        CallableProcessingInterceptor interceptor = Mockito.mock(CallableProcessingInterceptor.class);
        BDDMockito.given(interceptor.handleTimeout(this.asyncWebRequest, callable)).willReturn(22);
        this.asyncManager.registerCallableInterceptor("timeoutInterceptor", interceptor);
        this.asyncManager.startCallableProcessing(callable);
        this.asyncWebRequest.onTimeout(WebAsyncManagerTimeoutTests.ASYNC_EVENT);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(22, this.asyncManager.getConcurrentResult());
        Assert.assertEquals("/test", ((MockAsyncContext) (this.servletRequest.getAsyncContext())).getDispatchedPath());
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, callable);
    }

    @Test
    public void startCallableProcessingAfterTimeoutException() throws Exception {
        WebAsyncManagerTimeoutTests.StubCallable callable = new WebAsyncManagerTimeoutTests.StubCallable();
        Exception exception = new Exception();
        CallableProcessingInterceptor interceptor = Mockito.mock(CallableProcessingInterceptor.class);
        BDDMockito.given(interceptor.handleTimeout(this.asyncWebRequest, callable)).willThrow(exception);
        this.asyncManager.registerCallableInterceptor("timeoutInterceptor", interceptor);
        this.asyncManager.startCallableProcessing(callable);
        this.asyncWebRequest.onTimeout(WebAsyncManagerTimeoutTests.ASYNC_EVENT);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(exception, this.asyncManager.getConcurrentResult());
        Assert.assertEquals("/test", ((MockAsyncContext) (this.servletRequest.getAsyncContext())).getDispatchedPath());
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, callable);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startCallableProcessingTimeoutAndCheckThreadInterrupted() throws Exception {
        WebAsyncManagerTimeoutTests.StubCallable callable = new WebAsyncManagerTimeoutTests.StubCallable();
        Future future = Mockito.mock(Future.class);
        AsyncTaskExecutor executor = Mockito.mock(AsyncTaskExecutor.class);
        Mockito.when(executor.submit(ArgumentMatchers.any(Runnable.class))).thenReturn(future);
        this.asyncManager.setTaskExecutor(executor);
        this.asyncManager.startCallableProcessing(callable);
        this.asyncWebRequest.onTimeout(WebAsyncManagerTimeoutTests.ASYNC_EVENT);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Mockito.verify(future).cancel(true);
        Mockito.verifyNoMoreInteractions(future);
    }

    @Test
    public void startDeferredResultProcessingTimeoutAndComplete() throws Exception {
        DeferredResult<Integer> deferredResult = new DeferredResult();
        DeferredResultProcessingInterceptor interceptor = Mockito.mock(DeferredResultProcessingInterceptor.class);
        BDDMockito.given(interceptor.handleTimeout(this.asyncWebRequest, deferredResult)).willReturn(true);
        this.asyncManager.registerDeferredResultInterceptor("interceptor", interceptor);
        this.asyncManager.startDeferredResultProcessing(deferredResult);
        this.asyncWebRequest.onTimeout(WebAsyncManagerTimeoutTests.ASYNC_EVENT);
        this.asyncWebRequest.onComplete(WebAsyncManagerTimeoutTests.ASYNC_EVENT);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(AsyncRequestTimeoutException.class, this.asyncManager.getConcurrentResult().getClass());
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, deferredResult);
        Mockito.verify(interceptor).preProcess(this.asyncWebRequest, deferredResult);
        Mockito.verify(interceptor).afterCompletion(this.asyncWebRequest, deferredResult);
    }

    @Test
    public void startDeferredResultProcessingTimeoutAndResumeWithDefaultResult() throws Exception {
        DeferredResult<Integer> deferredResult = new DeferredResult(null, 23);
        this.asyncManager.startDeferredResultProcessing(deferredResult);
        AsyncEvent event = null;
        this.asyncWebRequest.onTimeout(event);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(23, this.asyncManager.getConcurrentResult());
        Assert.assertEquals("/test", ((MockAsyncContext) (this.servletRequest.getAsyncContext())).getDispatchedPath());
    }

    @Test
    public void startDeferredResultProcessingTimeoutAndResumeThroughCallback() throws Exception {
        final DeferredResult<Integer> deferredResult = new DeferredResult();
        deferredResult.onTimeout(new Runnable() {
            @Override
            public void run() {
                deferredResult.setResult(23);
            }
        });
        this.asyncManager.startDeferredResultProcessing(deferredResult);
        AsyncEvent event = null;
        this.asyncWebRequest.onTimeout(event);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(23, this.asyncManager.getConcurrentResult());
        Assert.assertEquals("/test", ((MockAsyncContext) (this.servletRequest.getAsyncContext())).getDispatchedPath());
    }

    @Test
    public void startDeferredResultProcessingTimeoutAndResumeThroughInterceptor() throws Exception {
        DeferredResult<Integer> deferredResult = new DeferredResult();
        DeferredResultProcessingInterceptor interceptor = new DeferredResultProcessingInterceptor() {
            @Override
            public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> result) throws Exception {
                result.setErrorResult(23);
                return true;
            }
        };
        this.asyncManager.registerDeferredResultInterceptor("interceptor", interceptor);
        this.asyncManager.startDeferredResultProcessing(deferredResult);
        AsyncEvent event = null;
        this.asyncWebRequest.onTimeout(event);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(23, this.asyncManager.getConcurrentResult());
        Assert.assertEquals("/test", ((MockAsyncContext) (this.servletRequest.getAsyncContext())).getDispatchedPath());
    }

    @Test
    public void startDeferredResultProcessingAfterTimeoutException() throws Exception {
        DeferredResult<Integer> deferredResult = new DeferredResult();
        final Exception exception = new Exception();
        DeferredResultProcessingInterceptor interceptor = new DeferredResultProcessingInterceptor() {
            @Override
            public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> result) throws Exception {
                throw exception;
            }
        };
        this.asyncManager.registerDeferredResultInterceptor("interceptor", interceptor);
        this.asyncManager.startDeferredResultProcessing(deferredResult);
        AsyncEvent event = null;
        this.asyncWebRequest.onTimeout(event);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(exception, this.asyncManager.getConcurrentResult());
        Assert.assertEquals("/test", ((MockAsyncContext) (this.servletRequest.getAsyncContext())).getDispatchedPath());
    }

    private final class StubCallable implements Callable<Object> {
        @Override
        public Object call() throws Exception {
            return 21;
        }
    }
}

