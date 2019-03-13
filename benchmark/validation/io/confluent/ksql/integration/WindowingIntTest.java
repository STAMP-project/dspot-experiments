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


import TopicCleanupPolicy.COMPACT;
import TopicCleanupPolicy.DELETE;
import com.google.common.collect.ImmutableMap;
import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.services.KafkaTopicClient;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.streams.kstream.SessionWindowedDeserializer;
import org.apache.kafka.streams.kstream.TimeWindowedDeserializer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.SessionWindow;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class WindowingIntTest {
    private static final String ORDERS_STREAM = "ORDERS";

    private static final StringDeserializer STRING_DESERIALIZER = new StringDeserializer();

    private static final TimeWindowedDeserializer<String> TIME_WINDOWED_DESERIALIZER = new TimeWindowedDeserializer(WindowingIntTest.STRING_DESERIALIZER);

    private static final SessionWindowedDeserializer<String> SESSION_WINDOWED_DESERIALIZER = new SessionWindowedDeserializer(WindowingIntTest.STRING_DESERIALIZER);

    private static final Duration VERIFY_TIMEOUT = Duration.ofSeconds(60);

    private final long batch0SentMs;

    private final long batch1Delay;

    private final long tenSecWindowStartMs;

    @ClassRule
    public static final IntegrationTestHarness TEST_HARNESS = IntegrationTestHarness.build();

    @Rule
    public final TestKsqlContext ksqlContext = WindowingIntTest.TEST_HARNESS.buildKsqlContext();

    private String sourceTopicName;

    private String resultStream0;

    private String resultStream1;

    private Schema resultSchema;

    private Set<String> preExistingTopics;

    private KafkaTopicClient topicClient;

    public WindowingIntTest() {
        final long currentTimeMillis = System.currentTimeMillis();
        // set the batch to be in the middle of a ten second window
        batch0SentMs = (currentTimeMillis - (currentTimeMillis % (TimeUnit.SECONDS.toMillis(10)))) + 5001;
        tenSecWindowStartMs = (batch0SentMs) - ((batch0SentMs) % (TimeUnit.SECONDS.toMillis(10)));
        batch1Delay = 500;
    }

    @Test
    public void shouldAggregateWithNoWindow() {
        // Given:
        givenTable(((("CREATE TABLE %s AS " + ("SELECT ITEMID, COUNT(ITEMID), SUM(ORDERUNITS), SUM(KEYVALUEMAP['key2']/2) " + "FROM ")) + (WindowingIntTest.ORDERS_STREAM)) + " WHERE ITEMID = 'ITEM_1' GROUP BY ITEMID;"));
        final Map<String, GenericRow> expected = ImmutableMap.of("ITEM_1", new GenericRow(Arrays.asList(null, null, "ITEM_1", 2, 20.0, 2.0)));
        // Then:
        assertOutputOf(resultStream0, expected, Matchers.is(expected));
        assertTableCanBeUsedAsSource(expected, Matchers.is(expected));
        assertTopicsCleanedUp(COMPACT);
    }

    @Test
    public void shouldAggregateTumblingWindow() {
        // Given:
        givenTable((((("CREATE TABLE %s AS " + ("SELECT ITEMID, COUNT(ITEMID), SUM(ORDERUNITS), SUM(ORDERUNITS * 10)/COUNT(*) " + "FROM ")) + (WindowingIntTest.ORDERS_STREAM)) + " WINDOW TUMBLING (SIZE 10 SECONDS) ") + "WHERE ITEMID = 'ITEM_1' GROUP BY ITEMID;"));
        final Map<Windowed<String>, GenericRow> expected = ImmutableMap.of(new Windowed("ITEM_1", new TimeWindow(tenSecWindowStartMs, Long.MAX_VALUE)), new GenericRow(Arrays.asList(null, null, "ITEM_1", 2, 20.0, 100.0)));
        // Then:
        assertOutputOf(resultStream0, expected, Matchers.is(expected));
        assertTableCanBeUsedAsSource(expected, Matchers.is(expected));
        assertTopicsCleanedUp(DELETE);
    }

    @Test
    public void shouldAggregateHoppingWindow() {
        // Given:
        givenTable((((("CREATE TABLE %s AS " + ("SELECT ITEMID, COUNT(ITEMID), SUM(ORDERUNITS), SUM(ORDERUNITS * 10) " + "FROM ")) + (WindowingIntTest.ORDERS_STREAM)) + " WINDOW HOPPING (SIZE 10 SECONDS, ADVANCE BY 5 SECONDS) ") + "WHERE ITEMID = 'ITEM_1' GROUP BY ITEMID;"));
        final long firstWindowStart = tenSecWindowStartMs;
        final long secondWindowStart = firstWindowStart + (TimeUnit.SECONDS.toMillis(5));
        final Map<Windowed<String>, GenericRow> expected = ImmutableMap.of(new Windowed("ITEM_1", new TimeWindow(firstWindowStart, Long.MAX_VALUE)), new GenericRow(Arrays.asList(null, null, "ITEM_1", 2, 20.0, 200.0)), new Windowed("ITEM_1", new TimeWindow(secondWindowStart, Long.MAX_VALUE)), new GenericRow(Arrays.asList(null, null, "ITEM_1", 2, 20.0, 200.0)));
        // Then:
        assertOutputOf(resultStream0, expected, Matchers.is(expected));
        assertTableCanBeUsedAsSource(expected, Matchers.is(expected));
        assertTopicsCleanedUp(DELETE);
    }

    @Test
    public void shouldAggregateSessionWindow() {
        // Given:
        givenTable((((("CREATE TABLE %s AS " + ("SELECT ORDERID, COUNT(*), SUM(ORDERUNITS) " + "FROM ")) + (WindowingIntTest.ORDERS_STREAM)) + " WINDOW SESSION (10 SECONDS) ") + "GROUP BY ORDERID;"));
        final long sessionEnd = (batch0SentMs) + (batch1Delay);
        final Map<Windowed<String>, GenericRow> expected = ImmutableMap.<Windowed<String>, GenericRow>builder().put(new Windowed("ORDER_1", new SessionWindow(batch0SentMs, sessionEnd)), new GenericRow(Arrays.asList(null, null, "ORDER_1", 2, 20.0))).put(new Windowed("ORDER_2", new SessionWindow(batch0SentMs, sessionEnd)), new GenericRow(Arrays.asList(null, null, "ORDER_2", 2, 40.0))).put(new Windowed("ORDER_3", new SessionWindow(batch0SentMs, sessionEnd)), new GenericRow(Arrays.asList(null, null, "ORDER_3", 2, 60.0))).put(new Windowed("ORDER_4", new SessionWindow(batch0SentMs, sessionEnd)), new GenericRow(Arrays.asList(null, null, "ORDER_4", 2, 80.0))).put(new Windowed("ORDER_5", new SessionWindow(batch0SentMs, sessionEnd)), new GenericRow(Arrays.asList(null, null, "ORDER_5", 2, 100.0))).put(new Windowed("ORDER_6", new SessionWindow(batch0SentMs, sessionEnd)), new GenericRow(Arrays.asList(null, null, "ORDER_6", 6, 420.0))).build();
        // Then:
        assertOutputOf(resultStream0, expected, mapHasItems(expected));
        assertTableCanBeUsedAsSource(expected, mapHasItems(expected));
        assertTopicsCleanedUp(DELETE);
    }
}

