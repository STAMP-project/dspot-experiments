/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.integration;


import StreamsConfig.APPLICATION_ID_CONFIG;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests all available joins of Kafka Streams DSL.
 */
@Category({ IntegrationTest.class })
@RunWith(Parameterized.class)
public class TableTableJoinIntegrationTest extends AbstractJoinIntegrationTest {
    private KTable<Long, String> leftTable;

    private KTable<Long, String> rightTable;

    public TableTableJoinIntegrationTest(final boolean cacheEnabled) {
        super(cacheEnabled);
    }

    private final String expectedFinalJoinResult = "D-d";

    private final String expectedFinalMultiJoinResult = "D-d-d";

    private final String storeName = (AbstractJoinIntegrationTest.appID) + "-store";

    private Materialized<Long, String, KeyValueStore<Bytes, byte[]>> materialized = Materialized.<Long, String, KeyValueStore<Bytes, byte[]>>as(storeName).withKeySerde(Serdes.Long()).withValueSerde(Serdes.String()).withCachingDisabled().withLoggingDisabled();

    private final class CountingPeek implements ForeachAction<Long, String> {
        private final String expected;

        CountingPeek(final boolean multiJoin) {
            this.expected = (multiJoin) ? expectedFinalMultiJoinResult : expectedFinalJoinResult;
        }

        @Override
        public void apply(final Long key, final String value) {
            (numRecordsExpected)++;
            if (expected.equals(value)) {
                final boolean ret = finalResultReached.compareAndSet(false, true);
                if (!ret) {
                    // do nothing; it is possible that we will see multiple duplicates of final results due to KAFKA-4309
                    // TODO: should be removed when KAFKA-4309 is fixed
                }
            }
        }
    }

    @Test
    public void testInner() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner"));
        if (cacheEnabled) {
            leftTable.join(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(false)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Collections.singletonList("A-a"), Collections.singletonList("B-a"), Collections.singletonList("B-b"), Collections.singletonList(((String) (null))), null, null, Collections.singletonList("C-c"), Collections.singletonList(((String) (null))), null, null, null, Collections.singletonList("D-d"));
            leftTable.join(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testLeft() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-left"));
        if (cacheEnabled) {
            leftTable.leftJoin(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(false)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, Collections.singletonList("A-null"), Collections.singletonList("A-a"), Collections.singletonList("B-a"), Collections.singletonList("B-b"), Collections.singletonList(((String) (null))), null, Collections.singletonList("C-null"), Collections.singletonList("C-c"), Collections.singletonList("C-null"), Collections.singletonList(((String) (null))), null, null, Collections.singletonList("D-d"));
            leftTable.leftJoin(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testOuter() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-outer"));
        if (cacheEnabled) {
            leftTable.outerJoin(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(false)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, Collections.singletonList("A-null"), Collections.singletonList("A-a"), Collections.singletonList("B-a"), Collections.singletonList("B-b"), Collections.singletonList("null-b"), Collections.singletonList(((String) (null))), Collections.singletonList("C-null"), Collections.singletonList("C-c"), Collections.singletonList("C-null"), Collections.singletonList(((String) (null))), null, Collections.singletonList("null-d"), Collections.singletonList("D-d"));
            leftTable.outerJoin(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testInnerInner() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-inner"));
        if (cacheEnabled) {
            leftTable.join(rightTable, valueJoiner).join(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            // FIXME: the duplicate below for all the multi-joins
            // are due to KAFKA-6443, should be updated once it is fixed.
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList(((String) (null))), null, null, Arrays.asList("C-c-c", "C-c-c"), null, null, null, null, Collections.singletonList("D-d-d"));
            leftTable.join(rightTable, valueJoiner).join(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testInnerLeft() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-left"));
        if (cacheEnabled) {
            leftTable.join(rightTable, valueJoiner).leftJoin(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList(((String) (null))), null, null, Arrays.asList("C-c-c", "C-c-c"), Collections.singletonList(((String) (null))), null, null, null, Collections.singletonList("D-d-d"));
            leftTable.join(rightTable, valueJoiner).leftJoin(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testInnerOuter() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-outer"));
        if (cacheEnabled) {
            leftTable.join(rightTable, valueJoiner).outerJoin(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList("null-b"), Collections.singletonList(((String) (null))), null, Arrays.asList("C-c-c", "C-c-c"), Arrays.asList(((String) (null)), null), null, null, null, Arrays.asList("null-d", "D-d-d"));
            leftTable.join(rightTable, valueJoiner).outerJoin(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testLeftInner() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-inner"));
        if (cacheEnabled) {
            leftTable.leftJoin(rightTable, valueJoiner).join(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList(((String) (null))), null, null, Arrays.asList("C-c-c", "C-c-c"), Collections.singletonList(((String) (null))), null, null, null, Collections.singletonList("D-d-d"));
            leftTable.leftJoin(rightTable, valueJoiner).join(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testLeftLeft() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-left"));
        if (cacheEnabled) {
            leftTable.leftJoin(rightTable, valueJoiner).leftJoin(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-null-null", "A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList(((String) (null))), null, null, Arrays.asList("C-null-null", "C-c-c", "C-c-c"), Arrays.asList("C-null-null", "C-null-null"), Collections.singletonList(((String) (null))), null, null, Collections.singletonList("D-d-d"));
            leftTable.leftJoin(rightTable, valueJoiner).leftJoin(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testLeftOuter() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-outer"));
        if (cacheEnabled) {
            leftTable.leftJoin(rightTable, valueJoiner).outerJoin(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-null-null", "A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList("null-b"), Collections.singletonList(((String) (null))), null, Arrays.asList("C-null-null", "C-c-c", "C-c-c"), Arrays.asList("C-null-null", "C-null-null"), Collections.singletonList(((String) (null))), null, null, Arrays.asList("null-d", "D-d-d"));
            leftTable.leftJoin(rightTable, valueJoiner).outerJoin(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testOuterInner() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-inner"));
        if (cacheEnabled) {
            leftTable.outerJoin(rightTable, valueJoiner).join(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList("null-b-b"), null, null, Arrays.asList("C-c-c", "C-c-c"), Collections.singletonList(((String) (null))), null, null, Arrays.asList("null-d-d", "null-d-d"), Collections.singletonList("D-d-d"));
            leftTable.outerJoin(rightTable, valueJoiner).join(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testOuterLeft() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-left"));
        if (cacheEnabled) {
            leftTable.outerJoin(rightTable, valueJoiner).leftJoin(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-null-null", "A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList("null-b-b"), Collections.singletonList(((String) (null))), null, Arrays.asList("C-null-null", "C-c-c", "C-c-c"), Arrays.asList("C-null-null", "C-null-null"), Collections.singletonList(((String) (null))), null, Arrays.asList("null-d-d", "null-d-d"), Collections.singletonList("D-d-d"));
            leftTable.outerJoin(rightTable, valueJoiner).leftJoin(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }

    @Test
    public void testOuterOuter() throws Exception {
        AbstractJoinIntegrationTest.STREAMS_CONFIG.put(APPLICATION_ID_CONFIG, ((AbstractJoinIntegrationTest.appID) + "-inner-outer"));
        if (cacheEnabled) {
            leftTable.outerJoin(rightTable, valueJoiner).outerJoin(rightTable, valueJoiner, materialized).toStream().peek(new TableTableJoinIntegrationTest.CountingPeek(true)).to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedFinalMultiJoinResult, storeName);
        } else {
            final List<List<String>> expectedResult = Arrays.asList(null, null, null, Arrays.asList("A-null-null", "A-a-a", "A-a-a"), Collections.singletonList("B-a-a"), Arrays.asList("B-b-b", "B-b-b"), Collections.singletonList("null-b-b"), Arrays.asList(((String) (null)), null), null, Arrays.asList("C-null-null", "C-c-c", "C-c-c"), Arrays.asList("C-null-null", "C-null-null"), Collections.singletonList(((String) (null))), null, null, Arrays.asList("null-d-d", "null-d-d", "D-d-d"));
            leftTable.outerJoin(rightTable, valueJoiner).outerJoin(rightTable, valueJoiner, materialized).toStream().to(AbstractJoinIntegrationTest.OUTPUT_TOPIC);
            runTest(expectedResult, storeName);
        }
    }
}

