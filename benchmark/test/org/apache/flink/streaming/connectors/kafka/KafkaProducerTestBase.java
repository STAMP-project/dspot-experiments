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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.CheckpointListener;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.flink.test.util.SuccessException;
import org.apache.flink.util.Preconditions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract test base for all Kafka producer tests.
 */
@SuppressWarnings("serial")
public abstract class KafkaProducerTestBase extends KafkaTestBaseWithFlink {
    private static final long KAFKA_READ_TIMEOUT = 60000L;

    /**
     * This tests verifies that custom partitioning works correctly, with a default topic
     * and dynamic topic. The number of partitions for each topic is deliberately different.
     *
     * <p>Test topology:
     *
     * <pre>
     *             +------> (sink) --+--> [DEFAULT_TOPIC-1] --> (source) -> (map) -----+
     *            /                  |                             |          |        |
     *           |                   |                             |          |  ------+--> (sink)
     *             +------> (sink) --+--> [DEFAULT_TOPIC-2] --> (source) -> (map) -----+
     *            /                  |
     *           |                   |
     * (source) ----------> (sink) --+--> [DYNAMIC_TOPIC-1] --> (source) -> (map) -----+
     *           |                   |                             |          |        |
     *            \                  |                             |          |        |
     *             +------> (sink) --+--> [DYNAMIC_TOPIC-2] --> (source) -> (map) -----+--> (sink)
     *           |                   |                             |          |        |
     *            \                  |                             |          |        |
     *             +------> (sink) --+--> [DYNAMIC_TOPIC-3] --> (source) -> (map) -----+
     * </pre>
     *
     * <p>Each topic has an independent mapper that validates the values come consistently from
     * the correct Kafka partition of the topic is is responsible of.
     *
     * <p>Each topic also has a final sink that validates that there are no duplicates and that all
     * partitions are present.
     */
    @Test
    public void testCustomPartitioning() {
        try {
            KafkaTestBase.LOG.info("Starting KafkaProducerITCase.testCustomPartitioning()");
            final String defaultTopic = "defaultTopic";
            final int defaultTopicPartitions = 2;
            final String dynamicTopic = "dynamicTopic";
            final int dynamicTopicPartitions = 3;
            KafkaTestBase.createTestTopic(defaultTopic, defaultTopicPartitions, 1);
            KafkaTestBase.createTestTopic(dynamicTopic, dynamicTopicPartitions, 1);
            Map<String, Integer> expectedTopicsToNumPartitions = new HashMap<>(2);
            expectedTopicsToNumPartitions.put(defaultTopic, defaultTopicPartitions);
            expectedTopicsToNumPartitions.put(dynamicTopic, dynamicTopicPartitions);
            TypeInformation<Tuple2<Long, String>> longStringInfo = TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<Tuple2<Long, String>>() {});
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setRestartStrategy(RestartStrategies.noRestart());
            env.getConfig().disableSysoutLogging();
            TypeInformationSerializationSchema<Tuple2<Long, String>> serSchema = new TypeInformationSerializationSchema(longStringInfo, env.getConfig());
            TypeInformationSerializationSchema<Tuple2<Long, String>> deserSchema = new TypeInformationSerializationSchema(longStringInfo, env.getConfig());
            // ------ producing topology ---------
            // source has DOP 1 to make sure it generates no duplicates
            DataStream<Tuple2<Long, String>> stream = env.addSource(new org.apache.flink.streaming.api.functions.source.SourceFunction<Tuple2<Long, String>>() {
                private boolean running = true;

                @Override
                public void run(SourceContext<Tuple2<Long, String>> ctx) throws Exception {
                    long cnt = 0;
                    while (running) {
                        ctx.collect(new Tuple2<Long, String>(cnt, ("kafka-" + cnt)));
                        cnt++;
                        if ((cnt % 100) == 0) {
                            Thread.sleep(1);
                        }
                    } 
                }

                @Override
                public void cancel() {
                    running = false;
                }
            }).setParallelism(1);
            Properties props = new Properties();
            props.putAll(FlinkKafkaProducerBase.getPropertiesFromBrokerList(KafkaTestBase.brokerConnectionStrings));
            props.putAll(KafkaTestBase.secureProps);
            // sink partitions into
            // this serialization schema will route between the default topic and dynamic topic
            KafkaTestBase.kafkaServer.produceIntoKafka(stream, defaultTopic, new KafkaProducerTestBase.CustomKeyedSerializationSchemaWrapper(serSchema, defaultTopic, dynamicTopic), props, new KafkaProducerTestBase.CustomPartitioner(expectedTopicsToNumPartitions)).setParallelism(Math.max(defaultTopicPartitions, dynamicTopicPartitions));
            // ------ consuming topology ---------
            Properties consumerProps = new Properties();
            consumerProps.putAll(KafkaTestBase.standardProps);
            consumerProps.putAll(KafkaTestBase.secureProps);
            FlinkKafkaConsumerBase<Tuple2<Long, String>> defaultTopicSource = KafkaTestBase.kafkaServer.getConsumer(defaultTopic, deserSchema, consumerProps);
            FlinkKafkaConsumerBase<Tuple2<Long, String>> dynamicTopicSource = KafkaTestBase.kafkaServer.getConsumer(dynamicTopic, deserSchema, consumerProps);
            env.addSource(defaultTopicSource).setParallelism(defaultTopicPartitions).map(new KafkaProducerTestBase.PartitionValidatingMapper(defaultTopicPartitions)).setParallelism(defaultTopicPartitions).addSink(new KafkaProducerTestBase.PartitionValidatingSink(defaultTopicPartitions)).setParallelism(1);
            env.addSource(dynamicTopicSource).setParallelism(dynamicTopicPartitions).map(new KafkaProducerTestBase.PartitionValidatingMapper(dynamicTopicPartitions)).setParallelism(dynamicTopicPartitions).addSink(new KafkaProducerTestBase.PartitionValidatingSink(dynamicTopicPartitions)).setParallelism(1);
            tryExecute(env, "custom partitioning test");
            KafkaTestBase.deleteTestTopic(defaultTopic);
            KafkaTestBase.deleteTestTopic(dynamicTopic);
            KafkaTestBase.LOG.info("Finished KafkaProducerITCase.testCustomPartitioning()");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests the at-least-once semantic for the simple writes into Kafka.
     */
    @Test
    public void testOneToOneAtLeastOnceRegularSink() throws Exception {
        testOneToOneAtLeastOnce(true);
    }

    /**
     * Tests the at-least-once semantic for the simple writes into Kafka.
     */
    @Test
    public void testOneToOneAtLeastOnceCustomOperator() throws Exception {
        testOneToOneAtLeastOnce(false);
    }

    /**
     * Tests the exactly-once semantic for the simple writes into Kafka.
     */
    @Test
    public void testExactlyOnceRegularSink() throws Exception {
        testExactlyOnce(true, 1);
    }

    /**
     * Tests the exactly-once semantic for the simple writes into Kafka.
     */
    @Test
    public void testExactlyOnceCustomOperator() throws Exception {
        testExactlyOnce(false, 1);
    }

    // ------------------------------------------------------------------------
    private static class CustomPartitioner extends FlinkKafkaPartitioner<Tuple2<Long, String>> implements Serializable {
        private final Map<String, Integer> expectedTopicsToNumPartitions;

        public CustomPartitioner(Map<String, Integer> expectedTopicsToNumPartitions) {
            this.expectedTopicsToNumPartitions = expectedTopicsToNumPartitions;
        }

        @Override
        public int partition(Tuple2<Long, String> next, byte[] serializedKey, byte[] serializedValue, String topic, int[] partitions) {
            Assert.assertEquals(expectedTopicsToNumPartitions.get(topic).intValue(), partitions.length);
            return ((int) ((next.f0) % (partitions.length)));
        }
    }

    /**
     * A {@link KeyedSerializationSchemaWrapper} that supports routing serialized records to different target topics.
     */
    public static class CustomKeyedSerializationSchemaWrapper extends KeyedSerializationSchemaWrapper<Tuple2<Long, String>> {
        private final String defaultTopic;

        private final String dynamicTopic;

        public CustomKeyedSerializationSchemaWrapper(SerializationSchema<Tuple2<Long, String>> serializationSchema, String defaultTopic, String dynamicTopic) {
            super(serializationSchema);
            this.defaultTopic = Preconditions.checkNotNull(defaultTopic);
            this.dynamicTopic = Preconditions.checkNotNull(dynamicTopic);
        }

        @Override
        public String getTargetTopic(Tuple2<Long, String> element) {
            return ((element.f0) % 2) == 0 ? defaultTopic : dynamicTopic;
        }
    }

    /**
     * Mapper that validates partitioning and maps to partition.
     */
    public static class PartitionValidatingMapper extends RichMapFunction<Tuple2<Long, String>, Integer> {
        private final int numPartitions;

        private int ourPartition = -1;

        public PartitionValidatingMapper(int numPartitions) {
            this.numPartitions = numPartitions;
        }

        @Override
        public Integer map(Tuple2<Long, String> value) throws Exception {
            int partition = (value.f0.intValue()) % (numPartitions);
            if ((ourPartition) != (-1)) {
                Assert.assertEquals("inconsistent partitioning", ourPartition, partition);
            } else {
                ourPartition = partition;
            }
            return partition;
        }
    }

    /**
     * Sink that validates records received from each partition and checks that there are no duplicates.
     */
    public static class PartitionValidatingSink implements SinkFunction<Integer> {
        private final int[] valuesPerPartition;

        public PartitionValidatingSink(int numPartitions) {
            this.valuesPerPartition = new int[numPartitions];
        }

        @Override
        public void invoke(Integer value) throws Exception {
            (valuesPerPartition[value])++;
            boolean missing = false;
            for (int i : valuesPerPartition) {
                if (i < 100) {
                    missing = true;
                    break;
                }
            }
            if (!missing) {
                throw new SuccessException();
            }
        }
    }

    private static class BrokerRestartingMapper<T> extends RichMapFunction<T, T> implements CheckpointListener , CheckpointedFunction {
        private static final long serialVersionUID = 6334389850158707313L;

        public static volatile boolean triggeredShutdown;

        public static volatile int lastSnapshotedElementBeforeShutdown;

        public static volatile Runnable shutdownAction;

        private final int failCount;

        private int numElementsTotal;

        private boolean failer;

        public static void resetState(Runnable shutdownAction) {
            KafkaProducerTestBase.BrokerRestartingMapper.triggeredShutdown = false;
            KafkaProducerTestBase.BrokerRestartingMapper.lastSnapshotedElementBeforeShutdown = 0;
            KafkaProducerTestBase.BrokerRestartingMapper.shutdownAction = shutdownAction;
        }

        public BrokerRestartingMapper(int failCount) {
            this.failCount = failCount;
        }

        @Override
        public void open(Configuration parameters) {
            failer = (KafkaProducerTestBase.BrokerRestartingMapper.getRuntimeContext().getIndexOfThisSubtask()) == 0;
        }

        @Override
        public T map(T value) throws Exception {
            (numElementsTotal)++;
            Thread.sleep(10);
            if (((!(KafkaProducerTestBase.BrokerRestartingMapper.triggeredShutdown)) && (failer)) && ((numElementsTotal) >= (failCount))) {
                // shut down a Kafka broker
                KafkaProducerTestBase.BrokerRestartingMapper.triggeredShutdown = true;
                KafkaProducerTestBase.BrokerRestartingMapper.shutdownAction.run();
            }
            return value;
        }

        @Override
        public void notifyCheckpointComplete(long checkpointId) {
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            if (!(KafkaProducerTestBase.BrokerRestartingMapper.triggeredShutdown)) {
                KafkaProducerTestBase.BrokerRestartingMapper.lastSnapshotedElementBeforeShutdown = numElementsTotal;
            }
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
        }
    }
}

