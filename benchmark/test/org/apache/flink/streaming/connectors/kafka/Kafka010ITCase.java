/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.kafka;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.streaming.api.operators.StreamSink;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;


/**
 * IT cases for Kafka 0.10 .
 */
public class Kafka010ITCase extends KafkaConsumerTestBase {
    // ------------------------------------------------------------------------
    // Suite of Tests
    // ------------------------------------------------------------------------
    @Test(timeout = 60000)
    public void testFailOnNoBroker() throws Exception {
        runFailOnNoBrokerTest();
    }

    @Test(timeout = 60000)
    public void testConcurrentProducerConsumerTopology() throws Exception {
        runSimpleConcurrentProducerConsumerTopology();
    }

    @Test(timeout = 60000)
    public void testKeyValueSupport() throws Exception {
        runKeyValueTest();
    }

    // --- canceling / failures ---
    @Test(timeout = 60000)
    public void testCancelingEmptyTopic() throws Exception {
        runCancelingOnEmptyInputTest();
    }

    @Test(timeout = 60000)
    public void testCancelingFullTopic() throws Exception {
        runCancelingOnFullInputTest();
    }

    // --- source to partition mappings and exactly once ---
    @Test(timeout = 60000)
    public void testOneToOneSources() throws Exception {
        runOneToOneExactlyOnceTest();
    }

    @Test(timeout = 60000)
    public void testOneSourceMultiplePartitions() throws Exception {
        runOneSourceMultiplePartitionsExactlyOnceTest();
    }

    @Test(timeout = 60000)
    public void testMultipleSourcesOnePartition() throws Exception {
        runMultipleSourcesOnePartitionExactlyOnceTest();
    }

    // --- broker failure ---
    @Test(timeout = 60000)
    public void testBrokerFailure() throws Exception {
        runBrokerFailureTest();
    }

    // --- special executions ---
    @Test(timeout = 60000)
    public void testBigRecordJob() throws Exception {
        runBigRecordTestTopology();
    }

    @Test(timeout = 60000)
    public void testMultipleTopics() throws Exception {
        runProduceConsumeMultipleTopics();
    }

    @Test(timeout = 60000)
    public void testAllDeletes() throws Exception {
        runAllDeletesTest();
    }

    @Test(timeout = 60000)
    public void testMetricsAndEndOfStream() throws Exception {
        runEndOfStreamTest();
    }

    // --- startup mode ---
    @Test(timeout = 60000)
    public void testStartFromEarliestOffsets() throws Exception {
        runStartFromEarliestOffsets();
    }

    @Test(timeout = 60000)
    public void testStartFromLatestOffsets() throws Exception {
        runStartFromLatestOffsets();
    }

    @Test(timeout = 60000)
    public void testStartFromGroupOffsets() throws Exception {
        runStartFromGroupOffsets();
    }

    @Test(timeout = 60000)
    public void testStartFromSpecificOffsets() throws Exception {
        runStartFromSpecificOffsets();
    }

    @Test(timeout = 60000)
    public void testStartFromTimestamp() throws Exception {
        runStartFromTimestamp();
    }

    // --- offset committing ---
    @Test(timeout = 60000)
    public void testCommitOffsetsToKafka() throws Exception {
        runCommitOffsetsToKafka();
    }

    @Test(timeout = 60000)
    public void testAutoOffsetRetrievalAndCommitToKafka() throws Exception {
        runAutoOffsetRetrievalAndCommitToKafka();
    }

    private static class TimestampValidatingOperator extends StreamSink<Long> {
        private static final long serialVersionUID = 1353168781235526806L;

        public TimestampValidatingOperator() {
            super(new org.apache.flink.streaming.api.functions.sink.SinkFunction<Long>() {
                private static final long serialVersionUID = -6676565693361786524L;

                @Override
                public void invoke(Long value) throws Exception {
                    throw new RuntimeException("Unexpected");
                }
            });
        }

        long elCount = 0;

        long wmCount = 0;

        long lastWM = Long.MIN_VALUE;

        @Override
        public void processElement(StreamRecord<Long> element) throws Exception {
            (elCount)++;
            if (((element.getValue()) * 2) != (element.getTimestamp())) {
                throw new RuntimeException(("Invalid timestamp: " + element));
            }
        }

        @Override
        public void processWatermark(Watermark mark) throws Exception {
            (wmCount)++;
            if ((lastWM) <= (mark.getTimestamp())) {
                lastWM = mark.getTimestamp();
            } else {
                throw new RuntimeException("Received watermark higher than the last one");
            }
            if ((((mark.getTimestamp()) % 10) != 0) && ((mark.getTimestamp()) != (Long.MAX_VALUE))) {
                throw new RuntimeException(("Invalid watermark: " + (mark.getTimestamp())));
            }
        }

        @Override
        public void close() throws Exception {
            super.close();
            if ((elCount) != 1000L) {
                throw new RuntimeException(("Wrong final element count " + (elCount)));
            }
            if ((wmCount) <= 2) {
                throw new RuntimeException(("Almost no watermarks have been sent " + (wmCount)));
            }
        }
    }

    private static class LimitedLongDeserializer implements KafkaDeserializationSchema<Long> {
        private static final long serialVersionUID = 6966177118923713521L;

        private final TypeInformation<Long> ti;

        private final TypeSerializer<Long> ser;

        long cnt = 0;

        public LimitedLongDeserializer() {
            this.ti = Types.LONG;
            this.ser = ti.createSerializer(new ExecutionConfig());
        }

        @Override
        public TypeInformation<Long> getProducedType() {
            return ti;
        }

        @Override
        public Long deserialize(ConsumerRecord<byte[], byte[]> record) throws IOException {
            (cnt)++;
            DataInputView in = new DataInputViewStreamWrapper(new ByteArrayInputStream(record.value()));
            Long e = ser.deserialize(in);
            return e;
        }

        @Override
        public boolean isEndOfStream(Long nextElement) {
            return (cnt) > 1000L;
        }
    }
}

