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


import Publisher.Builder.DEFAULT_BATCHING_SETTINGS;
import Publisher.Builder.DEFAULT_DELAY_THRESHOLD;
import Publisher.Builder.DEFAULT_ELEMENT_COUNT_THRESHOLD;
import Publisher.Builder.DEFAULT_EXECUTOR_PROVIDER;
import Publisher.Builder.DEFAULT_REQUEST_BYTES_THRESHOLD;
import Publisher.Builder.DEFAULT_RETRY_SETTINGS;
import Publisher.Builder.MIN_RPC_TIMEOUT;
import Publisher.Builder.MIN_TOTAL_TIMEOUT;
import Status.DATA_LOSS;
import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.rpc.DataLossException;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher.Builder;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PublishResponse;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.Server;
import io.grpc.Status;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.Duration;


@RunWith(JUnit4.class)
public class PublisherImplTest {
    private static final ProjectTopicName TEST_TOPIC = ProjectTopicName.of("test-project", "test-topic");

    private static final ExecutorProvider SINGLE_THREAD_EXECUTOR = InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build();

    private static final TransportChannelProvider TEST_CHANNEL_PROVIDER = LocalChannelProvider.create("test-server");

    private FakeScheduledExecutorService fakeExecutor;

    private FakePublisherServiceImpl testPublisherServiceImpl;

    private Server testServer;

    class FakeException extends Exception {}

    @Test
    public void testPublishByDuration() throws Exception {
        Publisher publisher = // To demonstrate that reaching duration will trigger publish
        getTestPublisherBuilder().setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setDelayThreshold(Duration.ofSeconds(5)).setElementCountThreshold(10L).build()).build();
        testPublisherServiceImpl.addPublishResponse(PublishResponse.newBuilder().addMessageIds("1").addMessageIds("2"));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        ApiFuture<String> publishFuture2 = sendTestMessage(publisher, "B");
        Assert.assertFalse(publishFuture1.isDone());
        Assert.assertFalse(publishFuture2.isDone());
        fakeExecutor.advanceTime(Duration.ofSeconds(10));
        Assert.assertEquals("1", publishFuture1.get());
        Assert.assertEquals("2", publishFuture2.get());
        Assert.assertEquals(2, testPublisherServiceImpl.getCapturedRequests().get(0).getMessagesCount());
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testPublishByNumBatchedMessages() throws Exception {
        Publisher publisher = getTestPublisherBuilder().setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(2L).setDelayThreshold(Duration.ofSeconds(100)).build()).build();
        testPublisherServiceImpl.addPublishResponse(PublishResponse.newBuilder().addMessageIds("1").addMessageIds("2")).addPublishResponse(PublishResponse.newBuilder().addMessageIds("3").addMessageIds("4"));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        ApiFuture<String> publishFuture2 = sendTestMessage(publisher, "B");
        ApiFuture<String> publishFuture3 = sendTestMessage(publisher, "C");
        // Note we are not advancing time but message should still get published
        Assert.assertEquals("1", publishFuture1.get());
        Assert.assertEquals("2", publishFuture2.get());
        Assert.assertFalse(publishFuture3.isDone());
        ApiFuture<String> publishFuture4 = publisher.publish(PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8("D")).build());
        Assert.assertEquals("3", publishFuture3.get());
        Assert.assertEquals("4", publishFuture4.get());
        Assert.assertEquals(2, testPublisherServiceImpl.getCapturedRequests().get(0).getMessagesCount());
        Assert.assertEquals(2, testPublisherServiceImpl.getCapturedRequests().get(1).getMessagesCount());
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testSinglePublishByNumBytes() throws Exception {
        Publisher publisher = getTestPublisherBuilder().setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(2L).setDelayThreshold(Duration.ofSeconds(100)).build()).build();
        testPublisherServiceImpl.addPublishResponse(PublishResponse.newBuilder().addMessageIds("1").addMessageIds("2")).addPublishResponse(PublishResponse.newBuilder().addMessageIds("3").addMessageIds("4"));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        ApiFuture<String> publishFuture2 = sendTestMessage(publisher, "B");
        ApiFuture<String> publishFuture3 = sendTestMessage(publisher, "C");
        // Note we are not advancing time but message should still get published
        Assert.assertEquals("1", publishFuture1.get());
        Assert.assertEquals("2", publishFuture2.get());
        Assert.assertFalse(publishFuture3.isDone());
        ApiFuture<String> publishFuture4 = sendTestMessage(publisher, "D");
        Assert.assertEquals("3", publishFuture3.get());
        Assert.assertEquals("4", publishFuture4.get());
        Assert.assertEquals(2, testPublisherServiceImpl.getCapturedRequests().size());
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testPublishMixedSizeAndDuration() throws Exception {
        Publisher publisher = // To demonstrate that reaching duration will trigger publish
        getTestPublisherBuilder().setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(2L).setDelayThreshold(Duration.ofSeconds(5)).build()).build();
        testPublisherServiceImpl.addPublishResponse(PublishResponse.newBuilder().addMessageIds("1").addMessageIds("2"));
        testPublisherServiceImpl.addPublishResponse(PublishResponse.newBuilder().addMessageIds("3"));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        fakeExecutor.advanceTime(Duration.ofSeconds(2));
        Assert.assertFalse(publishFuture1.isDone());
        ApiFuture<String> publishFuture2 = sendTestMessage(publisher, "B");
        // Publishing triggered by batch size
        Assert.assertEquals("1", publishFuture1.get());
        Assert.assertEquals("2", publishFuture2.get());
        ApiFuture<String> publishFuture3 = sendTestMessage(publisher, "C");
        Assert.assertFalse(publishFuture3.isDone());
        // Publishing triggered by time
        fakeExecutor.advanceTime(Duration.ofSeconds(5));
        Assert.assertEquals("3", publishFuture3.get());
        Assert.assertEquals(2, testPublisherServiceImpl.getCapturedRequests().get(0).getMessagesCount());
        Assert.assertEquals(1, testPublisherServiceImpl.getCapturedRequests().get(1).getMessagesCount());
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testErrorPropagation() throws Exception {
        Publisher publisher = getTestPublisherBuilder().setExecutorProvider(PublisherImplTest.SINGLE_THREAD_EXECUTOR).setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(1L).setDelayThreshold(Duration.ofSeconds(5)).build()).build();
        testPublisherServiceImpl.addPublishError(DATA_LOSS.asException());
        try {
            sendTestMessage(publisher, "A").get();
            Assert.fail("should throw exception");
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(DataLossException.class);
        }
    }

    @Test
    public void testPublishFailureRetries() throws Exception {
        Publisher publisher = getTestPublisherBuilder().setExecutorProvider(PublisherImplTest.SINGLE_THREAD_EXECUTOR).setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(1L).setDelayThreshold(Duration.ofSeconds(5)).build()).build();// To demonstrate that reaching duration will trigger publish

        testPublisherServiceImpl.addPublishError(new Throwable("Transiently failing"));
        testPublisherServiceImpl.addPublishResponse(PublishResponse.newBuilder().addMessageIds("1"));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        Assert.assertEquals("1", publishFuture1.get());
        Assert.assertEquals(2, testPublisherServiceImpl.getCapturedRequests().size());
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test(expected = ExecutionException.class)
    public void testPublishFailureRetries_retriesDisabled() throws Exception {
        Publisher publisher = getTestPublisherBuilder().setExecutorProvider(PublisherImplTest.SINGLE_THREAD_EXECUTOR).setRetrySettings(DEFAULT_RETRY_SETTINGS.toBuilder().setTotalTimeout(Duration.ofSeconds(10)).setMaxAttempts(1).build()).build();
        testPublisherServiceImpl.addPublishError(new Throwable("Transiently failing"));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        try {
            publishFuture1.get();
        } finally {
            Assert.assertSame(testPublisherServiceImpl.getCapturedRequests().size(), 1);
            publisher.shutdown();
            publisher.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    @Test
    public void testPublishFailureRetries_maxRetriesSetup() throws Exception {
        Publisher publisher = getTestPublisherBuilder().setExecutorProvider(PublisherImplTest.SINGLE_THREAD_EXECUTOR).setRetrySettings(DEFAULT_RETRY_SETTINGS.toBuilder().setTotalTimeout(Duration.ofSeconds(10)).setMaxAttempts(3).build()).build();
        testPublisherServiceImpl.addPublishError(new Throwable("Transiently failing"));
        testPublisherServiceImpl.addPublishError(new Throwable("Transiently failing"));
        testPublisherServiceImpl.addPublishResponse(PublishResponse.newBuilder().addMessageIds("1"));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        Assert.assertEquals("1", publishFuture1.get());
        Assert.assertEquals(3, testPublisherServiceImpl.getCapturedRequests().size());
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testPublishFailureRetries_maxRetriesSetUnlimited() throws Exception {
        Publisher publisher = getTestPublisherBuilder().setExecutorProvider(PublisherImplTest.SINGLE_THREAD_EXECUTOR).setRetrySettings(DEFAULT_RETRY_SETTINGS.toBuilder().setTotalTimeout(Duration.ofSeconds(10)).setMaxAttempts(0).build()).build();
        testPublisherServiceImpl.addPublishError(new Throwable("Transiently failing"));
        testPublisherServiceImpl.addPublishError(new Throwable("Transiently failing"));
        testPublisherServiceImpl.addPublishResponse(PublishResponse.newBuilder().addMessageIds("1"));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        Assert.assertEquals("1", publishFuture1.get());
        Assert.assertEquals(3, testPublisherServiceImpl.getCapturedRequests().size());
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test(expected = ExecutionException.class)
    public void testPublishFailureRetries_nonRetryableFailsImmediately() throws Exception {
        Publisher publisher = getTestPublisherBuilder().setExecutorProvider(PublisherImplTest.SINGLE_THREAD_EXECUTOR).setRetrySettings(DEFAULT_RETRY_SETTINGS.toBuilder().setTotalTimeout(Duration.ofSeconds(10)).build()).setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(1L).setDelayThreshold(Duration.ofSeconds(5)).build()).build();// To demonstrate that reaching duration will trigger publish

        testPublisherServiceImpl.addPublishError(new io.grpc.StatusException(Status.INVALID_ARGUMENT));
        ApiFuture<String> publishFuture1 = sendTestMessage(publisher, "A");
        try {
            publishFuture1.get();
        } finally {
            Assert.assertTrue(((testPublisherServiceImpl.getCapturedRequests().size()) >= 1));
            publisher.shutdown();
            publisher.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    @Test
    public void testPublisherGetters() throws Exception {
        Publisher.Builder builder = Publisher.newBuilder(PublisherImplTest.TEST_TOPIC);
        builder.setChannelProvider(PublisherImplTest.TEST_CHANNEL_PROVIDER);
        builder.setExecutorProvider(PublisherImplTest.SINGLE_THREAD_EXECUTOR);
        builder.setBatchingSettings(BatchingSettings.newBuilder().setRequestByteThreshold(10L).setDelayThreshold(Duration.ofMillis(11)).setElementCountThreshold(12L).build());
        builder.setCredentialsProvider(NoCredentialsProvider.create());
        Publisher publisher = builder.build();
        Assert.assertEquals(PublisherImplTest.TEST_TOPIC, publisher.getTopicName());
        Assert.assertEquals(10, ((long) (publisher.getBatchingSettings().getRequestByteThreshold())));
        Assert.assertEquals(Duration.ofMillis(11), publisher.getBatchingSettings().getDelayThreshold());
        Assert.assertEquals(12, ((long) (publisher.getBatchingSettings().getElementCountThreshold())));
        publisher.shutdown();
        publisher.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testBuilderParametersAndDefaults() {
        Publisher.Builder builder = Publisher.newBuilder(PublisherImplTest.TEST_TOPIC);
        Assert.assertEquals(PublisherImplTest.TEST_TOPIC.toString(), builder.topicName);
        Assert.assertEquals(DEFAULT_EXECUTOR_PROVIDER, builder.executorProvider);
        Assert.assertEquals(DEFAULT_REQUEST_BYTES_THRESHOLD, builder.batchingSettings.getRequestByteThreshold().longValue());
        Assert.assertEquals(DEFAULT_DELAY_THRESHOLD, builder.batchingSettings.getDelayThreshold());
        Assert.assertEquals(DEFAULT_ELEMENT_COUNT_THRESHOLD, builder.batchingSettings.getElementCountThreshold().longValue());
        Assert.assertEquals(DEFAULT_RETRY_SETTINGS, builder.retrySettings);
    }

    @Test
    public void testBuilderInvalidArguments() {
        Publisher.Builder builder = Publisher.newBuilder(PublisherImplTest.TEST_TOPIC);
        try {
            builder.setChannelProvider(null);
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (NullPointerException expected) {
            // Expected
        }
        try {
            builder.setExecutorProvider(null);
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (NullPointerException expected) {
            // Expected
        }
        try {
            builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setRequestByteThreshold(((Long) (null))).build());
            Assert.fail("Should have thrown an NullPointerException");
        } catch (NullPointerException expected) {
            // Expected
        }
        try {
            builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setRequestByteThreshold(0L).build());
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        try {
            builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setRequestByteThreshold((-1L)).build());
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setDelayThreshold(Duration.ofMillis(1)).build());
        try {
            builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setDelayThreshold(null).build());
            Assert.fail("Should have thrown an NullPointerException");
        } catch (NullPointerException expected) {
            // Expected
        }
        try {
            builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setDelayThreshold(Duration.ofMillis((-1))).build());
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(1L).build());
        try {
            builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(((Long) (null))).build());
            Assert.fail("Should have thrown an NullPointerException");
        } catch (NullPointerException expected) {
            // Expected
        }
        try {
            builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold(0L).build());
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        try {
            builder.setBatchingSettings(DEFAULT_BATCHING_SETTINGS.toBuilder().setElementCountThreshold((-1L)).build());
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        builder.setRetrySettings(DEFAULT_RETRY_SETTINGS.toBuilder().setInitialRpcTimeout(MIN_RPC_TIMEOUT).build());
        try {
            builder.setRetrySettings(DEFAULT_RETRY_SETTINGS.toBuilder().setInitialRpcTimeout(MIN_RPC_TIMEOUT.minusMillis(1)).build());
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
        builder.setRetrySettings(DEFAULT_RETRY_SETTINGS.toBuilder().setTotalTimeout(MIN_TOTAL_TIMEOUT).build());
        try {
            builder.setRetrySettings(DEFAULT_RETRY_SETTINGS.toBuilder().setTotalTimeout(MIN_TOTAL_TIMEOUT.minusMillis(1)).build());
            Assert.fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // Expected
        }
    }
}

