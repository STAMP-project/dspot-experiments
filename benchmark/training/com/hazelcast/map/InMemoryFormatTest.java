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


import InMemoryFormat.BINARY;
import InMemoryFormat.NATIVE;
import InMemoryFormat.OBJECT;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.monitor.impl.MemberPartitionStateImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InMemoryFormatTest extends HazelcastTestSupport {
    /**
     * if statistics enabled InMemoryFormat.Object does not work
     */
    @Test
    public void testIssue2622() {
        final String mapName = HazelcastTestSupport.randomString();
        Config config = new Config();
        final MapConfig mapConfig = new MapConfig(mapName);
        mapConfig.setInMemoryFormat(OBJECT);
        mapConfig.setStatisticsEnabled(true);
        config.addMapConfig(mapConfig);
        final HazelcastInstance instance = createHazelcastInstance(config);
        final IMap<String, InMemoryFormatTest.SerializationValue> map = instance.getMap(mapName);
        final InMemoryFormatTest.SerializationValue serializationValue = new InMemoryFormatTest.SerializationValue();
        map.put("key", serializationValue);
        // EntryProcessor should not trigger de-serialization
        map.executeOnKey("key", new AbstractEntryProcessor<String, InMemoryFormatTest.SerializationValue>() {
            @Override
            public Object process(final Map.Entry<String, InMemoryFormatTest.SerializationValue> entry) {
                return null;
            }
        });
        Assert.assertEquals(1, InMemoryFormatTest.SerializationValue.deSerializeCount.get());
    }

    @Test
    public void equals() {
        Config config = new Config();
        config.addMapConfig(new MapConfig("objectMap").setInMemoryFormat(OBJECT));
        config.addMapConfig(new MapConfig("binaryMap").setInMemoryFormat(BINARY));
        HazelcastInstance hz = createHazelcastInstance(config);
        InMemoryFormatTest.Pair v1 = new InMemoryFormatTest.Pair("a", "1");
        InMemoryFormatTest.Pair v2 = new InMemoryFormatTest.Pair("a", "2");
        IMap<String, InMemoryFormatTest.Pair> objectMap = hz.getMap("objectMap");
        IMap<String, InMemoryFormatTest.Pair> binaryMap = hz.getMap("binaryMap");
        objectMap.put("1", v1);
        binaryMap.put("1", v1);
        Assert.assertTrue(objectMap.containsValue(v1));
        Assert.assertTrue(objectMap.containsValue(v2));
        Assert.assertTrue(binaryMap.containsValue(v1));
        Assert.assertFalse(binaryMap.containsValue(v2));
    }

    @Test
    public void equalsReadLocalBackup() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        Config config = new Config();
        config.addMapConfig(new MapConfig("objectMap").setInMemoryFormat(OBJECT).setReadBackupData(true));
        HazelcastInstance hz1 = factory.newHazelcastInstance(config);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        InMemoryFormatTest.Pair pair = new InMemoryFormatTest.Pair("a", "1");
        IMap<String, InMemoryFormatTest.Pair> objectMap1 = hz1.getMap("objectMap");
        IMap<String, InMemoryFormatTest.Pair> objectMap2 = hz2.getMap("objectMap");
        objectMap1.put("1", pair);
        InMemoryFormatTest.Pair v1 = objectMap1.get("1");
        InMemoryFormatTest.Pair v2 = objectMap1.get("1");
        InMemoryFormatTest.Pair rv1 = objectMap2.get("1");
        InMemoryFormatTest.Pair rv2 = objectMap2.get("1");
        Assert.assertNotSame(pair, v1);
        Assert.assertNotSame(pair, v2);
        Assert.assertNotSame(v1, v2);
        Assert.assertNotSame(pair, rv1);
        Assert.assertNotSame(pair, rv2);
        Assert.assertNotSame(rv1, rv2);
        Assert.assertTrue(objectMap2.containsValue(v1));
    }

    @Test
    public void countDeserializationsOnContainsValue() {
        final Config config = new Config().addMapConfig(new MapConfig("default").setInMemoryFormat(OBJECT));
        final HazelcastInstance hz = createHazelcastInstance(config);
        final PartitionService partitionService = hz.getPartitionService();
        final IMap<Integer, Object> m = hz.getMap("mappy");
        final HashSet<Integer> nonEmptyPartitions = new HashSet<Integer>();
        for (int i = 0; i < ((MemberPartitionStateImpl.DEFAULT_PARTITION_COUNT) * 5); i++) {
            m.put(i, i);
            nonEmptyPartitions.add(partitionService.getPartition(i).getPartitionId());
        }
        final InMemoryFormatTest.SerializationCounting value = new InMemoryFormatTest.SerializationCounting();
        m.containsValue(value);
        Assert.assertEquals(nonEmptyPartitions.size(), InMemoryFormatTest.SerializationCounting.deserializationCount.get());
    }

    public static class SerializationCounting implements DataSerializable {
        public static AtomicInteger serializationCount = new AtomicInteger();

        public static AtomicInteger deserializationCount = new AtomicInteger();

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            InMemoryFormatTest.SerializationCounting.serializationCount.incrementAndGet();
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            InMemoryFormatTest.SerializationCounting.deserializationCount.incrementAndGet();
        }
    }

    public static final class Pair implements Serializable {
        private final String significant;

        private final String insignificant;

        Pair(String significant, String insignificant) {
            this.significant = significant;
            this.insignificant = insignificant;
        }

        @Override
        public boolean equals(Object thatObj) {
            if ((this) == thatObj) {
                return true;
            }
            if ((thatObj == null) || ((getClass()) != (thatObj.getClass()))) {
                return false;
            }
            InMemoryFormatTest.Pair that = ((InMemoryFormatTest.Pair) (thatObj));
            return this.significant.equals(that.significant);
        }

        @Override
        public int hashCode() {
            return significant.hashCode();
        }
    }

    public static class SerializationValue implements DataSerializable {
        static AtomicInteger deSerializeCount = new AtomicInteger();

        public SerializationValue() {
        }

        @Override
        public void writeData(final ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(final ObjectDataInput in) throws IOException {
            InMemoryFormatTest.SerializationValue.deSerializeCount.incrementAndGet();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNativeIMap_throwsException() throws Exception {
        Config config = getConfig();
        config.getMapConfig("default").setInMemoryFormat(NATIVE);
        HazelcastInstance member = createHazelcastInstance(config);
        member.getMap("default");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNativeNearCache_throwsException() throws Exception {
        NearCacheConfig nearCacheConfig = new NearCacheConfig();
        nearCacheConfig.setInMemoryFormat(NATIVE);
        Config config = getConfig();
        config.getMapConfig("default").setNearCacheConfig(nearCacheConfig);
        HazelcastInstance member = createHazelcastInstance(config);
        member.getMap("default");
    }
}

