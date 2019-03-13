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


import io.grpc.ForwardingTestUtil;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.StreamListener.MessageProducer;
import java.lang.reflect.Method;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ForwardingClientStreamListenerTest {
    private ClientStreamListener mock = Mockito.mock(ClientStreamListener.class);

    private ForwardingClientStreamListener forward = new ForwardingClientStreamListener() {
        @Override
        protected ClientStreamListener delegate() {
            return mock;
        }
    };

    @Test
    public void allMethodsForwarded() throws Exception {
        ForwardingTestUtil.testMethodsForwarded(ClientStreamListener.class, mock, forward, Collections.<Method>emptyList());
    }

    @Test
    public void headersReadTest() {
        Metadata headers = new Metadata();
        forward.headersRead(headers);
        Mockito.verify(mock).headersRead(ArgumentMatchers.same(headers));
    }

    @Test
    public void closedTest() {
        Status status = Status.UNKNOWN;
        Metadata trailers = new Metadata();
        forward.closed(status, trailers);
        Mockito.verify(mock).closed(ArgumentMatchers.same(status), ArgumentMatchers.same(trailers));
    }

    @Test
    public void messagesAvailableTest() {
        MessageProducer producer = Mockito.mock(MessageProducer.class);
        forward.messagesAvailable(producer);
        Mockito.verify(mock).messagesAvailable(ArgumentMatchers.same(producer));
    }
}

