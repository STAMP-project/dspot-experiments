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
package com.hazelcast.map.impl.querycache;


import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheIMapEventHandlingTest extends HazelcastTestSupport {
    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Integer> TRUE_PREDICATE = TruePredicate.INSTANCE;

    private HazelcastInstance member;

    private String mapName;

    private IMap<Integer, Integer> map;

    private QueryCache<Integer, Integer> queryCache;

    @Test
    public void testEvent_MERGED() throws Exception {
        final int key = 1;
        final int existingValue = 1;
        final int mergingValue = 2;
        map.put(key, existingValue);
        executeMergeOperation(member, mapName, key, mergingValue);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Integer currentValue = queryCache.get(key);
                Assert.assertEquals(mergingValue, ((Object) (currentValue)));
            }
        });
    }

    @Test
    public void testEvent_EXPIRED() throws Exception {
        int key = 1;
        int value = 1;
        final CountDownLatch latch = new CountDownLatch(1);
        queryCache.addEntryListener(new EntryAddedListener() {
            @Override
            public void entryAdded(EntryEvent event) {
                latch.countDown();
            }
        }, true);
        map.put(key, value, 1, TimeUnit.SECONDS);
        latch.await();
        HazelcastTestSupport.sleepSeconds(1);
        // map#get creates EXPIRED event
        map.get(key);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(0, queryCache.size());
            }
        });
    }

    @Test
    public void testListenerRegistration() {
        String addEntryListener = queryCache.addEntryListener(new EntryAddedListener<Integer, Integer>() {
            @Override
            public void entryAdded(EntryEvent<Integer, Integer> event) {
            }
        }, true);
        String removeEntryListener = queryCache.addEntryListener(new com.hazelcast.map.listener.EntryRemovedListener<Integer, Integer>() {
            @Override
            public void entryRemoved(EntryEvent<Integer, Integer> event) {
            }
        }, true);
        Assert.assertFalse(queryCache.removeEntryListener("notFound"));
        Assert.assertTrue(queryCache.removeEntryListener(removeEntryListener));
        Assert.assertFalse(queryCache.removeEntryListener(removeEntryListener));
        Assert.assertTrue(queryCache.removeEntryListener(addEntryListener));
        Assert.assertFalse(queryCache.removeEntryListener(addEntryListener));
    }
}

