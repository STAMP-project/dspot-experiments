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
package org.springframework.web.context.request.async;


import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.mock.web.test.MockHttpServletRequest;


/**
 * Test fixture with an {@link WebAsyncManager} with a mock AsyncWebRequest.
 *
 * @author Rossen Stoyanchev
 */
public class WebAsyncManagerTests {
    private WebAsyncManager asyncManager;

    private AsyncWebRequest asyncWebRequest;

    private MockHttpServletRequest servletRequest;

    @Test
    public void startAsyncProcessingWithoutAsyncWebRequest() throws Exception {
        WebAsyncManager manager = WebAsyncUtils.getAsyncManager(new MockHttpServletRequest());
        try {
            manager.startCallableProcessing(new WebAsyncManagerTests.StubCallable(1));
            Assert.fail("Expected exception");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("AsyncWebRequest must not be null", ex.getMessage());
        }
        try {
            manager.startDeferredResultProcessing(new DeferredResult<String>());
            Assert.fail("Expected exception");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("AsyncWebRequest must not be null", ex.getMessage());
        }
    }

    @Test
    public void isConcurrentHandlingStarted() {
        BDDMockito.given(this.asyncWebRequest.isAsyncStarted()).willReturn(false);
        Assert.assertFalse(this.asyncManager.isConcurrentHandlingStarted());
        Mockito.reset(this.asyncWebRequest);
        BDDMockito.given(this.asyncWebRequest.isAsyncStarted()).willReturn(true);
        Assert.assertTrue(this.asyncManager.isConcurrentHandlingStarted());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAsyncWebRequestAfterAsyncStarted() {
        this.asyncWebRequest.startAsync();
        this.asyncManager.setAsyncWebRequest(null);
    }

    @Test
    public void startCallableProcessing() throws Exception {
        int concurrentResult = 21;
        Callable<Object> task = new WebAsyncManagerTests.StubCallable(concurrentResult);
        CallableProcessingInterceptor interceptor = Mockito.mock(CallableProcessingInterceptor.class);
        setupDefaultAsyncScenario();
        this.asyncManager.registerCallableInterceptor("interceptor", interceptor);
        this.asyncManager.startCallableProcessing(task);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(concurrentResult, this.asyncManager.getConcurrentResult());
        verifyDefaultAsyncScenario();
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, task);
        Mockito.verify(interceptor).preProcess(this.asyncWebRequest, task);
        Mockito.verify(interceptor).postProcess(this.asyncWebRequest, task, concurrentResult);
    }

    @Test
    public void startCallableProcessingCallableException() throws Exception {
        Exception concurrentResult = new Exception();
        Callable<Object> task = new WebAsyncManagerTests.StubCallable(concurrentResult);
        CallableProcessingInterceptor interceptor = Mockito.mock(CallableProcessingInterceptor.class);
        setupDefaultAsyncScenario();
        this.asyncManager.registerCallableInterceptor("interceptor", interceptor);
        this.asyncManager.startCallableProcessing(task);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(concurrentResult, this.asyncManager.getConcurrentResult());
        verifyDefaultAsyncScenario();
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, task);
        Mockito.verify(interceptor).preProcess(this.asyncWebRequest, task);
        Mockito.verify(interceptor).postProcess(this.asyncWebRequest, task, concurrentResult);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startCallableProcessingBeforeConcurrentHandlingException() throws Exception {
        Callable<Object> task = new WebAsyncManagerTests.StubCallable(21);
        Exception exception = new Exception();
        CallableProcessingInterceptor interceptor = Mockito.mock(CallableProcessingInterceptor.class);
        BDDMockito.willThrow(exception).given(interceptor).beforeConcurrentHandling(this.asyncWebRequest, task);
        this.asyncManager.registerCallableInterceptor("interceptor", interceptor);
        try {
            this.asyncManager.startCallableProcessing(task);
            Assert.fail("Expected Exception");
        } catch (Exception ex) {
            Assert.assertEquals(exception, ex);
        }
        Assert.assertFalse(this.asyncManager.hasConcurrentResult());
        Mockito.verify(this.asyncWebRequest).addTimeoutHandler(ArgumentMatchers.notNull());
        Mockito.verify(this.asyncWebRequest).addErrorHandler(ArgumentMatchers.notNull());
        Mockito.verify(this.asyncWebRequest).addCompletionHandler(ArgumentMatchers.notNull());
    }

    @Test
    public void startCallableProcessingPreProcessException() throws Exception {
        Callable<Object> task = new WebAsyncManagerTests.StubCallable(21);
        Exception exception = new Exception();
        CallableProcessingInterceptor interceptor = Mockito.mock(CallableProcessingInterceptor.class);
        BDDMockito.willThrow(exception).given(interceptor).preProcess(this.asyncWebRequest, task);
        setupDefaultAsyncScenario();
        this.asyncManager.registerCallableInterceptor("interceptor", interceptor);
        this.asyncManager.startCallableProcessing(task);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(exception, this.asyncManager.getConcurrentResult());
        verifyDefaultAsyncScenario();
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, task);
    }

    @Test
    public void startCallableProcessingPostProcessException() throws Exception {
        Callable<Object> task = new WebAsyncManagerTests.StubCallable(21);
        Exception exception = new Exception();
        CallableProcessingInterceptor interceptor = Mockito.mock(CallableProcessingInterceptor.class);
        BDDMockito.willThrow(exception).given(interceptor).postProcess(this.asyncWebRequest, task, 21);
        setupDefaultAsyncScenario();
        this.asyncManager.registerCallableInterceptor("interceptor", interceptor);
        this.asyncManager.startCallableProcessing(task);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(exception, this.asyncManager.getConcurrentResult());
        verifyDefaultAsyncScenario();
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, task);
        Mockito.verify(interceptor).preProcess(this.asyncWebRequest, task);
    }

    @Test
    public void startCallableProcessingPostProcessContinueAfterException() throws Exception {
        Callable<Object> task = new WebAsyncManagerTests.StubCallable(21);
        Exception exception = new Exception();
        CallableProcessingInterceptor interceptor1 = Mockito.mock(CallableProcessingInterceptor.class);
        CallableProcessingInterceptor interceptor2 = Mockito.mock(CallableProcessingInterceptor.class);
        BDDMockito.willThrow(exception).given(interceptor2).postProcess(this.asyncWebRequest, task, 21);
        setupDefaultAsyncScenario();
        this.asyncManager.registerCallableInterceptors(interceptor1, interceptor2);
        this.asyncManager.startCallableProcessing(task);
        Assert.assertTrue(this.asyncManager.hasConcurrentResult());
        Assert.assertEquals(exception, this.asyncManager.getConcurrentResult());
        verifyDefaultAsyncScenario();
        Mockito.verify(interceptor1).beforeConcurrentHandling(this.asyncWebRequest, task);
        Mockito.verify(interceptor1).preProcess(this.asyncWebRequest, task);
        Mockito.verify(interceptor1).postProcess(this.asyncWebRequest, task, 21);
        Mockito.verify(interceptor2).beforeConcurrentHandling(this.asyncWebRequest, task);
        Mockito.verify(interceptor2).preProcess(this.asyncWebRequest, task);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startCallableProcessingWithAsyncTask() throws Exception {
        AsyncTaskExecutor executor = Mockito.mock(AsyncTaskExecutor.class);
        BDDMockito.given(this.asyncWebRequest.getNativeRequest(HttpServletRequest.class)).willReturn(this.servletRequest);
        WebAsyncTask<Object> asyncTask = new WebAsyncTask(1000L, executor, Mockito.mock(Callable.class));
        this.asyncManager.startCallableProcessing(asyncTask);
        Mockito.verify(executor).submit(((Runnable) (ArgumentMatchers.notNull())));
        Mockito.verify(this.asyncWebRequest).setTimeout(1000L);
        Mockito.verify(this.asyncWebRequest).addTimeoutHandler(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(this.asyncWebRequest).addErrorHandler(ArgumentMatchers.any(Consumer.class));
        Mockito.verify(this.asyncWebRequest).addCompletionHandler(ArgumentMatchers.any(Runnable.class));
        Mockito.verify(this.asyncWebRequest).startAsync();
    }

    @Test
    public void startCallableProcessingNullInput() throws Exception {
        try {
            this.asyncManager.startCallableProcessing(((Callable<?>) (null)));
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Callable must not be null", ex.getMessage());
        }
    }

    @Test
    public void startDeferredResultProcessing() throws Exception {
        DeferredResult<String> deferredResult = new DeferredResult(1000L);
        String concurrentResult = "abc";
        DeferredResultProcessingInterceptor interceptor = Mockito.mock(DeferredResultProcessingInterceptor.class);
        setupDefaultAsyncScenario();
        this.asyncManager.registerDeferredResultInterceptor("interceptor", interceptor);
        this.asyncManager.startDeferredResultProcessing(deferredResult);
        deferredResult.setResult(concurrentResult);
        Assert.assertEquals(concurrentResult, this.asyncManager.getConcurrentResult());
        verifyDefaultAsyncScenario();
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, deferredResult);
        Mockito.verify(interceptor).preProcess(this.asyncWebRequest, deferredResult);
        Mockito.verify(interceptor).postProcess(asyncWebRequest, deferredResult, concurrentResult);
        Mockito.verify(this.asyncWebRequest).setTimeout(1000L);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startDeferredResultProcessingBeforeConcurrentHandlingException() throws Exception {
        DeferredResult<Integer> deferredResult = new DeferredResult();
        Exception exception = new Exception();
        DeferredResultProcessingInterceptor interceptor = Mockito.mock(DeferredResultProcessingInterceptor.class);
        BDDMockito.willThrow(exception).given(interceptor).beforeConcurrentHandling(this.asyncWebRequest, deferredResult);
        this.asyncManager.registerDeferredResultInterceptor("interceptor", interceptor);
        try {
            this.asyncManager.startDeferredResultProcessing(deferredResult);
            Assert.fail("Expected Exception");
        } catch (Exception success) {
            Assert.assertEquals(exception, success);
        }
        Assert.assertFalse(this.asyncManager.hasConcurrentResult());
        Mockito.verify(this.asyncWebRequest).addTimeoutHandler(ArgumentMatchers.notNull());
        Mockito.verify(this.asyncWebRequest).addErrorHandler(ArgumentMatchers.notNull());
        Mockito.verify(this.asyncWebRequest).addCompletionHandler(ArgumentMatchers.notNull());
    }

    @Test
    public void startDeferredResultProcessingPreProcessException() throws Exception {
        DeferredResult<Integer> deferredResult = new DeferredResult();
        Exception exception = new Exception();
        DeferredResultProcessingInterceptor interceptor = Mockito.mock(DeferredResultProcessingInterceptor.class);
        BDDMockito.willThrow(exception).given(interceptor).preProcess(this.asyncWebRequest, deferredResult);
        setupDefaultAsyncScenario();
        this.asyncManager.registerDeferredResultInterceptor("interceptor", interceptor);
        this.asyncManager.startDeferredResultProcessing(deferredResult);
        deferredResult.setResult(25);
        Assert.assertEquals(exception, this.asyncManager.getConcurrentResult());
        verifyDefaultAsyncScenario();
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, deferredResult);
    }

    @Test
    public void startDeferredResultProcessingPostProcessException() throws Exception {
        DeferredResult<Integer> deferredResult = new DeferredResult();
        Exception exception = new Exception();
        DeferredResultProcessingInterceptor interceptor = Mockito.mock(DeferredResultProcessingInterceptor.class);
        BDDMockito.willThrow(exception).given(interceptor).postProcess(this.asyncWebRequest, deferredResult, 25);
        setupDefaultAsyncScenario();
        this.asyncManager.registerDeferredResultInterceptor("interceptor", interceptor);
        this.asyncManager.startDeferredResultProcessing(deferredResult);
        deferredResult.setResult(25);
        Assert.assertEquals(exception, this.asyncManager.getConcurrentResult());
        verifyDefaultAsyncScenario();
        Mockito.verify(interceptor).beforeConcurrentHandling(this.asyncWebRequest, deferredResult);
        Mockito.verify(interceptor).preProcess(this.asyncWebRequest, deferredResult);
    }

    @Test
    public void startDeferredResultProcessingNullInput() throws Exception {
        try {
            this.asyncManager.startDeferredResultProcessing(null);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("DeferredResult must not be null", ex.getMessage());
        }
    }

    private final class StubCallable implements Callable<Object> {
        private Object value;

        public StubCallable(Object value) {
            this.value = value;
        }

        @Override
        public Object call() throws Exception {
            if ((this.value) instanceof Exception) {
                throw ((Exception) (this.value));
            }
            return this.value;
        }
    }

    @SuppressWarnings("serial")
    private static class SyncTaskExecutor extends SimpleAsyncTaskExecutor {
        @Override
        public void execute(Runnable task, long startTimeout) {
            task.run();
        }
    }
}

