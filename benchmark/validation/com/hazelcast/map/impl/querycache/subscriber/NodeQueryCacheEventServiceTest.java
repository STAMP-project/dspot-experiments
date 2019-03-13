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
package com.hazelcast.map.impl.querycache.subscriber;


import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NodeQueryCacheEventServiceTest extends HazelcastTestSupport {
    @Test
    public void no_left_over_listener_after_concurrent_addition_and_removal_on_same_queryCache() throws InterruptedException {
        final String mapName = "test";
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        final HazelcastInstance node = factory.newHazelcastInstance();
        SubscriberContext subscriberContext = NodeQueryCacheEventServiceTest.getSubscriberContext(node);
        final NodeQueryCacheEventService nodeQueryCacheEventService = ((NodeQueryCacheEventService) (subscriberContext.getEventService()));
        final AtomicBoolean stop = new AtomicBoolean(false);
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (!(stop.get())) {
                        nodeQueryCacheEventService.addListener(mapName, "a", new EntryAddedListener() {
                            @Override
                            public void entryAdded(EntryEvent event) {
                            }
                        });
                        nodeQueryCacheEventService.removeAllListeners(mapName, "a");
                    } 
                }
            };
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        HazelcastTestSupport.sleepSeconds(5);
        stop.set(true);
        for (Thread thread : threads) {
            thread.join();
        }
        NodeQueryCacheEventServiceTest.assertNoUserListenerLeft(node);
    }
}

