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
package org.apache.flink.streaming.connectors.kafka;


import java.util.Collections;
import java.util.concurrent.Future;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.streaming.connectors.kafka.testutils.FakeStandardProducerConfig;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.util.TestLogger;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests for the {@link KafkaProducer}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FlinkKafkaProducerBase.class)
public class KafkaProducerTest extends TestLogger {
    @Test
    @SuppressWarnings("unchecked")
    public void testPropagateExceptions() {
        try {
            // mock kafka producer
            KafkaProducer<?, ?> kafkaProducerMock = Mockito.mock(KafkaProducer.class);
            // partition setup
            // returning a unmodifiable list to mimic KafkaProducer#partitionsFor() behaviour
            Mockito.when(kafkaProducerMock.partitionsFor(ArgumentMatchers.anyString())).thenReturn(Collections.singletonList(new PartitionInfo("mock_topic", 42, null, null, null)));
            // failure when trying to send an element
            Mockito.when(kafkaProducerMock.send(ArgumentMatchers.any(ProducerRecord.class), ArgumentMatchers.any(Callback.class))).thenAnswer(new Answer<Future<RecordMetadata>>() {
                @Override
                public Future<RecordMetadata> answer(InvocationOnMock invocation) throws Throwable {
                    Callback callback = ((Callback) (invocation.getArguments()[1]));
                    callback.onCompletion(null, new Exception("Test error"));
                    return null;
                }
            });
            // make sure the FlinkKafkaProducer instantiates our mock producer
            whenNew(KafkaProducer.class).withAnyArguments().thenReturn(kafkaProducerMock);
            // (1) producer that propagates errors
            FlinkKafkaProducer08<String> producerPropagating = new FlinkKafkaProducer08("mock_topic", new SimpleStringSchema(), FakeStandardProducerConfig.get(), ((FlinkKafkaPartitioner) (null)));
            OneInputStreamOperatorTestHarness<String, Object> testHarness = new OneInputStreamOperatorTestHarness(new org.apache.flink.streaming.api.operators.StreamSink(producerPropagating));
            testHarness.open();
            try {
                testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord("value"));
                testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord("value"));
                Assert.fail("This should fail with an exception");
            } catch (Exception e) {
                Assert.assertNotNull(e.getCause());
                Assert.assertNotNull(e.getCause().getMessage());
                Assert.assertTrue(e.getCause().getMessage().contains("Test error"));
            }
            testHarness.close();
            // (2) producer that only logs errors
            FlinkKafkaProducer08<String> producerLogging = new FlinkKafkaProducer08("mock_topic", new SimpleStringSchema(), FakeStandardProducerConfig.get(), ((FlinkKafkaPartitioner) (null)));
            producerLogging.setLogFailuresOnly(true);
            testHarness = new OneInputStreamOperatorTestHarness(new org.apache.flink.streaming.api.operators.StreamSink(producerLogging));
            testHarness.open();
            testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord("value"));
            testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord("value"));
            testHarness.close();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

