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
package org.apache.flink.streaming.connectors.elasticsearch6;


import ElasticsearchSink.Builder;
import ElasticsearchSinkBase.FlushBackoffType.EXPONENTIAL;
import Types.BOOLEAN;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.formats.json.JsonRowSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.operators.ChainingStrategy;
import org.apache.flink.streaming.api.transformations.StreamTransformation;
import org.apache.flink.streaming.connectors.elasticsearch.ActionRequestFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchUpsertTableSinkBase.ElasticsearchUpsertSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchUpsertTableSinkBase.Host;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchUpsertTableSinkBase.SinkOption;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchUpsertTableSinkFactoryTestBase;
import org.apache.flink.streaming.connectors.elasticsearch6.Elasticsearch6UpsertTableSink.DefaultRestClientFactory;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.types.Row;
import org.apache.http.HttpHost;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link Elasticsearch6UpsertTableSink} created by {@link Elasticsearch6UpsertTableSinkFactory}.
 */
public class Elasticsearch6UpsertTableSinkFactoryTest extends ElasticsearchUpsertTableSinkFactoryTestBase {
    @Test
    public void testBuilder() {
        final TableSchema schema = createTestSchema();
        final Elasticsearch6UpsertTableSinkFactoryTest.TestElasticsearch6UpsertTableSink testSink = new Elasticsearch6UpsertTableSinkFactoryTest.TestElasticsearch6UpsertTableSink(false, schema, Collections.singletonList(new Host(HOSTNAME, PORT, SCHEMA)), INDEX, DOC_TYPE, KEY_DELIMITER, KEY_NULL_LITERAL, new JsonRowSerializationSchema(schema.toRowType()), XContentType.JSON, new DummyFailureHandler(), createTestSinkOptions());
        final Elasticsearch6UpsertTableSinkFactoryTest.DataStreamMock dataStreamMock = new Elasticsearch6UpsertTableSinkFactoryTest.DataStreamMock(new Elasticsearch6UpsertTableSinkFactoryTest.StreamExecutionEnvironmentMock(), Types.TUPLE(BOOLEAN, schema.toRowType()));
        emitDataStream(dataStreamMock);
        final Builder<Tuple2<Boolean, Row>> expectedBuilder = new ElasticsearchSink.Builder<>(Collections.singletonList(new <HOSTNAME, PORT, SCHEMA>HttpHost()), new <INDEX, DOC_TYPE, KEY_DELIMITER, KEY_NULL_LITERAL>ElasticsearchUpsertSinkFunction(new JsonRowSerializationSchema(schema.toRowType()), XContentType.JSON, Elasticsearch6UpsertTableSink.UPDATE_REQUEST_FACTORY, new int[0]));
        expectedBuilder.setFailureHandler(new DummyFailureHandler());
        expectedBuilder.setBulkFlushBackoff(true);
        expectedBuilder.setBulkFlushBackoffType(EXPONENTIAL);
        expectedBuilder.setBulkFlushBackoffDelay(123);
        expectedBuilder.setBulkFlushBackoffRetries(3);
        expectedBuilder.setBulkFlushInterval(100);
        expectedBuilder.setBulkFlushMaxActions(1000);
        expectedBuilder.setBulkFlushMaxSizeMb(1);
        expectedBuilder.setRestClientFactory(new DefaultRestClientFactory(100, "/myapp"));
        Assert.assertEquals(expectedBuilder, testSink.builder);
    }

    // --------------------------------------------------------------------------------------------
    // Helper classes
    // --------------------------------------------------------------------------------------------
    private static class TestElasticsearch6UpsertTableSink extends Elasticsearch6UpsertTableSink {
        public Builder<Tuple2<Boolean, Row>> builder;

        public TestElasticsearch6UpsertTableSink(boolean isAppendOnly, TableSchema schema, List<Host> hosts, String index, String docType, String keyDelimiter, String keyNullLiteral, SerializationSchema<Row> serializationSchema, XContentType contentType, ActionRequestFailureHandler failureHandler, Map<SinkOption, String> sinkOptions) {
            super(isAppendOnly, schema, hosts, index, docType, keyDelimiter, keyNullLiteral, serializationSchema, contentType, failureHandler, sinkOptions);
        }

        @Override
        protected Builder<Tuple2<Boolean, Row>> createBuilder(ElasticsearchUpsertSinkFunction upsertSinkFunction, List<HttpHost> httpHosts) {
            builder = super.createBuilder(upsertSinkFunction, httpHosts);
            return builder;
        }
    }

    private static class StreamExecutionEnvironmentMock extends StreamExecutionEnvironment {
        @Override
        public JobExecutionResult execute(String jobName) {
            throw new UnsupportedOperationException();
        }
    }

    private static class DataStreamMock extends DataStream<Tuple2<Boolean, Row>> {
        public SinkFunction<?> sinkFunction;

        public DataStreamMock(StreamExecutionEnvironment environment, TypeInformation<Tuple2<Boolean, Row>> outType) {
            super(environment, new Elasticsearch6UpsertTableSinkFactoryTest.StreamTransformationMock("name", outType, 1));
        }

        @Override
        public DataStreamSink<Tuple2<Boolean, Row>> addSink(SinkFunction<Tuple2<Boolean, Row>> sinkFunction) {
            this.sinkFunction = sinkFunction;
            return super.addSink(sinkFunction);
        }
    }

    private static class StreamTransformationMock extends StreamTransformation<Tuple2<Boolean, Row>> {
        public StreamTransformationMock(String name, TypeInformation<Tuple2<Boolean, Row>> outputType, int parallelism) {
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

