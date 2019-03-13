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
package com.hazelcast.internal.eviction;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.impl.eviction.MapClearExpiredRecordsTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.OverridePropertyRule;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class MapExpirationStressTest extends HazelcastTestSupport {
    @Rule
    public final OverridePropertyRule overrideTaskSecondsRule = OverridePropertyRule.set(MapClearExpiredRecordsTask.PROP_TASK_PERIOD_SECONDS, "2");

    protected final String mapName = "test";

    private static final int CLUSTER_SIZE = 5;

    private static final int KEY_RANGE = 100000;

    private HazelcastInstance[] instances = new HazelcastInstance[MapExpirationStressTest.CLUSTER_SIZE];

    private TestHazelcastInstanceFactory factory;

    private Random random = new Random();

    private final AtomicBoolean done = new AtomicBoolean();

    private final int DURATION_SECONDS = 20;

    @Test
    public void test() throws InterruptedException {
        List<Thread> list = new ArrayList<Thread>();
        for (int i = 0; i < (MapExpirationStressTest.CLUSTER_SIZE); i++) {
            list.add(new Thread(new MapExpirationStressTest.TestRunner(instances[i].getMap(mapName), done)));
        }
        for (Thread thread : list) {
            thread.start();
        }
        HazelcastTestSupport.sleepAtLeastSeconds(DURATION_SECONDS);
        done.set(true);
        for (Thread thread : list) {
            thread.join();
        }
        assertRecords(instances);
    }

    class TestRunner implements Runnable {
        private IMap map;

        private AtomicBoolean done;

        TestRunner(IMap map, AtomicBoolean done) {
            this.map = map;
            this.done = done;
        }

        @Override
        public void run() {
            while (!(done.get())) {
                doOp(map);
            } 
        }
    }
}

