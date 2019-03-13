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


import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
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
public class IndexStatsChangingNumberOfMembersTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    protected static final int NODE_COUNT = 3;

    @Test
    public void testIndexStatsQueryingChangingNumberOfMembers() {
        int queriesBulk = 100;
        int entryCount = 1000;
        final int lessEqualCount = 20;
        double expectedEqual = 1.0 - (1.0 / entryCount);
        double expectedGreaterEqual = 1.0 - (((double) (lessEqualCount)) / entryCount);
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setInMemoryFormat(inMemoryFormat);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(IndexStatsChangingNumberOfMembersTest.NODE_COUNT);
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map1 = instance1.getMap(mapName);
        IMap<Integer, Integer> map2 = instance2.getMap(mapName);
        map1.addIndex("this", false);
        map2.addIndex("this", false);
        for (int i = 0; i < entryCount; ++i) {
            map1.put(i, i);
        }
        Assert.assertEquals(0, stats(map1).getQueryCount());
        Assert.assertEquals(0, stats(map1).getIndexedQueryCount());
        Assert.assertEquals(0, valueStats(map1).getQueryCount());
        Assert.assertEquals(0, stats(map2).getQueryCount());
        Assert.assertEquals(0, stats(map2).getIndexedQueryCount());
        Assert.assertEquals(0, valueStats(map2).getQueryCount());
        for (int i = 0; i < queriesBulk; i++) {
            map1.entrySet(Predicates.alwaysTrue());
            map1.entrySet(Predicates.equal("this", 10));
            map2.entrySet(Predicates.lessEqual("this", lessEqualCount));
        }
        Assert.assertEquals((3 * queriesBulk), stats(map1).getQueryCount());
        Assert.assertEquals((2 * queriesBulk), stats(map1).getIndexedQueryCount());
        Assert.assertEquals((2 * queriesBulk), valueStats(map1).getQueryCount());
        Assert.assertEquals((3 * queriesBulk), stats(map2).getQueryCount());
        Assert.assertEquals((2 * queriesBulk), stats(map2).getIndexedQueryCount());
        Assert.assertEquals((2 * queriesBulk), valueStats(map2).getQueryCount());
        double originalOverallAverageHitSelectivity = calculateOverallSelectivity(map1, map2);
        Assert.assertEquals(((expectedEqual + expectedGreaterEqual) / 2), originalOverallAverageHitSelectivity, 0.015);
        long originalMap1QueryCount = stats(map1).getQueryCount();
        long originalMap1IndexedQueryCount = stats(map1).getIndexedQueryCount();
        long originalMap1IndexQueryCount = valueStats(map1).getQueryCount();
        long originalMap1AverageHitLatency = valueStats(map1).getAverageHitLatency();
        double originalMap1AverageHitSelectivity = valueStats(map1).getAverageHitSelectivity();
        long originalMap2QueryCount = stats(map2).getQueryCount();
        long originalMap2IndexedQueryCount = stats(map2).getIndexedQueryCount();
        long originalMap2IndexQueryCount = valueStats(map2).getQueryCount();
        long originalMap2AverageHitLatency = valueStats(map2).getAverageHitLatency();
        double originalMap2AverageHitSelectivity = valueStats(map2).getAverageHitSelectivity();
        // let's add another member
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map3 = instance3.getMap(mapName);
        map3.addIndex("this", false);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2, instance3);
        // check that local stats were not affected by adding new member to cluster
        Assert.assertEquals(originalMap1QueryCount, stats(map1).getQueryCount());
        Assert.assertEquals(originalMap1IndexedQueryCount, stats(map1).getIndexedQueryCount());
        Assert.assertEquals(originalMap1IndexQueryCount, valueStats(map1).getQueryCount());
        Assert.assertEquals(originalMap1AverageHitLatency, valueStats(map1).getAverageHitLatency());
        Assert.assertEquals(originalMap1AverageHitSelectivity, valueStats(map1).getAverageHitSelectivity(), 0.001);
        Assert.assertEquals(originalMap2QueryCount, stats(map2).getQueryCount());
        Assert.assertEquals(originalMap2IndexedQueryCount, stats(map2).getIndexedQueryCount());
        Assert.assertEquals(originalMap2IndexQueryCount, valueStats(map2).getQueryCount());
        Assert.assertEquals(originalMap2AverageHitLatency, valueStats(map2).getAverageHitLatency());
        Assert.assertEquals(originalMap2AverageHitSelectivity, valueStats(map2).getAverageHitSelectivity(), 0.001);
        Assert.assertEquals(originalOverallAverageHitSelectivity, calculateOverallSelectivity(map1, map2, map3), 0.001);
        for (int i = 0; i < queriesBulk; i++) {
            map1.entrySet(Predicates.alwaysTrue());
            map3.entrySet(Predicates.equal("this", 10));
            map2.entrySet(Predicates.lessEqual("this", lessEqualCount));
        }
        Assert.assertEquals((6 * queriesBulk), stats(map1).getQueryCount());
        Assert.assertEquals((4 * queriesBulk), stats(map1).getIndexedQueryCount());
        Assert.assertEquals((4 * queriesBulk), valueStats(map1).getQueryCount());
        Assert.assertEquals((6 * queriesBulk), stats(map2).getQueryCount());
        Assert.assertEquals((4 * queriesBulk), stats(map2).getIndexedQueryCount());
        Assert.assertEquals((4 * queriesBulk), valueStats(map2).getQueryCount());
        Assert.assertEquals((3 * queriesBulk), stats(map3).getQueryCount());
        Assert.assertEquals((2 * queriesBulk), stats(map3).getIndexedQueryCount());
        Assert.assertEquals((2 * queriesBulk), valueStats(map3).getQueryCount());
        originalOverallAverageHitSelectivity = calculateOverallSelectivity(map1, map2, map3);
        Assert.assertEquals(((expectedEqual + expectedGreaterEqual) / 2), originalOverallAverageHitSelectivity, 0.015);
        originalMap1QueryCount = stats(map1).getQueryCount();
        originalMap1IndexedQueryCount = stats(map1).getIndexedQueryCount();
        originalMap1IndexQueryCount = valueStats(map1).getQueryCount();
        originalMap1AverageHitLatency = valueStats(map1).getAverageHitLatency();
        originalMap1AverageHitSelectivity = valueStats(map1).getAverageHitSelectivity();
        long originalMap3QueryCount = stats(map3).getQueryCount();
        long originalMap3IndexedQueryCount = stats(map3).getIndexedQueryCount();
        long originalMap3IndexQueryCount = valueStats(map3).getQueryCount();
        long originalMap3AverageHitLatency = valueStats(map3).getAverageHitLatency();
        double originalMap3AverageHitSelectivity = valueStats(map3).getAverageHitSelectivity();
        // After removing member AverageHitSelectivity will not provide accurate value => this serves just for ensure
        // that AverageHitSelectivity is still counted correctly on live members.
        long map2Hits = valueStats(map2).getHitCount();
        double map2TotalHitSelectivity = (valueStats(map2).getAverageHitSelectivity()) * map2Hits;
        // let's remove one member
        instance2.shutdown();
        HazelcastTestSupport.waitAllForSafeState(instance1, instance3);
        // check that local stats were not affected by removing member from cluster
        Assert.assertEquals(originalMap1QueryCount, stats(map1).getQueryCount());
        Assert.assertEquals(originalMap1IndexedQueryCount, stats(map1).getIndexedQueryCount());
        Assert.assertEquals(originalMap1IndexQueryCount, valueStats(map1).getQueryCount());
        Assert.assertEquals(originalMap1AverageHitLatency, valueStats(map1).getAverageHitLatency());
        Assert.assertEquals(originalMap1AverageHitSelectivity, valueStats(map1).getAverageHitSelectivity(), 0.001);
        Assert.assertEquals(originalMap3QueryCount, stats(map3).getQueryCount());
        Assert.assertEquals(originalMap3IndexedQueryCount, stats(map3).getIndexedQueryCount());
        Assert.assertEquals(originalMap3IndexQueryCount, valueStats(map3).getQueryCount());
        Assert.assertEquals(originalMap3AverageHitLatency, valueStats(map3).getAverageHitLatency());
        Assert.assertEquals(originalMap3AverageHitSelectivity, valueStats(map3).getAverageHitSelectivity(), 0.001);
        Assert.assertEquals(originalOverallAverageHitSelectivity, calculateOverallSelectivity(map2Hits, map2TotalHitSelectivity, map1, map3), 0.015);
        for (int i = 0; i < queriesBulk; i++) {
            map3.entrySet(Predicates.alwaysTrue());
            map1.entrySet(Predicates.equal("this", 10));
            map3.entrySet(Predicates.lessEqual("this", lessEqualCount));
        }
        Assert.assertEquals((9 * queriesBulk), stats(map1).getQueryCount());
        Assert.assertEquals((6 * queriesBulk), stats(map1).getIndexedQueryCount());
        Assert.assertEquals((6 * queriesBulk), valueStats(map1).getQueryCount());
        Assert.assertEquals((6 * queriesBulk), stats(map3).getQueryCount());
        Assert.assertEquals((4 * queriesBulk), stats(map3).getIndexedQueryCount());
        Assert.assertEquals((4 * queriesBulk), valueStats(map3).getQueryCount());
        // This work correctly only due to we stored data from shutdown member and uses this data for counting
        // originalOverallAverageHitSelectivity. However this not represent real scenario. This check is here just for ensure
        // that AverageHitSelectivity is still counted correctly on live members.
        originalOverallAverageHitSelectivity = calculateOverallSelectivity(map2Hits, map2TotalHitSelectivity, map1, map3);
        Assert.assertEquals(((expectedEqual + expectedGreaterEqual) / 2), originalOverallAverageHitSelectivity, 0.015);
    }

    @Test
    public void testIndexStatsOperationChangingNumberOfMembers() {
        int inserts = 100;
        int updates = 20;
        int removes = 20;
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = getConfig();
        config.getMapConfig(mapName).setInMemoryFormat(inMemoryFormat);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(IndexStatsChangingNumberOfMembersTest.NODE_COUNT);
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map1 = instance1.getMap(mapName);
        IMap<Integer, Integer> map2 = instance2.getMap(mapName);
        map1.addIndex("this", false);
        map2.addIndex("this", false);
        Assert.assertEquals(0, valueStats(map1).getInsertCount());
        Assert.assertEquals(0, valueStats(map1).getUpdateCount());
        Assert.assertEquals(0, valueStats(map1).getRemoveCount());
        Assert.assertEquals(0, valueStats(map1).getTotalInsertLatency());
        Assert.assertEquals(0, valueStats(map1).getTotalRemoveLatency());
        Assert.assertEquals(0, valueStats(map1).getTotalUpdateLatency());
        Assert.assertEquals(0, valueStats(map2).getInsertCount());
        Assert.assertEquals(0, valueStats(map2).getUpdateCount());
        Assert.assertEquals(0, valueStats(map2).getRemoveCount());
        Assert.assertEquals(0, valueStats(map2).getTotalInsertLatency());
        Assert.assertEquals(0, valueStats(map2).getTotalRemoveLatency());
        Assert.assertEquals(0, valueStats(map2).getTotalUpdateLatency());
        for (int i = 0; i < inserts; ++i) {
            map1.put(i, i);
        }
        for (int i = 0; i < updates; ++i) {
            map1.put(i, (i * i));
            map2.put((i + updates), (i * i));
        }
        for (int i = inserts - removes; i < inserts; ++i) {
            map2.remove(i);
        }
        Assert.assertEquals(inserts, ((valueStats(map1).getInsertCount()) + (valueStats(map2).getInsertCount())));
        Assert.assertEquals((2 * updates), ((valueStats(map1).getUpdateCount()) + (valueStats(map2).getUpdateCount())));
        Assert.assertEquals(removes, ((valueStats(map1).getRemoveCount()) + (valueStats(map2).getRemoveCount())));
        Assert.assertTrue(((valueStats(map1).getTotalInsertLatency()) > 0));
        Assert.assertTrue(((valueStats(map1).getTotalRemoveLatency()) > 0));
        Assert.assertTrue(((valueStats(map1).getTotalUpdateLatency()) > 0));
        Assert.assertTrue(((valueStats(map2).getTotalInsertLatency()) > 0));
        Assert.assertTrue(((valueStats(map2).getTotalRemoveLatency()) > 0));
        Assert.assertTrue(((valueStats(map2).getTotalUpdateLatency()) > 0));
        long originalMap1InsertCount = valueStats(map1).getInsertCount();
        long originalMap1UpdateCount = valueStats(map1).getUpdateCount();
        long originalMap1RemoveCount = valueStats(map1).getRemoveCount();
        long originalMap1TotalInsertLatency = valueStats(map1).getTotalInsertLatency();
        long originalMap1TotalRemoveLatency = valueStats(map1).getTotalRemoveLatency();
        long originalMap1TotalUpdateLatency = valueStats(map1).getTotalUpdateLatency();
        long originalMap2InsertCount = valueStats(map2).getInsertCount();
        long originalMap2UpdateCount = valueStats(map2).getUpdateCount();
        long originalMap2RemoveCount = valueStats(map2).getRemoveCount();
        long originalMap2TotalInsertLatency = valueStats(map2).getTotalInsertLatency();
        long originalMap2TotalRemoveLatency = valueStats(map2).getTotalRemoveLatency();
        long originalMap2TotalUpdateLatency = valueStats(map2).getTotalUpdateLatency();
        // let's add another member
        HazelcastInstance instance3 = factory.newHazelcastInstance(config);
        IMap<Integer, Integer> map3 = instance3.getMap(mapName);
        map3.addIndex("this", false);
        HazelcastTestSupport.waitAllForSafeState(instance1, instance2, instance3);
        Assert.assertEquals(originalMap1InsertCount, valueStats(map1).getInsertCount());
        Assert.assertEquals(originalMap1UpdateCount, valueStats(map1).getUpdateCount());
        Assert.assertEquals(originalMap1RemoveCount, valueStats(map1).getRemoveCount());
        Assert.assertEquals(originalMap1TotalInsertLatency, valueStats(map1).getTotalInsertLatency());
        Assert.assertEquals(originalMap1TotalRemoveLatency, valueStats(map1).getTotalRemoveLatency());
        Assert.assertEquals(originalMap1TotalUpdateLatency, valueStats(map1).getTotalUpdateLatency());
        Assert.assertEquals(originalMap2InsertCount, valueStats(map2).getInsertCount());
        Assert.assertEquals(originalMap2UpdateCount, valueStats(map2).getUpdateCount());
        Assert.assertEquals(originalMap2RemoveCount, valueStats(map2).getRemoveCount());
        Assert.assertEquals(originalMap2TotalInsertLatency, valueStats(map2).getTotalInsertLatency());
        Assert.assertEquals(originalMap2TotalRemoveLatency, valueStats(map2).getTotalRemoveLatency());
        Assert.assertEquals(originalMap2TotalUpdateLatency, valueStats(map2).getTotalUpdateLatency());
        for (int i = inserts; i < (2 * inserts); ++i) {
            map3.put(i, i);
        }
        for (int i = inserts; i < (inserts + updates); ++i) {
            map2.put(i, (i * i));
            map3.put((i + updates), (i * i));
        }
        for (int i = (2 * inserts) - updates; i < (2 * inserts); ++i) {
            map1.remove(i);
        }
        Assert.assertEquals((2 * inserts), (((valueStats(map1).getInsertCount()) + (valueStats(map2).getInsertCount())) + (valueStats(map3).getInsertCount())));
        Assert.assertEquals((4 * updates), (((valueStats(map1).getUpdateCount()) + (valueStats(map2).getUpdateCount())) + (valueStats(map3).getUpdateCount())));
        Assert.assertEquals((2 * removes), (((valueStats(map1).getRemoveCount()) + (valueStats(map2).getRemoveCount())) + (valueStats(map3).getRemoveCount())));
        originalMap1InsertCount = valueStats(map1).getInsertCount();
        originalMap1UpdateCount = valueStats(map1).getUpdateCount();
        originalMap1RemoveCount = valueStats(map1).getRemoveCount();
        originalMap1TotalInsertLatency = valueStats(map1).getTotalInsertLatency();
        originalMap1TotalRemoveLatency = valueStats(map1).getTotalRemoveLatency();
        originalMap1TotalUpdateLatency = valueStats(map1).getTotalUpdateLatency();
        long originalMap3InsertCount = valueStats(map3).getInsertCount();
        long originalMap3UpdateCount = valueStats(map3).getUpdateCount();
        long originalMap3RemoveCount = valueStats(map3).getRemoveCount();
        long originalMap3TotalInsertLatency = valueStats(map3).getTotalInsertLatency();
        long originalMap3TotalRemoveLatency = valueStats(map3).getTotalRemoveLatency();
        long originalMap3TotalUpdateLatency = valueStats(map3).getTotalUpdateLatency();
        // let's remove one member
        instance2.shutdown();
        HazelcastTestSupport.waitAllForSafeState(instance1, instance3);
        Assert.assertEquals(originalMap1InsertCount, valueStats(map1).getInsertCount());
        Assert.assertEquals(originalMap1UpdateCount, valueStats(map1).getUpdateCount());
        Assert.assertEquals(originalMap1RemoveCount, valueStats(map1).getRemoveCount());
        Assert.assertEquals(originalMap1TotalInsertLatency, valueStats(map1).getTotalInsertLatency());
        Assert.assertEquals(originalMap1TotalRemoveLatency, valueStats(map1).getTotalRemoveLatency());
        Assert.assertEquals(originalMap1TotalUpdateLatency, valueStats(map1).getTotalUpdateLatency());
        Assert.assertEquals(originalMap3InsertCount, valueStats(map3).getInsertCount());
        Assert.assertEquals(originalMap3UpdateCount, valueStats(map3).getUpdateCount());
        Assert.assertEquals(originalMap3RemoveCount, valueStats(map3).getRemoveCount());
        Assert.assertEquals(originalMap3TotalInsertLatency, valueStats(map3).getTotalInsertLatency());
        Assert.assertEquals(originalMap3TotalRemoveLatency, valueStats(map3).getTotalRemoveLatency());
        Assert.assertEquals(originalMap3TotalUpdateLatency, valueStats(map3).getTotalUpdateLatency());
        long originalMap1Map3InsertCount = (valueStats(map1).getInsertCount()) + (valueStats(map3).getInsertCount());
        long originalMap1Map3UpdateCount = (valueStats(map1).getUpdateCount()) + (valueStats(map3).getUpdateCount());
        long originalMap1Map3RemoveCount = (valueStats(map1).getRemoveCount()) + (valueStats(map3).getRemoveCount());
        for (int i = 2 * inserts; i < (3 * inserts); ++i) {
            map3.put(i, i);
        }
        for (int i = 2 * inserts; i < ((2 * inserts) + updates); ++i) {
            map3.put(i, (i * i));
            map1.put((i + updates), (i * i));
        }
        for (int i = (3 * inserts) - updates; i < (3 * inserts); ++i) {
            map3.remove(i);
        }
        Assert.assertEquals((originalMap1Map3InsertCount + inserts), ((valueStats(map1).getInsertCount()) + (valueStats(map3).getInsertCount())));
        Assert.assertEquals((originalMap1Map3UpdateCount + (2 * updates)), ((valueStats(map1).getUpdateCount()) + (valueStats(map3).getUpdateCount())));
        Assert.assertEquals((originalMap1Map3RemoveCount + removes), ((valueStats(map1).getRemoveCount()) + (valueStats(map3).getRemoveCount())));
    }
}

