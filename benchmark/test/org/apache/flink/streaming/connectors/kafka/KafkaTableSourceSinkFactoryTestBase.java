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


import StartupMode.SPECIFIC_OFFSETS;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.operators.ChainingStrategy;
import org.apache.flink.streaming.api.transformations.StreamTransformation;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Rowtime;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.descriptors.TestTableDescriptor;
import org.apache.flink.table.factories.StreamTableSinkFactory;
import org.apache.flink.table.factories.StreamTableSourceFactory;
import org.apache.flink.table.factories.TableFactoryService;
import org.apache.flink.table.factories.utils.TestDeserializationSchema;
import org.apache.flink.table.factories.utils.TestTableFormat;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.table.sources.TableSourceUtil;
import org.apache.flink.table.sources.tsextractors.ExistingField;
import org.apache.flink.table.sources.wmstrategies.AscendingTimestamps;
import org.apache.flink.types.Row;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract test base for {@link KafkaTableSourceSinkFactoryBase}.
 */
public abstract class KafkaTableSourceSinkFactoryTestBase extends TestLogger {
    private static final String TOPIC = "myTopic";

    private static final int PARTITION_0 = 0;

    private static final long OFFSET_0 = 100L;

    private static final int PARTITION_1 = 1;

    private static final long OFFSET_1 = 123L;

    private static final String FRUIT_NAME = "fruit-name";

    private static final String NAME = "name";

    private static final String COUNT = "count";

    private static final String TIME = "time";

    private static final String EVENT_TIME = "event-time";

    private static final String PROC_TIME = "proc-time";

    private static final Properties KAFKA_PROPERTIES = new Properties();

    static {
        KafkaTableSourceSinkFactoryTestBase.KAFKA_PROPERTIES.setProperty("zookeeper.connect", "dummy");
        KafkaTableSourceSinkFactoryTestBase.KAFKA_PROPERTIES.setProperty("group.id", "dummy");
        KafkaTableSourceSinkFactoryTestBase.KAFKA_PROPERTIES.setProperty("bootstrap.servers", "dummy");
    }

    private static final Map<Integer, Long> OFFSETS = new HashMap<>();

    static {
        KafkaTableSourceSinkFactoryTestBase.OFFSETS.put(KafkaTableSourceSinkFactoryTestBase.PARTITION_0, KafkaTableSourceSinkFactoryTestBase.OFFSET_0);
        KafkaTableSourceSinkFactoryTestBase.OFFSETS.put(KafkaTableSourceSinkFactoryTestBase.PARTITION_1, KafkaTableSourceSinkFactoryTestBase.OFFSET_1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTableSource() {
        // prepare parameters for Kafka table source
        final TableSchema schema = TableSchema.builder().field(KafkaTableSourceSinkFactoryTestBase.FRUIT_NAME, Types.STRING()).field(KafkaTableSourceSinkFactoryTestBase.COUNT, Types.DECIMAL()).field(KafkaTableSourceSinkFactoryTestBase.EVENT_TIME, Types.SQL_TIMESTAMP()).field(KafkaTableSourceSinkFactoryTestBase.PROC_TIME, Types.SQL_TIMESTAMP()).build();
        final List<RowtimeAttributeDescriptor> rowtimeAttributeDescriptors = Collections.singletonList(new RowtimeAttributeDescriptor(KafkaTableSourceSinkFactoryTestBase.EVENT_TIME, new ExistingField(KafkaTableSourceSinkFactoryTestBase.TIME), new AscendingTimestamps()));
        final Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put(KafkaTableSourceSinkFactoryTestBase.FRUIT_NAME, KafkaTableSourceSinkFactoryTestBase.NAME);
        fieldMapping.put(KafkaTableSourceSinkFactoryTestBase.NAME, KafkaTableSourceSinkFactoryTestBase.NAME);
        fieldMapping.put(KafkaTableSourceSinkFactoryTestBase.COUNT, KafkaTableSourceSinkFactoryTestBase.COUNT);
        fieldMapping.put(KafkaTableSourceSinkFactoryTestBase.TIME, KafkaTableSourceSinkFactoryTestBase.TIME);
        final Map<KafkaTopicPartition, Long> specificOffsets = new HashMap<>();
        specificOffsets.put(new KafkaTopicPartition(KafkaTableSourceSinkFactoryTestBase.TOPIC, KafkaTableSourceSinkFactoryTestBase.PARTITION_0), KafkaTableSourceSinkFactoryTestBase.OFFSET_0);
        specificOffsets.put(new KafkaTopicPartition(KafkaTableSourceSinkFactoryTestBase.TOPIC, KafkaTableSourceSinkFactoryTestBase.PARTITION_1), KafkaTableSourceSinkFactoryTestBase.OFFSET_1);
        final TestDeserializationSchema deserializationSchema = new TestDeserializationSchema(TableSchema.builder().field(KafkaTableSourceSinkFactoryTestBase.NAME, Types.STRING()).field(KafkaTableSourceSinkFactoryTestBase.COUNT, Types.DECIMAL()).field(KafkaTableSourceSinkFactoryTestBase.TIME, Types.SQL_TIMESTAMP()).build().toRowType());
        final KafkaTableSourceBase expected = getExpectedKafkaTableSource(schema, Optional.of(KafkaTableSourceSinkFactoryTestBase.PROC_TIME), rowtimeAttributeDescriptors, fieldMapping, KafkaTableSourceSinkFactoryTestBase.TOPIC, KafkaTableSourceSinkFactoryTestBase.KAFKA_PROPERTIES, deserializationSchema, SPECIFIC_OFFSETS, specificOffsets);
        TableSourceUtil.validateTableSource(expected);
        // construct table source using descriptors and table source factory
        final TestTableDescriptor testDesc = new TestTableDescriptor(// test if accepted although not needed
        new Kafka().version(getKafkaVersion()).topic(KafkaTableSourceSinkFactoryTestBase.TOPIC).properties(KafkaTableSourceSinkFactoryTestBase.KAFKA_PROPERTIES).sinkPartitionerRoundRobin().startFromSpecificOffsets(KafkaTableSourceSinkFactoryTestBase.OFFSETS)).withFormat(new TestTableFormat()).withSchema(// no from so it must match with the input
        new Schema().field(KafkaTableSourceSinkFactoryTestBase.FRUIT_NAME, Types.STRING()).from(KafkaTableSourceSinkFactoryTestBase.NAME).field(KafkaTableSourceSinkFactoryTestBase.COUNT, Types.DECIMAL()).field(KafkaTableSourceSinkFactoryTestBase.EVENT_TIME, Types.SQL_TIMESTAMP()).rowtime(new Rowtime().timestampsFromField(KafkaTableSourceSinkFactoryTestBase.TIME).watermarksPeriodicAscending()).field(KafkaTableSourceSinkFactoryTestBase.PROC_TIME, Types.SQL_TIMESTAMP()).proctime()).inAppendMode();
        final Map<String, String> propertiesMap = testDesc.toProperties();
        final TableSource<?> actualSource = TableFactoryService.find(StreamTableSourceFactory.class, propertiesMap).createStreamTableSource(propertiesMap);
        Assert.assertEquals(expected, actualSource);
        // test Kafka consumer
        final KafkaTableSourceBase actualKafkaSource = ((KafkaTableSourceBase) (actualSource));
        final KafkaTableSourceSinkFactoryTestBase.StreamExecutionEnvironmentMock mock = new KafkaTableSourceSinkFactoryTestBase.StreamExecutionEnvironmentMock();
        actualKafkaSource.getDataStream(mock);
        Assert.assertTrue(getExpectedFlinkKafkaConsumer().isAssignableFrom(mock.sourceFunction.getClass()));
    }

    /**
     * This test can be unified with the corresponding source test once we have fixed FLINK-9870.
     */
    @Test
    public void testTableSink() {
        // prepare parameters for Kafka table sink
        final TableSchema schema = TableSchema.builder().field(KafkaTableSourceSinkFactoryTestBase.FRUIT_NAME, Types.STRING()).field(KafkaTableSourceSinkFactoryTestBase.COUNT, Types.DECIMAL()).field(KafkaTableSourceSinkFactoryTestBase.EVENT_TIME, Types.SQL_TIMESTAMP()).build();
        final KafkaTableSinkBase expected = getExpectedKafkaTableSink(schema, KafkaTableSourceSinkFactoryTestBase.TOPIC, KafkaTableSourceSinkFactoryTestBase.KAFKA_PROPERTIES, Optional.of(new org.apache.flink.streaming.connectors.kafka.partitioner.FlinkFixedPartitioner()), new org.apache.flink.table.factories.utils.TestSerializationSchema(schema.toRowType()));
        // construct table sink using descriptors and table sink factory
        final TestTableDescriptor testDesc = // test if they accepted although not needed
        new TestTableDescriptor(new Kafka().version(getKafkaVersion()).topic(KafkaTableSourceSinkFactoryTestBase.TOPIC).properties(KafkaTableSourceSinkFactoryTestBase.KAFKA_PROPERTIES).sinkPartitionerFixed().startFromSpecificOffsets(KafkaTableSourceSinkFactoryTestBase.OFFSETS)).withFormat(new TestTableFormat()).withSchema(new Schema().field(KafkaTableSourceSinkFactoryTestBase.FRUIT_NAME, Types.STRING()).field(KafkaTableSourceSinkFactoryTestBase.COUNT, Types.DECIMAL()).field(KafkaTableSourceSinkFactoryTestBase.EVENT_TIME, Types.SQL_TIMESTAMP())).inAppendMode();
        final Map<String, String> propertiesMap = testDesc.toProperties();
        final TableSink<?> actualSink = TableFactoryService.find(StreamTableSinkFactory.class, propertiesMap).createStreamTableSink(propertiesMap);
        Assert.assertEquals(expected, actualSink);
        // test Kafka producer
        final KafkaTableSinkBase actualKafkaSink = ((KafkaTableSinkBase) (actualSink));
        final KafkaTableSourceSinkFactoryTestBase.DataStreamMock streamMock = new KafkaTableSourceSinkFactoryTestBase.DataStreamMock(new KafkaTableSourceSinkFactoryTestBase.StreamExecutionEnvironmentMock(), schema.toRowType());
        actualKafkaSink.emitDataStream(streamMock);
        Assert.assertTrue(getExpectedFlinkKafkaProducer().isAssignableFrom(streamMock.sinkFunction.getClass()));
    }

    private static class StreamExecutionEnvironmentMock extends StreamExecutionEnvironment {
        public SourceFunction<?> sourceFunction;

        @Override
        public <OUT> DataStreamSource<OUT> addSource(SourceFunction<OUT> sourceFunction) {
            this.sourceFunction = sourceFunction;
            return super.addSource(sourceFunction);
        }

        @Override
        public JobExecutionResult execute(String jobName) {
            throw new UnsupportedOperationException();
        }
    }

    private static class DataStreamMock extends DataStream<Row> {
        public SinkFunction<?> sinkFunction;

        public DataStreamMock(StreamExecutionEnvironment environment, TypeInformation<Row> outType) {
            super(environment, new KafkaTableSourceSinkFactoryTestBase.StreamTransformationMock("name", outType, 1));
        }

        @Override
        public DataStreamSink<Row> addSink(SinkFunction<Row> sinkFunction) {
            this.sinkFunction = sinkFunction;
            return super.addSink(sinkFunction);
        }
    }

    private static class StreamTransformationMock extends StreamTransformation<Row> {
        public StreamTransformationMock(String name, TypeInformation<Row> outputType, int parallelism) {
            super(name, outputType, parallelism);
        }

        @Override
        public void setChainingStrategy(ChainingStrategy strategy) {
            // do nothing
        }

        @Override
        public Collection<StreamTransformation<?>> getTransitivePredecessors() {
            return null;
        }
    }
}

