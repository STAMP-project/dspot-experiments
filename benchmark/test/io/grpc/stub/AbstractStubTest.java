/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.stub;


import io.grpc.CallOptions;
import io.grpc.Channel;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class AbstractStubTest {
    @Mock
    Channel channel;

    @Test(expected = NullPointerException.class)
    public void channelMustNotBeNull() {
        new AbstractStubTest.NoopStub(null);
    }

    @Test(expected = NullPointerException.class)
    public void callOptionsMustNotBeNull() {
        new AbstractStubTest.NoopStub(channel, null);
    }

    @Test(expected = NullPointerException.class)
    public void channelMustNotBeNull2() {
        new AbstractStubTest.NoopStub(null, CallOptions.DEFAULT);
    }

    @Test
    public void withWaitForReady() {
        AbstractStubTest.NoopStub stub = new AbstractStubTest.NoopStub(channel);
        CallOptions callOptions = getCallOptions();
        Assert.assertFalse(callOptions.isWaitForReady());
        stub = stub.withWaitForReady();
        callOptions = stub.getCallOptions();
        Assert.assertTrue(callOptions.isWaitForReady());
    }

    class NoopStub extends AbstractStub<AbstractStubTest.NoopStub> {
        NoopStub(Channel channel) {
            super(channel);
        }

        NoopStub(Channel channel, CallOptions options) {
            super(channel, options);
        }

        @Override
        protected AbstractStubTest.NoopStub build(Channel channel, CallOptions callOptions) {
            return new AbstractStubTest.NoopStub(channel, callOptions);
        }
    }

    @Test
    public void withExecutor() {
        AbstractStubTest.NoopStub stub = new AbstractStubTest.NoopStub(channel);
        CallOptions callOptions = getCallOptions();
        Assert.assertNull(callOptions.getExecutor());
        Executor executor = Mockito.mock(Executor.class);
        stub = stub.withExecutor(executor);
        callOptions = stub.getCallOptions();
        Assert.assertEquals(callOptions.getExecutor(), executor);
    }
}

