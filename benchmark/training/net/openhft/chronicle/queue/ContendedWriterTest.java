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


import java.util.concurrent.atomic.AtomicBoolean;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.threads.ThreadDump;
import net.openhft.chronicle.core.util.Histogram;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/* Created by Jerry Shea 16/11/17 */
@Ignore("long running")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RequiredForClient
public class ContendedWriterTest {
    private static final long NUMBER_OF_LONGS = 3;

    private final AtomicBoolean running = new AtomicBoolean(true);

    private ThreadDump threadDump;

    @Test
    public void oneThread() {
        test("oneThread", new ContendedWriterTest.Config(false, 1, 0));
    }

    @Test
    public void oneThreadDeferred() {
        test("oneThreadDeferred", new ContendedWriterTest.Config(true, 1, 0));
    }

    @Test
    public void sixThreads() {
        ContendedWriterTest.Config config15 = new ContendedWriterTest.Config(false, 1, 5);
        test("sixThreads", config15, config15, config15, config15, config15, config15);
    }

    @Test
    public void sixThreadsDeferred() {
        ContendedWriterTest.Config config15 = new ContendedWriterTest.Config(true, 1, 5);
        test("sixThreadsDeferred", config15, config15, config15, config15, config15, config15);
    }

    @Test
    public void twoThreadsWritingLargeMessagesAtSameSlowRate() {
        test("twoThreadsWritingLargeMessagesAtSameSlowRate", new ContendedWriterTest.Config(false, 1, 5), new ContendedWriterTest.Config(false, 1, 5));
    }

    @Test
    public void twoThreadsWritingLargeMessagesAtSameSlowRateBothDeferred() {
        test("twoThreadsWritingLargeMessagesAtSameSlowRateBothDeferred", new ContendedWriterTest.Config(true, 1, 5), new ContendedWriterTest.Config(true, 1, 5));
    }

    @Test
    public void twoThreadsWritingLargeMessagesOneFastOneSlow() {
        test("twoThreadsWritingLargeMessagesOneFastOneSlow", new ContendedWriterTest.Config(false, 1, 0), new ContendedWriterTest.Config(false, 1, 5));
    }

    @Test
    public void twoThreadsWritingLargeMessagesOneFastOneSlowAndDeferred() {
        test("twoThreadsWritingLargeMessagesOneFastOneSlowAndDeferred", new ContendedWriterTest.Config(false, 1, 0), new ContendedWriterTest.Config(true, 1, 5));
    }

    @Test
    public void twoThreadsWritingLargeMessagesFastAndSmallMessagesSlow() {
        test("twoThreadsWritingLargeMessagesFastAndSmallMessagesSlow", new ContendedWriterTest.Config(false, 1, 0), new ContendedWriterTest.Config(false, 0, 5));
    }

    @Test
    public void twoThreadsWritingLargeMessagesFastAndSmallMessagesSlowAndDeferred() {
        test("twoThreadsWritingLargeMessagesFastAndSmallMessagesSlowAndDeferred", new ContendedWriterTest.Config(false, 1, 0), new ContendedWriterTest.Config(true, 0, 5));
    }

    private static class Config {
        final boolean progressOnContention;

        final int writePause;// how long to keep writingContext open


        final int pauseBetweenWrites;

        private Config(boolean progressOnContention, int writePause, int pauseBetweenWrites) {
            this.progressOnContention = progressOnContention;
            this.writePause = writePause;
            this.pauseBetweenWrites = pauseBetweenWrites;
        }
    }

    private static class SlowToSerialiseAndDeserialise implements Marshallable {
        @SuppressWarnings("unused")
        private final StringBuilder sb = new StringBuilder();

        private final long writePauseMs;

        private SlowToSerialiseAndDeserialise(long writePauseMs) {
            this.writePauseMs = writePauseMs;
        }

        @Override
        public void readMarshallable(@NotNull
        WireIn wire) throws IORuntimeException {
            ValueIn valueIn = wire.getValueIn();
            for (int i = 0; i < (ContendedWriterTest.NUMBER_OF_LONGS); i++)
                Assert.assertEquals(i, valueIn.int64());

            // Jvm.pause(PAUSE_READ_MS);
        }

        @Override
        public void writeMarshallable(@NotNull
        WireOut wire) {
            ValueOut valueOut = wire.getValueOut();
            for (int i = 0; i < (ContendedWriterTest.NUMBER_OF_LONGS); i++)
                valueOut.int64(i);

            Jvm.pause(writePauseMs);
        }
    }

    private class StartAndMonitor {
        Histogram histo = new Histogram();

        public StartAndMonitor(ChronicleQueue queue, String name, int writePauseMs, int sleepBetweenMillis) {
            final ContendedWriterTest.SlowToSerialiseAndDeserialise object = new ContendedWriterTest.SlowToSerialiseAndDeserialise(writePauseMs);
            Thread thread = new Thread(() -> {
                try {
                    while (running.get()) {
                        long loopStart = System.nanoTime();
                        final ExcerptAppender appender = queue.acquireAppender();
                        // System.out.println("about to open");
                        try (final DocumentContext ctx = appender.writingDocument()) {
                            // System.out.println("about to write");
                            ctx.wire().getValueOut().marshallable(object);
                            // System.out.println("about to close");
                        }
                        // System.out.println("closed");
                        long timeTaken = (System.nanoTime()) - loopStart;
                        histo.sampleNanos(timeTaken);
                        Jvm.pause(sleepBetweenMillis);
                    } 
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }, name);
            thread.start();
        }
    }
}

