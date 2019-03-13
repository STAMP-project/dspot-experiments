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


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.test.GlobalStateManagerStub;
import org.apache.kafka.test.MockProcessorNode;
import org.apache.kafka.test.MockSourceNode;
import org.apache.kafka.test.NoOpProcessorContext;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class GlobalStateTaskTest {
    private final LogContext logContext = new LogContext();

    private final String topic1 = "t1";

    private final String topic2 = "t2";

    private final TopicPartition t1 = new TopicPartition(topic1, 1);

    private final TopicPartition t2 = new TopicPartition(topic2, 1);

    private final MockSourceNode sourceOne = new MockSourceNode(new String[]{ topic1 }, new StringDeserializer(), new StringDeserializer());

    private final MockSourceNode sourceTwo = new MockSourceNode(new String[]{ topic2 }, new IntegerDeserializer(), new IntegerDeserializer());

    private final MockProcessorNode processorOne = new MockProcessorNode<>();

    private final MockProcessorNode processorTwo = new MockProcessorNode<>();

    private final Map<TopicPartition, Long> offsets = new HashMap<>();

    private final NoOpProcessorContext context = new NoOpProcessorContext();

    private ProcessorTopology topology;

    private GlobalStateManagerStub stateMgr;

    private GlobalStateUpdateTask globalStateTask;

    @Test
    public void shouldInitializeStateManager() {
        final Map<TopicPartition, Long> startingOffsets = globalStateTask.initialize();
        Assert.assertTrue(stateMgr.initialized);
        Assert.assertEquals(offsets, startingOffsets);
    }

    @Test
    public void shouldInitializeContext() {
        globalStateTask.initialize();
        Assert.assertTrue(context.initialized);
    }

    @Test
    public void shouldInitializeProcessorTopology() {
        globalStateTask.initialize();
        Assert.assertTrue(sourceOne.initialized);
        Assert.assertTrue(sourceTwo.initialized);
        Assert.assertTrue(processorOne.initialized);
        Assert.assertTrue(processorTwo.initialized);
    }

    @Test
    public void shouldProcessRecordsForTopic() {
        globalStateTask.initialize();
        globalStateTask.update(new org.apache.kafka.clients.consumer.ConsumerRecord(topic1, 1, 1, "foo".getBytes(), "bar".getBytes()));
        Assert.assertEquals(1, sourceOne.numReceived);
        Assert.assertEquals(0, sourceTwo.numReceived);
    }

    @Test
    public void shouldProcessRecordsForOtherTopic() {
        final byte[] integerBytes = new IntegerSerializer().serialize("foo", 1);
        globalStateTask.initialize();
        globalStateTask.update(new org.apache.kafka.clients.consumer.ConsumerRecord(topic2, 1, 1, integerBytes, integerBytes));
        Assert.assertEquals(1, sourceTwo.numReceived);
        Assert.assertEquals(0, sourceOne.numReceived);
    }

    @Test
    public void shouldThrowStreamsExceptionWhenKeyDeserializationFails() {
        final byte[] key = new LongSerializer().serialize(topic2, 1L);
        final byte[] recordValue = new IntegerSerializer().serialize(topic2, 10);
        maybeDeserialize(globalStateTask, key, recordValue, true);
    }

    @Test
    public void shouldThrowStreamsExceptionWhenValueDeserializationFails() {
        final byte[] key = new IntegerSerializer().serialize(topic2, 1);
        final byte[] recordValue = new LongSerializer().serialize(topic2, 10L);
        maybeDeserialize(globalStateTask, key, recordValue, true);
    }

    @Test
    public void shouldNotThrowStreamsExceptionWhenKeyDeserializationFailsWithSkipHandler() {
        final GlobalStateUpdateTask globalStateTask2 = new GlobalStateUpdateTask(topology, context, stateMgr, new LogAndContinueExceptionHandler(), logContext);
        final byte[] key = new LongSerializer().serialize(topic2, 1L);
        final byte[] recordValue = new IntegerSerializer().serialize(topic2, 10);
        maybeDeserialize(globalStateTask2, key, recordValue, false);
    }

    @Test
    public void shouldNotThrowStreamsExceptionWhenValueDeserializationFails() {
        final GlobalStateUpdateTask globalStateTask2 = new GlobalStateUpdateTask(topology, context, stateMgr, new LogAndContinueExceptionHandler(), logContext);
        final byte[] key = new IntegerSerializer().serialize(topic2, 1);
        final byte[] recordValue = new LongSerializer().serialize(topic2, 10L);
        maybeDeserialize(globalStateTask2, key, recordValue, false);
    }

    @Test
    public void shouldFlushStateManagerWithOffsets() throws IOException {
        final Map<TopicPartition, Long> expectedOffsets = new HashMap<>();
        expectedOffsets.put(t1, 52L);
        expectedOffsets.put(t2, 100L);
        globalStateTask.initialize();
        globalStateTask.update(new org.apache.kafka.clients.consumer.ConsumerRecord(topic1, 1, 51, "foo".getBytes(), "foo".getBytes()));
        globalStateTask.flushState();
        Assert.assertEquals(expectedOffsets, stateMgr.checkpointed());
    }

    @Test
    public void shouldCheckpointOffsetsWhenStateIsFlushed() {
        final Map<TopicPartition, Long> expectedOffsets = new HashMap<>();
        expectedOffsets.put(t1, 102L);
        expectedOffsets.put(t2, 100L);
        globalStateTask.initialize();
        globalStateTask.update(new org.apache.kafka.clients.consumer.ConsumerRecord(topic1, 1, 101, "foo".getBytes(), "foo".getBytes()));
        globalStateTask.flushState();
        MatcherAssert.assertThat(stateMgr.checkpointed(), CoreMatchers.equalTo(expectedOffsets));
    }
}

