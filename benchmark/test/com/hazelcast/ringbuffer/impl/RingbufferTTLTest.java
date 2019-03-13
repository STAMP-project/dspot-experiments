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


import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RingbufferTTLTest extends HazelcastTestSupport {
    private HazelcastInstance hz;

    private Ringbuffer ringbuffer;

    private RingbufferContainer ringbufferContainer;

    private ArrayRingbuffer arrayRingbuffer;

    // when ttl is set, then eventually all the items needs to get expired.
    // In this particular test we fill the ringbuffer and wait for it to expire.
    // Expiration is given a 2 second martin of error. So we have a ttl of
    // 10 seconds, then we expect that within 12 seconds the buffer is empty.
    @Test
    public void whenTTLEnabled_thenEventuallyRingbufferEmpties() throws Exception {
        int ttl = 10;
        int maximumVisibleTTL = ttl + 2;
        setup(new RingbufferConfig("foo").setTimeToLiveSeconds(ttl));
        for (int k = 0; k < (ringbuffer.capacity()); k++) {
            ringbuffer.add(("item" + k));
        }
        final long tail = ringbuffer.tailSequence();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(tail, ringbuffer.tailSequence());
                Assert.assertEquals((tail + 1), ringbuffer.headSequence());
                Assert.assertEquals(0, ringbuffer.size());
                Assert.assertEquals(ringbuffer.capacity(), ringbuffer.remainingCapacity());
            }
        }, maximumVisibleTTL);
        // and we verify that the slots are nulled so we don't have a memory leak on our hands.
        for (int k = 0; k < (ringbuffer.capacity()); k++) {
            Assert.assertNull(arrayRingbuffer.getItems()[k]);
        }
    }

    // if ttl is set, we need to make sure that during that period all items are accessible.
    // In this particular test we set the ttl period to 10 second. The minimumVisibilitySeconds to
    // give us an error margin of 2 seconds (due to threading etc... we don't want to have another
    // spurious failing test.
    @Test
    public void whenTTLEnabled_thenVisibilityIsGuaranteed() {
        int ttl = 10;
        int minimumVisibleTTL = ttl - 2;
        setup(new RingbufferConfig("foo").setTimeToLiveSeconds(ttl).setCapacity(100));
        for (int k = 0; k < (ringbuffer.capacity()); k++) {
            ringbuffer.add(("item" + k));
        }
        final long head = ringbuffer.headSequence();
        final long tail = ringbuffer.tailSequence();
        final long size = ringbuffer.size();
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(head, ringbuffer.headSequence());
                Assert.assertEquals(tail, ringbuffer.tailSequence());
                Assert.assertEquals(size, ringbuffer.size());
                for (long seq = head; seq <= tail; seq++) {
                    Assert.assertEquals(("item" + seq), ringbuffer.readOne(seq));
                }
            }
        }, minimumVisibleTTL);
    }

    @Test
    public void whenTTLDisabled_thenNothingRetires() {
        setup(new RingbufferConfig("foo").setTimeToLiveSeconds(0).setCapacity(100));
        for (int k = 0; k < (ringbuffer.capacity()); k++) {
            ringbuffer.add(("item" + k));
        }
        final long head = ringbuffer.headSequence();
        final long tail = ringbuffer.tailSequence();
        final long size = ringbuffer.size();
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(head, ringbuffer.headSequence());
                Assert.assertEquals(tail, ringbuffer.tailSequence());
                Assert.assertEquals(size, ringbuffer.size());
                for (long seq = head; seq <= tail; seq++) {
                    Assert.assertEquals(("item" + seq), ringbuffer.readOne(seq));
                }
            }
        }, 5);
    }
}

