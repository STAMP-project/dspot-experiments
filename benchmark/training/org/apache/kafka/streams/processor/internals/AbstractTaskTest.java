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
import StreamsConfig.STATE_DIR_CONFIG;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LockException;
import org.apache.kafka.streams.errors.ProcessorStateException;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.apache.kafka.test.MockRestoreCallback;
import org.apache.kafka.test.TestUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class AbstractTaskTest {
    private final TaskId id = new TaskId(0, 0);

    private StateDirectory stateDirectory = EasyMock.createMock(StateDirectory.class);

    private final TopicPartition storeTopicPartition1 = new TopicPartition("t1", 0);

    private final TopicPartition storeTopicPartition2 = new TopicPartition("t2", 0);

    private final TopicPartition storeTopicPartition3 = new TopicPartition("t3", 0);

    private final TopicPartition storeTopicPartition4 = new TopicPartition("t4", 0);

    private final Collection<TopicPartition> storeTopicPartitions = Utils.mkSet(storeTopicPartition1, storeTopicPartition2, storeTopicPartition3, storeTopicPartition4);

    @Test(expected = ProcessorStateException.class)
    public void shouldThrowProcessorStateExceptionOnInitializeOffsetsWhenAuthorizationException() {
        final Consumer consumer = mockConsumer(new AuthorizationException("blah"));
        final AbstractTask task = createTask(consumer, Collections.<StateStore, String>emptyMap());
        task.updateOffsetLimits();
    }

    @Test(expected = ProcessorStateException.class)
    public void shouldThrowProcessorStateExceptionOnInitializeOffsetsWhenKafkaException() {
        final Consumer consumer = mockConsumer(new KafkaException("blah"));
        final AbstractTask task = createTask(consumer, Collections.<StateStore, String>emptyMap());
        task.updateOffsetLimits();
    }

    @Test(expected = WakeupException.class)
    public void shouldThrowWakeupExceptionOnInitializeOffsetsWhenWakeupException() {
        final Consumer consumer = mockConsumer(new WakeupException());
        final AbstractTask task = createTask(consumer, Collections.<StateStore, String>emptyMap());
        task.updateOffsetLimits();
    }

    @Test
    public void shouldThrowLockExceptionIfFailedToLockStateDirectoryWhenTopologyHasStores() throws IOException {
        final Consumer consumer = EasyMock.createNiceMock(Consumer.class);
        final StateStore store = EasyMock.createNiceMock(StateStore.class);
        expect(stateDirectory.lock(id)).andReturn(false);
        EasyMock.replay(stateDirectory);
        final AbstractTask task = createTask(consumer, Collections.singletonMap(store, "dummy"));
        try {
            task.registerStateStores();
            Assert.fail("Should have thrown LockException");
        } catch (final LockException e) {
            // ok
        }
    }

    @Test
    public void shouldNotAttemptToLockIfNoStores() {
        final Consumer consumer = EasyMock.createNiceMock(Consumer.class);
        EasyMock.replay(stateDirectory);
        final AbstractTask task = createTask(consumer, Collections.<StateStore, String>emptyMap());
        task.registerStateStores();
        // should fail if lock is called
        EasyMock.verify(stateDirectory);
    }

    @Test
    public void shouldDeleteAndRecreateStoreDirectoryOnReinitialize() throws IOException {
        final StreamsConfig streamsConfig = new StreamsConfig(new Properties() {
            {
                put(APPLICATION_ID_CONFIG, "app-id");
                put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                put(STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());
            }
        });
        final Consumer consumer = EasyMock.createNiceMock(Consumer.class);
        final StateStore store1 = EasyMock.createNiceMock(StateStore.class);
        final StateStore store2 = EasyMock.createNiceMock(StateStore.class);
        final StateStore store3 = EasyMock.createNiceMock(StateStore.class);
        final StateStore store4 = EasyMock.createNiceMock(StateStore.class);
        final String storeName1 = "storeName1";
        final String storeName2 = "storeName2";
        final String storeName3 = "storeName3";
        final String storeName4 = "storeName4";
        expect(store1.name()).andReturn(storeName1).anyTimes();
        EasyMock.replay(store1);
        expect(store2.name()).andReturn(storeName2).anyTimes();
        EasyMock.replay(store2);
        expect(store3.name()).andReturn(storeName3).anyTimes();
        EasyMock.replay(store3);
        expect(store4.name()).andReturn(storeName4).anyTimes();
        EasyMock.replay(store4);
        final StateDirectory stateDirectory = new StateDirectory(streamsConfig, new MockTime(), true);
        final AbstractTask task = createTask(consumer, new HashMap<StateStore, String>() {
            {
                put(store1, storeTopicPartition1.topic());
                put(store2, storeTopicPartition2.topic());
                put(store3, storeTopicPartition3.topic());
                put(store4, storeTopicPartition4.topic());
            }
        }, stateDirectory);
        final String taskDir = stateDirectory.directoryForTask(task.id).getAbsolutePath();
        final File storeDirectory1 = new File(((((taskDir + (File.separator)) + "rocksdb") + (File.separator)) + storeName1));
        final File storeDirectory2 = new File(((((taskDir + (File.separator)) + "rocksdb") + (File.separator)) + storeName2));
        final File storeDirectory3 = new File(((taskDir + (File.separator)) + storeName3));
        final File storeDirectory4 = new File(((taskDir + (File.separator)) + storeName4));
        final File testFile1 = new File((((storeDirectory1.getAbsolutePath()) + (File.separator)) + "testFile"));
        final File testFile2 = new File((((storeDirectory2.getAbsolutePath()) + (File.separator)) + "testFile"));
        final File testFile3 = new File((((storeDirectory3.getAbsolutePath()) + (File.separator)) + "testFile"));
        final File testFile4 = new File((((storeDirectory4.getAbsolutePath()) + (File.separator)) + "testFile"));
        storeDirectory1.mkdirs();
        storeDirectory2.mkdirs();
        storeDirectory3.mkdirs();
        storeDirectory4.mkdirs();
        testFile1.createNewFile();
        Assert.assertTrue(testFile1.exists());
        testFile2.createNewFile();
        Assert.assertTrue(testFile2.exists());
        testFile3.createNewFile();
        Assert.assertTrue(testFile3.exists());
        testFile4.createNewFile();
        Assert.assertTrue(testFile4.exists());
        task.processorContext = new InternalMockProcessorContext(stateDirectory.directoryForTask(task.id), streamsConfig);
        task.stateMgr.register(store1, new MockRestoreCallback());
        task.stateMgr.register(store2, new MockRestoreCallback());
        task.stateMgr.register(store3, new MockRestoreCallback());
        task.stateMgr.register(store4, new MockRestoreCallback());
        // only reinitialize store1 and store3 -- store2 and store4 should be untouched
        task.reinitializeStateStoresForPartitions(Utils.mkSet(storeTopicPartition1, storeTopicPartition3));
        Assert.assertFalse(testFile1.exists());
        Assert.assertTrue(testFile2.exists());
        Assert.assertFalse(testFile3.exists());
        Assert.assertTrue(testFile4.exists());
    }
}

