/**
 * Copyright 2011 LMAX Ltd.
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
package com.lmax.disruptor;


import com.lmax.disruptor.support.StubEvent;
import java.util.concurrent.CountDownLatch;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public final class LifecycleAwareTest {
    private final CountDownLatch startLatch = new CountDownLatch(1);

    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private final RingBuffer<StubEvent> ringBuffer = RingBuffer.createMultiProducer(StubEvent.EVENT_FACTORY, 16);

    private final SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

    private final LifecycleAwareTest.LifecycleAwareEventHandler handler = new LifecycleAwareTest.LifecycleAwareEventHandler();

    private final BatchEventProcessor<StubEvent> batchEventProcessor = new BatchEventProcessor<StubEvent>(ringBuffer, sequenceBarrier, handler);

    @Test
    public void shouldNotifyOfBatchProcessorLifecycle() throws Exception {
        new Thread(batchEventProcessor).start();
        startLatch.await();
        batchEventProcessor.halt();
        shutdownLatch.await();
        Assert.assertThat(Integer.valueOf(handler.startCounter), Is.is(Integer.valueOf(1)));
        Assert.assertThat(Integer.valueOf(handler.shutdownCounter), Is.is(Integer.valueOf(1)));
    }

    private final class LifecycleAwareEventHandler implements EventHandler<StubEvent> , LifecycleAware {
        private int startCounter = 0;

        private int shutdownCounter = 0;

        @Override
        public void onEvent(final StubEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        }

        @Override
        public void onStart() {
            ++(startCounter);
            startLatch.countDown();
        }

        @Override
        public void onShutdown() {
            ++(shutdownCounter);
            shutdownLatch.countDown();
        }
    }
}

