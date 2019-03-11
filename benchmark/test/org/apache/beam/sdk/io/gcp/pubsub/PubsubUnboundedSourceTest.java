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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.IncomingMessage;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.SubscriptionPath;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubClient.TopicPath;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubTestClient.PubsubTestClientFactory;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubUnboundedSource.PubsubCheckpoint;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubUnboundedSource.PubsubReader;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubUnboundedSource.PubsubSource;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test PubsubUnboundedSource.
 */
@RunWith(JUnit4.class)
public class PubsubUnboundedSourceTest {
    private static final SubscriptionPath SUBSCRIPTION = PubsubClient.subscriptionPathFromName("testProject", "testSubscription");

    private static final String DATA = "testData";

    private static final long TIMESTAMP = 1234L;

    private static final long REQ_TIME = 6373L;

    private static final String TIMESTAMP_ATTRIBUTE = "timestamp";

    private static final String ID_ATTRIBUTE = "id";

    private static final String ACK_ID = "testAckId";

    private static final String RECORD_ID = "testRecordId";

    private static final int ACK_TIMEOUT_S = 60;

    private AtomicLong now;

    private Clock clock;

    private PubsubTestClientFactory factory;

    private PubsubSource primSource;

    @Rule
    public TestPipeline p = TestPipeline.create();

    @Test
    public void checkpointCoderIsSane() {
        setupOneMessage(ImmutableList.of());
        CoderProperties.coderSerializable(primSource.getCheckpointMarkCoder());
        // Since we only serialize/deserialize the 'notYetReadIds', and we don't want to make
        // equals on checkpoints ignore those fields, we'll test serialization and deserialization
        // of checkpoints in multipleReaders below.
    }

    @Test
    public void readOneMessage() throws IOException {
        setupOneMessage();
        PubsubReader reader = primSource.createReader(p.getOptions(), null);
        // Read one message.
        Assert.assertTrue(reader.start());
        Assert.assertEquals(PubsubUnboundedSourceTest.DATA, PubsubUnboundedSourceTest.data(reader.getCurrent()));
        TestCase.assertFalse(reader.advance());
        // ACK the message.
        PubsubCheckpoint checkpoint = reader.getCheckpointMark();
        checkpoint.finalizeCheckpoint();
        reader.close();
    }

    @Test
    public void timeoutAckAndRereadOneMessage() throws IOException {
        setupOneMessage();
        PubsubReader reader = primSource.createReader(p.getOptions(), null);
        PubsubTestClient pubsubClient = ((PubsubTestClient) (reader.getPubsubClient()));
        Assert.assertTrue(reader.start());
        Assert.assertEquals(PubsubUnboundedSourceTest.DATA, PubsubUnboundedSourceTest.data(reader.getCurrent()));
        // Let the ACK deadline for the above expire.
        now.addAndGet((65 * 1000));
        pubsubClient.advance();
        // We'll now receive the same message again.
        Assert.assertTrue(reader.advance());
        Assert.assertEquals(PubsubUnboundedSourceTest.DATA, PubsubUnboundedSourceTest.data(reader.getCurrent()));
        TestCase.assertFalse(reader.advance());
        // Now ACK the message.
        PubsubCheckpoint checkpoint = reader.getCheckpointMark();
        checkpoint.finalizeCheckpoint();
        reader.close();
    }

    @Test
    public void extendAck() throws IOException {
        setupOneMessage();
        PubsubReader reader = primSource.createReader(p.getOptions(), null);
        PubsubTestClient pubsubClient = ((PubsubTestClient) (reader.getPubsubClient()));
        // Pull the first message but don't take a checkpoint for it.
        Assert.assertTrue(reader.start());
        Assert.assertEquals(PubsubUnboundedSourceTest.DATA, PubsubUnboundedSourceTest.data(reader.getCurrent()));
        // Extend the ack
        now.addAndGet((55 * 1000));
        pubsubClient.advance();
        TestCase.assertFalse(reader.advance());
        // Extend the ack again
        now.addAndGet((25 * 1000));
        pubsubClient.advance();
        TestCase.assertFalse(reader.advance());
        // Now ACK the message.
        PubsubCheckpoint checkpoint = reader.getCheckpointMark();
        checkpoint.finalizeCheckpoint();
        reader.close();
    }

    @Test
    public void timeoutAckExtensions() throws IOException {
        setupOneMessage();
        PubsubReader reader = primSource.createReader(p.getOptions(), null);
        PubsubTestClient pubsubClient = ((PubsubTestClient) (reader.getPubsubClient()));
        // Pull the first message but don't take a checkpoint for it.
        Assert.assertTrue(reader.start());
        Assert.assertEquals(PubsubUnboundedSourceTest.DATA, PubsubUnboundedSourceTest.data(reader.getCurrent()));
        // Extend the ack.
        now.addAndGet((55 * 1000));
        pubsubClient.advance();
        TestCase.assertFalse(reader.advance());
        // Let the ack expire.
        for (int i = 0; i < 3; i++) {
            now.addAndGet((25 * 1000));
            pubsubClient.advance();
            TestCase.assertFalse(reader.advance());
        }
        // Wait for resend.
        now.addAndGet((25 * 1000));
        pubsubClient.advance();
        // Reread the same message.
        Assert.assertTrue(reader.advance());
        Assert.assertEquals(PubsubUnboundedSourceTest.DATA, PubsubUnboundedSourceTest.data(reader.getCurrent()));
        // Now ACK the message.
        PubsubCheckpoint checkpoint = reader.getCheckpointMark();
        checkpoint.finalizeCheckpoint();
        reader.close();
    }

    @Test
    public void multipleReaders() throws IOException {
        List<IncomingMessage> incoming = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            String data = String.format("data_%d", i);
            String ackid = String.format("ackid_%d", i);
            incoming.add(new IncomingMessage(data.getBytes(StandardCharsets.UTF_8), null, PubsubUnboundedSourceTest.TIMESTAMP, 0, ackid, PubsubUnboundedSourceTest.RECORD_ID));
        }
        setupOneMessage(incoming);
        PubsubReader reader = primSource.createReader(p.getOptions(), null);
        // Consume two messages, only read one.
        Assert.assertTrue(reader.start());
        Assert.assertEquals("data_0", PubsubUnboundedSourceTest.data(reader.getCurrent()));
        // Grab checkpoint.
        PubsubCheckpoint checkpoint = reader.getCheckpointMark();
        checkpoint.finalizeCheckpoint();
        Assert.assertEquals(1, checkpoint.notYetReadIds.size());
        Assert.assertEquals("ackid_1", checkpoint.notYetReadIds.get(0));
        // Read second message.
        Assert.assertTrue(reader.advance());
        Assert.assertEquals("data_1", PubsubUnboundedSourceTest.data(reader.getCurrent()));
        // Restore from checkpoint.
        byte[] checkpointBytes = CoderUtils.encodeToByteArray(primSource.getCheckpointMarkCoder(), checkpoint);
        checkpoint = CoderUtils.decodeFromByteArray(primSource.getCheckpointMarkCoder(), checkpointBytes);
        Assert.assertEquals(1, checkpoint.notYetReadIds.size());
        Assert.assertEquals("ackid_1", checkpoint.notYetReadIds.get(0));
        // Re-read second message.
        reader = primSource.createReader(p.getOptions(), checkpoint);
        Assert.assertTrue(reader.start());
        Assert.assertEquals("data_1", PubsubUnboundedSourceTest.data(reader.getCurrent()));
        // We are done.
        TestCase.assertFalse(reader.advance());
        // ACK final message.
        checkpoint = reader.getCheckpointMark();
        checkpoint.finalizeCheckpoint();
        reader.close();
    }

    @Test
    public void readManyMessages() throws IOException {
        Map<String, Integer> dataToMessageNum = new HashMap<>();
        final int m = 97;
        final int n = 10000;
        List<IncomingMessage> incoming = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            // Make the messages timestamps slightly out of order.
            int messageNum = (((i / m) * m) + (m - 1)) - (i % m);
            String data = String.format("data_%d", messageNum);
            dataToMessageNum.put(data, messageNum);
            String recid = String.format("recordid_%d", messageNum);
            String ackId = String.format("ackid_%d", messageNum);
            incoming.add(new IncomingMessage(data.getBytes(StandardCharsets.UTF_8), null, messageNumToTimestamp(messageNum), 0, ackId, recid));
        }
        setupOneMessage(incoming);
        PubsubReader reader = primSource.createReader(p.getOptions(), null);
        PubsubTestClient pubsubClient = ((PubsubTestClient) (reader.getPubsubClient()));
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                Assert.assertTrue(reader.start());
            } else {
                Assert.assertTrue(reader.advance());
            }
            // We'll checkpoint and ack within the 2min limit.
            now.addAndGet(30);
            pubsubClient.advance();
            String data = PubsubUnboundedSourceTest.data(reader.getCurrent());
            Integer messageNum = dataToMessageNum.remove(data);
            // No duplicate messages.
            Assert.assertNotNull(messageNum);
            // Preserve timestamp.
            Assert.assertEquals(new Instant(messageNumToTimestamp(messageNum)), reader.getCurrentTimestamp());
            // Preserve record id.
            String recid = String.format("recordid_%d", messageNum);
            Assert.assertArrayEquals(recid.getBytes(StandardCharsets.UTF_8), reader.getCurrentRecordId());
            if ((i % 1000) == 999) {
                // Estimated watermark can never get ahead of actual outstanding messages.
                long watermark = reader.getWatermark().getMillis();
                long minOutstandingTimestamp = Long.MAX_VALUE;
                for (Integer outstandingMessageNum : dataToMessageNum.values()) {
                    minOutstandingTimestamp = Math.min(minOutstandingTimestamp, messageNumToTimestamp(outstandingMessageNum));
                }
                Assert.assertThat(watermark, Matchers.lessThanOrEqualTo(minOutstandingTimestamp));
                // Ack messages, but only every other finalization.
                PubsubCheckpoint checkpoint = reader.getCheckpointMark();
                if ((i % 2000) == 1999) {
                    checkpoint.finalizeCheckpoint();
                }
            }
        }
        // We are done.
        TestCase.assertFalse(reader.advance());
        // We saw each message exactly once.
        Assert.assertTrue(dataToMessageNum.isEmpty());
        reader.close();
    }

    @Test
    public void noSubscriptionSplitGeneratesSubscription() throws Exception {
        TopicPath topicPath = PubsubClient.topicPathFromName("my_project", "my_topic");
        factory = PubsubTestClient.createFactoryForCreateSubscription();
        PubsubUnboundedSource source = /* subscription */
        /* timestampLabel */
        /* idLabel */
        /* needsAttributes */
        new PubsubUnboundedSource(factory, StaticValueProvider.of(PubsubClient.projectPathFromId("my_project")), StaticValueProvider.of(topicPath), null, null, null, false);
        Assert.assertThat(source.getSubscription(), Matchers.nullValue());
        Assert.assertThat(source.getSubscription(), Matchers.nullValue());
        PipelineOptions options = PipelineOptionsFactory.create();
        List<PubsubSource> splits = new PubsubSource(source).split(3, options);
        // We have at least one returned split
        Assert.assertThat(splits, Matchers.hasSize(Matchers.greaterThan(0)));
        for (PubsubSource split : splits) {
            // Each split is equal
            Assert.assertThat(split, Matchers.equalTo(splits.get(0)));
        }
        Assert.assertThat(splits.get(0).subscriptionPath, Matchers.not(Matchers.nullValue()));
    }

    @Test
    public void noSubscriptionNoSplitGeneratesSubscription() throws Exception {
        TopicPath topicPath = PubsubClient.topicPathFromName("my_project", "my_topic");
        factory = PubsubTestClient.createFactoryForCreateSubscription();
        PubsubUnboundedSource source = /* subscription */
        /* timestampLabel */
        /* idLabel */
        /* needsAttributes */
        new PubsubUnboundedSource(factory, StaticValueProvider.of(PubsubClient.projectPathFromId("my_project")), StaticValueProvider.of(topicPath), null, null, null, false);
        Assert.assertThat(source.getSubscription(), Matchers.nullValue());
        Assert.assertThat(source.getSubscription(), Matchers.nullValue());
        PipelineOptions options = PipelineOptionsFactory.create();
        PubsubSource actualSource = new PubsubSource(source);
        PubsubReader reader = actualSource.createReader(options, null);
        SubscriptionPath createdSubscription = reader.subscription;
        Assert.assertThat(createdSubscription, Matchers.not(Matchers.nullValue()));
        PubsubCheckpoint checkpoint = reader.getCheckpointMark();
        Assert.assertThat(checkpoint.subscriptionPath, Matchers.equalTo(createdSubscription.getPath()));
        checkpoint.finalizeCheckpoint();
        PubsubCheckpoint deserCheckpoint = CoderUtils.clone(actualSource.getCheckpointMarkCoder(), checkpoint);
        Assert.assertThat(checkpoint.subscriptionPath, Matchers.not(Matchers.nullValue()));
        Assert.assertThat(checkpoint.subscriptionPath, Matchers.equalTo(deserCheckpoint.subscriptionPath));
        PubsubReader readerFromOriginal = actualSource.createReader(options, checkpoint);
        PubsubReader readerFromDeser = actualSource.createReader(options, deserCheckpoint);
        Assert.assertThat(readerFromOriginal.subscription, Matchers.equalTo(createdSubscription));
        Assert.assertThat(readerFromDeser.subscription, Matchers.equalTo(createdSubscription));
    }

    /**
     * Tests that checkpoints finalized after the reader is closed succeed.
     */
    @Test
    public void closeWithActiveCheckpoints() throws Exception {
        setupOneMessage();
        PubsubReader reader = primSource.createReader(p.getOptions(), null);
        reader.start();
        PubsubCheckpoint checkpoint = reader.getCheckpointMark();
        reader.close();
        checkpoint.finalizeCheckpoint();
    }
}

