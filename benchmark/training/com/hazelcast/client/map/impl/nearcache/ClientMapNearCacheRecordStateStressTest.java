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
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class ClientMapNearCacheRecordStateStressTest extends HazelcastTestSupport {
    private static final int KEY_SPACE = 100;

    private static final int TEST_RUN_SECONDS = 60;

    private static final int GET_ALL_THREAD_COUNT = 3;

    private static final int GET_THREAD_COUNT = 2;

    private static final int PUT_THREAD_COUNT = 1;

    private static final int CLEAR_THREAD_COUNT = 1;

    private static final int REMOVE_THREAD_COUNT = 1;

    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    private final AtomicBoolean stop = new AtomicBoolean();

    private IMap<Integer, Integer> memberMap;

    private IMap<Integer, Integer> clientMap;

    @Test
    public void allRecordsAreInReadableStateInTheEnd() throws Exception {
        // initial population of IMap from member
        for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
            memberMap.put(i, i);
        }
        List<Thread> threads = new ArrayList<Thread>();
        // member
        for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.PUT_THREAD_COUNT); i++) {
            ClientMapNearCacheRecordStateStressTest.Put put = new ClientMapNearCacheRecordStateStressTest.Put(memberMap);
            threads.add(put);
        }
        // client
        for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.GET_ALL_THREAD_COUNT); i++) {
            ClientMapNearCacheRecordStateStressTest.GetAll getAll = new ClientMapNearCacheRecordStateStressTest.GetAll(clientMap);
            threads.add(getAll);
        }
        for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.GET_THREAD_COUNT); i++) {
            ClientMapNearCacheRecordStateStressTest.Get get = new ClientMapNearCacheRecordStateStressTest.Get(clientMap);
            threads.add(get);
        }
        for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.REMOVE_THREAD_COUNT); i++) {
            ClientMapNearCacheRecordStateStressTest.Remove remove = new ClientMapNearCacheRecordStateStressTest.Remove(clientMap);
            threads.add(remove);
        }
        for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.CLEAR_THREAD_COUNT); i++) {
            ClientMapNearCacheRecordStateStressTest.Clear clear = new ClientMapNearCacheRecordStateStressTest.Clear(clientMap);
            threads.add(clear);
        }
        // start threads
        for (Thread thread : threads) {
            thread.start();
        }
        // stress for a while
        sleepSeconds(ClientMapNearCacheRecordStateStressTest.TEST_RUN_SECONDS);
        // stop threads
        stop.set(true);
        for (Thread thread : threads) {
            thread.join();
        }
        ClientMapNearCacheRecordStateStressTest.assertFinalRecordStateIsReadPermitted(clientMap);
    }

    private class Put extends Thread {
        private final IMap<Integer, Integer> map;

        private Put(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    map.put(i, getInt(ClientMapNearCacheRecordStateStressTest.KEY_SPACE));
                }
                sleepAtLeastMillis(100);
            } while (!(stop.get()) );
        }
    }

    private class Remove extends Thread {
        private final IMap<Integer, Integer> map;

        private Remove(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    map.remove(i);
                }
                sleepAtLeastMillis(100);
            } while (!(stop.get()) );
        }
    }

    private class Clear extends Thread {
        private final IMap<Integer, Integer> map;

        private Clear(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            do {
                map.clear();
                sleepAtLeastMillis(5000);
            } while (!(stop.get()) );
        }
    }

    private class GetAll extends Thread {
        private final IMap<Integer, Integer> map;

        private GetAll(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            HashSet<Integer> keys = new HashSet<Integer>();
            for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                keys.add(i);
            }
            do {
                map.getAll(keys);
                sleepAtLeastMillis(2);
            } while (!(stop.get()) );
        }
    }

    private class Get extends Thread {
        private final IMap<Integer, Integer> map;

        private Get(IMap<Integer, Integer> map) {
            this.map = map;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (ClientMapNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    map.get(i);
                }
            } while (!(stop.get()) );
        }
    }
}

