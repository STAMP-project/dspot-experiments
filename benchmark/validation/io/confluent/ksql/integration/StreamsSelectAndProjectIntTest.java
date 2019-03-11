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


import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.util.OrderDataProvider;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.connect.data.Schema;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class StreamsSelectAndProjectIntTest {
    private static final String JSON_STREAM_NAME = "orders_json";

    private static final String AVRO_STREAM_NAME = "orders_avro";

    private static final String AVRO_TIMESTAMP_STREAM_NAME = "orders_timestamp_avro";

    private static final OrderDataProvider DATA_PROVIDER = new OrderDataProvider();

    @ClassRule
    public static final IntegrationTestHarness TEST_HARNESS = IntegrationTestHarness.build();

    @Rule
    public final TestKsqlContext ksqlContext = StreamsSelectAndProjectIntTest.TEST_HARNESS.buildKsqlContext();

    private String jsonTopicName;

    private String avroTopicName;

    private String intermediateStream;

    private String resultStream;

    private Map<String, RecordMetadata> producedAvroRecords;

    private Map<String, RecordMetadata> producedJsonRecords;

    @Test
    public void testTimestampColumnSelectionJson() {
        testTimestampColumnSelection(StreamsSelectAndProjectIntTest.JSON_STREAM_NAME, JSON, producedJsonRecords);
    }

    @Test
    public void testTimestampColumnSelectionAvro() {
        testTimestampColumnSelection(StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME, AVRO, producedAvroRecords);
    }

    @Test
    public void testSelectProjectKeyTimestampJson() {
        testSelectProjectKeyTimestamp(StreamsSelectAndProjectIntTest.JSON_STREAM_NAME, JSON, producedJsonRecords);
    }

    @Test
    public void testSelectProjectKeyTimestampAvro() {
        testSelectProjectKeyTimestamp(StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME, AVRO, producedAvroRecords);
    }

    @Test
    public void testSelectProjectJson() {
        testSelectProject(StreamsSelectAndProjectIntTest.JSON_STREAM_NAME, JSON);
    }

    @Test
    public void testSelectProjectAvro() {
        testSelectProject(StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME, AVRO);
    }

    @Test
    public void testSelectStarJson() {
        testSelectStar(StreamsSelectAndProjectIntTest.JSON_STREAM_NAME, JSON);
    }

    @Test
    public void testSelectStarAvro() {
        testSelectStar(StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME, AVRO);
    }

    @Test
    public void testSelectWithFilterJson() {
        testSelectWithFilter(StreamsSelectAndProjectIntTest.JSON_STREAM_NAME, JSON);
    }

    @Test
    public void testSelectWithFilterAvro() {
        testSelectWithFilter(StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME, AVRO);
    }

    @Test
    public void shouldSkipBadData() {
        ksqlContext.sql(((((("CREATE STREAM " + (intermediateStream)) + " AS") + " SELECT * FROM ") + (StreamsSelectAndProjectIntTest.JSON_STREAM_NAME)) + ";"));
        StreamsSelectAndProjectIntTest.TEST_HARNESS.produceRecord(intermediateStream.toUpperCase(), "bad", "something that is not json");
        testSelectWithFilter(intermediateStream, JSON);
    }

    @Test
    public void shouldSkipBadDataAvro() {
        ksqlContext.sql(((((("CREATE STREAM " + (intermediateStream)) + " AS") + " SELECT * FROM ") + (StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME)) + ";"));
        StreamsSelectAndProjectIntTest.TEST_HARNESS.produceRecord(intermediateStream.toUpperCase(), "bad", "something that is not avro");
        testSelectWithFilter(intermediateStream, AVRO);
    }

    @Test
    public void shouldUseStringTimestampWithFormat() throws Exception {
        ksqlContext.sql((((((((((((("CREATE STREAM " + (intermediateStream)) + " WITH (timestamp='TIMESTAMP', timestamp_format='yyyy-MM-dd') AS") + " SELECT ORDERID, TIMESTAMP FROM ") + (StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME)) + " WHERE ITEMID='ITEM_6';") + "") + " CREATE STREAM ") + (resultStream)) + " AS") + " SELECT ORDERID, TIMESTAMP from ") + (intermediateStream)) + ";"));
        final List<ConsumerRecord<String, String>> records = StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableRecords(resultStream.toUpperCase(), 1);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final long timestamp = records.get(0).timestamp();
        MatcherAssert.assertThat(timestamp, CoreMatchers.equalTo(dateFormat.parse("2018-01-06").getTime()));
    }

    @Test
    public void shouldUseTimestampExtractedFromDDLStatement() throws Exception {
        ksqlContext.sql(((((("CREATE STREAM " + (resultStream)) + " WITH(timestamp='ordertime')") + " AS SELECT ORDERID, ORDERTIME FROM ") + (StreamsSelectAndProjectIntTest.AVRO_TIMESTAMP_STREAM_NAME)) + " WHERE ITEMID='ITEM_4';"));
        final List<ConsumerRecord<String, String>> records = StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableRecords(resultStream.toUpperCase(), 1);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final long timestamp = records.get(0).timestamp();
        MatcherAssert.assertThat(timestamp, CoreMatchers.equalTo(dateFormat.parse("2018-01-04").getTime()));
    }

    @Test
    public void testSelectProjectAvroJson() {
        ksqlContext.sql(String.format(("CREATE STREAM %s WITH ( value_format = 'JSON') AS SELECT " + (("ITEMID, " + "ORDERUNITS, ") + "PRICEARRAY FROM %s;")), resultStream, StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME));
        final Schema resultSchema = ksqlContext.getMetaStore().getSource(resultStream.toUpperCase()).getSchema();
        final List<ConsumerRecord<String, GenericRow>> results = StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableRows(resultStream.toUpperCase(), StreamsSelectAndProjectIntTest.DATA_PROVIDER.data().size(), JSON, resultSchema);
        final GenericRow value = results.get(0).value();
        // skip over first to values (rowKey, rowTime)
        Assert.assertEquals("ITEM_1", value.getColumns().get(2).toString());
    }

    @Test
    public void testInsertIntoJson() {
        givenStreamExists(resultStream, "ITEMID, ORDERUNITS, PRICEARRAY", StreamsSelectAndProjectIntTest.JSON_STREAM_NAME);
        ksqlContext.sql((((("INSERT INTO " + (resultStream)) + " SELECT ITEMID, ORDERUNITS, PRICEARRAY FROM ") + (StreamsSelectAndProjectIntTest.JSON_STREAM_NAME)) + ";"));
        final Schema resultSchema = ksqlContext.getMetaStore().getSource(resultStream.toUpperCase()).getSchema();
        final List<ConsumerRecord<String, GenericRow>> results = StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableRows(resultStream.toUpperCase(), StreamsSelectAndProjectIntTest.DATA_PROVIDER.data().size(), JSON, resultSchema);
        final GenericRow value = results.get(0).value();
        // skip over first to values (rowKey, rowTime)
        Assert.assertEquals("ITEM_1", value.getColumns().get(2).toString());
    }

    @Test
    public void testInsertIntoAvro() {
        givenStreamExists(resultStream, "ITEMID, ORDERUNITS, PRICEARRAY", StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME);
        ksqlContext.sql(((((("INSERT INTO " + (resultStream)) + " ") + "SELECT ITEMID, ORDERUNITS, PRICEARRAY FROM ") + (StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME)) + ";"));
        final Schema resultSchema = ksqlContext.getMetaStore().getSource(resultStream.toUpperCase()).getSchema();
        final List<ConsumerRecord<String, GenericRow>> results = StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableRows(resultStream.toUpperCase(), StreamsSelectAndProjectIntTest.DATA_PROVIDER.data().size(), AVRO, resultSchema);
        final GenericRow value = results.get(0).value();
        // skip over first to values (rowKey, rowTime)
        Assert.assertEquals("ITEM_1", value.getColumns().get(2).toString());
    }

    @Test
    public void testInsertSelectStarJson() {
        givenStreamExists(resultStream, "*", StreamsSelectAndProjectIntTest.JSON_STREAM_NAME);
        ksqlContext.sql((((("INSERT INTO " + (resultStream)) + " SELECT * FROM ") + (StreamsSelectAndProjectIntTest.JSON_STREAM_NAME)) + ";"));
        final Map<String, GenericRow> results = StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableUniqueRows(resultStream.toUpperCase(), StreamsSelectAndProjectIntTest.DATA_PROVIDER.data().size(), JSON, StreamsSelectAndProjectIntTest.DATA_PROVIDER.schema());
        MatcherAssert.assertThat(results, Matchers.is(StreamsSelectAndProjectIntTest.DATA_PROVIDER.data()));
    }

    @Test
    public void testInsertSelectStarAvro() {
        givenStreamExists(resultStream, "*", StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME);
        ksqlContext.sql((((("INSERT INTO " + (resultStream)) + " SELECT * FROM ") + (StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME)) + ";"));
        final Map<String, GenericRow> results = StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableUniqueRows(resultStream.toUpperCase(), StreamsSelectAndProjectIntTest.DATA_PROVIDER.data().size(), AVRO, StreamsSelectAndProjectIntTest.DATA_PROVIDER.schema());
        MatcherAssert.assertThat(results, Matchers.is(StreamsSelectAndProjectIntTest.DATA_PROVIDER.data()));
    }

    @Test
    public void testInsertSelectWithFilterJson() {
        givenStreamExists(resultStream, "*", StreamsSelectAndProjectIntTest.JSON_STREAM_NAME);
        ksqlContext.sql((((("INSERT INTO " + (resultStream)) + " SELECT * FROM ") + (StreamsSelectAndProjectIntTest.JSON_STREAM_NAME)) + " WHERE ORDERUNITS > 40;"));
        StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableRows(resultStream.toUpperCase(), 4, JSON, StreamsSelectAndProjectIntTest.DATA_PROVIDER.schema());
    }

    @Test
    public void testInsertSelectWithFilterAvro() {
        givenStreamExists(resultStream, "*", StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME);
        ksqlContext.sql((((("INSERT INTO " + (resultStream)) + " SELECT * FROM ") + (StreamsSelectAndProjectIntTest.AVRO_STREAM_NAME)) + " WHERE ORDERUNITS > 40;"));
        StreamsSelectAndProjectIntTest.TEST_HARNESS.verifyAvailableRows(resultStream.toUpperCase(), 4, AVRO, StreamsSelectAndProjectIntTest.DATA_PROVIDER.schema());
    }
}

