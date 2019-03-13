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
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.NightlyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * This test fails sporadically. It seems to indicate a problem within the core because there is not much logic
 * in the atomicwrapper that can fail (just increment).
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class AtomicLongUpdateStressTest extends StressTestSupport {
    public static final int CLIENT_THREAD_COUNT = 5;

    public static final int REFERENCE_COUNT = 10 * 1000;

    private HazelcastInstance client;

    private IAtomicLong[] references;

    private AtomicLongUpdateStressTest.StressThread[] stressThreads;

    @Test(timeout = 600000)
    public void testFixedCluster() {
        test(false);
    }

    public class StressThread extends StressTestSupport.TestThread {
        private final int[] increments = new int[AtomicLongUpdateStressTest.REFERENCE_COUNT];

        @Override
        public void doRun() throws Exception {
            while (!(isStopped())) {
                int index = random.nextInt(AtomicLongUpdateStressTest.REFERENCE_COUNT);
                int increment = random.nextInt(100);
                increments[index] += increment;
                IAtomicLong reference = references[index];
                reference.addAndGet(increment);
            } 
        }

        public void addIncrements(int[] increments) {
            for (int k = 0; k < (increments.length); k++) {
                increments[k] += this.increments[k];
            }
        }
    }
}

