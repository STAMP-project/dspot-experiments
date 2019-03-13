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


import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.Pubsub.Projects.Subscriptions;
import com.google.api.services.pubsub.Pubsub.Projects.Topics;
import com.google.api.services.pubsub.model.ListSubscriptionsResponse;
import com.google.api.services.pubsub.model.ListTopicsResponse;
import com.google.api.services.pubsub.model.PublishRequest;
import com.google.api.services.pubsub.model.PublishResponse;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.api.services.pubsub.model.PullRequest;
import com.google.api.services.pubsub.model.PullResponse;
import com.google.api.services.pubsub.model.ReceivedMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.IncomingMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.OutgoingMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.ProjectPath;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.SubscriptionPath;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.TopicPath;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for PubsubJsonClient.
 */
@RunWith(JUnit4.class)
public class PubsubJsonClientTest {
    private Pubsub mockPubsub;

    private PubsubClient client;

    private static final ProjectPath PROJECT = PubsubClient.projectPathFromId("testProject");

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

    @Test
    public void pullOneMessage() throws IOException {
        String expectedSubscription = PubsubJsonClientTest.SUBSCRIPTION.getPath();
        PullRequest expectedRequest = new PullRequest().setReturnImmediately(true).setMaxMessages(10);
        PubsubMessage expectedPubsubMessage = new PubsubMessage().setMessageId(PubsubJsonClientTest.MESSAGE_ID).encodeData(PubsubJsonClientTest.DATA.getBytes(StandardCharsets.UTF_8)).setPublishTime(String.valueOf(PubsubJsonClientTest.PUB_TIME)).setAttributes(ImmutableMap.of(PubsubJsonClientTest.TIMESTAMP_ATTRIBUTE, String.valueOf(PubsubJsonClientTest.MESSAGE_TIME), PubsubJsonClientTest.ID_ATTRIBUTE, PubsubJsonClientTest.RECORD_ID));
        ReceivedMessage expectedReceivedMessage = new ReceivedMessage().setMessage(expectedPubsubMessage).setAckId(PubsubJsonClientTest.ACK_ID);
        PullResponse expectedResponse = new PullResponse().setReceivedMessages(ImmutableList.of(expectedReceivedMessage));
        Mockito.when(((Object) (mockPubsub.projects().subscriptions().pull(expectedSubscription, expectedRequest).execute()))).thenReturn(expectedResponse);
        List<IncomingMessage> acutalMessages = client.pull(PubsubJsonClientTest.REQ_TIME, PubsubJsonClientTest.SUBSCRIPTION, 10, true);
        Assert.assertEquals(1, acutalMessages.size());
        IncomingMessage actualMessage = acutalMessages.get(0);
        Assert.assertEquals(PubsubJsonClientTest.ACK_ID, actualMessage.ackId);
        Assert.assertEquals(PubsubJsonClientTest.DATA, new String(actualMessage.elementBytes, StandardCharsets.UTF_8));
        Assert.assertEquals(PubsubJsonClientTest.RECORD_ID, actualMessage.recordId);
        Assert.assertEquals(PubsubJsonClientTest.REQ_TIME, actualMessage.requestTimeMsSinceEpoch);
        Assert.assertEquals(PubsubJsonClientTest.MESSAGE_TIME, actualMessage.timestampMsSinceEpoch);
    }

    @Test
    public void pullOneMessageWithNoData() throws IOException {
        String expectedSubscription = PubsubJsonClientTest.SUBSCRIPTION.getPath();
        PullRequest expectedRequest = new PullRequest().setReturnImmediately(true).setMaxMessages(10);
        PubsubMessage expectedPubsubMessage = new PubsubMessage().setMessageId(PubsubJsonClientTest.MESSAGE_ID).setPublishTime(String.valueOf(PubsubJsonClientTest.PUB_TIME)).setAttributes(ImmutableMap.of(PubsubJsonClientTest.TIMESTAMP_ATTRIBUTE, String.valueOf(PubsubJsonClientTest.MESSAGE_TIME), PubsubJsonClientTest.ID_ATTRIBUTE, PubsubJsonClientTest.RECORD_ID));
        ReceivedMessage expectedReceivedMessage = new ReceivedMessage().setMessage(expectedPubsubMessage).setAckId(PubsubJsonClientTest.ACK_ID);
        PullResponse expectedResponse = new PullResponse().setReceivedMessages(ImmutableList.of(expectedReceivedMessage));
        Mockito.when(((Object) (mockPubsub.projects().subscriptions().pull(expectedSubscription, expectedRequest).execute()))).thenReturn(expectedResponse);
        List<IncomingMessage> acutalMessages = client.pull(PubsubJsonClientTest.REQ_TIME, PubsubJsonClientTest.SUBSCRIPTION, 10, true);
        Assert.assertEquals(1, acutalMessages.size());
        IncomingMessage actualMessage = acutalMessages.get(0);
        Assert.assertArrayEquals(new byte[0], actualMessage.elementBytes);
    }

    @Test
    public void publishOneMessage() throws IOException {
        String expectedTopic = PubsubJsonClientTest.TOPIC.getPath();
        PubsubMessage expectedPubsubMessage = new PubsubMessage().encodeData(PubsubJsonClientTest.DATA.getBytes(StandardCharsets.UTF_8)).setAttributes(ImmutableMap.<String, String>builder().put(PubsubJsonClientTest.TIMESTAMP_ATTRIBUTE, String.valueOf(PubsubJsonClientTest.MESSAGE_TIME)).put(PubsubJsonClientTest.ID_ATTRIBUTE, PubsubJsonClientTest.RECORD_ID).put("k", "v").build());
        PublishRequest expectedRequest = new PublishRequest().setMessages(ImmutableList.of(expectedPubsubMessage));
        PublishResponse expectedResponse = new PublishResponse().setMessageIds(ImmutableList.of(PubsubJsonClientTest.MESSAGE_ID));
        Mockito.when(((Object) (mockPubsub.projects().topics().publish(expectedTopic, expectedRequest).execute()))).thenReturn(expectedResponse);
        Map<String, String> attrs = new HashMap<>();
        attrs.put("k", "v");
        OutgoingMessage actualMessage = new OutgoingMessage(PubsubJsonClientTest.DATA.getBytes(StandardCharsets.UTF_8), attrs, PubsubJsonClientTest.MESSAGE_TIME, PubsubJsonClientTest.RECORD_ID);
        int n = client.publish(PubsubJsonClientTest.TOPIC, ImmutableList.of(actualMessage));
        Assert.assertEquals(1, n);
    }

    @Test
    public void publishOneMessageWithOnlyTimestampAndIdAttributes() throws IOException {
        String expectedTopic = PubsubJsonClientTest.TOPIC.getPath();
        PubsubMessage expectedPubsubMessage = new PubsubMessage().encodeData(PubsubJsonClientTest.DATA.getBytes(StandardCharsets.UTF_8)).setAttributes(ImmutableMap.<String, String>builder().put(PubsubJsonClientTest.TIMESTAMP_ATTRIBUTE, String.valueOf(PubsubJsonClientTest.MESSAGE_TIME)).put(PubsubJsonClientTest.ID_ATTRIBUTE, PubsubJsonClientTest.RECORD_ID).build());
        PublishRequest expectedRequest = new PublishRequest().setMessages(ImmutableList.of(expectedPubsubMessage));
        PublishResponse expectedResponse = new PublishResponse().setMessageIds(ImmutableList.of(PubsubJsonClientTest.MESSAGE_ID));
        Mockito.when(((Object) (mockPubsub.projects().topics().publish(expectedTopic, expectedRequest).execute()))).thenReturn(expectedResponse);
        OutgoingMessage actualMessage = new OutgoingMessage(PubsubJsonClientTest.DATA.getBytes(StandardCharsets.UTF_8), ImmutableMap.of(), PubsubJsonClientTest.MESSAGE_TIME, PubsubJsonClientTest.RECORD_ID);
        int n = client.publish(PubsubJsonClientTest.TOPIC, ImmutableList.of(actualMessage));
        Assert.assertEquals(1, n);
    }

    @Test
    public void publishOneMessageWithNoTimestampOrIdAttribute() throws IOException {
        // For this test, create a new PubsubJsonClient without the timestamp attribute
        // or id attribute set.
        client = new PubsubJsonClient(null, null, mockPubsub);
        String expectedTopic = PubsubJsonClientTest.TOPIC.getPath();
        PubsubMessage expectedPubsubMessage = new PubsubMessage().encodeData(PubsubJsonClientTest.DATA.getBytes(StandardCharsets.UTF_8)).setAttributes(ImmutableMap.<String, String>builder().put("k", "v").build());
        PublishRequest expectedRequest = new PublishRequest().setMessages(ImmutableList.of(expectedPubsubMessage));
        PublishResponse expectedResponse = new PublishResponse().setMessageIds(ImmutableList.of(PubsubJsonClientTest.MESSAGE_ID));
        Mockito.when(((Object) (mockPubsub.projects().topics().publish(expectedTopic, expectedRequest).execute()))).thenReturn(expectedResponse);
        Map<String, String> attrs = new HashMap<>();
        attrs.put("k", "v");
        OutgoingMessage actualMessage = new OutgoingMessage(PubsubJsonClientTest.DATA.getBytes(StandardCharsets.UTF_8), attrs, PubsubJsonClientTest.MESSAGE_TIME, PubsubJsonClientTest.RECORD_ID);
        int n = client.publish(PubsubJsonClientTest.TOPIC, ImmutableList.of(actualMessage));
        Assert.assertEquals(1, n);
    }

    @Test
    public void listTopics() throws Exception {
        ListTopicsResponse expectedResponse1 = new ListTopicsResponse();
        expectedResponse1.setTopics(Collections.singletonList(PubsubJsonClientTest.buildTopic(1)));
        expectedResponse1.setNextPageToken("AVgJH3Z7aHxiDBs");
        ListTopicsResponse expectedResponse2 = new ListTopicsResponse();
        expectedResponse2.setTopics(Collections.singletonList(PubsubJsonClientTest.buildTopic(2)));
        Topics.List request = mockPubsub.projects().topics().list(PubsubJsonClientTest.PROJECT.getPath());
        Mockito.when(((Object) (request.execute()))).thenReturn(expectedResponse1, expectedResponse2);
        List<TopicPath> topicPaths = client.listTopics(PubsubJsonClientTest.PROJECT);
        Assert.assertEquals(2, topicPaths.size());
    }

    @Test
    public void listSubscriptions() throws Exception {
        ListSubscriptionsResponse expectedResponse1 = new ListSubscriptionsResponse();
        expectedResponse1.setSubscriptions(Collections.singletonList(PubsubJsonClientTest.buildSubscription(1)));
        expectedResponse1.setNextPageToken("AVgJH3Z7aHxiDBs");
        ListSubscriptionsResponse expectedResponse2 = new ListSubscriptionsResponse();
        expectedResponse2.setSubscriptions(Collections.singletonList(PubsubJsonClientTest.buildSubscription(2)));
        Subscriptions.List request = mockPubsub.projects().subscriptions().list(PubsubJsonClientTest.PROJECT.getPath());
        Mockito.when(((Object) (request.execute()))).thenReturn(expectedResponse1, expectedResponse2);
        final TopicPath topic101 = PubsubClient.topicPathFromName("testProject", "Topic2");
        List<SubscriptionPath> subscriptionPaths = client.listSubscriptions(PubsubJsonClientTest.PROJECT, topic101);
        Assert.assertEquals(1, subscriptionPaths.size());
    }
}

