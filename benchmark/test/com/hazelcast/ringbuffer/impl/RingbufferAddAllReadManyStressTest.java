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


import InMemoryFormat.OBJECT;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestThread;
import com.hazelcast.test.TimeConstants;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class RingbufferAddAllReadManyStressTest extends HazelcastTestSupport {
    private static final int MAX_BATCH = 100;

    private final ILogger logger = Logger.getLogger(RingbufferAddAllReadManyStressTest.class);

    private final AtomicBoolean stop = new AtomicBoolean();

    private Ringbuffer<Long> ringbuffer;

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void whenNoTTL() {
        RingbufferConfig ringbufferConfig = new RingbufferConfig("rb").setCapacity(((20 * 1000) * 1000)).setInMemoryFormat(OBJECT).setTimeToLiveSeconds(0);
        test(ringbufferConfig);
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void whenTTLEnabled() {
        RingbufferConfig ringbufferConfig = new RingbufferConfig("rb").setCapacity((200 * 1000)).setTimeToLiveSeconds(2);
        test(ringbufferConfig);
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void whenLongTTLAndSmallBuffer() {
        RingbufferConfig ringbufferConfig = new RingbufferConfig("rb").setCapacity(1000).setTimeToLiveSeconds(30);
        test(ringbufferConfig);
    }

    @Test(timeout = (TimeConstants.MINUTE) * 10)
    public void whenShortTTLAndBigBuffer() {
        RingbufferConfig ringbufferConfig = new RingbufferConfig("rb").setInMemoryFormat(OBJECT).setCapacity(((20 * 1000) * 1000)).setTimeToLiveSeconds(2);
        test(ringbufferConfig);
    }

    class ProduceThread extends TestThread {
        private final ILogger logger = Logger.getLogger(RingbufferAddAllReadManyStressTest.ProduceThread.class);

        private final Random random = new Random();

        private long lastLogMs = 0;

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
            while (!(stop.get())) {
                List<Long> items = makeBatch();
                addAll(items);
            } 
            ringbuffer.add(Long.MIN_VALUE);
        }

        @SuppressWarnings("NonAtomicOperationOnVolatileField")
        private List<Long> makeBatch() {
            int count = Math.max(1, random.nextInt(RingbufferAddAllReadManyStressTest.MAX_BATCH));
            LinkedList<Long> items = new LinkedList<Long>();
            for (int k = 0; k < count; k++) {
                items.add(produced);
                (produced)++;
                long currentTimeMs = System.currentTimeMillis();
                if (((lastLogMs) + (TimeUnit.SECONDS.toMillis(5))) < currentTimeMs) {
                    lastLogMs = currentTimeMs;
                    logger.info((((getName()) + " at ") + (produced)));
                }
            }
            return items;
        }

        private void addAll(List<Long> items) throws Exception {
            long sleepMs = 100;
            for (; ;) {
                long result = ringbuffer.addAllAsync(items, OverflowPolicy.FAIL).get();
                if (result != (-1)) {
                    break;
                }
                logger.info("Backoff");
                TimeUnit.MILLISECONDS.sleep(sleepMs);
                sleepMs = sleepMs * 2;
                if (sleepMs > 1000) {
                    sleepMs = 1000;
                }
            }
        }
    }

    class ConsumeThread extends TestThread {
        private final ILogger logger = Logger.getLogger(RingbufferAddAllReadManyStressTest.ConsumeThread.class);

        private final Random random = new Random();

        private long lastLogMs = 0;

        private volatile long seq;

        ConsumeThread(int id) {
            super(("ConsumeThread-" + id));
        }

        @Override
        public void onError(Throwable t) {
            stop.set(true);
        }

        @Override
        @SuppressWarnings("NonAtomicOperationOnVolatileField")
        public void doRun() throws Throwable {
            seq = ringbuffer.headSequence();
            for (; ;) {
                int max = Math.max(1, random.nextInt(RingbufferAddAllReadManyStressTest.MAX_BATCH));
                ReadResultSet<Long> result = null;
                while (result == null) {
                    try {
                        result = ringbuffer.readManyAsync(seq, 1, max, null).get();
                    } catch (ExecutionException e) {
                        if ((e.getCause()) instanceof StaleSequenceException) {
                            // this consumer is used in a stress test and can fall behind the producer if it gets delayed
                            // by any reason. This is ok, just jump to the the middle of the ringbuffer.
                            logger.info(((getName()) + " has fallen behind, catching up..."));
                            final long tail = ringbuffer.tailSequence();
                            final long head = ringbuffer.headSequence();
                            seq = (tail >= head) ? (tail + head) / 2 : head;
                        } else {
                            throw e;
                        }
                    }
                } 
                for (Long item : result) {
                    if (item.equals(Long.MIN_VALUE)) {
                        return;
                    }
                    Assert.assertEquals(new Long(seq), item);
                    long currentTimeMs = System.currentTimeMillis();
                    if (((lastLogMs) + (TimeUnit.SECONDS.toMillis(5))) < currentTimeMs) {
                        lastLogMs = currentTimeMs;
                        logger.info((((getName()) + " at ") + (seq)));
                    }
                    (seq)++;
                }
            }
        }
    }
}

