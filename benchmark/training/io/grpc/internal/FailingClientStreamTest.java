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


import RpcProgress.DROPPED;
import RpcProgress.PROCESSED;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener.RpcProgress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link FailingClientStream}.
 */
@RunWith(JUnit4.class)
public class FailingClientStreamTest {
    @Test
    public void processedRpcProgressPopulatedToListener() {
        ClientStreamListener listener = Mockito.mock(ClientStreamListener.class);
        Status status = Status.UNAVAILABLE;
        ClientStream stream = new FailingClientStream(status);
        stream.start(listener);
        Mockito.verify(listener).closed(ArgumentMatchers.eq(status), ArgumentMatchers.eq(PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void droppedRpcProgressPopulatedToListener() {
        ClientStreamListener listener = Mockito.mock(ClientStreamListener.class);
        Status status = Status.UNAVAILABLE;
        ClientStream stream = new FailingClientStream(status, RpcProgress.DROPPED);
        stream.start(listener);
        Mockito.verify(listener).closed(ArgumentMatchers.eq(status), ArgumentMatchers.eq(DROPPED), ArgumentMatchers.any(Metadata.class));
    }
}

