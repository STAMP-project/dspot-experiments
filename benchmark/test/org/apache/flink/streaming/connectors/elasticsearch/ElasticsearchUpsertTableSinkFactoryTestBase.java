/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.elasticsearch;


import XContentType.JSON;
import java.util.Collections;
import java.util.Map;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchUpsertTableSinkBase.Host;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.descriptors.Elasticsearch;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.descriptors.TestTableDescriptor;
import org.apache.flink.table.factories.StreamTableSinkFactory;
import org.apache.flink.table.factories.TableFactoryService;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.util.TestLogger;
import org.elasticsearch.action.ActionRequest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Version-agnostic test base for {@link ElasticsearchUpsertTableSinkFactoryBase}.
 */
public abstract class ElasticsearchUpsertTableSinkFactoryTestBase extends TestLogger {
    protected static final String HOSTNAME = "host1";

    protected static final int PORT = 1234;

    protected static final String SCHEMA = "https";

    protected static final String INDEX = "MyIndex";

    protected static final String DOC_TYPE = "MyType";

    protected static final String KEY_DELIMITER = "#";

    protected static final String KEY_NULL_LITERAL = "";

    private static final String FIELD_KEY = "key";

    private static final String FIELD_FRUIT_NAME = "fruit_name";

    private static final String FIELD_COUNT = "count";

    private static final String FIELD_TS = "ts";

    @Test
    public void testTableSink() {
        // prepare parameters for Elasticsearch table sink
        final TableSchema schema = createTestSchema();
        final ElasticsearchUpsertTableSinkBase expectedSink = getExpectedTableSink(false, schema, Collections.singletonList(new Host(ElasticsearchUpsertTableSinkFactoryTestBase.HOSTNAME, ElasticsearchUpsertTableSinkFactoryTestBase.PORT, ElasticsearchUpsertTableSinkFactoryTestBase.SCHEMA)), ElasticsearchUpsertTableSinkFactoryTestBase.INDEX, ElasticsearchUpsertTableSinkFactoryTestBase.DOC_TYPE, ElasticsearchUpsertTableSinkFactoryTestBase.KEY_DELIMITER, ElasticsearchUpsertTableSinkFactoryTestBase.KEY_NULL_LITERAL, new org.apache.flink.formats.json.JsonRowSerializationSchema(schema.toRowType()), JSON, new ElasticsearchUpsertTableSinkFactoryTestBase.DummyFailureHandler(), createTestSinkOptions());
        // construct table sink using descriptors and table sink factory
        final TestTableDescriptor testDesc = new TestTableDescriptor(new Elasticsearch().version(getElasticsearchVersion()).host(ElasticsearchUpsertTableSinkFactoryTestBase.HOSTNAME, ElasticsearchUpsertTableSinkFactoryTestBase.PORT, ElasticsearchUpsertTableSinkFactoryTestBase.SCHEMA).index(ElasticsearchUpsertTableSinkFactoryTestBase.INDEX).documentType(ElasticsearchUpsertTableSinkFactoryTestBase.DOC_TYPE).keyDelimiter(ElasticsearchUpsertTableSinkFactoryTestBase.KEY_DELIMITER).keyNullLiteral(ElasticsearchUpsertTableSinkFactoryTestBase.KEY_NULL_LITERAL).bulkFlushBackoffExponential().bulkFlushBackoffDelay(123L).bulkFlushBackoffMaxRetries(3).bulkFlushInterval(100L).bulkFlushMaxActions(1000).bulkFlushMaxSize("1 MB").failureHandlerCustom(ElasticsearchUpsertTableSinkFactoryTestBase.DummyFailureHandler.class).connectionMaxRetryTimeout(100).connectionPathPrefix("/myapp")).withFormat(new Json().deriveSchema()).withSchema(new Schema().field(ElasticsearchUpsertTableSinkFactoryTestBase.FIELD_KEY, Types.LONG()).field(ElasticsearchUpsertTableSinkFactoryTestBase.FIELD_FRUIT_NAME, Types.STRING()).field(ElasticsearchUpsertTableSinkFactoryTestBase.FIELD_COUNT, Types.DECIMAL()).field(ElasticsearchUpsertTableSinkFactoryTestBase.FIELD_TS, Types.SQL_TIMESTAMP())).inUpsertMode();
        final Map<String, String> propertiesMap = testDesc.toProperties();
        final TableSink<?> actualSink = TableFactoryService.find(StreamTableSinkFactory.class, propertiesMap).createStreamTableSink(propertiesMap);
        Assert.assertEquals(expectedSink, actualSink);
    }

    // --------------------------------------------------------------------------------------------
    // Helper classes
    // --------------------------------------------------------------------------------------------
    /**
     * Custom failure handler for testing.
     */
    public static class DummyFailureHandler implements ActionRequestFailureHandler {
        @Override
        public void onFailure(ActionRequest action, Throwable failure, int restStatusCode, RequestIndexer indexer) {
            // do nothing
        }

        @Override
        public boolean equals(Object o) {
            return ((this) == o) || (o instanceof ElasticsearchUpsertTableSinkFactoryTestBase.DummyFailureHandler);
        }

        @Override
        public int hashCode() {
            return ElasticsearchUpsertTableSinkFactoryTestBase.DummyFailureHandler.class.hashCode();
        }
    }
}

