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


import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;


/**
 *
 *
 * @author Mattias Severson
 * @author Juergen Hoeller
 */
public class SettableListenableFutureTests {
    private final SettableListenableFuture<String> settableListenableFuture = new SettableListenableFuture();

    @Test
    public void validateInitialValues() {
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertFalse(settableListenableFuture.isDone());
    }

    @Test
    public void returnsSetValue() throws InterruptedException, ExecutionException {
        String string = "hello";
        Assert.assertTrue(settableListenableFuture.set(string));
        Assert.assertThat(settableListenableFuture.get(), equalTo(string));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void returnsSetValueFromCompletable() throws InterruptedException, ExecutionException {
        String string = "hello";
        Assert.assertTrue(settableListenableFuture.set(string));
        Future<String> completable = settableListenableFuture.completable();
        Assert.assertThat(completable.get(), equalTo(string));
        Assert.assertFalse(completable.isCancelled());
        Assert.assertTrue(completable.isDone());
    }

    @Test
    public void setValueUpdatesDoneStatus() {
        settableListenableFuture.set("hello");
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void throwsSetExceptionWrappedInExecutionException() throws Exception {
        Throwable exception = new RuntimeException();
        Assert.assertTrue(settableListenableFuture.setException(exception));
        try {
            settableListenableFuture.get();
            Assert.fail("Expected ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertThat(ex.getCause(), equalTo(exception));
        }
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void throwsSetExceptionWrappedInExecutionExceptionFromCompletable() throws Exception {
        Throwable exception = new RuntimeException();
        Assert.assertTrue(settableListenableFuture.setException(exception));
        Future<String> completable = settableListenableFuture.completable();
        try {
            completable.get();
            Assert.fail("Expected ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertThat(ex.getCause(), equalTo(exception));
        }
        Assert.assertFalse(completable.isCancelled());
        Assert.assertTrue(completable.isDone());
    }

    @Test
    public void throwsSetErrorWrappedInExecutionException() throws Exception {
        Throwable exception = new OutOfMemoryError();
        Assert.assertTrue(settableListenableFuture.setException(exception));
        try {
            settableListenableFuture.get();
            Assert.fail("Expected ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertThat(ex.getCause(), equalTo(exception));
        }
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void throwsSetErrorWrappedInExecutionExceptionFromCompletable() throws Exception {
        Throwable exception = new OutOfMemoryError();
        Assert.assertTrue(settableListenableFuture.setException(exception));
        Future<String> completable = settableListenableFuture.completable();
        try {
            completable.get();
            Assert.fail("Expected ExecutionException");
        } catch (ExecutionException ex) {
            Assert.assertThat(ex.getCause(), equalTo(exception));
        }
        Assert.assertFalse(completable.isCancelled());
        Assert.assertTrue(completable.isDone());
    }

    @Test
    public void setValueTriggersCallback() {
        String string = "hello";
        final String[] callbackHolder = new String[1];
        settableListenableFuture.addCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callbackHolder[0] = result;
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail("Expected onSuccess() to be called");
            }
        });
        settableListenableFuture.set(string);
        Assert.assertThat(callbackHolder[0], equalTo(string));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void setValueTriggersCallbackOnlyOnce() {
        String string = "hello";
        final String[] callbackHolder = new String[1];
        settableListenableFuture.addCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callbackHolder[0] = result;
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail("Expected onSuccess() to be called");
            }
        });
        settableListenableFuture.set(string);
        Assert.assertFalse(settableListenableFuture.set("good bye"));
        Assert.assertThat(callbackHolder[0], equalTo(string));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void setExceptionTriggersCallback() {
        Throwable exception = new RuntimeException();
        final Throwable[] callbackHolder = new Throwable[1];
        settableListenableFuture.addCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Assert.fail("Expected onFailure() to be called");
            }

            @Override
            public void onFailure(Throwable ex) {
                callbackHolder[0] = ex;
            }
        });
        settableListenableFuture.setException(exception);
        Assert.assertThat(callbackHolder[0], equalTo(exception));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void setExceptionTriggersCallbackOnlyOnce() {
        Throwable exception = new RuntimeException();
        final Throwable[] callbackHolder = new Throwable[1];
        settableListenableFuture.addCallback(new ListenableFutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Assert.fail("Expected onFailure() to be called");
            }

            @Override
            public void onFailure(Throwable ex) {
                callbackHolder[0] = ex;
            }
        });
        settableListenableFuture.setException(exception);
        Assert.assertFalse(settableListenableFuture.setException(new IllegalArgumentException()));
        Assert.assertThat(callbackHolder[0], equalTo(exception));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void nullIsAcceptedAsValueToSet() throws InterruptedException, ExecutionException {
        settableListenableFuture.set(null);
        Assert.assertNull(settableListenableFuture.get());
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void getWaitsForCompletion() throws InterruptedException, ExecutionException {
        final String string = "hello";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20L);
                    settableListenableFuture.set(string);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
        String value = settableListenableFuture.get();
        Assert.assertThat(value, equalTo(string));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void getWithTimeoutThrowsTimeoutException() throws InterruptedException, ExecutionException {
        try {
            settableListenableFuture.get(1L, TimeUnit.MILLISECONDS);
            Assert.fail("Expected TimeoutException");
        } catch (TimeoutException ex) {
            // expected
        }
    }

    @Test
    public void getWithTimeoutWaitsForCompletion() throws InterruptedException, ExecutionException, TimeoutException {
        final String string = "hello";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20L);
                    settableListenableFuture.set(string);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
        String value = settableListenableFuture.get(500L, TimeUnit.MILLISECONDS);
        Assert.assertThat(value, equalTo(string));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void cancelPreventsValueFromBeingSet() {
        Assert.assertTrue(settableListenableFuture.cancel(true));
        Assert.assertFalse(settableListenableFuture.set("hello"));
        Assert.assertTrue(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void cancelSetsFutureToDone() {
        settableListenableFuture.cancel(true);
        Assert.assertTrue(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void cancelWithMayInterruptIfRunningTrueCallsOverriddenMethod() {
        SettableListenableFutureTests.InterruptibleSettableListenableFuture interruptibleFuture = new SettableListenableFutureTests.InterruptibleSettableListenableFuture();
        Assert.assertTrue(cancel(true));
        Assert.assertTrue(interruptibleFuture.calledInterruptTask());
        Assert.assertTrue(isCancelled());
        Assert.assertTrue(isDone());
    }

    @Test
    public void cancelWithMayInterruptIfRunningFalseDoesNotCallOverriddenMethod() {
        SettableListenableFutureTests.InterruptibleSettableListenableFuture interruptibleFuture = new SettableListenableFutureTests.InterruptibleSettableListenableFuture();
        Assert.assertTrue(cancel(false));
        Assert.assertFalse(interruptibleFuture.calledInterruptTask());
        Assert.assertTrue(isCancelled());
        Assert.assertTrue(isDone());
    }

    @Test
    public void setPreventsCancel() {
        Assert.assertTrue(settableListenableFuture.set("hello"));
        Assert.assertFalse(settableListenableFuture.cancel(true));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void cancelPreventsExceptionFromBeingSet() {
        Assert.assertTrue(settableListenableFuture.cancel(true));
        Assert.assertFalse(settableListenableFuture.setException(new RuntimeException()));
        Assert.assertTrue(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void setExceptionPreventsCancel() {
        Assert.assertTrue(settableListenableFuture.setException(new RuntimeException()));
        Assert.assertFalse(settableListenableFuture.cancel(true));
        Assert.assertFalse(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void cancelStateThrowsExceptionWhenCallingGet() throws InterruptedException, ExecutionException {
        settableListenableFuture.cancel(true);
        try {
            settableListenableFuture.get();
            Assert.fail("Expected CancellationException");
        } catch (CancellationException ex) {
            // expected
        }
        Assert.assertTrue(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    public void cancelStateThrowsExceptionWhenCallingGetWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20L);
                    settableListenableFuture.cancel(true);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
        try {
            settableListenableFuture.get(500L, TimeUnit.MILLISECONDS);
            Assert.fail("Expected CancellationException");
        } catch (CancellationException ex) {
            // expected
        }
        Assert.assertTrue(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void cancelDoesNotNotifyCallbacksOnSet() {
        ListenableFutureCallback callback = Mockito.mock(ListenableFutureCallback.class);
        settableListenableFuture.addCallback(callback);
        settableListenableFuture.cancel(true);
        Mockito.verify(callback).onFailure(any(CancellationException.class));
        Mockito.verifyNoMoreInteractions(callback);
        settableListenableFuture.set("hello");
        Mockito.verifyNoMoreInteractions(callback);
        Assert.assertTrue(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void cancelDoesNotNotifyCallbacksOnSetException() {
        ListenableFutureCallback callback = Mockito.mock(ListenableFutureCallback.class);
        settableListenableFuture.addCallback(callback);
        settableListenableFuture.cancel(true);
        Mockito.verify(callback).onFailure(any(CancellationException.class));
        Mockito.verifyNoMoreInteractions(callback);
        settableListenableFuture.setException(new RuntimeException());
        Mockito.verifyNoMoreInteractions(callback);
        Assert.assertTrue(settableListenableFuture.isCancelled());
        Assert.assertTrue(settableListenableFuture.isDone());
    }

    private static class InterruptibleSettableListenableFuture extends SettableListenableFuture<String> {
        private boolean interrupted = false;

        @Override
        protected void interruptTask() {
            interrupted = true;
        }

        boolean calledInterruptTask() {
            return interrupted;
        }
    }
}

