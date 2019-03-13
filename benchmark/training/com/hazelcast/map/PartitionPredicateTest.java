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


import TruePredicate.INSTANCE;
import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.projection.Projections;
import com.hazelcast.query.PartitionPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PartitionPredicateTest extends HazelcastTestSupport {
    private static final int PARTITIONS = 10;

    private static final int ITEMS_PER_PARTITION = 20;

    private HazelcastInstance local;

    private IMap<String, Integer> map;

    private IMap<String, Integer> aggMap;

    private String partitionKey;

    private int partitionId;

    private String localPartitionKey;

    private int localPartitionId;

    private Predicate<String, Integer> predicate;

    private Predicate<String, Integer> aggPredicate;

    private Predicate<String, Integer> localPredicate;

    @Test
    public void values() {
        Collection<Integer> values = map.values(predicate);
        Assert.assertEquals(PartitionPredicateTest.ITEMS_PER_PARTITION, values.size());
        for (Integer value : values) {
            Assert.assertEquals(partitionId, value.intValue());
        }
    }

    @Test
    public void keySet() {
        Collection<String> keys = map.keySet(predicate);
        Assert.assertEquals(PartitionPredicateTest.ITEMS_PER_PARTITION, keys.size());
        for (String key : keys) {
            Assert.assertEquals(partitionId, local.getPartitionService().getPartition(key).getPartitionId());
        }
    }

    @Test
    public void entries() {
        Collection<Map.Entry<String, Integer>> entries = map.entrySet(predicate);
        Assert.assertEquals(PartitionPredicateTest.ITEMS_PER_PARTITION, entries.size());
        for (Map.Entry<String, Integer> entry : entries) {
            Assert.assertEquals(partitionId, local.getPartitionService().getPartition(entry.getKey()).getPartitionId());
            Assert.assertEquals(partitionId, entry.getValue().intValue());
        }
    }

    @Test
    public void localKeySet() {
        Collection<String> keys = aggMap.localKeySet(localPredicate);
        Assert.assertEquals(1, keys.size());
        for (String key : keys) {
            Assert.assertEquals(localPartitionId, local.getPartitionService().getPartition(key).getPartitionId());
        }
    }

    @Test
    public void aggregate() {
        Long aggregate = aggMap.aggregate(Aggregators.<Map.Entry<String, Integer>>count(), aggPredicate);
        Assert.assertEquals(1, aggregate.longValue());
    }

    @Test
    public void project() {
        Collection<Integer> values = aggMap.project(Projections.<Map.Entry<String, Integer>, Integer>singleAttribute("this"), aggPredicate);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(partitionId, values.iterator().next().intValue());
    }

    @Test
    public void executeOnEntries() {
        PartitionPredicate<String, Integer> lessThan10pp = new PartitionPredicate<String, Integer>(partitionKey, Predicates.lessThan("this", 10));
        Map<String, Object> result = aggMap.executeOnEntries(new PartitionPredicateTest.EntryNoop(), lessThan10pp);
        Assert.assertEquals(10, result.size());
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            Assert.assertEquals(partitionId, local.getPartitionService().getPartition(entry.getKey()).getPartitionId());
            Assert.assertEquals((-1), entry.getValue());
        }
    }

    @Test
    public void removeAll() {
        int sizeBefore = map.size();
        int partitionSizeBefore = map.keySet(predicate).size();
        map.removeAll(predicate);
        Assert.assertEquals((sizeBefore - partitionSizeBefore), map.size());
        Assert.assertEquals(0, map.keySet(predicate).size());
        for (int i = 0; i < (PartitionPredicateTest.ITEMS_PER_PARTITION); ++i) {
            String key;
            do {
                key = HazelcastTestSupport.generateKeyForPartition(local, partitionId);
            } while (map.containsKey(key) );
            map.put(key, i);
        }
        sizeBefore = map.size();
        partitionSizeBefore = map.keySet(predicate).size();
        Assert.assertEquals(PartitionPredicateTest.ITEMS_PER_PARTITION, partitionSizeBefore);
        map.removeAll(new PartitionPredicate<String, Integer>(partitionKey, Predicates.equal("this", ((PartitionPredicateTest.ITEMS_PER_PARTITION) - 1))));
        Assert.assertEquals((sizeBefore - 1), map.size());
        Assert.assertEquals((partitionSizeBefore - 1), map.keySet(predicate).size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void apply() {
        Assert.assertTrue(predicate.apply(null));
    }

    @Test
    public void testToString() {
        HazelcastTestSupport.assertContains(predicate.toString(), "PartitionPredicate");
        HazelcastTestSupport.assertContains(predicate.toString(), ("partitionKey=" + (partitionKey)));
    }

    @Test
    public void testSerialization() {
        SerializationService serializationService = HazelcastTestSupport.getSerializationService(local);
        Data serialized = serializationService.toData(predicate);
        PartitionPredicate deserialized = serializationService.toObject(serialized);
        Assert.assertEquals(partitionKey, deserialized.getPartitionKey());
        Assert.assertEquals(INSTANCE, deserialized.getTarget());
    }

    private static class EntryNoop extends AbstractEntryProcessor<String, Integer> {
        @Override
        public Object process(Map.Entry<String, Integer> entry) {
            return -1;
        }
    }
}

