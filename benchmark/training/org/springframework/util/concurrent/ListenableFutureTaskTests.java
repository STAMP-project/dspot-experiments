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
package org.springframework.util.concurrent;


import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Sebastien Deleuze
 */
@SuppressWarnings("unchecked")
public class ListenableFutureTaskTests {
    @Test
    public void success() throws Exception {
        final String s = "Hello World";
        Callable<String> callable = () -> s;
        ListenableFutureTask<String> task = new ListenableFutureTask(callable);
        task.addCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Assert.assertEquals(s, result);
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        task.run();
        Assert.assertSame(s, task.get());
        Assert.assertSame(s, task.completable().get());
        task.completable().thenAccept(( v) -> assertSame(s, v));
    }

    @Test
    public void failure() throws Exception {
        final String s = "Hello World";
        Callable<String> callable = () -> {
            throw new IOException(s);
        };
        ListenableFutureTask<String> task = new ListenableFutureTask(callable);
        task.addCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Assert.fail("onSuccess not expected");
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.assertEquals(s, ex.getMessage());
            }
        });
        task.run();
        try {
            task.get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertSame(s, ex.getCause().getMessage());
        }
        try {
            task.completable().get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertSame(s, ex.getCause().getMessage());
        }
    }

    @Test
    public void successWithLambdas() throws Exception {
        final String s = "Hello World";
        Callable<String> callable = () -> s;
        SuccessCallback<String> successCallback = Mockito.mock(SuccessCallback.class);
        FailureCallback failureCallback = Mockito.mock(FailureCallback.class);
        ListenableFutureTask<String> task = new ListenableFutureTask(callable);
        task.addCallback(successCallback, failureCallback);
        task.run();
        Mockito.verify(successCallback).onSuccess(s);
        Mockito.verifyZeroInteractions(failureCallback);
        Assert.assertSame(s, task.get());
        Assert.assertSame(s, task.completable().get());
        task.completable().thenAccept(( v) -> assertSame(s, v));
    }

    @Test
    public void failureWithLambdas() throws Exception {
        final String s = "Hello World";
        IOException ex = new IOException(s);
        Callable<String> callable = () -> {
            throw ex;
        };
        SuccessCallback<String> successCallback = Mockito.mock(SuccessCallback.class);
        FailureCallback failureCallback = Mockito.mock(FailureCallback.class);
        ListenableFutureTask<String> task = new ListenableFutureTask(callable);
        task.addCallback(successCallback, failureCallback);
        task.run();
        Mockito.verify(failureCallback).onFailure(ex);
        Mockito.verifyZeroInteractions(successCallback);
        try {
            task.get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex2) {
            Assert.assertSame(s, ex2.getCause().getMessage());
        }
        try {
            task.completable().get();
            Assert.fail("Should have thrown ExecutionException");
        } catch (ExecutionException ex2) {
            Assert.assertSame(s, ex2.getCause().getMessage());
        }
    }
}

