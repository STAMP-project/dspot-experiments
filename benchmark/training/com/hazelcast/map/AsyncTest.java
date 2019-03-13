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


import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AsyncTest extends HazelcastTestSupport {
    private final String key = "key";

    private final String value1 = "value1";

    private final String value2 = "value2";

    protected HazelcastInstance instance;

    @Test
    public void testGetAsync() throws Exception {
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        map.put(key, value1);
        Future<String> f1 = map.getAsync(key);
        Assert.assertEquals(value1, f1.get());
    }

    @Test
    public void testPutAsync() throws Exception {
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        Future<String> f1 = map.putAsync(key, value1);
        String f1Val = f1.get();
        Assert.assertNull(f1Val);
        Future<String> f2 = map.putAsync(key, value2);
        String f2Val = f2.get();
        Assert.assertEquals(value1, f2Val);
    }

    @Test
    public void testPutAsyncWithTtl() throws Exception {
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        final CountDownLatch latch = new CountDownLatch(1);
        map.addEntryListener(new com.hazelcast.map.listener.EntryEvictedListener<String, String>() {
            public void entryEvicted(EntryEvent<String, String> event) {
                latch.countDown();
            }
        }, true);
        Future<String> f1 = map.putAsync(key, value1, 3, TimeUnit.SECONDS);
        String f1Val = f1.get();
        Assert.assertNull(f1Val);
        Assert.assertEquals(value1, map.get(key));
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testSetAsync() throws Exception {
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        Future<Void> f1 = map.setAsync(key, value1);
        f1.get();
        Assert.assertEquals(value1, map.get(key));
        Future<Void> f2 = map.setAsync(key, value2);
        f2.get();
        Assert.assertEquals(value2, map.get(key));
    }

    @Test
    public void testSetAsync_issue9599() throws Exception {
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        Future<Void> f = map.setAsync(key, value1);
        // the return value was not of type Void, but Boolean. So assignment to Void would fail.
        Void v = f.get();
        Assert.assertNull(v);
    }

    @Test
    public void testSetAsyncWithTtl() throws Exception {
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        final CountDownLatch latch = new CountDownLatch(1);
        map.addEntryListener(new com.hazelcast.map.listener.EntryEvictedListener<String, String>() {
            public void entryEvicted(EntryEvent<String, String> event) {
                latch.countDown();
            }
        }, true);
        Future<Void> f1 = map.setAsync(key, value1, 3, TimeUnit.SECONDS);
        f1.get();
        Assert.assertEquals(value1, map.get(key));
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testRemoveAsync() throws Exception {
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        // populate map
        map.put(key, value1);
        Future<String> f1 = map.removeAsync(key);
        Assert.assertEquals(value1, f1.get());
    }

    @Test
    public void testRemoveAsyncWithImmediateTimeout() throws Exception {
        final IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        // populate map
        map.put(key, value1);
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            public void run() {
                map.lock(key);
                latch.countDown();
            }
        }).start();
        Assert.assertTrue(latch.await(20, TimeUnit.SECONDS));
        Future<String> f1 = map.removeAsync(key);
        try {
            Assert.assertEquals(value1, f1.get(0L, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            // expected
            return;
        }
        Assert.fail("Failed to throw TimeoutException with zero timeout");
    }

    @Test
    public void testRemoveAsyncWithNonExistentKey() throws Exception {
        IMap<String, String> map = instance.getMap(HazelcastTestSupport.randomString());
        Future<String> f1 = map.removeAsync(key);
        Assert.assertNull(f1.get());
    }
}

