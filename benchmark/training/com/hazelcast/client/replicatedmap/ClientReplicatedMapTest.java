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
package com.hazelcast.client.replicatedmap;


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientReplicatedMapTest extends HazelcastTestSupport {
    private static final int OPERATION_COUNT = 100;

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    private Config config = new Config();

    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void testEmptyMapIsEmpty() {
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap<Integer, Integer> map = client.getReplicatedMap(randomName());
        Assert.assertTrue("map should be empty", map.isEmpty());
    }

    @Test
    public void testNonEmptyMapIsNotEmpty() {
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap<Integer, Integer> map = client.getReplicatedMap(randomName());
        map.put(1, 1);
        Assert.assertFalse("map should not be empty", map.isEmpty());
    }

    @Test
    public void testPutAll() {
        HazelcastInstance server = factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap<String, String> map1 = client.getReplicatedMap("default");
        ReplicatedMap<String, String> map2 = server.getReplicatedMap("default");
        Map<String, String> mapTest = new HashMap<String, String>();
        for (int i = 0; i < 100; i++) {
            mapTest.put(("foo-" + i), "bar");
        }
        map1.putAll(mapTest);
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
    }

    @Test
    public void testGet() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<String, String> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<String, String> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            map1.put(("foo-" + i), "bar");
        }
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            Assert.assertEquals("bar", map1.get(("foo-" + i)));
            Assert.assertEquals("bar", map2.get(("foo-" + i)));
        }
    }

    @Test
    public void testPutNullReturnValueDeserialization() {
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap<Object, Object> map = client.getReplicatedMap(randomMapName());
        Assert.assertNull(map.put(1, 2));
    }

    @Test
    public void testPutReturnValueDeserialization() {
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap<Object, Object> map = client.getReplicatedMap(randomMapName());
        map.put(1, 2);
        Assert.assertEquals(2, map.put(1, 3));
    }

    @Test
    public void testAdd() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<String, String> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<String, String> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            map1.put(("foo-" + i), "bar");
        }
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
    }

    @Test
    public void testClear() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<String, String> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<String, String> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            map1.put(("foo-" + i), "bar");
        }
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
        map1.clear();
        Assert.assertEquals(0, map1.size());
        Assert.assertEquals(0, map2.size());
    }

    @Test
    public void testUpdate() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<String, String> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<String, String> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            map1.put(("foo-" + i), "bar");
        }
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            map2.put(("foo-" + i), "bar2");
        }
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            Assert.assertEquals("bar2", entry.getValue());
        }
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            Assert.assertEquals("bar2", entry.getValue());
        }
    }

    @Test
    public void testRemove() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<String, String> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<String, String> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            map1.put(("foo-" + i), "bar");
        }
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            assertStartsWith("foo-", entry.getKey());
            Assert.assertEquals("bar", entry.getValue());
        }
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            map2.remove(("foo-" + i));
        }
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            Assert.assertNull(map2.get(("foo-" + i)));
        }
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            Assert.assertNull(map1.get(("foo-" + i)));
        }
    }

    @Test
    public void testSize() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<Integer, Integer> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<Integer, Integer> map2 = instance2.getReplicatedMap("default");
        final AbstractMap.SimpleEntry<Integer, Integer>[] testValues = ClientReplicatedMapTest.buildTestValues();
        int half = (testValues.length) / 2;
        for (int i = 0; i < (testValues.length); i++) {
            ReplicatedMap<Integer, Integer> map = (i < half) ? map1 : map2;
            AbstractMap.SimpleEntry<Integer, Integer> entry = testValues[i];
            map.put(entry.getKey(), entry.getValue());
        }
        Assert.assertEquals(testValues.length, map1.size());
        Assert.assertEquals(testValues.length, map2.size());
    }

    @Test
    public void testContainsKey() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<String, String> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<String, String> map2 = instance2.getReplicatedMap("default");
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            map1.put(("foo-" + i), "bar");
        }
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            Assert.assertTrue(map2.containsKey(("foo-" + i)));
        }
        for (int i = 0; i < (ClientReplicatedMapTest.OPERATION_COUNT); i++) {
            Assert.assertTrue(map1.containsKey(("foo-" + i)));
        }
    }

    @Test
    public void testContainsValue() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<Integer, Integer> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<Integer, Integer> map2 = instance2.getReplicatedMap("default");
        final AbstractMap.SimpleEntry<Integer, Integer>[] testValues = ClientReplicatedMapTest.buildTestValues();
        int half = (testValues.length) / 2;
        for (int i = 0; i < (testValues.length); i++) {
            ReplicatedMap<Integer, Integer> map = (i < half) ? map1 : map2;
            AbstractMap.SimpleEntry<Integer, Integer> entry = testValues[i];
            map.put(entry.getKey(), entry.getValue());
        }
        for (AbstractMap.SimpleEntry<Integer, Integer> testValue : testValues) {
            Assert.assertTrue(map2.containsValue(testValue.getValue()));
        }
        for (AbstractMap.SimpleEntry<Integer, Integer> testValue : testValues) {
            Assert.assertTrue(map1.containsValue(testValue.getValue()));
        }
    }

    @Test
    public void testValues() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<Integer, Integer> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<Integer, Integer> map2 = instance2.getReplicatedMap("default");
        final AbstractMap.SimpleEntry<Integer, Integer>[] testValues = ClientReplicatedMapTest.buildTestValues();
        int half = (testValues.length) / 2;
        for (int i = 0; i < (testValues.length); i++) {
            ReplicatedMap<Integer, Integer> map = (i < half) ? map1 : map2;
            AbstractMap.SimpleEntry<Integer, Integer> entry = testValues[i];
            map.put(entry.getKey(), entry.getValue());
        }
        Set<Integer> values1 = new HashSet<Integer>(map1.values());
        Set<Integer> values2 = new HashSet<Integer>(map2.values());
        for (AbstractMap.SimpleEntry<Integer, Integer> e : testValues) {
            assertContains(values1, e.getValue());
            assertContains(values2, e.getValue());
        }
    }

    @Test
    public void testKeySet() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<Integer, Integer> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<Integer, Integer> map2 = instance2.getReplicatedMap("default");
        final AbstractMap.SimpleEntry<Integer, Integer>[] testValues = ClientReplicatedMapTest.buildTestValues();
        int half = (testValues.length) / 2;
        for (int i = 0; i < (testValues.length); i++) {
            ReplicatedMap<Integer, Integer> map = (i < half) ? map1 : map2;
            AbstractMap.SimpleEntry<Integer, Integer> entry = testValues[i];
            map.put(entry.getKey(), entry.getValue());
        }
        Set<Integer> keys1 = new HashSet<Integer>(map1.keySet());
        Set<Integer> keys2 = new HashSet<Integer>(map2.keySet());
        for (AbstractMap.SimpleEntry<Integer, Integer> e : testValues) {
            assertContains(keys1, e.getKey());
            assertContains(keys2, e.getKey());
        }
    }

    @Test
    public void testEntrySet() {
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastClient();
        final ReplicatedMap<Integer, Integer> map1 = instance1.getReplicatedMap("default");
        final ReplicatedMap<Integer, Integer> map2 = instance2.getReplicatedMap("default");
        final AbstractMap.SimpleEntry<Integer, Integer>[] testValues = ClientReplicatedMapTest.buildTestValues();
        int half = (testValues.length) / 2;
        for (int i = 0; i < (testValues.length); i++) {
            ReplicatedMap<Integer, Integer> map = (i < half) ? map1 : map2;
            AbstractMap.SimpleEntry<Integer, Integer> entry = testValues[i];
            map.put(entry.getKey(), entry.getValue());
        }
        Set<Map.Entry<Integer, Integer>> entrySet1 = new HashSet<Map.Entry<Integer, Integer>>(map1.entrySet());
        Set<Map.Entry<Integer, Integer>> entrySet2 = new HashSet<Map.Entry<Integer, Integer>>(map2.entrySet());
        for (Map.Entry<Integer, Integer> entry : entrySet2) {
            Integer value = ClientReplicatedMapTest.findValue(entry.getKey(), testValues);
            Assert.assertEquals(value, entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : entrySet1) {
            Integer value = ClientReplicatedMapTest.findValue(entry.getKey(), testValues);
            Assert.assertEquals(value, entry.getValue());
        }
    }

    @Test
    public void testRetrieveUnknownValue() {
        factory.newHazelcastInstance(config);
        HazelcastInstance instance = factory.newHazelcastClient();
        ReplicatedMap<String, String> map = instance.getReplicatedMap("default");
        String value = map.get("foo");
        Assert.assertNull(value);
    }

    @Test
    public void testNearCacheInvalidation() {
        String mapName = randomString();
        ClientConfig clientConfig = ClientReplicatedMapTest.getClientConfigWithNearCacheInvalidationEnabled();
        factory.newHazelcastInstance(config);
        HazelcastInstance client1 = factory.newHazelcastClient(clientConfig);
        HazelcastInstance client2 = factory.newHazelcastClient(clientConfig);
        final ReplicatedMap<Integer, Integer> replicatedMap1 = client1.getReplicatedMap(mapName);
        replicatedMap1.put(1, 1);
        // puts key 1 to Near Cache
        replicatedMap1.get(1);
        ReplicatedMap<Integer, Integer> replicatedMap2 = client2.getReplicatedMap(mapName);
        // this should invalidate Near Cache of replicatedMap1
        replicatedMap2.put(1, 2);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(2, ((int) (replicatedMap1.get(1))));
            }
        });
    }

    @Test
    public void testNearCacheInvalidation_withClear() {
        String mapName = randomString();
        ClientConfig clientConfig = ClientReplicatedMapTest.getClientConfigWithNearCacheInvalidationEnabled();
        factory.newHazelcastInstance(config);
        HazelcastInstance client1 = factory.newHazelcastClient(clientConfig);
        HazelcastInstance client2 = factory.newHazelcastClient(clientConfig);
        final ReplicatedMap<Integer, Integer> replicatedMap1 = client1.getReplicatedMap(mapName);
        replicatedMap1.put(1, 1);
        // puts key 1 to Near Cache
        replicatedMap1.get(1);
        ReplicatedMap replicatedMap2 = client2.getReplicatedMap(mapName);
        // this should invalidate Near Cache of replicatedMap1
        replicatedMap2.clear();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNull(replicatedMap1.get(1));
            }
        });
    }

    @Test
    public void testClientPortableWithoutRegisteringToNode() {
        if ((inMemoryFormat) == (InMemoryFormat.OBJECT)) {
            return;
        }
        SerializationConfig serializationConfig = new SerializationConfig().addPortableFactory(5, new PortableFactory() {
            public Portable create(int classId) {
                return new ClientReplicatedMapTest.SamplePortable();
            }
        });
        ClientConfig clientConfig = new ClientConfig().setSerializationConfig(serializationConfig);
        factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        ReplicatedMap<Integer, ClientReplicatedMapTest.SamplePortable> sampleMap = client.getReplicatedMap(randomString());
        sampleMap.put(1, new ClientReplicatedMapTest.SamplePortable(666));
        ClientReplicatedMapTest.SamplePortable samplePortable = sampleMap.get(1);
        Assert.assertEquals(666, samplePortable.a);
    }

    @Test
    public void clear_empties_internal_ttl_schedulers() {
        String mapName = "test";
        HazelcastInstance node = factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap map = client.getReplicatedMap(mapName);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i, 100, TimeUnit.DAYS);
        }
        map.clear();
        ClientReplicatedMapTest.assertAllTtlSchedulersEmpty(node.getReplicatedMap(mapName));
    }

    @Test
    public void remove_empties_internal_ttl_schedulers() {
        String mapName = "test";
        HazelcastInstance node = factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap map = client.getReplicatedMap(mapName);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i, 100, TimeUnit.DAYS);
        }
        for (int i = 0; i < 1000; i++) {
            map.remove(i);
        }
        ClientReplicatedMapTest.assertAllTtlSchedulersEmpty(node.getReplicatedMap(mapName));
    }

    @Test
    public void no_key_value_deserialization_on_server_when_entry_is_removed() {
        // only run this test for BINARY replicated maps.
        Assume.assumeThat(inMemoryFormat, Matchers.is(InMemoryFormat.BINARY));
        Config config = new Config();
        config.getReplicatedMapConfig("default").setInMemoryFormat(inMemoryFormat);
        HazelcastInstance server = factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap<ClientReplicatedMapTest.DeserializationCounter, ClientReplicatedMapTest.DeserializationCounter> replicatedMap = client.getReplicatedMap("test");
        ClientReplicatedMapTest.DeserializationCounter key = new ClientReplicatedMapTest.Key1();
        ClientReplicatedMapTest.DeserializationCounter value = new ClientReplicatedMapTest.Value1();
        replicatedMap.put(key, value);
        replicatedMap.remove(key);
        Assert.assertEquals(0, ((ClientReplicatedMapTest.Key1) (key)).COUNTER.get());
        // expect only 1 deserialization in ClientReplicatedMapProxy#remove method
        Assert.assertEquals(1, ((ClientReplicatedMapTest.Value1) (value)).COUNTER.get());
    }

    @Test
    public void no_key_value_deserialization_on_server_when_entry_is_get() {
        // only run this test for BINARY replicated maps.
        Assume.assumeThat(inMemoryFormat, Matchers.is(InMemoryFormat.BINARY));
        Config config = new Config();
        config.getReplicatedMapConfig("default").setInMemoryFormat(inMemoryFormat);
        HazelcastInstance server = factory.newHazelcastInstance(config);
        HazelcastInstance client = factory.newHazelcastClient();
        ReplicatedMap<ClientReplicatedMapTest.DeserializationCounter, ClientReplicatedMapTest.DeserializationCounter> replicatedMap = client.getReplicatedMap("test");
        ClientReplicatedMapTest.DeserializationCounter key = new ClientReplicatedMapTest.Key2();
        ClientReplicatedMapTest.DeserializationCounter value = new ClientReplicatedMapTest.Value2();
        replicatedMap.put(key, value);
        replicatedMap.get(key);
        Assert.assertEquals(0, ((ClientReplicatedMapTest.Key2) (key)).COUNTER.get());
        // expect only 1 deserialization in ClientReplicatedMapProxy#remove method
        Assert.assertEquals(1, ((ClientReplicatedMapTest.Value2) (value)).COUNTER.get());
    }

    public static class Key1 extends ClientReplicatedMapTest.DeserializationCounter {
        static final AtomicInteger COUNTER = new AtomicInteger();

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            ClientReplicatedMapTest.Key1.COUNTER.incrementAndGet();
        }
    }

    public static class Value1 extends ClientReplicatedMapTest.DeserializationCounter {
        static final AtomicInteger COUNTER = new AtomicInteger();

        @Override
        public void readData(ObjectDataInput in) {
            ClientReplicatedMapTest.Value1.COUNTER.incrementAndGet();
        }
    }

    public static class Key2 extends ClientReplicatedMapTest.DeserializationCounter {
        static final AtomicInteger COUNTER = new AtomicInteger();

        @Override
        public void readData(ObjectDataInput in) {
            ClientReplicatedMapTest.Key2.COUNTER.incrementAndGet();
        }
    }

    public static class Value2 extends ClientReplicatedMapTest.DeserializationCounter {
        static final AtomicInteger COUNTER = new AtomicInteger();

        @Override
        public void readData(ObjectDataInput in) {
            ClientReplicatedMapTest.Value2.COUNTER.incrementAndGet();
        }
    }

    public abstract static class DeserializationCounter implements DataSerializable {
        @Override
        public void writeData(ObjectDataOutput out) {
            // NOP since we only care deserialization counts
        }
    }

    static class SamplePortable implements Portable {
        public int a;

        SamplePortable(int a) {
            this.a = a;
        }

        SamplePortable() {
        }

        @Override
        public int getFactoryId() {
            return 5;
        }

        @Override
        public int getClassId() {
            return 6;
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            writer.writeInt("a", a);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            a = reader.readInt("a");
        }
    }
}

