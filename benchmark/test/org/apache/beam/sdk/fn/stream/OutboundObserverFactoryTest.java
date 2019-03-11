/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.fn.stream;


import java.util.concurrent.Executors;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.CallStreamObserver;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.stub.StreamObserver;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link OutboundObserverFactory}.
 */
@RunWith(JUnit4.class)
public class OutboundObserverFactoryTest {
    @Mock
    private StreamObserver<Integer> mockRequestObserver;

    @Mock
    private CallStreamObserver<String> mockResponseObserver;

    @Test
    public void testDefaultInstantiation() {
        StreamObserver<String> observer = OutboundObserverFactory.clientDirect().outboundObserverFor(fakeFactory(), mockRequestObserver);
        Assert.assertThat(observer, Matchers.instanceOf(DirectStreamObserver.class));
    }

    @Test
    public void testBufferedStreamInstantiation() {
        StreamObserver<String> observer = OutboundObserverFactory.clientBuffered(Executors.newSingleThreadExecutor()).outboundObserverFor(fakeFactory(), mockRequestObserver);
        Assert.assertThat(observer, Matchers.instanceOf(BufferingStreamObserver.class));
    }

    @Test
    public void testBufferedStreamWithLimitInstantiation() {
        StreamObserver<String> observer = OutboundObserverFactory.clientBuffered(Executors.newSingleThreadExecutor(), 1).outboundObserverFor(fakeFactory(), mockRequestObserver);
        Assert.assertThat(observer, Matchers.instanceOf(BufferingStreamObserver.class));
        Assert.assertEquals(1, ((BufferingStreamObserver<String>) (observer)).getBufferSize());
    }
}

