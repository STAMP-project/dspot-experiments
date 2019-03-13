/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.sender;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class StandbySpanStreamDataSendWorkerTest {
    private static SpanStreamSendDataFactory factory;

    @Test
    public void standbySpanStreamDataSendWorkerTest1() throws InterruptedException {
        CountDownLatch testCountDownLatch = new CountDownLatch(2);
        long blockTime = 1000;
        StandbySpanStreamDataSendWorker sendWorker = new StandbySpanStreamDataSendWorker(new StandbySpanStreamDataSendWorkerTest.TestFlushHandler(testCountDownLatch), new StandbySpanStreamDataStorage(10, blockTime));
        sendWorker.start();
        try {
            SpanStreamSendData spanStreamSendData = createSpanStreamSendData("a".getBytes());
            sendWorker.addStandbySpanStreamData(spanStreamSendData);
            spanStreamSendData = createSpanStreamSendData("b".getBytes());
            sendWorker.addStandbySpanStreamData(spanStreamSendData);
            boolean onEvent = testCountDownLatch.await((blockTime * 2), TimeUnit.MILLISECONDS);
            Assert.assertTrue(onEvent);
        } finally {
            sendWorker.stop();
        }
    }

    @Test
    public void standbySpanStreamDataSendWorkerTest2() throws InterruptedException {
        CountDownLatch testCountDownLatch = new CountDownLatch(1);
        long blockTime = 1000;
        StandbySpanStreamDataSendWorker sendWorker = new StandbySpanStreamDataSendWorker(new StandbySpanStreamDataSendWorkerTest.TestFlushHandler(testCountDownLatch), new StandbySpanStreamDataStorage(10, blockTime));
        sendWorker.start();
        try {
            SpanStreamSendData spanStreamSendData = createSpanStreamSendData("a".getBytes());
            sendWorker.addStandbySpanStreamData(spanStreamSendData);
            spanStreamSendData = createSpanStreamSendData("b".getBytes());
            sendWorker.addStandbySpanStreamData(spanStreamSendData);
            sendWorker.getStandbySpanStreamSendData();
            boolean onEvent = testCountDownLatch.await((blockTime * 2), TimeUnit.MILLISECONDS);
            Assert.assertTrue(onEvent);
        } finally {
            sendWorker.stop();
        }
    }

    private class TestFlushHandler implements StandbySpanStreamDataFlushHandler {
        private final CountDownLatch testLatch;

        public TestFlushHandler(CountDownLatch testLatch) {
            this.testLatch = testLatch;
        }

        @Override
        public void handleFlush(SpanStreamSendData spanStreamSendData) {
            testLatch.countDown();
        }

        @Override
        public void exceptionCaught(SpanStreamSendData spanStreamSendData, Throwable e) {
        }
    }
}

