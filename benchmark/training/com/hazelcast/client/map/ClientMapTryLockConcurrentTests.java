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
package com.hazelcast.client.map;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapTryLockConcurrentTests {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    @Test
    public void concurrent_MapTryLockTest() throws InterruptedException {
        concurrent_MapTryLock(false);
    }

    @Test
    public void concurrent_MapTryLockTimeOutTest() throws InterruptedException {
        concurrent_MapTryLock(true);
    }

    static class MapTryLockThread extends ClientMapTryLockConcurrentTests.TestHelper {
        public MapTryLockThread(IMap map, String upKey, String downKey) {
            super(map, upKey, downKey);
        }

        public void doRun() throws Exception {
            if (map.tryLock(upKey)) {
                try {
                    if (map.tryLock(downKey)) {
                        try {
                            work();
                        } finally {
                            map.unlock(downKey);
                        }
                    }
                } finally {
                    map.unlock(upKey);
                }
            }
        }
    }

    static class MapTryLockTimeOutThread extends ClientMapTryLockConcurrentTests.TestHelper {
        public MapTryLockTimeOutThread(IMap map, String upKey, String downKey) {
            super(map, upKey, downKey);
        }

        public void doRun() throws Exception {
            if (map.tryLock(upKey, 1, TimeUnit.MILLISECONDS)) {
                try {
                    if (map.tryLock(downKey, 1, TimeUnit.MILLISECONDS)) {
                        try {
                            work();
                        } finally {
                            map.unlock(downKey);
                        }
                    }
                } finally {
                    map.unlock(upKey);
                }
            }
        }
    }

    abstract static class TestHelper extends Thread {
        protected static final int ITERATIONS = 1000 * 10;

        protected final Random random = new Random();

        protected final IMap<String, Integer> map;

        protected final String upKey;

        protected final String downKey;

        public TestHelper(IMap map, String upKey, String downKey) {
            this.map = map;
            this.upKey = upKey;
            this.downKey = downKey;
        }

        public void run() {
            try {
                for (int i = 0; i < (ClientMapTryLockConcurrentTests.TestHelper.ITERATIONS); i++) {
                    doRun();
                }
            } catch (Exception e) {
                throw new RuntimeException("Test Thread crashed with ", e);
            }
        }

        abstract void doRun() throws Exception;

        public void work() {
            int upTotal = map.get(upKey);
            int downTotal = map.get(downKey);
            int dif = random.nextInt(1000);
            upTotal += dif;
            downTotal -= dif;
            map.put(upKey, upTotal);
            map.put(downKey, downTotal);
        }
    }
}

