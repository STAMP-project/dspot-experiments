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
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener.RpcProgress;
import io.grpc.testing.TestMethodDescriptors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link FailingClientTransport}.
 */
@RunWith(JUnit4.class)
public class FailingClientTransportTest {
    @Test
    public void newStreamStart() {
        Status error = Status.UNAVAILABLE;
        RpcProgress rpcProgress = RpcProgress.DROPPED;
        FailingClientTransport transport = new FailingClientTransport(error, rpcProgress);
        ClientStream stream = transport.newStream(TestMethodDescriptors.voidMethod(), new Metadata(), DEFAULT);
        ClientStreamListener listener = Mockito.mock(ClientStreamListener.class);
        stream.start(listener);
        Mockito.verify(listener).closed(ArgumentMatchers.eq(error), ArgumentMatchers.eq(rpcProgress), ArgumentMatchers.any(Metadata.class));
    }
}

