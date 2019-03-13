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
package com.hazelcast.client.stress;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.NightlyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * This tests verifies that map updates are not lost. So we have a client which is going to do updates on a map
 * and in the end we verify that the actual updates in the map, are the same as the expected updates.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class MapUpdateStressTest extends StressTestSupport {
    public static final int CLIENT_THREAD_COUNT = 5;

    public static final int MAP_SIZE = 100 * 1000;

    private HazelcastInstance client;

    private IMap<Integer, Integer> map;

    private MapUpdateStressTest.StressThread[] stressThreads;

    @Test(timeout = 600000)
    public void testFixedCluster() {
        test(false);
    }

    public class StressThread extends StressTestSupport.TestThread {
        private final int[] increments = new int[MapUpdateStressTest.MAP_SIZE];

        @Override
        public void doRun() throws Exception {
            while (!(isStopped())) {
                int key = random.nextInt(MapUpdateStressTest.MAP_SIZE);
                int increment = random.nextInt(10);
                increments[key] += increment;
                for (; ;) {
                    int oldValue = map.get(key);
                    if (map.replace(key, oldValue, (oldValue + increment))) {
                        break;
                    }
                }
            } 
        }

        public void addIncrements(int[] increments) {
            for (int k = 0; k < (increments.length); k++) {
                increments[k] += this.increments[k];
            }
        }
    }
}

