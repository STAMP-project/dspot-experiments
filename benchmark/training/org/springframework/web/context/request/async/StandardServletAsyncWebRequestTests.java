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


import java.util.function.Consumer;
import javax.servlet.AsyncEvent;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.test.MockAsyncContext;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * A test fixture with a {@link StandardServletAsyncWebRequest}.
 *
 * @author Rossen Stoyanchev
 */
public class StandardServletAsyncWebRequestTests {
    private StandardServletAsyncWebRequest asyncRequest;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test
    public void isAsyncStarted() throws Exception {
        Assert.assertFalse(this.asyncRequest.isAsyncStarted());
        this.asyncRequest.startAsync();
        Assert.assertTrue(this.asyncRequest.isAsyncStarted());
    }

    @Test
    public void startAsync() throws Exception {
        this.asyncRequest.startAsync();
        MockAsyncContext context = ((MockAsyncContext) (this.request.getAsyncContext()));
        Assert.assertNotNull(context);
        Assert.assertEquals("Timeout value not set", (44 * 1000), context.getTimeout());
        Assert.assertEquals(1, context.getListeners().size());
        Assert.assertSame(this.asyncRequest, context.getListeners().get(0));
    }

    @Test
    public void startAsyncMultipleTimes() throws Exception {
        this.asyncRequest.startAsync();
        this.asyncRequest.startAsync();
        this.asyncRequest.startAsync();
        this.asyncRequest.startAsync();// idempotent

        MockAsyncContext context = ((MockAsyncContext) (this.request.getAsyncContext()));
        Assert.assertNotNull(context);
        Assert.assertEquals(1, context.getListeners().size());
    }

    @Test
    public void startAsyncNotSupported() throws Exception {
        this.request.setAsyncSupported(false);
        try {
            this.asyncRequest.startAsync();
            Assert.fail("expected exception");
        } catch (IllegalStateException ex) {
            Assert.assertThat(ex.getMessage(), Matchers.containsString("Async support must be enabled"));
        }
    }

    @Test
    public void startAsyncAfterCompleted() throws Exception {
        this.asyncRequest.onComplete(new AsyncEvent(new MockAsyncContext(this.request, this.response)));
        try {
            this.asyncRequest.startAsync();
            Assert.fail("expected exception");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Async processing has already completed", ex.getMessage());
        }
    }

    @Test
    public void onTimeoutDefaultBehavior() throws Exception {
        this.asyncRequest.onTimeout(new AsyncEvent(new MockAsyncContext(this.request, this.response)));
        Assert.assertEquals(200, this.response.getStatus());
    }

    @Test
    public void onTimeoutHandler() throws Exception {
        Runnable timeoutHandler = Mockito.mock(Runnable.class);
        this.asyncRequest.addTimeoutHandler(timeoutHandler);
        this.asyncRequest.onTimeout(new AsyncEvent(new MockAsyncContext(this.request, this.response)));
        Mockito.verify(timeoutHandler).run();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void onErrorHandler() throws Exception {
        Consumer<Throwable> errorHandler = Mockito.mock(Consumer.class);
        this.asyncRequest.addErrorHandler(errorHandler);
        Exception e = new Exception();
        this.asyncRequest.onError(new AsyncEvent(new MockAsyncContext(this.request, this.response), e));
        Mockito.verify(errorHandler).accept(e);
    }

    @Test(expected = IllegalStateException.class)
    public void setTimeoutDuringConcurrentHandling() {
        this.asyncRequest.startAsync();
        this.asyncRequest.setTimeout(25L);
    }

    @Test
    public void onCompletionHandler() throws Exception {
        Runnable handler = Mockito.mock(Runnable.class);
        this.asyncRequest.addCompletionHandler(handler);
        this.asyncRequest.startAsync();
        this.asyncRequest.onComplete(new AsyncEvent(this.request.getAsyncContext()));
        Mockito.verify(handler).run();
        Assert.assertTrue(this.asyncRequest.isAsyncComplete());
    }

    // SPR-13292
    @SuppressWarnings("unchecked")
    @Test
    public void onErrorHandlerAfterOnErrorEvent() throws Exception {
        Consumer<Throwable> handler = Mockito.mock(Consumer.class);
        this.asyncRequest.addErrorHandler(handler);
        this.asyncRequest.startAsync();
        Exception e = new Exception();
        this.asyncRequest.onError(new AsyncEvent(this.request.getAsyncContext(), e));
        Mockito.verify(handler).accept(e);
    }

    @Test
    public void onCompletionHandlerAfterOnCompleteEvent() throws Exception {
        Runnable handler = Mockito.mock(Runnable.class);
        this.asyncRequest.addCompletionHandler(handler);
        this.asyncRequest.startAsync();
        this.asyncRequest.onComplete(new AsyncEvent(this.request.getAsyncContext()));
        Mockito.verify(handler).run();
        Assert.assertTrue(this.asyncRequest.isAsyncComplete());
    }
}

