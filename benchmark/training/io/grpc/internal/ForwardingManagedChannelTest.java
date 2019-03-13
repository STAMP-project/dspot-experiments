/**
 * Copyright 2018 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import CallOptions.DEFAULT;
import ConnectivityState.READY;
import io.grpc.CallOptions;
import io.grpc.ForwardingTestUtil;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.testing.TestMethodDescriptors;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;


@RunWith(JUnit4.class)
public final class ForwardingManagedChannelTest {
    private final ManagedChannel mock = Mockito.mock(ManagedChannel.class);

    private final ForwardingManagedChannel forward = new ForwardingManagedChannel(mock) {};

    @Test
    public void allMethodsForwarded() throws Exception {
        ForwardingTestUtil.testMethodsForwarded(ManagedChannel.class, mock, forward, Collections.<Method>emptyList());
    }

    @Test
    public void shutdown() {
        ManagedChannel ret = Mockito.mock(ManagedChannel.class);
        Mockito.when(mock.shutdown()).thenReturn(ret);
        Assert.assertSame(ret, forward.shutdown());
    }

    @Test
    public void isShutdown() {
        Mockito.when(mock.isShutdown()).thenReturn(true);
        Assert.assertSame(true, forward.isShutdown());
    }

    @Test
    public void isTerminated() {
        Mockito.when(mock.isTerminated()).thenReturn(true);
        Assert.assertSame(true, forward.isTerminated());
    }

    @Test
    public void shutdownNow() {
        ManagedChannel ret = Mockito.mock(ManagedChannel.class);
        Mockito.when(mock.shutdownNow()).thenReturn(ret);
        Assert.assertSame(ret, forward.shutdownNow());
    }

    @Test
    public void awaitTermination() throws Exception {
        long timeout = 1234;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        forward.awaitTermination(timeout, unit);
        Mockito.verify(mock).awaitTermination(ArgumentMatchers.eq(timeout), eq(unit));
    }

    @Test
    public void newCall() {
        NoopClientCall<Void, Void> clientCall = new NoopClientCall();
        CallOptions callOptions = DEFAULT.withoutWaitForReady();
        MethodDescriptor<Void, Void> method = TestMethodDescriptors.voidMethod();
        Mockito.when(mock.newCall(ArgumentMatchers.same(method), ArgumentMatchers.same(callOptions))).thenReturn(clientCall);
        Assert.assertSame(clientCall, forward.newCall(method, callOptions));
    }

    @Test
    public void authority() {
        String authority = "authority5678";
        Mockito.when(mock.authority()).thenReturn(authority);
        Assert.assertSame(authority, forward.authority());
    }

    @Test
    public void getState() {
        Mockito.when(mock.getState(true)).thenReturn(READY);
        Assert.assertSame(READY, forward.getState(true));
    }

    @Test
    public void notifyWhenStateChanged() {
        Runnable callback = new Runnable() {
            @Override
            public void run() {
            }
        };
        forward.notifyWhenStateChanged(READY, callback);
        Mockito.verify(mock).notifyWhenStateChanged(ArgumentMatchers.same(READY), ArgumentMatchers.same(callback));
    }
}

