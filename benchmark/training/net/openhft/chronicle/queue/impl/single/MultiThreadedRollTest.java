/**
 * Copyright 2016 higherfrequencytrading.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.queue.impl.single;


import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.threads.NamedThreadFactory;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Test;


/**
 *
 *
 * @author Rob Austin.
 */
public class MultiThreadedRollTest {
    final ExecutorService reader = Executors.newSingleThreadExecutor(new NamedThreadFactory("reader", true));

    @Test(timeout = 10000)
    public void test() throws InterruptedException, ExecutionException {
        final SetTimeProvider timeProvider = new SetTimeProvider();
        timeProvider.currentTimeMillis(1000);
        final File path = DirectoryUtils.tempDir("MultiThreadedRollTest");
        try (final ChronicleQueue wqueue = SingleChronicleQueueBuilder.binary(path).testBlockSize().timeProvider(timeProvider).rollCycle(RollCycles.TEST_SECONDLY).build()) {
            wqueue.acquireAppender().writeText("hello world");
            try (final ChronicleQueue rqueue = SingleChronicleQueueBuilder.binary(path).testBlockSize().timeProvider(timeProvider).rollCycle(RollCycles.TEST_SECONDLY).build()) {
                ExcerptTailer tailer = rqueue.createTailer();
                Future f = reader.submit(() -> {
                    long index;
                    do {
                        try (DocumentContext documentContext = tailer.readingDocument()) {
                            System.out.println(("tailer.state: " + (tailer.state())));
                            // index is only meaningful if present.
                            index = documentContext.index();
                            // if (documentContext.isPresent())
                            final boolean present = documentContext.isPresent();
                            System.out.println((("documentContext.isPresent=" + present) + (present ? ",index=" + (Long.toHexString(index)) : ", no index")));
                            Jvm.pause(50);
                        }
                    } while ((index != 8589934592L) && (!(reader.isShutdown())) );
                });
                timeProvider.currentTimeMillis(2000);
                writeEndOfCycleIfRequired();
                Jvm.pause(200);
                wqueue.acquireAppender().writeText("hello world");
                f.get();
            }
        }
    }
}

