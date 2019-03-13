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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * This tests puts a lot of key/values in a map, where the value is the same as the key. With a client these
 * key/values are read and are expected to be consistent, even if member join and leave the cluster all the time.
 * <p/>
 * If there would be a bug in replicating the data, it could pop up here.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class MapStableReadStressTest extends StressTestSupport {
    public static final int CLIENT_THREAD_COUNT = 5;

    public static final int MAP_SIZE = 100 * 1000;

    private HazelcastInstance client;

    private IMap<Integer, Integer> map;

    private MapStableReadStressTest.StressThread[] stressThreads;

    @Test(timeout = 600000)
    public void testChangingCluster() {
        test(true);
    }

    @Test(timeout = 600000)
    public void testFixedCluster() {
        test(false);
    }

    public class StressThread extends StressTestSupport.TestThread {
        @Override
        public void doRun() throws Exception {
            while (!(isStopped())) {
                int key = random.nextInt(MapStableReadStressTest.MAP_SIZE);
                int value = map.get(key);
                Assert.assertEquals("The value for the key was not consistent", key, value);
            } 
        }
    }
}

