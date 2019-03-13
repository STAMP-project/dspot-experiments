/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.integration;


import com.google.common.collect.ImmutableMap;
import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.serde.DataSource;
import io.confluent.ksql.serde.DataSource.DataSourceSerDe;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ IntegrationTest.class })
public class UdfIntTest {
    private static final String JSON_TOPIC_NAME = "jsonTopic";

    private static final String JSON_STREAM_NAME = "orders_json";

    private static final String AVRO_TOPIC_NAME = "avroTopic";

    private static final String AVRO_STREAM_NAME = "orders_avro";

    private static final String DELIMITED_TOPIC_NAME = "delimitedTopic";

    private static final String DELIMITED_STREAM_NAME = "items_delimited";

    private static Map<String, RecordMetadata> jsonRecordMetadataMap;

    private static Map<String, RecordMetadata> avroRecordMetadataMap;

    @ClassRule
    public static final IntegrationTestHarness TEST_HARNESS = IntegrationTestHarness.build();

    @Rule
    public final TestKsqlContext ksqlContext = UdfIntTest.TEST_HARNESS.buildKsqlContext();

    private final UdfIntTest.TestData testData;

    private String resultStreamName;

    private String intermediateStream;

    public UdfIntTest(final DataSource.DataSourceSerDe format) {
        switch (format) {
            case AVRO :
                this.testData = new UdfIntTest.TestData(format, UdfIntTest.AVRO_TOPIC_NAME, UdfIntTest.AVRO_STREAM_NAME, UdfIntTest.avroRecordMetadataMap);
                break;
            case JSON :
                this.testData = new UdfIntTest.TestData(format, UdfIntTest.JSON_TOPIC_NAME, UdfIntTest.JSON_STREAM_NAME, UdfIntTest.jsonRecordMetadataMap);
                break;
            default :
                this.testData = new UdfIntTest.TestData(format, UdfIntTest.DELIMITED_TOPIC_NAME, UdfIntTest.DELIMITED_STREAM_NAME, ImmutableMap.of());
                break;
        }
    }

    @Test
    public void testApplyUdfsToColumns() {
        Assume.assumeThat(testData.format, Matchers.is(Matchers.not(DELIMITED)));
        // Given:
        final String queryString = String.format(("CREATE STREAM \"%s\" AS SELECT " + ((((("ITEMID, " + "ORDERUNITS*10, ") + "PRICEARRAY[0]+10, ") + "KEYVALUEMAP['key1'] * KEYVALUEMAP['key2']+10, ") + "PRICEARRAY[1] > 1000 ") + "FROM %s WHERE ORDERUNITS > 20 AND ITEMID LIKE '%%_8';")), resultStreamName, testData.sourceStreamName);
        // When:
        ksqlContext.sql(queryString);
        // Then:
        final Map<String, GenericRow> results = consumeOutputMessages();
        MatcherAssert.assertThat(results, Matchers.is(ImmutableMap.of("8", new GenericRow(Arrays.asList("ITEM_8", 800.0, 1110.0, 12.0, true)))));
    }

    @Test
    public void testShouldCastSelectedColumns() {
        Assume.assumeThat(testData.format, Matchers.is(Matchers.not(DELIMITED)));
        // Given:
        final String queryString = String.format(("CREATE STREAM \"%s\" AS SELECT " + (((("CAST (ORDERUNITS AS INTEGER), " + "CAST( PRICEARRAY[1]>1000 AS STRING), ") + "CAST (SUBSTRING(ITEMID, 6) AS DOUBLE), ") + "CAST(ORDERUNITS AS VARCHAR) ") + "FROM %s WHERE ORDERUNITS > 20 AND ITEMID LIKE '%%_8';")), resultStreamName, testData.sourceStreamName);
        // When:
        ksqlContext.sql(queryString);
        // Then:
        final Map<String, GenericRow> results = consumeOutputMessages();
        MatcherAssert.assertThat(results, Matchers.is(ImmutableMap.of("8", new GenericRow(Arrays.asList(80, "true", 8.0, "80.0")))));
    }

    @Test
    public void testTimestampColumnSelection() {
        Assume.assumeThat(testData.format, Matchers.is(Matchers.not(DELIMITED)));
        // Given:
        final String queryString = String.format(("CREATE STREAM \"%s\" AS SELECT " + ((((("ROWKEY AS RKEY, ROWTIME+10000 AS RTIME, ROWTIME+100 AS RT100, ORDERID, ITEMID " + "FROM %s WHERE ORDERUNITS > 20 AND ITEMID = 'ITEM_8';") + "") + "CREATE STREAM \"%s\" AS SELECT ") + "ROWKEY AS NEWRKEY, ROWTIME AS NEWRTIME, RKEY, RTIME, RT100, ORDERID, ITEMID ") + "FROM %s;")), intermediateStream, testData.sourceStreamName, resultStreamName, intermediateStream);
        // When:
        ksqlContext.sql(queryString);
        // Then:
        final Map<String, GenericRow> results = consumeOutputMessages();
        final long ts = testData.recordMetadata.get("8").timestamp();
        MatcherAssert.assertThat(results, CoreMatchers.equalTo(ImmutableMap.of("8", new GenericRow(Arrays.asList("8", ts, "8", (ts + 10000), (ts + 100), "ORDER_6", "ITEM_8")))));
    }

    @Test
    public void testApplyUdfsToColumnsDelimited() {
        Assume.assumeThat(testData.format, Matchers.is(DELIMITED));
        // Given:
        final String queryString = String.format("CREATE STREAM \"%s\" AS SELECT ID, DESCRIPTION FROM %s WHERE ID LIKE \'%%_1\';", resultStreamName, UdfIntTest.DELIMITED_STREAM_NAME);
        // When:
        ksqlContext.sql(queryString);
        // Then:
        final Map<String, GenericRow> results = consumeOutputMessages();
        MatcherAssert.assertThat(results, CoreMatchers.equalTo(Collections.singletonMap("ITEM_1", new GenericRow(Arrays.asList("ITEM_1", "home cinema")))));
    }

    private static class TestData {
        private final DataSourceSerDe format;

        private final String sourceStreamName;

        private final String sourceTopicName;

        private final Map<String, RecordMetadata> recordMetadata;

        private TestData(final DataSourceSerDe format, final String sourceTopicName, final String sourceStreamName, final Map<String, RecordMetadata> recordMetadata) {
            this.format = format;
            this.sourceStreamName = sourceStreamName;
            this.sourceTopicName = sourceTopicName;
            this.recordMetadata = recordMetadata;
        }
    }
}

