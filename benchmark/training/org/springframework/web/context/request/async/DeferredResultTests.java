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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.async.DeferredResult.DeferredResultHandler;


/**
 * DeferredResult tests.
 *
 * @author Rossen Stoyanchev
 */
public class DeferredResultTests {
    @Test
    public void setResult() {
        DeferredResultHandler handler = Mockito.mock(DeferredResultHandler.class);
        DeferredResult<String> result = new DeferredResult();
        result.setResultHandler(handler);
        Assert.assertTrue(result.setResult("hello"));
        Mockito.verify(handler).handleResult("hello");
    }

    @Test
    public void setResultTwice() {
        DeferredResultHandler handler = Mockito.mock(DeferredResultHandler.class);
        DeferredResult<String> result = new DeferredResult();
        result.setResultHandler(handler);
        Assert.assertTrue(result.setResult("hello"));
        Assert.assertFalse(result.setResult("hi"));
        Mockito.verify(handler).handleResult("hello");
    }

    @Test
    public void isSetOrExpired() {
        DeferredResultHandler handler = Mockito.mock(DeferredResultHandler.class);
        DeferredResult<String> result = new DeferredResult();
        result.setResultHandler(handler);
        Assert.assertFalse(result.isSetOrExpired());
        result.setResult("hello");
        Assert.assertTrue(result.isSetOrExpired());
        Mockito.verify(handler).handleResult("hello");
    }

    @Test
    public void hasResult() {
        DeferredResultHandler handler = Mockito.mock(DeferredResultHandler.class);
        DeferredResult<String> result = new DeferredResult();
        result.setResultHandler(handler);
        Assert.assertFalse(result.hasResult());
        Assert.assertNull(result.getResult());
        result.setResult("hello");
        Assert.assertEquals("hello", result.getResult());
    }

    @Test
    public void onCompletion() throws Exception {
        final StringBuilder sb = new StringBuilder();
        DeferredResult<String> result = new DeferredResult();
        result.onCompletion(new Runnable() {
            @Override
            public void run() {
                sb.append("completion event");
            }
        });
        result.getInterceptor().afterCompletion(null, null);
        Assert.assertTrue(result.isSetOrExpired());
        Assert.assertEquals("completion event", sb.toString());
    }

    @Test
    public void onTimeout() throws Exception {
        final StringBuilder sb = new StringBuilder();
        DeferredResultHandler handler = Mockito.mock(DeferredResultHandler.class);
        DeferredResult<String> result = new DeferredResult(null, "timeout result");
        result.setResultHandler(handler);
        result.onTimeout(new Runnable() {
            @Override
            public void run() {
                sb.append("timeout event");
            }
        });
        result.getInterceptor().handleTimeout(null, null);
        Assert.assertEquals("timeout event", sb.toString());
        Assert.assertFalse("Should not be able to set result a second time", result.setResult("hello"));
        Mockito.verify(handler).handleResult("timeout result");
    }

    @Test
    public void onError() throws Exception {
        final StringBuilder sb = new StringBuilder();
        DeferredResultHandler handler = Mockito.mock(DeferredResultHandler.class);
        DeferredResult<String> result = new DeferredResult(null, "error result");
        result.setResultHandler(handler);
        Exception e = new Exception();
        result.onError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) {
                sb.append("error event");
            }
        });
        result.getInterceptor().handleError(null, null, e);
        Assert.assertEquals("error event", sb.toString());
        Assert.assertFalse("Should not be able to set result a second time", result.setResult("hello"));
        Mockito.verify(handler).handleResult(e);
    }
}

