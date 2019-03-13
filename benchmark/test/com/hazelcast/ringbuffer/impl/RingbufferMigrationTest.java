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
package com.hazelcast.ringbuffer.impl;


import GroupProperty.PARTITION_COUNT;
import com.hazelcast.config.Config;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RingbufferMigrationTest extends HazelcastTestSupport {
    public static final int CAPACITY = 100;

    public static final String BOUNCING_TEST_PARTITION_COUNT = "10";

    private TestHazelcastInstanceFactory instanceFactory;

    @Test
    public void test() throws Exception {
        final String ringbufferName = "ringbuffer";
        final Config config = new Config().addRingBufferConfig(new RingbufferConfig(ringbufferName).setTimeToLiveSeconds(0));
        config.setProperty(PARTITION_COUNT.getName(), RingbufferMigrationTest.BOUNCING_TEST_PARTITION_COUNT);
        HazelcastInstance hz1 = instanceFactory.newHazelcastInstance(config);
        for (int k = 0; k < (10 * (RingbufferMigrationTest.CAPACITY)); k++) {
            hz1.getRingbuffer(ringbufferName).add(k);
        }
        long oldTailSeq = hz1.getRingbuffer(ringbufferName).tailSequence();
        long oldHeadSeq = hz1.getRingbuffer(ringbufferName).headSequence();
        HazelcastInstance hz2 = instanceFactory.newHazelcastInstance(config);
        HazelcastInstance hz3 = instanceFactory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(3, hz2);
        HazelcastTestSupport.waitAllForSafeState(hz1, hz2, hz3);
        hz1.shutdown();
        HazelcastTestSupport.assertClusterSizeEventually(2, hz2);
        HazelcastTestSupport.waitAllForSafeState(hz2, hz3);
        Assert.assertEquals(oldTailSeq, hz2.getRingbuffer(ringbufferName).tailSequence());
        Assert.assertEquals(oldHeadSeq, hz2.getRingbuffer(ringbufferName).headSequence());
    }
}

