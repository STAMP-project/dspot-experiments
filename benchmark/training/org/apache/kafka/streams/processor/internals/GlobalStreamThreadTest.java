/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import GlobalStreamThread.State.DEAD;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.kafka.clients.consumer.InvalidOffsetException;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.test.MockStateRestoreListener;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


public class GlobalStreamThreadTest {
    private final InternalTopologyBuilder builder = new InternalTopologyBuilder();

    private final MockConsumer<byte[], byte[]> mockConsumer = new MockConsumer(OffsetResetStrategy.NONE);

    private final MockTime time = new MockTime();

    private final MockStateRestoreListener stateRestoreListener = new MockStateRestoreListener();

    private GlobalStreamThread globalStreamThread;

    private StreamsConfig config;

    private static final String GLOBAL_STORE_TOPIC_NAME = "foo";

    private static final String GLOBAL_STORE_NAME = "bar";

    private final TopicPartition topicPartition = new TopicPartition(GlobalStreamThreadTest.GLOBAL_STORE_TOPIC_NAME, 0);

    @Test
    public void shouldThrowStreamsExceptionOnStartupIfThereIsAStreamsException() {
        // should throw as the MockConsumer hasn't been configured and there are no
        // partitions available
        try {
            globalStreamThread.start();
            Assert.fail("Should have thrown StreamsException if start up failed");
        } catch (final StreamsException e) {
            // ok
        }
        Assert.assertFalse(globalStreamThread.stillRunning());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldThrowStreamsExceptionOnStartupIfExceptionOccurred() {
        final MockConsumer<byte[], byte[]> mockConsumer = new MockConsumer(OffsetResetStrategy.EARLIEST) {
            @Override
            public List<PartitionInfo> partitionsFor(final String topic) {
                throw new RuntimeException("KABOOM!");
            }
        };
        globalStreamThread = new GlobalStreamThread(builder.buildGlobalStateTopology(), config, mockConsumer, new StateDirectory(config, time, true), 0, new Metrics(), new MockTime(), "clientId", stateRestoreListener);
        try {
            globalStreamThread.start();
            Assert.fail("Should have thrown StreamsException if start up failed");
        } catch (final StreamsException e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(RuntimeException.class));
            MatcherAssert.assertThat(e.getCause().getMessage(), CoreMatchers.equalTo("KABOOM!"));
        }
        Assert.assertFalse(globalStreamThread.stillRunning());
    }

    @Test
    public void shouldBeRunningAfterSuccessfulStart() {
        initializeConsumer();
        globalStreamThread.start();
        Assert.assertTrue(globalStreamThread.stillRunning());
    }

    @Test(timeout = 30000)
    public void shouldStopRunningWhenClosedByUser() throws Exception {
        initializeConsumer();
        globalStreamThread.start();
        globalStreamThread.shutdown();
        globalStreamThread.join();
        Assert.assertEquals(DEAD, globalStreamThread.state());
    }

    @Test
    public void shouldCloseStateStoresOnClose() throws Exception {
        initializeConsumer();
        globalStreamThread.start();
        final StateStore globalStore = builder.globalStateStores().get(GlobalStreamThreadTest.GLOBAL_STORE_NAME);
        Assert.assertTrue(globalStore.isOpen());
        globalStreamThread.shutdown();
        globalStreamThread.join();
        Assert.assertFalse(globalStore.isOpen());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTransitionToDeadOnClose() throws Exception {
        initializeConsumer();
        globalStreamThread.start();
        globalStreamThread.shutdown();
        globalStreamThread.join();
        Assert.assertEquals(DEAD, globalStreamThread.state());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldStayDeadAfterTwoCloses() throws Exception {
        initializeConsumer();
        globalStreamThread.start();
        globalStreamThread.shutdown();
        globalStreamThread.join();
        globalStreamThread.shutdown();
        Assert.assertEquals(DEAD, globalStreamThread.state());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTransitionToRunningOnStart() throws Exception {
        initializeConsumer();
        globalStreamThread.start();
        TestUtils.waitForCondition(() -> (globalStreamThread.state()) == (RUNNING), (10 * 1000), "Thread never started.");
        globalStreamThread.shutdown();
    }

    @Test
    public void shouldDieOnInvalidOffsetException() throws Exception {
        initializeConsumer();
        globalStreamThread.start();
        TestUtils.waitForCondition(() -> (globalStreamThread.state()) == (RUNNING), (10 * 1000), "Thread never started.");
        mockConsumer.updateEndOffsets(Collections.singletonMap(topicPartition, 1L));
        mockConsumer.addRecord(new org.apache.kafka.clients.consumer.ConsumerRecord(GlobalStreamThreadTest.GLOBAL_STORE_TOPIC_NAME, 0, 0L, "K1".getBytes(), "V1".getBytes()));
        TestUtils.waitForCondition(() -> (mockConsumer.position(topicPartition)) == 1L, (10 * 1000), "Input record never consumed");
        mockConsumer.setException(new InvalidOffsetException("Try Again!") {
            @Override
            public Set<TopicPartition> partitions() {
                return Collections.singleton(topicPartition);
            }
        });
        // feed first record for recovery
        mockConsumer.addRecord(new org.apache.kafka.clients.consumer.ConsumerRecord(GlobalStreamThreadTest.GLOBAL_STORE_TOPIC_NAME, 0, 0L, "K1".getBytes(), "V1".getBytes()));
        TestUtils.waitForCondition(() -> (globalStreamThread.state()) == (DEAD), (10 * 1000), "GlobalStreamThread should have died.");
    }
}

