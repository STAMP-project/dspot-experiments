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
package com.hazelcast.map.merge;


import LifecycleEvent.LifecycleState;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.SplitBrainTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Runs several iterations of a split-brain and split-brain healing cycle on a constant data set.
 * <p>
 * There are {@value #MAP_COUNT} maps which are filled with {@value #ENTRY_COUNT} entries each.
 * The configured pass through merge policy will trigger the split-brain healing and some merge code,
 * but will not change any data.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class MapSplitBrainStressTest extends SplitBrainTestSupport {
    static final int ITERATION_COUNT = 50;

    static final int MAP_COUNT = 100;

    static final int ENTRY_COUNT = 100;

    static final int FIRST_BRAIN_SIZE = 3;

    static final int SECOND_BRAIN_SIZE = 2;

    static final Class MERGE_POLICY = PassThroughMergePolicy.class;

    static final int TEST_TIMEOUT_IN_MILLIS = (15 * 60) * 1000;

    static final String MAP_NAME_PREFIX = (MapSplitBrainStressTest.class.getSimpleName()) + "-";

    static final ILogger LOGGER = Logger.getLogger(MapSplitBrainStressTest.class);

    final Map<HazelcastInstance, String> listenerRegistry = new ConcurrentHashMap<HazelcastInstance, String>();

    final Map<Integer, String> mapNames = new ConcurrentHashMap<Integer, String>();

    MapSplitBrainStressTest.MergeLifecycleListener mergeLifecycleListener;

    int iteration = 1;

    @Test(timeout = MapSplitBrainStressTest.TEST_TIMEOUT_IN_MILLIS)
    @Override
    public void testSplitBrain() throws Exception {
        super.testSplitBrain();
    }

    private static class MergeLifecycleListener implements LifecycleListener {
        private final CountDownLatch latch;

        MergeLifecycleListener(int mergingClusterSize) {
            latch = new CountDownLatch(mergingClusterSize);
        }

        @Override
        public void stateChanged(LifecycleEvent event) {
            if ((event.getState()) == (LifecycleState.MERGED)) {
                latch.countDown();
            }
        }

        public void await() {
            HazelcastTestSupport.assertOpenEventually(latch);
        }
    }
}

