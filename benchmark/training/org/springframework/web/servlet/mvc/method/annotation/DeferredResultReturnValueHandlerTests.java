/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.web.servlet.mvc.method.annotation;


import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;


/**
 * Unit tests for {@link DeferredResultMethodReturnValueHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class DeferredResultReturnValueHandlerTests {
    private DeferredResultMethodReturnValueHandler handler;

    private MockHttpServletRequest request;

    private NativeWebRequest webRequest;

    @Test
    public void supportsReturnType() throws Exception {
        Assert.assertTrue(this.handler.supportsReturnType(on(DeferredResultReturnValueHandlerTests.TestController.class).resolveReturnType(DeferredResult.class, String.class)));
        Assert.assertTrue(this.handler.supportsReturnType(on(DeferredResultReturnValueHandlerTests.TestController.class).resolveReturnType(ListenableFuture.class, String.class)));
        Assert.assertTrue(this.handler.supportsReturnType(on(DeferredResultReturnValueHandlerTests.TestController.class).resolveReturnType(CompletableFuture.class, String.class)));
    }

    @Test
    public void doesNotSupportReturnType() throws Exception {
        Assert.assertFalse(this.handler.supportsReturnType(on(DeferredResultReturnValueHandlerTests.TestController.class).resolveReturnType(String.class)));
    }

    @Test
    public void deferredResult() throws Exception {
        DeferredResult<String> result = new DeferredResult();
        IllegalStateException ex = new IllegalStateException();
        testHandle(result, DeferredResult.class, () -> result.setErrorResult(ex), ex);
    }

    @Test
    public void listenableFuture() throws Exception {
        SettableListenableFuture<String> future = new SettableListenableFuture();
        testHandle(future, ListenableFuture.class, () -> future.set("foo"), "foo");
    }

    @Test
    public void completableFuture() throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();
        testHandle(future, CompletableFuture.class, () -> future.complete("foo"), "foo");
    }

    @Test
    public void deferredResultWithError() throws Exception {
        DeferredResult<String> result = new DeferredResult();
        testHandle(result, DeferredResult.class, () -> result.setResult("foo"), "foo");
    }

    @Test
    public void listenableFutureWithError() throws Exception {
        SettableListenableFuture<String> future = new SettableListenableFuture();
        IllegalStateException ex = new IllegalStateException();
        testHandle(future, ListenableFuture.class, () -> future.setException(ex), ex);
    }

    @Test
    public void completableFutureWithError() throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();
        IllegalStateException ex = new IllegalStateException();
        testHandle(future, CompletableFuture.class, () -> future.completeExceptionally(ex), ex);
    }

    @SuppressWarnings("unused")
    static class TestController {
        String handleString() {
            return null;
        }

        DeferredResult<String> handleDeferredResult() {
            return null;
        }

        ListenableFuture<String> handleListenableFuture() {
            return null;
        }

        CompletableFuture<String> handleCompletableFuture() {
            return null;
        }
    }
}

