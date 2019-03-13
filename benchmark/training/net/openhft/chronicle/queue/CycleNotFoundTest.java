/**
 * Copyright 2016 higherfrequencytrading.com
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
package net.openhft.chronicle.queue;


import RollCycles.TEST_SECONDLY;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Assert;
import org.junit.Test;


@RequiredForClient
public class CycleNotFoundTest extends ChronicleQueueTestBase {
    private static final int NUMBER_OF_TAILERS = 10;

    private static final long INTERVAL_US = 25;

    private static final long NUMBER_OF_MSG = 100000;// this is working this  1_000_000 but


    // reduced so that it runs quicker for the continuous integration (CI)
    @Test(timeout = 50000L)
    public void tailerCycleNotFoundTest() throws InterruptedException, ExecutionException {
        File path = DirectoryUtils.tempDir("tailerCycleNotFoundTest");// added nano time just to make

        ExecutorService executorService = Executors.newFixedThreadPool(((int) (CycleNotFoundTest.NUMBER_OF_MSG)));
        AtomicLong counter = new AtomicLong();
        Runnable reader = () -> {
            long count = 0;
            try (ChronicleQueue rqueue = SingleChronicleQueueBuilder.fieldlessBinary(path).testBlockSize().rollCycle(TEST_SECONDLY).build()) {
                final ExcerptTailer tailer = rqueue.createTailer();
                long last = -1;
                while (count < (CycleNotFoundTest.NUMBER_OF_MSG)) {
                    try (DocumentContext dc = tailer.readingDocument()) {
                        if (!(dc.isPresent()))
                            continue;

                        Assert.assertTrue(dc.isData());
                        Assert.assertEquals((last + 1), (last = dc.wire().read().int64()));
                        count++;
                        counter.incrementAndGet();
                    }
                    if (executorService.isShutdown())
                        Assert.fail();

                } 
                // check nothing after the NUMBER_OF_MSG
                try (DocumentContext dc = tailer.readingDocument()) {
                    Assert.assertFalse(dc.isPresent());
                }
            } finally {
                System.out.printf((("Read %,d messages, thread=" + (Thread.currentThread().getName())) + "\n"), count);
            }
        };
        List<Future> tailers = new ArrayList<>();
        for (int i = 0; i < (CycleNotFoundTest.NUMBER_OF_TAILERS); i++) {
            tailers.add(executorService.submit(reader));
        }
        // Appender run in a different thread
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        Future<?> submit = executorService1.submit(() -> {
            ChronicleQueue wqueue = SingleChronicleQueueBuilder.fieldlessBinary(path).testBlockSize().rollCycle(RollCycles.TEST_SECONDLY).build();
            final ExcerptAppender appender = wqueue.acquireAppender();
            long next = (System.nanoTime()) + ((CycleNotFoundTest.INTERVAL_US) * 1000);
            for (int i = 0; i < (CycleNotFoundTest.NUMBER_OF_MSG); i++) {
                /* busy wait */
                while ((System.nanoTime()) < next);
                try (DocumentContext dc = appender.writingDocument()) {
                    dc.wire().write().int64(i);
                }
                next += (CycleNotFoundTest.INTERVAL_US) * 1000;
                if (executorService1.isShutdown())
                    return;

            }
            wqueue.close();
        });
        submit.get();
        System.out.println("appender is done.");
        // wait for all the tailer to finish
        for (Future f : tailers) {
            f.get();
        }
        Assert.assertEquals(((CycleNotFoundTest.NUMBER_OF_MSG) * (CycleNotFoundTest.NUMBER_OF_TAILERS)), counter.get());
    }
}

