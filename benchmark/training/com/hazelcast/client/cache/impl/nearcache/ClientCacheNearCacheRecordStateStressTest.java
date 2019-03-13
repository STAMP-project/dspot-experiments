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
package com.hazelcast.client.cache.impl.nearcache;


import NearCacheConfig.LocalUpdatePolicy;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.Cache;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(NightlyTest.class)
public class ClientCacheNearCacheRecordStateStressTest extends HazelcastTestSupport {
    private static final int KEY_SPACE = 100;

    private static final int TEST_RUN_SECONDS = 60;

    private static final int GET_ALL_THREAD_COUNT = 2;

    private static final int PUT_ALL_THREAD_COUNT = 1;

    private static final int GET_THREAD_COUNT = 1;

    private static final int PUT_THREAD_COUNT = 1;

    private static final int PUT_IF_ABSENT_THREAD_COUNT = 1;

    private static final int CLEAR_THREAD_COUNT = 1;

    private static final int REMOVE_THREAD_COUNT = 1;

    @Parameterized.Parameter
    public LocalUpdatePolicy localUpdatePolicy;

    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    private final AtomicBoolean stop = new AtomicBoolean();

    private Cache<Integer, Integer> clientCache;

    private Cache<Integer, Integer> memberCache;

    @Test
    public void allRecordsAreInReadableStateInTheEnd() {
        // populated from member
        for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.KEY_SPACE); i++) {
            memberCache.put(i, i);
        }
        List<Thread> threads = new ArrayList<Thread>();
        // member
        for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.PUT_THREAD_COUNT); i++) {
            ClientCacheNearCacheRecordStateStressTest.Put put = new ClientCacheNearCacheRecordStateStressTest.Put(memberCache);
            threads.add(put);
        }
        // client
        for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.GET_ALL_THREAD_COUNT); i++) {
            ClientCacheNearCacheRecordStateStressTest.GetAll getAll = new ClientCacheNearCacheRecordStateStressTest.GetAll(clientCache);
            threads.add(getAll);
        }
        for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.PUT_ALL_THREAD_COUNT); i++) {
            ClientCacheNearCacheRecordStateStressTest.PutAll putAll = new ClientCacheNearCacheRecordStateStressTest.PutAll(clientCache);
            threads.add(putAll);
        }
        for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.PUT_IF_ABSENT_THREAD_COUNT); i++) {
            ClientCacheNearCacheRecordStateStressTest.PutIfAbsent putIfAbsent = new ClientCacheNearCacheRecordStateStressTest.PutIfAbsent(clientCache);
            threads.add(putIfAbsent);
        }
        for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.GET_THREAD_COUNT); i++) {
            ClientCacheNearCacheRecordStateStressTest.Get get = new ClientCacheNearCacheRecordStateStressTest.Get(clientCache);
            threads.add(get);
        }
        for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.REMOVE_THREAD_COUNT); i++) {
            ClientCacheNearCacheRecordStateStressTest.Remove remove = new ClientCacheNearCacheRecordStateStressTest.Remove(clientCache);
            threads.add(remove);
        }
        for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.CLEAR_THREAD_COUNT); i++) {
            ClientCacheNearCacheRecordStateStressTest.Clear clear = new ClientCacheNearCacheRecordStateStressTest.Clear(clientCache);
            threads.add(clear);
        }
        // start threads
        for (Thread thread : threads) {
            thread.start();
        }
        // stress for a while
        sleepSeconds(ClientCacheNearCacheRecordStateStressTest.TEST_RUN_SECONDS);
        // stop threads
        stop.set(true);
        for (Thread thread : threads) {
            assertJoinable(thread);
        }
        ClientCacheNearCacheRecordStateStressTest.assertFinalRecordStateIsReadPermitted(clientCache);
    }

    private class Put extends Thread {
        private final Cache<Integer, Integer> cache;

        private Put(Cache<Integer, Integer> cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    cache.put(i, getInt(ClientCacheNearCacheRecordStateStressTest.KEY_SPACE));
                }
                sleepAtLeastMillis(5000);
            } while (!(stop.get()) );
        }
    }

    private class Remove extends Thread {
        private final Cache<Integer, Integer> cache;

        private Remove(Cache<Integer, Integer> cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    cache.remove(i);
                }
                sleepAtLeastMillis(1000);
            } while (!(stop.get()) );
        }
    }

    private class PutIfAbsent extends Thread {
        private final Cache<Integer, Integer> cache;

        private PutIfAbsent(Cache<Integer, Integer> cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    cache.putIfAbsent(i, getInt(Integer.MAX_VALUE));
                }
                sleepAtLeastMillis(1000);
            } while (!(stop.get()) );
        }
    }

    private class Clear extends Thread {
        private final Cache<Integer, Integer> cache;

        private Clear(Cache<Integer, Integer> cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            do {
                cache.clear();
                sleepAtLeastMillis(5000);
            } while (!(stop.get()) );
        }
    }

    private class GetAll extends Thread {
        private final Cache<Integer, Integer> cache;

        private GetAll(Cache<Integer, Integer> cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            HashSet<Integer> keys = new HashSet<Integer>();
            for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                keys.add(i);
            }
            do {
                cache.getAll(keys);
                sleepAtLeastMillis(10);
            } while (!(stop.get()) );
        }
    }

    private class PutAll extends Thread {
        private final Cache<Integer, Integer> cache;

        private PutAll(Cache<Integer, Integer> cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            do {
                map.clear();
                for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    map.put(i, getInt(Integer.MAX_VALUE));
                }
                cache.putAll(map);
                sleepAtLeastMillis(2000);
            } while (!(stop.get()) );
        }
    }

    private class Get extends Thread {
        private final Cache<Integer, Integer> cache;

        private Get(Cache<Integer, Integer> cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            do {
                for (int i = 0; i < (ClientCacheNearCacheRecordStateStressTest.KEY_SPACE); i++) {
                    cache.get(i);
                }
            } while (!(stop.get()) );
        }
    }
}

