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
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestThread;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class RingbufferAddReadOneStressTest extends HazelcastTestSupport {
    private final AtomicBoolean stop = new AtomicBoolean();

    private Ringbuffer<Long> ringbuffer;

    @Test
    public void whenNoTTL() throws Exception {
        RingbufferConfig ringbufferConfig = new RingbufferConfig("foo").setCapacity((200 * 1000)).setTimeToLiveSeconds(0);
        test(ringbufferConfig);
    }

    @Test
    public void whenTTLEnabled() throws Exception {
        RingbufferConfig ringbufferConfig = new RingbufferConfig("foo").setCapacity((200 * 1000)).setTimeToLiveSeconds(2);
        test(ringbufferConfig);
    }

    @Test
    public void whenLongTTLAndSmallBuffer() throws Exception {
        RingbufferConfig ringbufferConfig = new RingbufferConfig("foo").setCapacity((3 * 1000)).setTimeToLiveSeconds(30);
        test(ringbufferConfig);
    }

    @Test(timeout = (15 * 60) * 1000)
    public void whenShortTTLAndBigBuffer() throws Exception {
        RingbufferConfig ringbufferConfig = new RingbufferConfig("foo").setCapacity(((10 * 1000) * 1000)).setTimeToLiveSeconds(2);
        test(ringbufferConfig);
    }

    class ProduceThread extends TestThread {
        private volatile long produced;

        public ProduceThread() {
            super("ProduceThread");
        }

        @Override
        public void onError(Throwable t) {
            stop.set(true);
        }

        @Override
        public void doRun() throws Throwable {
            long prev = System.currentTimeMillis();
            while (!(stop.get())) {
                ringbuffer.add(produced);
                (produced)++;
                long now = System.currentTimeMillis();
                if (now > (prev + 2000)) {
                    prev = now;
                    System.out.println((((getName()) + " at ") + (produced)));
                }
            } 
            ringbuffer.add(Long.MIN_VALUE);
        }
    }

    class ConsumeThread extends TestThread {
        volatile long seq;

        public ConsumeThread(int id) {
            super(("ConsumeThread-" + id));
        }

        @Override
        public void onError(Throwable t) {
            stop.set(true);
        }

        @Override
        public void doRun() throws Throwable {
            seq = ringbuffer.headSequence();
            long prev = System.currentTimeMillis();
            for (; ;) {
                Long item = null;
                while (item == null) {
                    try {
                        item = ringbuffer.readOne(seq);
                    } catch (StaleSequenceException e) {
                        // this consumer is used in a stress test and can fall behind the producer if it gets delayed
                        // by any reason. This is ok, just jump to the the middle of the ringbuffer.
                        System.out.println(((getName()) + " has fallen behind, catching up..."));
                        final long tail = ringbuffer.tailSequence();
                        final long head = ringbuffer.headSequence();
                        seq = (tail >= head) ? (tail + head) / 2 : head;
                    }
                } 
                if (item.equals(Long.MIN_VALUE)) {
                    break;
                }
                Assert.assertEquals(new Long(seq), item);
                (seq)++;
                long now = System.currentTimeMillis();
                if (now > (prev + 2000)) {
                    prev = now;
                    System.out.println((((getName()) + " at ") + (seq)));
                }
            }
        }
    }
}

