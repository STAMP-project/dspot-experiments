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
package org.apache.beam.sdk.io.gcp.pubsub;


import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PublishRequest;
import com.google.pubsub.v1.PublishResponse;
import com.google.pubsub.v1.PublisherGrpc.PublisherImplBase;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import com.google.pubsub.v1.SubscriberGrpc.SubscriberImplBase;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.IncomingMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.OutgoingMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.SubscriptionPath;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.TopicPath;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for PubsubGrpcClient.
 */
@RunWith(JUnit4.class)
public class PubsubGrpcClientTest {
    private ManagedChannel inProcessChannel;

    private PubsubClient client;

    private String channelName;

    private static final TopicPath TOPIC = PubsubClient.topicPathFromName("testProject", "testTopic");

    private static final SubscriptionPath SUBSCRIPTION = PubsubClient.subscriptionPathFromName("testProject", "testSubscription");

    private static final long REQ_TIME = 1234L;

    private static final long PUB_TIME = 3456L;

    private static final long MESSAGE_TIME = 6789L;

    private static final String TIMESTAMP_ATTRIBUTE = "timestamp";

    private static final String ID_ATTRIBUTE = "id";

    private static final String MESSAGE_ID = "testMessageId";

    private static final String DATA = "testData";

    private static final String RECORD_ID = "testRecordId";

    private static final String ACK_ID = "testAckId";

    private static final ImmutableMap<String, String> ATTRIBUTES = ImmutableMap.<String, String>builder().put("a", "b").put("c", "d").build();

    @Test
    public void pullOneMessage() throws IOException {
        String expectedSubscription = PubsubGrpcClientTest.SUBSCRIPTION.getPath();
        final PullRequest expectedRequest = PullRequest.newBuilder().setSubscription(expectedSubscription).setReturnImmediately(true).setMaxMessages(10).build();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(((PubsubGrpcClientTest.PUB_TIME) / 1000)).setNanos((((int) ((PubsubGrpcClientTest.PUB_TIME) % 1000)) * 1000)).build();
        PubsubMessage expectedPubsubMessage = PubsubMessage.newBuilder().setMessageId(PubsubGrpcClientTest.MESSAGE_ID).setData(ByteString.copyFrom(PubsubGrpcClientTest.DATA.getBytes(StandardCharsets.UTF_8))).setPublishTime(timestamp).putAllAttributes(PubsubGrpcClientTest.ATTRIBUTES).putAllAttributes(ImmutableMap.of(PubsubGrpcClientTest.TIMESTAMP_ATTRIBUTE, String.valueOf(PubsubGrpcClientTest.MESSAGE_TIME), PubsubGrpcClientTest.ID_ATTRIBUTE, PubsubGrpcClientTest.RECORD_ID)).build();
        ReceivedMessage expectedReceivedMessage = ReceivedMessage.newBuilder().setMessage(expectedPubsubMessage).setAckId(PubsubGrpcClientTest.ACK_ID).build();
        final PullResponse response = PullResponse.newBuilder().addAllReceivedMessages(ImmutableList.of(expectedReceivedMessage)).build();
        final List<PullRequest> requestsReceived = new ArrayList<>();
        SubscriberImplBase subscriberImplBase = new SubscriberImplBase() {
            @Override
            public void pull(PullRequest request, StreamObserver<PullResponse> responseObserver) {
                requestsReceived.add(request);
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
        Server server = InProcessServerBuilder.forName(channelName).addService(subscriberImplBase).build().start();
        try {
            List<IncomingMessage> acutalMessages = client.pull(PubsubGrpcClientTest.REQ_TIME, PubsubGrpcClientTest.SUBSCRIPTION, 10, true);
            Assert.assertEquals(1, acutalMessages.size());
            IncomingMessage actualMessage = acutalMessages.get(0);
            Assert.assertEquals(PubsubGrpcClientTest.ACK_ID, actualMessage.ackId);
            Assert.assertEquals(PubsubGrpcClientTest.DATA, new String(actualMessage.elementBytes, StandardCharsets.UTF_8));
            Assert.assertEquals(PubsubGrpcClientTest.RECORD_ID, actualMessage.recordId);
            Assert.assertEquals(PubsubGrpcClientTest.REQ_TIME, actualMessage.requestTimeMsSinceEpoch);
            Assert.assertEquals(PubsubGrpcClientTest.MESSAGE_TIME, actualMessage.timestampMsSinceEpoch);
            Assert.assertEquals(expectedRequest, Iterables.getOnlyElement(requestsReceived));
        } finally {
            server.shutdownNow();
        }
    }

    @Test
    public void publishOneMessage() throws IOException {
        String expectedTopic = PubsubGrpcClientTest.TOPIC.getPath();
        PubsubMessage expectedPubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFrom(PubsubGrpcClientTest.DATA.getBytes(StandardCharsets.UTF_8))).putAllAttributes(PubsubGrpcClientTest.ATTRIBUTES).putAllAttributes(ImmutableMap.of(PubsubGrpcClientTest.TIMESTAMP_ATTRIBUTE, String.valueOf(PubsubGrpcClientTest.MESSAGE_TIME), PubsubGrpcClientTest.ID_ATTRIBUTE, PubsubGrpcClientTest.RECORD_ID)).build();
        final PublishRequest expectedRequest = PublishRequest.newBuilder().setTopic(expectedTopic).addAllMessages(ImmutableList.of(expectedPubsubMessage)).build();
        final PublishResponse response = PublishResponse.newBuilder().addAllMessageIds(ImmutableList.of(PubsubGrpcClientTest.MESSAGE_ID)).build();
        final List<PublishRequest> requestsReceived = new ArrayList<>();
        PublisherImplBase publisherImplBase = new PublisherImplBase() {
            @Override
            public void publish(PublishRequest request, StreamObserver<PublishResponse> responseObserver) {
                requestsReceived.add(request);
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
        Server server = InProcessServerBuilder.forName(channelName).addService(publisherImplBase).build().start();
        try {
            OutgoingMessage actualMessage = new OutgoingMessage(PubsubGrpcClientTest.DATA.getBytes(StandardCharsets.UTF_8), PubsubGrpcClientTest.ATTRIBUTES, PubsubGrpcClientTest.MESSAGE_TIME, PubsubGrpcClientTest.RECORD_ID);
            int n = client.publish(PubsubGrpcClientTest.TOPIC, ImmutableList.of(actualMessage));
            Assert.assertEquals(1, n);
            Assert.assertEquals(expectedRequest, Iterables.getOnlyElement(requestsReceived));
        } finally {
            server.shutdownNow();
        }
    }
}

