/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common.thrift;


import com.linecorp.armeria.testing.internal.AnticipatedException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AsyncMethodCallbacksTest {
    @Test
    public void transferSuccess() {
        @SuppressWarnings("unchecked")
        final AsyncMethodCallback<String> callback = Mockito.mock(AsyncMethodCallback.class);
        AsyncMethodCallbacks.transfer(CompletableFuture.completedFuture("foo"), callback);
        Mockito.verify(callback, Mockito.only()).onComplete("foo");
    }

    @Test
    public void transferFailure_Exception() {
        @SuppressWarnings("unchecked")
        final AsyncMethodCallback<String> callback = Mockito.mock(AsyncMethodCallback.class);
        AsyncMethodCallbacks.transfer(exceptionallyCompletedFuture(new AnticipatedException()), callback);
        Mockito.verify(callback, Mockito.only()).onError(ArgumentMatchers.isA(AnticipatedException.class));
    }

    @Test
    public void transferFailure_Throwable() {
        @SuppressWarnings("unchecked")
        final AsyncMethodCallback<String> callback = Mockito.mock(AsyncMethodCallback.class);
        AsyncMethodCallbacks.transfer(exceptionallyCompletedFuture(new Throwable("foo")), callback);
        Mockito.verify(callback, Mockito.only()).onError(ArgumentMatchers.argThat(( argument) -> {
            return (argument instanceof CompletionException) && ("foo".equals(argument.getCause().getMessage()));
        }));
    }

    @Test
    public void transferCallbackError() {
        @SuppressWarnings("unchecked")
        final AsyncMethodCallback<String> callback = Mockito.mock(AsyncMethodCallback.class);
        Mockito.doThrow(new AnticipatedException()).when(callback).onComplete(ArgumentMatchers.any());
        AsyncMethodCallbacks.transfer(CompletableFuture.completedFuture("foo"), callback);
        Mockito.verify(callback, Mockito.only()).onComplete("foo");
    }
}

