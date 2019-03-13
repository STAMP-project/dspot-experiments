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
package com.hazelcast.cluster;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class JoinStressTest extends HazelcastTestSupport {
    private static final long TEN_MINUTES_IN_MILLIS = (10 * 60) * 1000L;

    private ILogger logger = Logger.getLogger(JoinStressTest.class);

    @Test(timeout = JoinStressTest.TEN_MINUTES_IN_MILLIS)
    public void testTCPIPJoinWithManyNodes() throws InterruptedException {
        testJoinWithManyNodes(false);
    }

    @Test(timeout = JoinStressTest.TEN_MINUTES_IN_MILLIS)
    public void testMulticastJoinWithManyNodes() throws InterruptedException {
        testJoinWithManyNodes(true);
    }

    @Test(timeout = JoinStressTest.TEN_MINUTES_IN_MILLIS)
    public void testJoinCompletesCorrectlyWhenMultipleNodesStartedParallel() throws Exception {
        int count = 10;
        final TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory(count);
        final HazelcastInstance[] instances = new HazelcastInstance[count];
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    instances[index] = factory.newHazelcastInstance(createConfig());
                    latch.countDown();
                }
            }).start();
        }
        HazelcastTestSupport.assertOpenEventually(latch);
        for (int i = 0; i < count; i++) {
            HazelcastTestSupport.assertClusterSizeEventually(count, instances[i]);
        }
    }

    @Test(timeout = JoinStressTest.TEN_MINUTES_IN_MILLIS)
    public void testTCPIPJoinWithManyNodesMultipleGroups() throws InterruptedException {
        testJoinWithManyNodesMultipleGroups(false);
    }

    @Test(timeout = JoinStressTest.TEN_MINUTES_IN_MILLIS)
    public void testMulticastJoinWithManyNodesMultipleGroups() throws InterruptedException {
        testJoinWithManyNodesMultipleGroups(true);
    }

    @Test(timeout = 300000)
    public void testJoinWhenMemberClosedInBetween() throws InterruptedException {
        // Test is expecting to all can join safely.
        // On the failed case the last opened instance throws java.lang.IllegalStateException: Node failed to start!
        Config config = new Config();
        HazelcastInstance i1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance i2 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance i3 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance i4 = Hazelcast.newHazelcastInstance(config);
        final IMap<Integer, Integer> map = i4.getMap("a");
        int numThreads = 40;
        final int loop = 5000;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    Random random = new Random();
                    for (int j = 0; j < loop; j++) {
                        int op = random.nextInt(3);
                        if (op == 0) {
                            map.put(j, j);
                        } else
                            if (op == 1) {
                                Integer val = map.remove(j);
                                assert (val == null) || (val.equals(j));
                            } else {
                                Integer val = map.get(j);
                                assert (val == null) || (val.equals(j));
                            }

                    }
                }
            });
            threads[i].start();
        }
        i1.shutdown();
        i2.shutdown();
        i3.shutdown();
        // Should not throw java.lang.IllegalStateException: Node failed to start!
        Hazelcast.newHazelcastInstance(config);
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    }

    @Test
    public void testTcpJoin_whenInitialMembersTerminated_duringStartup() throws Exception {
        testJoin_whenInitialMembersTerminated_duringStartup(false);
    }

    @Test
    public void testMulticastJoin_whenInitialMembersTerminated_duringStartup() throws Exception {
        testJoin_whenInitialMembersTerminated_duringStartup(true);
    }
}

