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
import io.confluent.ksql.util.ItemDataProvider;
import io.confluent.ksql.util.OrderDataProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.test.TestUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class JoinIntTest {
    private static final String ORDER_STREAM_NAME_JSON = "Orders_json";

    private static final String ORDER_STREAM_NAME_AVRO = "Orders_avro";

    private static final String ITEM_TABLE_NAME_JSON = "Item_json";

    private static final String ITEM_TABLE_NAME_AVRO = "Item_avro";

    private static final ItemDataProvider ITEM_DATA_PROVIDER = new ItemDataProvider();

    private static final OrderDataProvider ORDER_DATA_PROVIDER = new OrderDataProvider();

    private static final long MAX_WAIT_MS = TimeUnit.SECONDS.toMillis(150);

    @ClassRule
    public static final IntegrationTestHarness TEST_HARNESS = IntegrationTestHarness.build();

    @Rule
    public final TestKsqlContext ksqlContext = JoinIntTest.TEST_HARNESS.buildKsqlContext();

    private final long now = System.currentTimeMillis();

    private String itemTableTopicJson = "ItemTopicJson";

    private String orderStreamTopicJson = "OrderTopicJson";

    private String orderStreamTopicAvro = "OrderTopicAvro";

    private String itemTableTopicAvro = "ItemTopicAvro";

    @Test
    public void shouldInsertLeftJoinOrderAndItems() throws Exception {
        final String testStreamName = "OrderedWithDescription".toUpperCase();
        final String csasQueryString = String.format(("CREATE STREAM %s AS SELECT ORDERID, ITEMID, ORDERUNITS, DESCRIPTION FROM %s LEFT JOIN " + ("%s " + " on %s.ITEMID = %s.ID WHERE %s.ITEMID = 'Hello' ;")), testStreamName, JoinIntTest.ORDER_STREAM_NAME_JSON, JoinIntTest.ITEM_TABLE_NAME_JSON, JoinIntTest.ORDER_STREAM_NAME_JSON, JoinIntTest.ITEM_TABLE_NAME_JSON, JoinIntTest.ORDER_STREAM_NAME_JSON);
        final String insertQueryString = String.format(("INSERT INTO %s SELECT ORDERID, ITEMID, ORDERUNITS, DESCRIPTION FROM %s LEFT JOIN " + ("%s " + " on %s.ITEMID = %s.ID WHERE %s.ITEMID = 'ITEM_1' ;")), testStreamName, JoinIntTest.ORDER_STREAM_NAME_JSON, JoinIntTest.ITEM_TABLE_NAME_JSON, JoinIntTest.ORDER_STREAM_NAME_JSON, JoinIntTest.ITEM_TABLE_NAME_JSON, JoinIntTest.ORDER_STREAM_NAME_JSON);
        ksqlContext.sql(csasQueryString);
        ksqlContext.sql(insertQueryString);
        final Schema resultSchema = ksqlContext.getMetaStore().getSource(testStreamName).getSchema();
        final Map<String, GenericRow> expectedResults = Collections.singletonMap("ITEM_1", new GenericRow(Arrays.asList(null, null, "ORDER_1", "ITEM_1", 10.0, "home cinema")));
        final Map<String, GenericRow> results = new HashMap<>();
        TestUtils.waitForCondition(() -> {
            results.putAll(TEST_HARNESS.verifyAvailableUniqueRows(testStreamName, 1, JSON, resultSchema));
            final boolean success = results.equals(expectedResults);
            if (!success) {
                try {
                    // The join may not be triggered fist time around due to order in which the
                    // consumer pulls the records back. So we publish again to make the stream
                    // trigger the join.
                    TEST_HARNESS.produceRows(orderStreamTopicJson, ORDER_DATA_PROVIDER, JSON, () -> now);
                } catch (final  e) {
                    throw new <e>RuntimeException();
                }
            }
            return success;
        }, JoinIntTest.MAX_WAIT_MS, "failed to complete join correctly");
    }

    @Test
    public void shouldLeftJoinOrderAndItemsJson() throws Exception {
        shouldLeftJoinOrderAndItems("ORDERWITHDESCRIPTIONJSON", orderStreamTopicJson, JoinIntTest.ORDER_STREAM_NAME_JSON, JoinIntTest.ITEM_TABLE_NAME_JSON, DataSource.DataSourceSerDe.JSON);
    }

    @Test
    public void shouldLeftJoinOrderAndItemsAvro() throws Exception {
        shouldLeftJoinOrderAndItems("ORDERWITHDESCRIPTIONAVRO", orderStreamTopicAvro, JoinIntTest.ORDER_STREAM_NAME_AVRO, JoinIntTest.ITEM_TABLE_NAME_AVRO, AVRO);
    }

    @Test
    public void shouldUseTimeStampFieldFromStream() throws Exception {
        final String queryString = String.format(("CREATE STREAM JOINED AS SELECT ORDERID, ITEMID, ORDERUNITS, DESCRIPTION FROM %s LEFT JOIN" + (" %s on %s.ITEMID = %s.ID WHERE %s.ITEMID = 'ITEM_1';" + "CREATE STREAM OUTPUT AS SELECT ORDERID, DESCRIPTION, ROWTIME AS RT FROM JOINED;")), JoinIntTest.ORDER_STREAM_NAME_AVRO, JoinIntTest.ITEM_TABLE_NAME_AVRO, JoinIntTest.ORDER_STREAM_NAME_AVRO, JoinIntTest.ITEM_TABLE_NAME_AVRO, JoinIntTest.ORDER_STREAM_NAME_AVRO);
        ksqlContext.sql(queryString);
        final String outputStream = "OUTPUT";
        final Schema resultSchema = ksqlContext.getMetaStore().getSource(outputStream).getSchema();
        final Map<String, GenericRow> expectedResults = Collections.singletonMap("ITEM_1", new GenericRow(Arrays.asList(null, null, "ORDER_1", "home cinema", 1)));
        final Map<String, GenericRow> results = new HashMap<>();
        TestUtils.waitForCondition(() -> {
            results.putAll(TEST_HARNESS.verifyAvailableUniqueRows(outputStream, 1, AVRO, resultSchema));
            final boolean success = results.equals(expectedResults);
            if (!success) {
                try {
                    // The join may not be triggered fist time around due to order in which the
                    // consumer pulls the records back. So we publish again to make the stream
                    // trigger the join.
                    TEST_HARNESS.produceRows(orderStreamTopicAvro, ORDER_DATA_PROVIDER, AVRO, () -> now);
                } catch (final  e) {
                    throw new <e>RuntimeException();
                }
            }
            return success;
        }, 120000, "failed to complete join correctly");
    }
}

