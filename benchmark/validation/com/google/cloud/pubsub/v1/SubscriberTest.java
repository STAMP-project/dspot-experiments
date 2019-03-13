/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.pubsub.v1;


import StatusCode.Code.INVALID_ARGUMENT;
import Subscriber.State.FAILED;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.ApiException;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Tests for {@link Subscriber}.
 */
public class SubscriberTest {
    private static final ProjectSubscriptionName TEST_SUBSCRIPTION = ProjectSubscriptionName.of("test-project", "test-subscription");

    private ManagedChannel testChannel;

    private FakeScheduledExecutorService fakeExecutor;

    private FakeSubscriberServiceImpl fakeSubscriberServiceImpl;

    private Server testServer;

    private final MessageReceiver testReceiver = new MessageReceiver() {
        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            consumer.ack();
        }
    };

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testOpenedChannels() throws Exception {
        int expectedChannelCount = 1;
        Subscriber subscriber = startSubscriber(getTestSubscriberBuilder(testReceiver));
        Assert.assertEquals(expectedChannelCount, fakeSubscriberServiceImpl.waitForOpenedStreams(expectedChannelCount));
        subscriber.stopAsync().awaitTerminated();
    }

    @Test
    public void testFailedChannel_recoverableError_channelReopened() throws Exception {
        int expectedChannelCount = 1;
        Subscriber subscriber = startSubscriber(getTestSubscriberBuilder(testReceiver).setSystemExecutorProvider(InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build()));
        // Recoverable error
        fakeSubscriberServiceImpl.sendError(new io.grpc.StatusException(Status.INTERNAL));
        Assert.assertEquals(1, fakeSubscriberServiceImpl.waitForClosedStreams(1));
        Assert.assertEquals(expectedChannelCount, fakeSubscriberServiceImpl.waitForOpenedStreams(expectedChannelCount));
        subscriber.stopAsync().awaitTerminated();
    }

    @Test(expected = IllegalStateException.class)
    public void testFailedChannel_fatalError_subscriberFails() throws Exception {
        Subscriber subscriber = startSubscriber(getTestSubscriberBuilder(testReceiver).setSystemExecutorProvider(InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(10).build()));
        // Fatal error
        fakeSubscriberServiceImpl.sendError(new io.grpc.StatusException(Status.INVALID_ARGUMENT));
        try {
            subscriber.awaitTerminated();
        } finally {
            // The subscriber must finish with an state error because its FAILED status.
            Assert.assertEquals(FAILED, subscriber.state());
            Throwable t = subscriber.failureCause();
            Assert.assertTrue((t instanceof ApiException));
            ApiException ex = ((ApiException) (t));
            Assert.assertTrue(((ex.getStatusCode()) instanceof GrpcStatusCode));
            GrpcStatusCode grpcCode = ((GrpcStatusCode) (ex.getStatusCode()));
            Assert.assertEquals(INVALID_ARGUMENT, grpcCode.getCode());
        }
    }
}

