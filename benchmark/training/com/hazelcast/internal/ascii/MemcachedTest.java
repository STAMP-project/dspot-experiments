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
package com.hazelcast.internal.ascii;


import MemcacheCommandProcessor.DEFAULT_MAP_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class MemcachedTest extends HazelcastTestSupport {
    protected HazelcastInstance instance;

    protected MemcachedClient client;

    @Test
    public void testSetAndGet() throws Exception {
        String key = "key";
        String value = "value";
        OperationFuture<Boolean> future = client.set(key, 0, value);
        Assert.assertEquals(Boolean.TRUE, future.get());
        Assert.assertEquals(value, client.get(key));
        checkStats(1, 1, 1, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testAddAndGet() throws Exception {
        String key = "key";
        String value = "value";
        String key2 = "key2";
        String value2 = "value2";
        OperationFuture<Boolean> future = client.set(key, 0, value);
        Assert.assertEquals(Boolean.TRUE, future.get());
        future = client.add(key, 0, value2);
        Assert.assertEquals(Boolean.FALSE, future.get());
        Assert.assertEquals(value, client.get(key));
        future = client.add(key2, 0, value2);
        Assert.assertEquals(Boolean.TRUE, future.get());
        Assert.assertEquals(value2, client.get(key2));
        checkStats(3, 2, 2, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testReplace() throws Exception {
        String key = "key";
        String value = "value";
        String value2 = "value2";
        OperationFuture<Boolean> future = client.replace(key, 0, value2);
        Assert.assertEquals(Boolean.FALSE, future.get());
        Assert.assertNull(client.get(key));
        future = client.set(key, 0, value);
        Assert.assertEquals(Boolean.TRUE, future.get());
        future = client.replace(key, 0, value2);
        Assert.assertEquals(Boolean.TRUE, future.get());
        Assert.assertEquals(value2, client.get(key));
        checkStats(3, 2, 1, 1, 0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testDelete() throws Exception {
        String key = "key";
        String value = "value";
        OperationFuture<Boolean> future = client.delete(key);
        Assert.assertEquals(Boolean.FALSE, future.get());
        future = client.set(key, 0, value);
        Assert.assertEquals(Boolean.TRUE, future.get());
        future = client.delete(key);
        Assert.assertEquals(Boolean.TRUE, future.get());
        Assert.assertNull(client.get(key));
        checkStats(1, 1, 0, 1, 1, 1, 0, 0, 0, 0);
    }

    @Test
    public void testBulkGet() throws Exception {
        List<String> keys = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            keys.add(("key" + i));
        }
        Map<String, Object> result = client.getBulk(keys);
        Assert.assertEquals(0, result.size());
        String value = "value";
        for (String key : keys) {
            OperationFuture<Boolean> future = client.set(key, 0, value);
            future.get();
        }
        result = client.getBulk(keys);
        Assert.assertEquals(keys.size(), result.size());
        for (String key : keys) {
            Assert.assertEquals(value, result.get(key));
        }
        checkStats(keys.size(), ((keys.size()) * 2), keys.size(), keys.size(), 0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testSetGetDelete_WithDefaultIMap() throws Exception {
        testSetGetDelete_WithIMap(DEFAULT_MAP_NAME, "");
    }

    @Test
    public void testSetGetDelete_WithCustomIMap() throws Exception {
        String mapName = HazelcastTestSupport.randomMapName();
        testSetGetDelete_WithIMap(((MemcacheCommandProcessor.MAP_NAME_PREFIX) + mapName), (mapName + ":"));
    }

    @Test
    public void testDeleteAll_withIMapPrefix() throws Exception {
        String mapName = HazelcastTestSupport.randomMapName();
        String prefix = mapName + ":";
        IMap<String, Object> map = instance.getMap(((MemcacheCommandProcessor.MAP_NAME_PREFIX) + mapName));
        for (int i = 0; i < 100; i++) {
            map.put(String.valueOf(i), i);
        }
        OperationFuture<Boolean> future = client.delete(prefix);
        future.get();
        for (int i = 0; i < 100; i++) {
            Assert.assertNull(client.get((prefix + (String.valueOf(i)))));
        }
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testIncrement() throws Exception {
        String key = "key";
        long value = client.incr(key, 1);
        Assert.assertEquals((-1), value);
        Assert.assertNull(client.get(key));
        OperationFuture<Boolean> future = client.set(key, 0, 1);
        future.get();
        value = client.incr(key, 10);
        Assert.assertEquals(11, value);
        value = client.incr(key, (-5));
        Assert.assertEquals(6, value);
        checkStats(1, 1, 0, 1, 0, 0, 2, 1, 0, 0);
    }

    @Test
    public void testDecrement() throws Exception {
        String key = "key";
        long value = client.decr(key, 1);
        Assert.assertEquals((-1), value);
        Assert.assertNull(client.get(key));
        OperationFuture<Boolean> future = client.set(key, 0, 5);
        future.get();
        value = client.decr(key, 2);
        Assert.assertEquals(3, value);
        value = client.decr(key, (-2));
        Assert.assertEquals(5, value);
        checkStats(1, 1, 0, 1, 0, 0, 0, 0, 2, 1);
    }

    @Test
    public void testAppend() throws Exception {
        String key = "key";
        String value = "value";
        String append = "123";
        OperationFuture<Boolean> future = client.append(key, append);
        Assert.assertEquals(Boolean.FALSE, future.get());
        future = client.set(key, 0, value);
        future.get();
        future = client.append(key, append);
        Assert.assertEquals(Boolean.TRUE, future.get());
        Assert.assertEquals((value + append), client.get(key));
    }

    @Test
    public void testPrepend() throws Exception {
        String key = "key";
        String value = "value";
        String prepend = "123";
        OperationFuture<Boolean> future = client.prepend(key, prepend);
        Assert.assertEquals(Boolean.FALSE, future.get());
        future = client.set(key, 0, value);
        future.get();
        future = client.prepend(key, prepend);
        Assert.assertEquals(Boolean.TRUE, future.get());
        Assert.assertEquals((prepend + value), client.get(key));
    }

    @Test
    public void testExpiration() throws Exception {
        final String key = "key";
        client.set(key, 3, "value").get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNull(client.get(key));
            }
        });
    }

    @Test
    public void testSetGet_withLargeValue() throws Exception {
        String key = "key";
        int capacity = 10000;
        StringBuilder value = new StringBuilder(capacity);
        while ((value.length()) < capacity) {
            value.append(HazelcastTestSupport.randomString());
        } 
        OperationFuture<Boolean> future = client.set(key, 0, value.toString());
        future.get();
        Object result = client.get(key);
        Assert.assertEquals(value.toString(), result);
    }

    @Test
    public void testBulkSetGet_withManyKeys() throws Exception {
        int numberOfKeys = 1000;
        Collection<String> keys = new HashSet<String>(numberOfKeys);
        for (int i = 0; i < numberOfKeys; i++) {
            String key = "key" + i;
            OperationFuture<Boolean> future = client.set(key, 0, key);
            future.get();
            keys.add(key);
        }
        Map<String, Object> result = client.getBulk(keys);
        for (String key : keys) {
            Assert.assertEquals(key, result.get(key));
        }
    }
}

