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
package com.hazelcast.client.map.impl.nearcache;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapNearCacheInvalidationFromClientTest extends HazelcastTestSupport {
    private String mapName;

    private TestHazelcastFactory factory;

    private HazelcastInstance lite;

    private HazelcastInstance client;

    @Test
    public void testPut() {
        IMap<Object, Object> map = client.getMap(mapName);
        int count = 100;
        for (int i = 0; i < count; i++) {
            map.put(i, i);
        }
        IMap<Object, Object> liteMap = lite.getMap(mapName);
        for (int i = 0; i < count; i++) {
            Assert.assertNotNull(liteMap.get(i));
        }
        NearCache nearCache = getNearCache(lite, mapName);
        int sizeAfterPut = nearCache.size();
        Assert.assertTrue(("Near Cache size should be > 0 but was " + sizeAfterPut), (sizeAfterPut > 0));
    }

    @Test
    public void testClear() {
        IMap<Object, Object> map = client.getMap(mapName);
        final int count = 100;
        for (int i = 0; i < count; i++) {
            map.put(i, i);
        }
        final IMap<Object, Object> liteMap = lite.getMap(mapName);
        final NearCache nearCache = getNearCache(lite, mapName);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    liteMap.get(i);
                }
                Assert.assertEquals(count, nearCache.size());
            }
        });
        map.clear();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, nearCache.size());
            }
        });
    }

    @Test
    public void testEvictAll() {
        IMap<Object, Object> map = client.getMap(mapName);
        final int count = 100;
        for (int i = 0; i < count; i++) {
            map.put(i, i);
        }
        final IMap<Object, Object> liteMap = lite.getMap(mapName);
        final NearCache nearCache = getNearCache(lite, mapName);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    liteMap.get(i);
                }
                Assert.assertEquals(count, nearCache.size());
            }
        });
        map.evictAll();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((nearCache.size()) < count));
            }
        });
    }

    @Test
    public void testEvict() {
        IMap<Object, Object> map = client.getMap(mapName);
        final int count = 100;
        for (int i = 0; i < count; i++) {
            map.put(i, i);
        }
        final IMap<Object, Object> liteMap = lite.getMap(mapName);
        final NearCache nearCache = getNearCache(lite, mapName);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    liteMap.get(i);
                }
                Assert.assertEquals(count, nearCache.size());
            }
        });
        map.evict(0);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((nearCache.size()) < count));
            }
        });
    }

    @Test
    public void testUpdate() {
        IMap<Object, Object> map = client.getMap(mapName);
        map.put(1, 1);
        final IMap<Object, Object> liteMap = lite.getMap(mapName);
        final NearCache<Object, Object> nearCache = getNearCache(lite, mapName);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                liteMap.get(1);
                Assert.assertEquals(1, nearCache.get(1));
            }
        });
        map.put(1, 2);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNull(nearCache.get(1));
            }
        });
    }

    @Test
    public void testRemove() {
        IMap<Object, Object> map = client.getMap(mapName);
        map.put(1, 1);
        final IMap<Object, Object> liteMap = lite.getMap(mapName);
        final NearCache<Object, Object> nearCache = getNearCache(lite, mapName);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                liteMap.get(1);
                Assert.assertEquals(1, nearCache.get(1));
            }
        });
        map.remove(1);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNull(nearCache.get(1));
            }
        });
    }
}

