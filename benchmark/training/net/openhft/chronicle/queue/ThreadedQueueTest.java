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


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.threads.ThreadDump;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 *
 *
 * @author Rob Austin.
 */
public class ThreadedQueueTest {
    public static final int REQUIRED_COUNT = 10;

    private ThreadDump threadDump;

    @Test(timeout = 10000)
    public void testMultipleThreads() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        final File path = DirectoryUtils.tempDir("testMultipleThreads");
        final AtomicInteger counter = new AtomicInteger();
        ExecutorService tailerES = /* new NamedThreadFactory("tailer", true) */
        Executors.newSingleThreadExecutor();
        Future tf = tailerES.submit(() -> {
            try {
                final ChronicleQueue rqueue = ChronicleQueue.singleBuilder(path).testBlockSize().build();
                final ExcerptTailer tailer = rqueue.createTailer();
                final Bytes bytes = Bytes.elasticByteBuffer();
                while (((counter.get()) < (ThreadedQueueTest.REQUIRED_COUNT)) && (!(Thread.interrupted()))) {
                    bytes.clear();
                    if (tailer.readBytes(bytes))
                        counter.incrementAndGet();

                } 
                bytes.release();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        ExecutorService appenderES = /* new NamedThreadFactory("appender", true) */
        Executors.newSingleThreadExecutor();
        Future af = appenderES.submit(() -> {
            try {
                final ChronicleQueue wqueue = ChronicleQueue.singleBuilder(path).testBlockSize().build();
                final ExcerptAppender appender = wqueue.acquireAppender();
                final Bytes message = Bytes.elasticByteBuffer();
                for (int i = 0; i < (ThreadedQueueTest.REQUIRED_COUNT); i++) {
                    message.clear();
                    message.append(i);
                    appender.writeBytes(message);
                }
                message.release();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        appenderES.shutdown();
        tailerES.shutdown();
        long end = (System.currentTimeMillis()) + 9000;
        af.get(9000, TimeUnit.MILLISECONDS);
        tf.get((end - (System.currentTimeMillis())), TimeUnit.MILLISECONDS);
        Assert.assertEquals(ThreadedQueueTest.REQUIRED_COUNT, counter.get());
    }

    // (timeout = 5000)
    @Test
    public void testTailerReadingEmptyQueue() {
        Assume.assumeFalse(Jvm.isArm());
        final File path = DirectoryUtils.tempDir("testTailerReadingEmptyQueue");
        final ChronicleQueue rqueue = SingleChronicleQueueBuilder.fieldlessBinary(path).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build();
        final ExcerptTailer tailer = rqueue.createTailer();
        final ChronicleQueue wqueue = SingleChronicleQueueBuilder.fieldlessBinary(path).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build();
        Bytes bytes = Bytes.elasticByteBuffer();
        Assert.assertFalse(tailer.readBytes(bytes));
        final ExcerptAppender appender = wqueue.acquireAppender();
        appender.writeBytes(Bytes.wrapForRead("Hello World".getBytes(StandardCharsets.ISO_8859_1)));
        bytes.clear();
        Assert.assertTrue(tailer.readBytes(bytes));
        Assert.assertEquals("Hello World", bytes.toString());
        bytes.release();
    }
}

