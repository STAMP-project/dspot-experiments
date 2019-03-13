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
package com.hazelcast.client.multimap;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
public class ClientMultiMapListenerStressTest {
    private static final int MAX_SECONDS = 60 * 10;

    private static final int NUMBER_OF_CLIENTS = 8;

    private static final int THREADS_PER_CLIENT = 4;

    private static final String MAP_NAME = HazelcastTestSupport.randomString();

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance server;

    @Test
    public void listenerAddStressTest() throws InterruptedException {
        final ClientMultiMapListenerStressTest.PutItemsThread[] putThreads = new ClientMultiMapListenerStressTest.PutItemsThread[(ClientMultiMapListenerStressTest.NUMBER_OF_CLIENTS) * (ClientMultiMapListenerStressTest.THREADS_PER_CLIENT)];
        int idx = 0;
        for (int i = 0; i < (ClientMultiMapListenerStressTest.NUMBER_OF_CLIENTS); i++) {
            HazelcastInstance client = hazelcastFactory.newHazelcastClient();
            for (int j = 0; j < (ClientMultiMapListenerStressTest.THREADS_PER_CLIENT); j++) {
                ClientMultiMapListenerStressTest.PutItemsThread t = new ClientMultiMapListenerStressTest.PutItemsThread(client);
                putThreads[(idx++)] = t;
            }
        }
        for (int i = 0; i < (putThreads.length); i++) {
            putThreads[i].start();
        }
        MultiMap multiMap = server.getMultiMap(ClientMultiMapListenerStressTest.MAP_NAME);
        HazelcastTestSupport.assertJoinable(ClientMultiMapListenerStressTest.MAX_SECONDS, putThreads);
        final int expectedSize = (ClientMultiMapListenerStressTest.PutItemsThread.MAX_ITEMS) * (putThreads.length);
        Assert.assertEquals(expectedSize, multiMap.size());
        assertReceivedEventsSize(expectedSize, putThreads);
    }

    public class PutItemsThread extends Thread {
        public static final int MAX_ITEMS = 100;

        public final ClientMultiMapListenerStressTest.MyEntryListener listener = new ClientMultiMapListenerStressTest.MyEntryListener();

        public HazelcastInstance client;

        public MultiMap mm;

        public String id;

        public PutItemsThread(HazelcastInstance client) {
            this.id = HazelcastTestSupport.randomString();
            this.client = client;
            this.mm = client.getMultiMap(ClientMultiMapListenerStressTest.MAP_NAME);
            mm.addEntryListener(listener, true);
        }

        public void run() {
            for (int i = 0; i < (ClientMultiMapListenerStressTest.PutItemsThread.MAX_ITEMS); i++) {
                mm.put(((id) + i), ((id) + i));
            }
        }

        public void assertResult(final int target) {
            HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                @Override
                public void run() throws Exception {
                    Assert.assertEquals(target, listener.add.get());
                }
            });
        }
    }

    static class MyEntryListener extends EntryAdapter {
        public AtomicInteger add = new AtomicInteger(0);

        public void entryAdded(EntryEvent event) {
            add.incrementAndGet();
        }
    }
}

