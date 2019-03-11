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


import com.google.api.client.util.Clock;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.IncomingMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.OutgoingMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.SubscriptionPath;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.TopicPath;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubTestClient.PubsubTestClientFactory;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for PubsubTestClient.
 */
@RunWith(JUnit4.class)
public class PubsubTestClientTest {
    private static final TopicPath TOPIC = PubsubClient.topicPathFromName("testProject", "testTopic");

    private static final SubscriptionPath SUBSCRIPTION = PubsubClient.subscriptionPathFromName("testProject", "testSubscription");

    private static final long REQ_TIME = 1234L;

    private static final long MESSAGE_TIME = 6789L;

    private static final String MESSAGE_ID = "testMessageId";

    private static final String DATA = "testData";

    private static final String ACK_ID = "testAckId";

    private static final int ACK_TIMEOUT_S = 60;

    @Test
    public void pullOneMessage() throws IOException {
        final AtomicLong now = new AtomicLong();
        Clock clock = now::get;
        IncomingMessage expectedIncomingMessage = new IncomingMessage(PubsubTestClientTest.DATA.getBytes(StandardCharsets.UTF_8), null, PubsubTestClientTest.MESSAGE_TIME, PubsubTestClientTest.REQ_TIME, PubsubTestClientTest.ACK_ID, PubsubTestClientTest.MESSAGE_ID);
        try (PubsubTestClientFactory factory = PubsubTestClient.createFactoryForPull(clock, PubsubTestClientTest.SUBSCRIPTION, PubsubTestClientTest.ACK_TIMEOUT_S, Lists.newArrayList(expectedIncomingMessage))) {
            try (PubsubTestClient client = ((PubsubTestClient) (factory.newClient(null, null, null)))) {
                now.set(PubsubTestClientTest.REQ_TIME);
                client.advance();
                List<IncomingMessage> incomingMessages = client.pull(now.get(), PubsubTestClientTest.SUBSCRIPTION, 1, true);
                Assert.assertEquals(1, incomingMessages.size());
                Assert.assertEquals(expectedIncomingMessage, incomingMessages.get(0));
                // Timeout on ACK.
                now.addAndGet((((PubsubTestClientTest.ACK_TIMEOUT_S) + 10) * 1000));
                client.advance();
                incomingMessages = client.pull(now.get(), PubsubTestClientTest.SUBSCRIPTION, 1, true);
                Assert.assertEquals(1, incomingMessages.size());
                Assert.assertEquals(expectedIncomingMessage.withRequestTime(now.get()), incomingMessages.get(0));
                now.addAndGet((10 * 1000));
                client.advance();
                // Extend ack
                client.modifyAckDeadline(PubsubTestClientTest.SUBSCRIPTION, ImmutableList.of(PubsubTestClientTest.ACK_ID), 20);
                // Timeout on extended ACK
                now.addAndGet((30 * 1000));
                client.advance();
                incomingMessages = client.pull(now.get(), PubsubTestClientTest.SUBSCRIPTION, 1, true);
                Assert.assertEquals(1, incomingMessages.size());
                Assert.assertEquals(expectedIncomingMessage.withRequestTime(now.get()), incomingMessages.get(0));
                // Extend ack
                client.modifyAckDeadline(PubsubTestClientTest.SUBSCRIPTION, ImmutableList.of(PubsubTestClientTest.ACK_ID), 20);
                // Ack
                now.addAndGet((15 * 1000));
                client.advance();
                client.acknowledge(PubsubTestClientTest.SUBSCRIPTION, ImmutableList.of(PubsubTestClientTest.ACK_ID));
            }
        }
    }

    @Test
    public void publishOneMessage() throws IOException {
        OutgoingMessage expectedOutgoingMessage = new OutgoingMessage(PubsubTestClientTest.DATA.getBytes(StandardCharsets.UTF_8), null, PubsubTestClientTest.MESSAGE_TIME, PubsubTestClientTest.MESSAGE_ID);
        try (PubsubTestClientFactory factory = PubsubTestClient.createFactoryForPublish(PubsubTestClientTest.TOPIC, Sets.newHashSet(expectedOutgoingMessage), ImmutableList.of())) {
            try (PubsubTestClient client = ((PubsubTestClient) (factory.newClient(null, null, null)))) {
                client.publish(PubsubTestClientTest.TOPIC, ImmutableList.of(expectedOutgoingMessage));
            }
        }
    }
}

