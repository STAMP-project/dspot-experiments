/**
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.util;


import com.navercorp.pinpoint.common.util.PinpointThreadFactory;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class SimpleOrderedThreadPoolTest {
    private final Random random = new Random();

    @Test
    public void testExecute() throws Exception {
        SimpleOrderedThreadPool threadPool = new SimpleOrderedThreadPool(3, 100, new PinpointThreadFactory("test", true));
        int testCount = 100;
        CountDownLatch latch = new CountDownLatch(testCount);
        for (int i = 0; i < testCount; i++) {
            final int selectKey = random.nextInt();
            threadPool.execute(new SimpleOrderedThreadPoolTest.TestHashSelectorRunnable(selectKey, latch));
        }
        threadPool.shutdown();
        threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(latch.getCount(), 0);
    }

    public class TestHashSelectorRunnable implements SimpleOrderedThreadPool.HashSelector , Runnable {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private int selectKey;

        private final CountDownLatch latch;

        public TestHashSelectorRunnable(int selectKey, CountDownLatch latch) {
            this.selectKey = selectKey;
            this.latch = latch;
        }

        @Override
        public int select() {
            return selectKey;
        }

        @Override
        public void run() {
            latch.countDown();
            final String name = Thread.currentThread().getName();
            logger.debug("selectKey{}, threadName:{}", selectKey, name);
        }
    }
}

