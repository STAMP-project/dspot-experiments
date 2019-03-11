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


import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import StreamsConfig.RETRIES_CONFIG;
import StreamsConfig.STATE_DIR_CONFIG;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.consumer.InvalidOffsetException;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LockException;
import org.apache.kafka.streams.errors.ProcessorStateException;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.processor.StateRestoreCallback;
import org.apache.kafka.streams.state.TimestampedBytesStore;
import org.apache.kafka.streams.state.internals.OffsetCheckpoint;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.apache.kafka.test.MockStateRestoreListener;
import org.apache.kafka.test.NoOpReadOnlyStore;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static ProcessorStateManager.CHECKPOINT_FILE_NAME;


public class GlobalStateManagerImplTest {
    private final MockTime time = new MockTime();

    private final GlobalStateManagerImplTest.TheStateRestoreCallback stateRestoreCallback = new GlobalStateManagerImplTest.TheStateRestoreCallback();

    private final MockStateRestoreListener stateRestoreListener = new MockStateRestoreListener();

    private final String storeName1 = "t1-store";

    private final String storeName2 = "t2-store";

    private final String storeName3 = "t3-store";

    private final String storeName4 = "t4-store";

    private final TopicPartition t1 = new TopicPartition("t1", 1);

    private final TopicPartition t2 = new TopicPartition("t2", 1);

    private final TopicPartition t3 = new TopicPartition("t3", 1);

    private final TopicPartition t4 = new TopicPartition("t4", 1);

    private GlobalStateManagerImpl stateManager;

    private StateDirectory stateDirectory;

    private StreamsConfig streamsConfig;

    private NoOpReadOnlyStore<Object, Object> store1;

    private NoOpReadOnlyStore<Object, Object> store2;

    private NoOpReadOnlyStore<Object, Object> store3;

    private NoOpReadOnlyStore<Object, Object> store4;

    private MockConsumer<byte[], byte[]> consumer;

    private File checkpointFile;

    private ProcessorTopology topology;

    private InternalMockProcessorContext processorContext;

    @Test
    public void shouldLockGlobalStateDirectory() {
        stateManager.initialize();
        Assert.assertTrue(new File(stateDirectory.globalStateDir(), ".lock").exists());
    }

    @Test(expected = LockException.class)
    public void shouldThrowLockExceptionIfCantGetLock() throws IOException {
        final StateDirectory stateDir = new StateDirectory(streamsConfig, time, true);
        try {
            stateDir.lockGlobalState();
            stateManager.initialize();
        } finally {
            stateDir.unlockGlobalState();
        }
    }

    @Test
    public void shouldReadCheckpointOffsets() throws IOException {
        final Map<TopicPartition, Long> expected = writeCheckpoint();
        stateManager.initialize();
        final Map<TopicPartition, Long> offsets = stateManager.checkpointed();
        Assert.assertEquals(expected, offsets);
    }

    @Test
    public void shouldNotDeleteCheckpointFileAfterLoaded() throws IOException {
        writeCheckpoint();
        stateManager.initialize();
        Assert.assertTrue(checkpointFile.exists());
    }

    @Test(expected = StreamsException.class)
    public void shouldThrowStreamsExceptionIfFailedToReadCheckpointedOffsets() throws IOException {
        writeCorruptCheckpoint();
        stateManager.initialize();
    }

    @Test
    public void shouldInitializeStateStores() {
        stateManager.initialize();
        Assert.assertTrue(store1.initialized);
        Assert.assertTrue(store2.initialized);
    }

    @Test
    public void shouldReturnInitializedStoreNames() {
        final Set<String> storeNames = stateManager.initialize();
        Assert.assertEquals(Utils.mkSet(storeName1, storeName2, storeName3, storeName4), storeNames);
    }

    @Test
    public void shouldThrowIllegalArgumentIfTryingToRegisterStoreThatIsNotGlobal() {
        stateManager.initialize();
        try {
            stateManager.register(new NoOpReadOnlyStore("not-in-topology"), stateRestoreCallback);
            Assert.fail("should have raised an illegal argument exception as store is not in the topology");
        } catch (final IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfAttemptingToRegisterStoreTwice() {
        stateManager.initialize();
        initializeConsumer(2, 0, t1);
        stateManager.register(store1, stateRestoreCallback);
        try {
            stateManager.register(store1, stateRestoreCallback);
            Assert.fail("should have raised an illegal argument exception as store has already been registered");
        } catch (final IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void shouldThrowStreamsExceptionIfNoPartitionsFoundForStore() {
        stateManager.initialize();
        try {
            stateManager.register(store1, stateRestoreCallback);
            Assert.fail("Should have raised a StreamsException as there are no partition for the store");
        } catch (final StreamsException e) {
            // pass
        }
    }

    @Test
    public void shouldNotConvertValuesIfStoreDoesNotImplementTimestampedBytesStore() {
        initializeConsumer(1, 0, t1);
        stateManager.initialize();
        stateManager.register(store1, stateRestoreCallback);
        final KeyValue<byte[], byte[]> restoredRecord = stateRestoreCallback.restored.get(0);
        Assert.assertEquals(3, restoredRecord.key.length);
        Assert.assertEquals(5, restoredRecord.value.length);
    }

    @Test
    public void shouldNotConvertValuesIfInnerStoreDoesNotImplementTimestampedBytesStore() {
        initializeConsumer(1, 0, t1);
        stateManager.initialize();
        stateManager.register(new org.apache.kafka.streams.state.internals.WrappedStateStore<NoOpReadOnlyStore<Object, Object>, Object, Object>(store1) {}, stateRestoreCallback);
        final KeyValue<byte[], byte[]> restoredRecord = stateRestoreCallback.restored.get(0);
        Assert.assertEquals(3, restoredRecord.key.length);
        Assert.assertEquals(5, restoredRecord.value.length);
    }

    @Test
    public void shouldConvertValuesIfStoreImplementsTimestampedBytesStore() {
        initializeConsumer(1, 0, t2);
        stateManager.initialize();
        stateManager.register(store2, stateRestoreCallback);
        final KeyValue<byte[], byte[]> restoredRecord = stateRestoreCallback.restored.get(0);
        Assert.assertEquals(3, restoredRecord.key.length);
        Assert.assertEquals(13, restoredRecord.value.length);
    }

    @Test
    public void shouldConvertValuesIfInnerStoreImplementsTimestampedBytesStore() {
        initializeConsumer(1, 0, t2);
        stateManager.initialize();
        stateManager.register(new org.apache.kafka.streams.state.internals.WrappedStateStore<NoOpReadOnlyStore<Object, Object>, Object, Object>(store2) {}, stateRestoreCallback);
        final KeyValue<byte[], byte[]> restoredRecord = stateRestoreCallback.restored.get(0);
        Assert.assertEquals(3, restoredRecord.key.length);
        Assert.assertEquals(13, restoredRecord.value.length);
    }

    @Test
    public void shouldRestoreRecordsUpToHighwatermark() {
        initializeConsumer(2, 0, t1);
        stateManager.initialize();
        stateManager.register(store1, stateRestoreCallback);
        Assert.assertEquals(2, stateRestoreCallback.restored.size());
    }

    @Test
    public void shouldRecoverFromInvalidOffsetExceptionAndRestoreRecords() {
        initializeConsumer(2, 0, t1);
        consumer.setException(new InvalidOffsetException("Try Again!") {
            public Set<TopicPartition> partitions() {
                return Collections.singleton(t1);
            }
        });
        stateManager.initialize();
        stateManager.register(store1, stateRestoreCallback);
        Assert.assertEquals(2, stateRestoreCallback.restored.size());
    }

    @Test
    public void shouldListenForRestoreEvents() {
        initializeConsumer(5, 1, t1);
        stateManager.initialize();
        stateManager.register(store1, stateRestoreCallback);
        MatcherAssert.assertThat(stateRestoreListener.restoreStartOffset, CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat(stateRestoreListener.restoreEndOffset, CoreMatchers.equalTo(6L));
        MatcherAssert.assertThat(stateRestoreListener.totalNumRestored, CoreMatchers.equalTo(5L));
        MatcherAssert.assertThat(stateRestoreListener.storeNameCalledStates.get(MockStateRestoreListener.RESTORE_START), CoreMatchers.equalTo(store1.name()));
        MatcherAssert.assertThat(stateRestoreListener.storeNameCalledStates.get(MockStateRestoreListener.RESTORE_BATCH), CoreMatchers.equalTo(store1.name()));
        MatcherAssert.assertThat(stateRestoreListener.storeNameCalledStates.get(MockStateRestoreListener.RESTORE_END), CoreMatchers.equalTo(store1.name()));
    }

    @Test
    public void shouldRestoreRecordsFromCheckpointToHighwatermark() throws IOException {
        initializeConsumer(5, 5, t1);
        final OffsetCheckpoint offsetCheckpoint = new OffsetCheckpoint(new File(stateManager.baseDir(), CHECKPOINT_FILE_NAME));
        offsetCheckpoint.write(Collections.singletonMap(t1, 5L));
        stateManager.initialize();
        stateManager.register(store1, stateRestoreCallback);
        Assert.assertEquals(5, stateRestoreCallback.restored.size());
    }

    @Test
    public void shouldFlushStateStores() {
        stateManager.initialize();
        // register the stores
        initializeConsumer(1, 0, t1);
        stateManager.register(store1, stateRestoreCallback);
        initializeConsumer(1, 0, t2);
        stateManager.register(store2, stateRestoreCallback);
        stateManager.flush();
        Assert.assertTrue(store1.flushed);
        Assert.assertTrue(store2.flushed);
    }

    @Test(expected = ProcessorStateException.class)
    public void shouldThrowProcessorStateStoreExceptionIfStoreFlushFailed() {
        stateManager.initialize();
        // register the stores
        initializeConsumer(1, 0, t1);
        stateManager.register(new NoOpReadOnlyStore(store1.name()) {
            @Override
            public void flush() {
                throw new RuntimeException("KABOOM!");
            }
        }, stateRestoreCallback);
        stateManager.flush();
    }

    @Test
    public void shouldCloseStateStores() throws IOException {
        stateManager.initialize();
        // register the stores
        initializeConsumer(1, 0, t1);
        stateManager.register(store1, stateRestoreCallback);
        initializeConsumer(1, 0, t2);
        stateManager.register(store2, stateRestoreCallback);
        stateManager.close(true);
        Assert.assertFalse(store1.isOpen());
        Assert.assertFalse(store2.isOpen());
    }

    @Test(expected = ProcessorStateException.class)
    public void shouldThrowProcessorStateStoreExceptionIfStoreCloseFailed() throws IOException {
        stateManager.initialize();
        initializeConsumer(1, 0, t1);
        stateManager.register(new NoOpReadOnlyStore(store1.name()) {
            @Override
            public void close() {
                throw new RuntimeException("KABOOM!");
            }
        }, stateRestoreCallback);
        stateManager.close(true);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfCallbackIsNull() {
        stateManager.initialize();
        try {
            stateManager.register(store1, null);
            Assert.fail("should have thrown due to null callback");
        } catch (final IllegalArgumentException e) {
            // pass
        }
    }

    @Test
    public void shouldUnlockGlobalStateDirectoryOnClose() throws IOException {
        stateManager.initialize();
        stateManager.close(true);
        final StateDirectory stateDir = new StateDirectory(streamsConfig, new MockTime(), true);
        try {
            // should be able to get the lock now as it should've been released in close
            Assert.assertTrue(stateDir.lockGlobalState());
        } finally {
            stateDir.unlockGlobalState();
        }
    }

    @Test
    public void shouldNotCloseStoresIfCloseAlreadyCalled() throws IOException {
        stateManager.initialize();
        initializeConsumer(1, 0, t1);
        stateManager.register(new NoOpReadOnlyStore("t1-store") {
            @Override
            public void close() {
                if (!(isOpen())) {
                    throw new RuntimeException("store already closed");
                }
                super.close();
            }
        }, stateRestoreCallback);
        stateManager.close(true);
        stateManager.close(true);
    }

    @Test
    public void shouldAttemptToCloseAllStoresEvenWhenSomeException() throws IOException {
        stateManager.initialize();
        initializeConsumer(1, 0, t1);
        final NoOpReadOnlyStore store = new NoOpReadOnlyStore("t1-store") {
            @Override
            public void close() {
                super.close();
                throw new RuntimeException("KABOOM!");
            }
        };
        stateManager.register(store, stateRestoreCallback);
        initializeConsumer(1, 0, t2);
        stateManager.register(store2, stateRestoreCallback);
        try {
            stateManager.close(true);
        } catch (final ProcessorStateException e) {
            // expected
        }
        Assert.assertFalse(store.isOpen());
        Assert.assertFalse(store2.isOpen());
    }

    @Test
    public void shouldReleaseLockIfExceptionWhenLoadingCheckpoints() throws IOException {
        writeCorruptCheckpoint();
        try {
            stateManager.initialize();
        } catch (final StreamsException e) {
            // expected
        }
        final StateDirectory stateDir = new StateDirectory(streamsConfig, new MockTime(), true);
        try {
            // should be able to get the lock now as it should've been released
            Assert.assertTrue(stateDir.lockGlobalState());
        } finally {
            stateDir.unlockGlobalState();
        }
    }

    @Test
    public void shouldCheckpointOffsets() throws IOException {
        final Map<TopicPartition, Long> offsets = Collections.singletonMap(t1, 25L);
        stateManager.initialize();
        stateManager.checkpoint(offsets);
        final Map<TopicPartition, Long> result = readOffsetsCheckpoint();
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(offsets));
        MatcherAssert.assertThat(stateManager.checkpointed(), CoreMatchers.equalTo(offsets));
    }

    @Test
    public void shouldNotRemoveOffsetsOfUnUpdatedTablesDuringCheckpoint() {
        stateManager.initialize();
        initializeConsumer(10, 0, t1);
        stateManager.register(store1, stateRestoreCallback);
        initializeConsumer(20, 0, t2);
        stateManager.register(store2, stateRestoreCallback);
        final Map<TopicPartition, Long> initialCheckpoint = stateManager.checkpointed();
        stateManager.checkpoint(Collections.singletonMap(t1, 101L));
        final Map<TopicPartition, Long> updatedCheckpoint = stateManager.checkpointed();
        MatcherAssert.assertThat(updatedCheckpoint.get(t2), CoreMatchers.equalTo(initialCheckpoint.get(t2)));
        MatcherAssert.assertThat(updatedCheckpoint.get(t1), CoreMatchers.equalTo(101L));
    }

    @Test
    public void shouldSkipNullKeysWhenRestoring() {
        final HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        startOffsets.put(t1, 1L);
        final HashMap<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(t1, 3L);
        consumer.updatePartitions(t1.topic(), Collections.singletonList(new PartitionInfo(t1.topic(), t1.partition(), null, null, null)));
        consumer.assign(Collections.singletonList(t1));
        consumer.updateEndOffsets(endOffsets);
        consumer.updateBeginningOffsets(startOffsets);
        consumer.addRecord(new org.apache.kafka.clients.consumer.ConsumerRecord(t1.topic(), t1.partition(), 1, null, "null".getBytes()));
        final byte[] expectedKey = "key".getBytes();
        final byte[] expectedValue = "value".getBytes();
        consumer.addRecord(new org.apache.kafka.clients.consumer.ConsumerRecord(t1.topic(), t1.partition(), 2, expectedKey, expectedValue));
        stateManager.initialize();
        stateManager.register(store1, stateRestoreCallback);
        final KeyValue<byte[], byte[]> restoredKv = stateRestoreCallback.restored.get(0);
        MatcherAssert.assertThat(stateRestoreCallback.restored, CoreMatchers.equalTo(Collections.singletonList(KeyValue.pair(restoredKv.key, restoredKv.value))));
    }

    @Test
    public void shouldCheckpointRestoredOffsetsToFile() throws IOException {
        stateManager.initialize();
        initializeConsumer(10, 0, t1);
        stateManager.register(store1, stateRestoreCallback);
        stateManager.checkpoint(Collections.emptyMap());
        stateManager.close(true);
        final Map<TopicPartition, Long> checkpointMap = stateManager.checkpointed();
        MatcherAssert.assertThat(checkpointMap, CoreMatchers.equalTo(Collections.singletonMap(t1, 10L)));
        MatcherAssert.assertThat(readOffsetsCheckpoint(), CoreMatchers.equalTo(checkpointMap));
    }

    @Test
    public void shouldSkipGlobalInMemoryStoreOffsetsToFile() throws IOException {
        stateManager.initialize();
        initializeConsumer(10, 0, t3);
        stateManager.register(store3, stateRestoreCallback);
        stateManager.close(true);
        MatcherAssert.assertThat(readOffsetsCheckpoint(), CoreMatchers.equalTo(Collections.emptyMap()));
    }

    @Test
    public void shouldThrowLockExceptionIfIOExceptionCaughtWhenTryingToLockStateDir() {
        stateManager = new GlobalStateManagerImpl(new LogContext("mock"), topology, consumer, new StateDirectory(streamsConfig, time, true) {
            @Override
            public boolean lockGlobalState() throws IOException {
                throw new IOException("KABOOM!");
            }
        }, stateRestoreListener, streamsConfig);
        try {
            stateManager.initialize();
            Assert.fail("Should have thrown LockException");
        } catch (final LockException e) {
            // pass
        }
    }

    @Test
    public void shouldRetryWhenEndOffsetsThrowsTimeoutException() {
        final int retries = 2;
        final AtomicInteger numberOfCalls = new AtomicInteger(0);
        consumer = new MockConsumer<byte[], byte[]>(OffsetResetStrategy.EARLIEST) {
            @Override
            public synchronized Map<TopicPartition, Long> endOffsets(final Collection<TopicPartition> partitions) {
                numberOfCalls.incrementAndGet();
                throw new TimeoutException();
            }
        };
        streamsConfig = new StreamsConfig(new Properties() {
            {
                put(APPLICATION_ID_CONFIG, "appId");
                put(BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
                put(STATE_DIR_CONFIG, TestUtils.tempDirectory().getPath());
                put(RETRIES_CONFIG, retries);
            }
        });
        try {
            new GlobalStateManagerImpl(new LogContext("mock"), topology, consumer, stateDirectory, stateRestoreListener, streamsConfig);
        } catch (final StreamsException expected) {
            Assert.assertEquals(numberOfCalls.get(), retries);
        }
    }

    @Test
    public void shouldRetryWhenPartitionsForThrowsTimeoutException() {
        final int retries = 2;
        final AtomicInteger numberOfCalls = new AtomicInteger(0);
        consumer = new MockConsumer<byte[], byte[]>(OffsetResetStrategy.EARLIEST) {
            @Override
            public synchronized List<PartitionInfo> partitionsFor(final String topic) {
                numberOfCalls.incrementAndGet();
                throw new TimeoutException();
            }
        };
        streamsConfig = new StreamsConfig(new Properties() {
            {
                put(APPLICATION_ID_CONFIG, "appId");
                put(BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
                put(STATE_DIR_CONFIG, TestUtils.tempDirectory().getPath());
                put(RETRIES_CONFIG, retries);
            }
        });
        try {
            new GlobalStateManagerImpl(new LogContext("mock"), topology, consumer, stateDirectory, stateRestoreListener, streamsConfig);
        } catch (final StreamsException expected) {
            Assert.assertEquals(numberOfCalls.get(), retries);
        }
    }

    @Test
    public void shouldDeleteAndRecreateStoreDirectoryOnReinitialize() throws IOException {
        final File storeDirectory1 = new File((((((stateDirectory.globalStateDir().getAbsolutePath()) + (File.separator)) + "rocksdb") + (File.separator)) + (storeName1)));
        final File storeDirectory2 = new File((((((stateDirectory.globalStateDir().getAbsolutePath()) + (File.separator)) + "rocksdb") + (File.separator)) + (storeName2)));
        final File storeDirectory3 = new File((((stateDirectory.globalStateDir().getAbsolutePath()) + (File.separator)) + (storeName3)));
        final File storeDirectory4 = new File((((stateDirectory.globalStateDir().getAbsolutePath()) + (File.separator)) + (storeName4)));
        final File testFile1 = new File((((storeDirectory1.getAbsolutePath()) + (File.separator)) + "testFile"));
        final File testFile2 = new File((((storeDirectory2.getAbsolutePath()) + (File.separator)) + "testFile"));
        final File testFile3 = new File((((storeDirectory3.getAbsolutePath()) + (File.separator)) + "testFile"));
        final File testFile4 = new File((((storeDirectory4.getAbsolutePath()) + (File.separator)) + "testFile"));
        consumer.updatePartitions(t1.topic(), Collections.singletonList(new PartitionInfo(t1.topic(), t1.partition(), null, null, null)));
        consumer.updatePartitions(t2.topic(), Collections.singletonList(new PartitionInfo(t2.topic(), t2.partition(), null, null, null)));
        consumer.updatePartitions(t3.topic(), Collections.singletonList(new PartitionInfo(t3.topic(), t3.partition(), null, null, null)));
        consumer.updatePartitions(t4.topic(), Collections.singletonList(new PartitionInfo(t4.topic(), t4.partition(), null, null, null)));
        consumer.updateBeginningOffsets(new HashMap<TopicPartition, Long>() {
            {
                put(t1, 0L);
                put(t2, 0L);
                put(t3, 0L);
                put(t4, 0L);
            }
        });
        consumer.updateEndOffsets(new HashMap<TopicPartition, Long>() {
            {
                put(t1, 0L);
                put(t2, 0L);
                put(t3, 0L);
                put(t4, 0L);
            }
        });
        stateManager.initialize();
        stateManager.register(store1, stateRestoreCallback);
        stateManager.register(store2, stateRestoreCallback);
        stateManager.register(store3, stateRestoreCallback);
        stateManager.register(store4, stateRestoreCallback);
        testFile1.createNewFile();
        Assert.assertTrue(testFile1.exists());
        testFile2.createNewFile();
        Assert.assertTrue(testFile2.exists());
        testFile3.createNewFile();
        Assert.assertTrue(testFile3.exists());
        testFile4.createNewFile();
        Assert.assertTrue(testFile4.exists());
        // only delete and recreate store 1 and 3 -- 2 and 4 must be untouched
        stateManager.reinitializeStateStoresForPartitions(Arrays.asList(t1, t3), processorContext);
        Assert.assertFalse(testFile1.exists());
        Assert.assertTrue(testFile2.exists());
        Assert.assertFalse(testFile3.exists());
        Assert.assertTrue(testFile4.exists());
    }

    private static class TheStateRestoreCallback implements StateRestoreCallback {
        private final List<KeyValue<byte[], byte[]>> restored = new ArrayList<>();

        @Override
        public void restore(final byte[] key, final byte[] value) {
            restored.add(KeyValue.pair(key, value));
        }
    }

    private class ConverterStore<K, V> extends NoOpReadOnlyStore<K, V> implements TimestampedBytesStore {
        ConverterStore(final String name, final boolean rocksdbStore) {
            super(name, rocksdbStore);
        }
    }
}

