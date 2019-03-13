/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalIndexStatsTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    protected static final int PARTITIONS = 137;

    private static final int QUERIES = 10;

    private LocalIndexStatsTest.QueryType[] queryTypes;

    protected String mapName;

    protected String noStatsMapName;

    protected HazelcastInstance instance;

    protected IMap<Integer, Integer> map;

    protected IMap<Integer, Integer> noStatsMap;

    @Test
    public void testQueryCounting() {
        map.addIndex("this", false);
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        long expectedQueryCount = 0;
        long expectedIndexedQueryCount = 0;
        Assert.assertEquals(expectedQueryCount, stats().getQueryCount());
        Assert.assertEquals(expectedIndexedQueryCount, stats().getIndexedQueryCount());
        for (LocalIndexStatsTest.QueryType queryType : queryTypes) {
            query(queryType, Predicates.alwaysTrue(), Predicates.equal("this", 10));
            expectedQueryCount += (LocalIndexStatsTest.QUERIES) * 2;
            if (queryType.isIndexed()) {
                expectedIndexedQueryCount += LocalIndexStatsTest.QUERIES;
            }
            Assert.assertEquals(expectedQueryCount, stats().getQueryCount());
            Assert.assertEquals(expectedIndexedQueryCount, stats().getIndexedQueryCount());
        }
    }

    @Test
    public void testHitAndQueryCounting_WhenAllIndexesHit() {
        map.addIndex("__key", false);
        map.addIndex("this", true);
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        long expectedKeyHitCount = 0;
        long expectedKeyQueryCount = 0;
        long expectedValueHitCount = 0;
        long expectedValueQueryCount = 0;
        Assert.assertEquals(expectedKeyHitCount, keyStats().getHitCount());
        Assert.assertEquals(expectedKeyQueryCount, keyStats().getQueryCount());
        Assert.assertEquals(expectedValueHitCount, valueStats().getHitCount());
        Assert.assertEquals(expectedValueQueryCount, valueStats().getQueryCount());
        for (LocalIndexStatsTest.QueryType queryType : queryTypes) {
            query(queryType, Predicates.alwaysTrue(), Predicates.equal("__key", 10), Predicates.equal("this", 10));
            if (queryType.isIndexed()) {
                expectedKeyHitCount += LocalIndexStatsTest.QUERIES;
                expectedKeyQueryCount += LocalIndexStatsTest.QUERIES;
                expectedValueHitCount += LocalIndexStatsTest.QUERIES;
                expectedValueQueryCount += LocalIndexStatsTest.QUERIES;
            }
            Assert.assertEquals(expectedKeyHitCount, keyStats().getHitCount());
            Assert.assertEquals(expectedKeyQueryCount, keyStats().getQueryCount());
            Assert.assertEquals(expectedValueHitCount, valueStats().getHitCount());
            Assert.assertEquals(expectedValueQueryCount, valueStats().getQueryCount());
        }
    }

    @Test
    public void testHitAndQueryCounting_WhenSingleIndexHit() {
        map.addIndex("__key", false);
        map.addIndex("this", true);
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        long expectedKeyHitCount = 0;
        long expectedKeyQueryCount = 0;
        Assert.assertEquals(expectedKeyHitCount, keyStats().getHitCount());
        Assert.assertEquals(expectedKeyQueryCount, keyStats().getQueryCount());
        Assert.assertEquals(0, valueStats().getHitCount());
        Assert.assertEquals(0, valueStats().getQueryCount());
        for (LocalIndexStatsTest.QueryType queryType : queryTypes) {
            query(queryType, Predicates.alwaysTrue(), Predicates.equal("__key", 10));
            if (queryType.isIndexed()) {
                expectedKeyHitCount += LocalIndexStatsTest.QUERIES;
                expectedKeyQueryCount += LocalIndexStatsTest.QUERIES;
            }
            Assert.assertEquals(expectedKeyHitCount, keyStats().getHitCount());
            Assert.assertEquals(expectedKeyQueryCount, keyStats().getQueryCount());
            Assert.assertEquals(0, valueStats().getHitCount());
            Assert.assertEquals(0, valueStats().getQueryCount());
        }
    }

    @Test
    public void testHitCounting_WhenIndexHitMultipleTimes() {
        map.addIndex("__key", false);
        map.addIndex("this", true);
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        long expectedKeyHitCount = 0;
        long expectedKeyQueryCount = 0;
        long expectedValueHitCount = 0;
        long expectedValueQueryCount = 0;
        Assert.assertEquals(expectedKeyHitCount, keyStats().getHitCount());
        Assert.assertEquals(expectedKeyQueryCount, keyStats().getQueryCount());
        Assert.assertEquals(expectedValueHitCount, valueStats().getHitCount());
        Assert.assertEquals(expectedValueQueryCount, valueStats().getQueryCount());
        for (LocalIndexStatsTest.QueryType queryType : queryTypes) {
            query(queryType, Predicates.alwaysTrue(), Predicates.or(Predicates.equal("__key", 10), Predicates.equal("__key", 20)), Predicates.equal("this", 10));
            if (queryType.isIndexed()) {
                expectedKeyHitCount += (LocalIndexStatsTest.QUERIES) * 2;
                expectedKeyQueryCount += LocalIndexStatsTest.QUERIES;
                expectedValueHitCount += LocalIndexStatsTest.QUERIES;
                expectedValueQueryCount += LocalIndexStatsTest.QUERIES;
            }
            Assert.assertEquals(expectedKeyHitCount, keyStats().getHitCount());
            Assert.assertEquals(expectedKeyQueryCount, keyStats().getQueryCount());
            Assert.assertEquals(expectedValueHitCount, valueStats().getHitCount());
            Assert.assertEquals(expectedValueQueryCount, valueStats().getQueryCount());
        }
    }

    @Test
    public void testAverageQuerySelectivityCalculation_WhenSomePartitionsAreEmpty() {
        testAverageQuerySelectivityCalculation(100);
    }

    @Test
    public void testAverageQuerySelectivityCalculation_WhenAllPartitionsArePopulated() {
        testAverageQuerySelectivityCalculation(1000);
    }

    @Test
    public void testAverageQuerySelectivityCalculation_WhenAllPartitionsAreHeavilyPopulated() {
        testAverageQuerySelectivityCalculation(10000);
    }

    @Test
    public void testAverageQuerySelectivityCalculation_ChangingNumberOfIndex() {
        double expected1 = 1.0 - 0.001;
        double expected2 = 1.0 - 0.1;
        double expected3 = 1.0 - 0.4;
        map.addIndex("__key", false);
        map.addIndex("this", true);
        for (int i = 0; i < 1000; ++i) {
            map.put(i, i);
        }
        Assert.assertEquals(0.0, keyStats().getAverageHitSelectivity(), 0.0);
        Assert.assertEquals(0.0, valueStats().getAverageHitSelectivity(), 0.0);
        for (int i = 0; i < (LocalIndexStatsTest.QUERIES); ++i) {
            map.entrySet(Predicates.equal("__key", 10));
            map.entrySet(Predicates.equal("this", 10));
            Assert.assertEquals(expected1, keyStats().getAverageHitSelectivity(), 0.015);
            Assert.assertEquals(expected1, valueStats().getAverageHitSelectivity(), 0.015);
        }
        for (int i = 1000; i < 2000; ++i) {
            map.put(i, i);
        }
        for (int i = 1; i <= (LocalIndexStatsTest.QUERIES); ++i) {
            map.entrySet(Predicates.greaterEqual("__key", 1800));
            map.entrySet(Predicates.greaterEqual("this", 1800));
            Assert.assertEquals((((expected1 * (LocalIndexStatsTest.QUERIES)) + (expected2 * i)) / ((LocalIndexStatsTest.QUERIES) + i)), keyStats().getAverageHitSelectivity(), 0.015);
            Assert.assertEquals((((expected1 * (LocalIndexStatsTest.QUERIES)) + (expected2 * i)) / ((LocalIndexStatsTest.QUERIES) + i)), valueStats().getAverageHitSelectivity(), 0.015);
        }
        for (int i = 1500; i < 2000; ++i) {
            map.remove(i);
        }
        for (int i = 1; i <= (LocalIndexStatsTest.QUERIES); ++i) {
            map.entrySet(Predicates.greaterEqual("__key", 900));
            map.entrySet(Predicates.greaterEqual("this", 900));
            Assert.assertEquals(((((expected1 + expected2) * (LocalIndexStatsTest.QUERIES)) + (expected3 * i)) / ((2 * (LocalIndexStatsTest.QUERIES)) + i)), keyStats().getAverageHitSelectivity(), 0.015);
            Assert.assertEquals(((((expected1 + expected2) * (LocalIndexStatsTest.QUERIES)) + (expected3 * i)) / ((2 * (LocalIndexStatsTest.QUERIES)) + i)), valueStats().getAverageHitSelectivity(), 0.015);
        }
    }

    @Test
    public void testQueryCounting_WhenTwoMapsUseIndexesNamedTheSame() {
        map.addIndex("__key", false);
        map.addIndex("this", true);
        IMap<Integer, Integer> otherMap = instance.getMap(((map.getName()) + "_other_map"));
        otherMap.addIndex("__key", false);
        otherMap.addIndex("this", true);
        for (int i = 0; i < 100; ++i) {
            otherMap.put(i, i);
        }
        otherMap.entrySet(Predicates.equal("__key", 10));
        Assert.assertEquals(0, keyStats().getQueryCount());
        Assert.assertEquals(0, valueStats().getQueryCount());
        map.entrySet(Predicates.equal("__key", 10));
        Assert.assertEquals(1, keyStats().getQueryCount());
        Assert.assertEquals(0, valueStats().getQueryCount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testQueryCounting_WhenPartitionPredicateIsUsed() {
        map.addIndex("this", false);
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        map.entrySet(new com.hazelcast.query.PartitionPredicate(10, Predicates.equal("this", 10)));
        Assert.assertEquals(1, stats().getQueryCount());
        Assert.assertEquals(0, stats().getIndexedQueryCount());
        Assert.assertEquals(0, valueStats().getQueryCount());
    }

    @Test
    public void testQueryCounting_WhenStatisticsIsDisabled() {
        map.addIndex("__key", false);
        map.addIndex("this", true);
        noStatsMap.addIndex("__key", false);
        noStatsMap.addIndex("this", true);
        for (int i = 0; i < 100; ++i) {
            noStatsMap.put(i, i);
        }
        Assert.assertEquals(0, noStats().getQueryCount());
        Assert.assertEquals(0, stats().getQueryCount());
        noStatsMap.entrySet(Predicates.equal("__key", 10));
        Assert.assertEquals(0, noStats().getQueryCount());
        Assert.assertEquals(0, stats().getQueryCount());
        map.entrySet(Predicates.equal("__key", 10));
        Assert.assertEquals(0, noStats().getQueryCount());
        Assert.assertEquals(1, stats().getQueryCount());
    }

    @Test
    public void testMemoryCostTracking() {
        map.addIndex("__key", false);
        map.addIndex("this", true);
        long keyEmptyCost = keyStats().getMemoryCost();
        long valueEmptyCost = valueStats().getMemoryCost();
        Assert.assertTrue((keyEmptyCost > 0));
        Assert.assertTrue((valueEmptyCost > 0));
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        long keyFullCost = keyStats().getMemoryCost();
        long valueFullCost = valueStats().getMemoryCost();
        Assert.assertTrue((keyFullCost > keyEmptyCost));
        Assert.assertTrue((valueFullCost > valueEmptyCost));
        for (int i = 0; i < 50; ++i) {
            map.remove(i);
        }
        long keyHalfFullCost = keyStats().getMemoryCost();
        long valueHalfFullCost = valueStats().getMemoryCost();
        Assert.assertTrue(((keyHalfFullCost > keyEmptyCost) && (keyHalfFullCost < keyFullCost)));
        Assert.assertTrue(((valueHalfFullCost > valueEmptyCost) && (valueHalfFullCost < valueFullCost)));
        for (int i = 0; i < 50; ++i) {
            map.put(i, i);
        }
        Assert.assertTrue(((keyStats().getMemoryCost()) > keyHalfFullCost));
        Assert.assertTrue(((valueStats().getMemoryCost()) > valueHalfFullCost));
        for (int i = 0; i < 50; ++i) {
            map.set(i, (i * i));
        }
        Assert.assertTrue(((keyStats().getMemoryCost()) > keyHalfFullCost));
        Assert.assertTrue(((valueStats().getMemoryCost()) > valueHalfFullCost));
    }

    @Test
    public void testAverageQueryLatencyTracking() {
        map.addIndex("__key", false);
        Assert.assertEquals(0, keyStats().getAverageHitLatency());
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        Assert.assertEquals(0, keyStats().getAverageHitLatency());
        long totalMeasuredLatency = 0;
        for (int i = 1; i <= (LocalIndexStatsTest.QUERIES); ++i) {
            long start = System.nanoTime();
            map.entrySet(Predicates.equal("__key", i));
            totalMeasuredLatency += (System.nanoTime()) - start;
            Assert.assertTrue(((keyStats().getAverageHitLatency()) > 0));
            Assert.assertTrue(((keyStats().getAverageHitLatency()) <= (totalMeasuredLatency / i)));
        }
        long originalAvgHitLatency = keyStats().getAverageHitLatency();
        for (int i = 1; i <= (LocalIndexStatsTest.QUERIES); ++i) {
            map.entrySet(Predicates.alwaysTrue());
        }
        long avgHitLatencyAfterNonIndexedQueries = keyStats().getAverageHitLatency();
        Assert.assertEquals(originalAvgHitLatency, avgHitLatencyAfterNonIndexedQueries);
    }

    @Test
    public void testInsertsTracking() {
        map.addIndex("__key", false);
        Assert.assertEquals(0, keyStats().getInsertCount());
        Assert.assertEquals(0, keyStats().getTotalInsertLatency());
        long totalMeasuredLatency = 0;
        long previousTotalInsertLatency = 0;
        for (int i = 1; i <= 100; ++i) {
            long start = System.nanoTime();
            map.put(i, i);
            totalMeasuredLatency += (System.nanoTime()) - start;
            Assert.assertEquals(i, keyStats().getInsertCount());
            Assert.assertTrue(((keyStats().getTotalInsertLatency()) > previousTotalInsertLatency));
            Assert.assertTrue(((keyStats().getTotalInsertLatency()) <= totalMeasuredLatency));
            previousTotalInsertLatency = keyStats().getTotalInsertLatency();
        }
        Assert.assertEquals(0, keyStats().getUpdateCount());
        Assert.assertEquals(0, keyStats().getRemoveCount());
    }

    @Test
    public void testUpdateTracking() {
        map.addIndex("__key", false);
        Assert.assertEquals(0, keyStats().getUpdateCount());
        Assert.assertEquals(0, keyStats().getTotalUpdateLatency());
        for (int i = 1; i <= 100; ++i) {
            map.put(i, i);
        }
        Assert.assertEquals(0, keyStats().getUpdateCount());
        Assert.assertEquals(0, keyStats().getTotalUpdateLatency());
        long totalMeasuredLatency = 0;
        long previousTotalUpdateLatency = 0;
        for (int i = 1; i <= 50; ++i) {
            long start = System.nanoTime();
            map.put(i, (i * 2));
            totalMeasuredLatency += (System.nanoTime()) - start;
            Assert.assertEquals(i, keyStats().getUpdateCount());
            Assert.assertTrue(((keyStats().getTotalUpdateLatency()) > previousTotalUpdateLatency));
            Assert.assertTrue(((keyStats().getTotalUpdateLatency()) <= totalMeasuredLatency));
            previousTotalUpdateLatency = keyStats().getTotalUpdateLatency();
        }
        Assert.assertEquals(100, keyStats().getInsertCount());
        Assert.assertEquals(0, keyStats().getRemoveCount());
    }

    @Test
    public void testRemoveTracking() {
        map.addIndex("__key", false);
        Assert.assertEquals(0, keyStats().getRemoveCount());
        Assert.assertEquals(0, keyStats().getTotalRemoveLatency());
        for (int i = 1; i <= 100; ++i) {
            map.put(i, i);
        }
        Assert.assertEquals(0, keyStats().getRemoveCount());
        Assert.assertEquals(0, keyStats().getTotalRemoveLatency());
        long totalMeasuredLatency = 0;
        long previousTotalRemoveLatency = 0;
        for (int i = 1; i <= 50; ++i) {
            long start = System.nanoTime();
            map.remove(i);
            totalMeasuredLatency += (System.nanoTime()) - start;
            Assert.assertEquals(i, keyStats().getRemoveCount());
            Assert.assertTrue(((keyStats().getTotalRemoveLatency()) > previousTotalRemoveLatency));
            Assert.assertTrue(((keyStats().getTotalRemoveLatency()) <= totalMeasuredLatency));
            previousTotalRemoveLatency = keyStats().getTotalRemoveLatency();
        }
        Assert.assertEquals(100, keyStats().getInsertCount());
        Assert.assertEquals(0, keyStats().getUpdateCount());
    }

    @Test
    public void testInsertUpdateRemoveAreNotAffectingEachOther() {
        map.addIndex("__key", false);
        Assert.assertEquals(0, keyStats().getInsertCount());
        Assert.assertEquals(0, keyStats().getUpdateCount());
        Assert.assertEquals(0, keyStats().getRemoveCount());
        long originalTotalInsertLatency = keyStats().getTotalInsertLatency();
        long originalTotalUpdateLatency = keyStats().getTotalUpdateLatency();
        long originalTotalRemoveLatency = keyStats().getTotalRemoveLatency();
        for (int i = 1; i <= 100; ++i) {
            map.put(i, i);
        }
        Assert.assertTrue(((keyStats().getTotalInsertLatency()) > originalTotalInsertLatency));
        Assert.assertEquals(originalTotalUpdateLatency, keyStats().getTotalUpdateLatency());
        Assert.assertEquals(originalTotalRemoveLatency, keyStats().getTotalRemoveLatency());
        originalTotalInsertLatency = keyStats().getTotalInsertLatency();
        originalTotalUpdateLatency = keyStats().getTotalUpdateLatency();
        originalTotalRemoveLatency = keyStats().getTotalRemoveLatency();
        for (int i = 1; i <= 50; ++i) {
            map.put(i, (i * 2));
        }
        Assert.assertEquals(originalTotalInsertLatency, keyStats().getTotalInsertLatency());
        Assert.assertTrue(((keyStats().getTotalUpdateLatency()) > originalTotalUpdateLatency));
        Assert.assertEquals(originalTotalRemoveLatency, keyStats().getTotalRemoveLatency());
        originalTotalInsertLatency = keyStats().getTotalInsertLatency();
        originalTotalUpdateLatency = keyStats().getTotalUpdateLatency();
        originalTotalRemoveLatency = keyStats().getTotalRemoveLatency();
        for (int i = 1; i <= 20; ++i) {
            map.remove(i);
        }
        Assert.assertEquals(originalTotalInsertLatency, keyStats().getTotalInsertLatency());
        Assert.assertEquals(originalTotalUpdateLatency, keyStats().getTotalUpdateLatency());
        Assert.assertTrue(((keyStats().getTotalRemoveLatency()) > originalTotalRemoveLatency));
        Assert.assertEquals(100, keyStats().getInsertCount());
        Assert.assertEquals(50, keyStats().getUpdateCount());
        Assert.assertEquals(20, keyStats().getRemoveCount());
    }

    @Test
    public void testIndexStatsAfterMapDestroy() {
        map.addIndex("this", true);
        for (int i = 0; i < 100; ++i) {
            map.put(i, i);
        }
        for (int i = 90; i < 100; ++i) {
            map.remove(i, i);
        }
        for (int i = 70; i < 90; ++i) {
            map.set(i, (i * i));
        }
        map.entrySet(Predicates.equal("this", 10));
        Assert.assertEquals(1, stats().getQueryCount());
        Assert.assertEquals(1, stats().getIndexedQueryCount());
        Assert.assertEquals(1, valueStats().getQueryCount());
        Assert.assertEquals(100, valueStats().getInsertCount());
        Assert.assertEquals(20, valueStats().getUpdateCount());
        Assert.assertEquals(10, valueStats().getRemoveCount());
        Assert.assertTrue(((valueStats().getTotalInsertLatency()) > 0));
        Assert.assertTrue(((valueStats().getTotalRemoveLatency()) > 0));
        Assert.assertTrue(((valueStats().getTotalUpdateLatency()) > 0));
        Assert.assertTrue(((valueStats().getAverageHitLatency()) > 0));
        map.destroy();
        Assert.assertNull(valueStats());
        map = instance.getMap(mapName);
        Assert.assertNull(valueStats());
        map.addIndex("this", true);
        Assert.assertNotNull(valueStats());
        Assert.assertEquals(0, stats().getQueryCount());
        Assert.assertEquals(0, stats().getIndexedQueryCount());
        Assert.assertEquals(0, valueStats().getQueryCount());
        Assert.assertEquals(0, valueStats().getInsertCount());
        Assert.assertEquals(0, valueStats().getUpdateCount());
        Assert.assertEquals(0, valueStats().getRemoveCount());
        Assert.assertEquals(0, valueStats().getTotalInsertLatency());
        Assert.assertEquals(0, valueStats().getTotalRemoveLatency());
        Assert.assertEquals(0, valueStats().getTotalUpdateLatency());
        Assert.assertEquals(0, valueStats().getAverageHitLatency());
        for (int i = 0; i < 50; ++i) {
            map.put(i, i);
        }
        for (int i = 45; i < 50; ++i) {
            map.remove(i, i);
        }
        for (int i = 35; i < 45; ++i) {
            map.set(i, (i * i));
        }
        map.entrySet(Predicates.equal("this", 10));
        Assert.assertEquals(1, stats().getQueryCount());
        Assert.assertEquals(1, stats().getIndexedQueryCount());
        Assert.assertEquals(1, valueStats().getQueryCount());
        Assert.assertEquals(50, valueStats().getInsertCount());
        Assert.assertEquals(10, valueStats().getUpdateCount());
        Assert.assertEquals(5, valueStats().getRemoveCount());
        Assert.assertTrue(((valueStats().getTotalInsertLatency()) > 0));
        Assert.assertTrue(((valueStats().getTotalRemoveLatency()) > 0));
        Assert.assertTrue(((valueStats().getTotalUpdateLatency()) > 0));
        Assert.assertTrue(((valueStats().getAverageHitLatency()) > 0));
    }

    private interface QueryType {
        boolean isIndexed();

        void query(Predicate predicate);
    }
}

