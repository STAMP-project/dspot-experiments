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
package net.openhft.chronicle.queue.impl.single;


import RollCycles.MINUTELY;
import SingleChronicleQueue.SUFFIX;
import TailerDirection.BACKWARD;
import TailerDirection.FORWARD;
import TailerState.UNINITIALISED;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.annotation.UsedViaReflection;
import net.openhft.chronicle.core.onoes.ExceptionKey;
import net.openhft.chronicle.core.threads.ThreadDump;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.core.time.TimeProvider;
import net.openhft.chronicle.core.util.StringUtils;
import net.openhft.chronicle.queue.ChronicleQueueTestBase;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.RollCycleDefaultingTest;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.RollingChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueExcerpts.InternalAppender;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueExcerpts.StoreAppender;
import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static TailerState.UNINITIALISED;
import static WireType.BINARY;
import static WireType.DEFAULT_ZERO_BINARY;
import static net.openhft.chronicle.queue.AbstractMarshallable.<init>;
import static net.openhft.chronicle.queue.RollCycles.RollCycles.TEST_SECONDLY;


@RunWith(Parameterized.class)
public class SingleChronicleQueueTest extends ChronicleQueueTestBase {
    private static final long TIMES = 4L << 20L;

    @NotNull
    protected final WireType wireType;

    protected final boolean encryption;

    // *************************************************************************
    // 
    // TESTS
    // 
    // *************************************************************************
    private ThreadDump threadDump;

    private Map<ExceptionKey, Integer> exceptionKeyIntegerMap;

    public SingleChronicleQueueTest(@NotNull
    WireType wireType, boolean encryption) {
        this.wireType = wireType;
        this.encryption = encryption;
    }

    @Test
    public void testAppend() {
        try (final ChronicleQueue queue = builder(getTmpDir(), wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            for (int i = 0; i < 10; i++) {
                final int n = i;
                appender.writeDocument(( w) -> w.write(TestKey.test).int32(n));
                Assert.assertEquals(n, queue.rollCycle().toSequenceNumber(appender.lastIndexAppended()));
            }
            Assert.assertThat(SingleChronicleQueueTest.countEntries(queue), CoreMatchers.is(10L));
        }
    }

    @Test
    public void testTextReadWrite() {
        File tmpDir = getTmpDir();
        try (final ChronicleQueue queue = builder(tmpDir, wireType).build()) {
            queue.acquireAppender().writeText("hello world");
            Assert.assertEquals("hello world", queue.createTailer().readText());
        }
    }

    @Test
    public void testCleanupDir() {
        File tmpDir = getTmpDir();
        try (final ChronicleQueue queue = builder(tmpDir, wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write("hello").text("world");
            }
        }
        DirectoryUtils.deleteDir(tmpDir);
        if (OS.isWindows()) {
            System.err.println("#460 Directory clean up not supported on Windows");
        } else {
            Assert.assertFalse(tmpDir.exists());
        }
    }

    @Test
    public void testRollbackOnAppend() {
        try (final ChronicleQueue queue = builder(getTmpDir(), wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write("hello").text("world");
            }
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write("hello").text("world2");
            }
            ExcerptTailer tailer = queue.createTailer();
            try (DocumentContext dc = tailer.readingDocument()) {
                dc.wire().read("hello");
                dc.rollbackOnClose();
            }
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertEquals("world", dc.wire().read("hello").text());
            }
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertEquals("world2", dc.wire().read("hello").text());
            }
        }
    }

    @Test
    public void testWriteWithDocumentReadBytesDifferentThreads() throws InterruptedException, ExecutionException, TimeoutException {
        try (final ChronicleQueue queue = builder(getTmpDir(), wireType).build()) {
            final String expected = "some long message";
            ExecutorService service1 = Executors.newSingleThreadExecutor();
            ScheduledExecutorService service2 = null;
            try {
                Future f = service1.submit(() -> {
                    final ExcerptAppender appender = queue.acquireAppender();
                    try (final DocumentContext dc = appender.writingDocument()) {
                        dc.wire().writeEventName(() -> "key").text(expected);
                    }
                });
                BlockingQueue<Bytes> result = new ArrayBlockingQueue<>(10);
                service2 = Executors.newSingleThreadScheduledExecutor();
                service2.scheduleAtFixedRate(() -> {
                    Bytes b = Bytes.elasticHeapByteBuffer(128);
                    final ExcerptTailer tailer = queue.createTailer();
                    tailer.readBytes(b);
                    if ((b.readRemaining()) == 0)
                        return;

                    b.readPosition(0);
                    result.add(b);
                    throw new RejectedExecutionException();
                }, 1, 1, TimeUnit.MICROSECONDS);
                final Bytes bytes = result.poll(5, TimeUnit.SECONDS);
                if (bytes == null) {
                    // troubleshoot failed test http://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshothttp://teamcity.higherfrequencytrading.com:8111/viewLog.html?buildId=264141&tab=buildResultsDiv&buildTypeId=OpenHFT_ChronicleQueue4_Snapshot
                    f.get(1, TimeUnit.SECONDS);
                    throw new NullPointerException("nothing in result");
                }
                try {
                    final String actual = this.wireType.apply(bytes).read(() -> "key").text();
                    Assert.assertEquals(expected, actual);
                    f.get(1, TimeUnit.SECONDS);
                } finally {
                    bytes.release();
                }
            } finally {
                service1.shutdownNow();
                if (service2 != null)
                    service2.shutdownNow();

            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldBlowUpIfTryingToCreateQueueWithUnparseableRollCycle() {
        File tmpDir = getTmpDir();
        try (final ChronicleQueue queue = builder(tmpDir, wireType).rollCycle(new RollCycleDefaultingTest.MyRollcycle()).build()) {
            try (DocumentContext documentContext = queue.acquireAppender().writingDocument()) {
                documentContext.wire().write("somekey").text("somevalue");
            }
        }
        try (final ChronicleQueue ignored = builder(tmpDir, wireType).rollCycle(HOURLY).build()) {
        }
    }

    @Test
    public void shouldNotBlowUpIfTryingToCreateQueueWithIncorrectRollCycle() {
        File tmpDir = getTmpDir();
        try (final ChronicleQueue queue = builder(tmpDir, wireType).rollCycle(DAILY).build()) {
            try (DocumentContext documentContext = queue.acquireAppender().writingDocument()) {
                documentContext.wire().write("somekey").text("somevalue");
            }
        }
        // we don't store which RollCycles enum was used and we try and match by format string, we
        // match the first RollCycles with the same format string, which may not
        // be the RollCycles it was written with
        try (final ChronicleQueue ignored = builder(tmpDir, wireType).rollCycle(HOURLY).build()) {
            Assert.assertEquals(TEST_DAILY, ignored.rollCycle());
        }
    }

    @Test
    public void shouldOverrideDifferentEpoch() {
        File tmpDir = getTmpDir();
        try (final ChronicleQueue queue = builder(tmpDir, wireType).rollCycle(TEST_SECONDLY).epoch(100).build()) {
            try (DocumentContext documentContext = queue.acquireAppender().writingDocument()) {
                documentContext.wire().write("somekey").text("somevalue");
            }
        }
        try (final ChronicleQueue ignored = builder(tmpDir, wireType).rollCycle(TEST_SECONDLY).epoch(10).build()) {
            Assert.assertEquals(100, epoch());
        }
    }

    @Test
    public void testReadWriteHourly() {
        File tmpDir = getTmpDir();
        try (final ChronicleQueue qAppender = builder(tmpDir, wireType).rollCycle(HOURLY).build()) {
            try (DocumentContext documentContext = qAppender.acquireAppender().writingDocument()) {
                documentContext.wire().write("somekey").text("somevalue");
            }
        }
        try (final ChronicleQueue qTailer = builder(tmpDir, wireType).rollCycle(HOURLY).build()) {
            try (DocumentContext documentContext2 = qTailer.createTailer().readingDocument()) {
                String str = documentContext2.wire().read("somekey").text();
                Assert.assertEquals("somevalue", str);
            }
        }
    }

    @Test
    public void testMetaIndexTest() {
        File tmpDir = getTmpDir();
        try (final ChronicleQueue q = builder(tmpDir, wireType).rollCycle(HOURLY).build()) {
            {
                ExcerptAppender appender = q.acquireAppender();
                try (DocumentContext documentContext = appender.writingDocument()) {
                    documentContext.wire().getValueOut().text("one");
                }
                try (DocumentContext documentContext = appender.writingDocument()) {
                    documentContext.wire().getValueOut().text("two");
                }
                try (DocumentContext documentContext = appender.writingDocument(true)) {
                    documentContext.wire().getValueOut().text("meta1");
                }
                try (DocumentContext documentContext = appender.writingDocument()) {
                    documentContext.wire().getValueOut().text("three");
                }
                try (DocumentContext documentContext = appender.writingDocument(true)) {
                    documentContext.wire().getValueOut().text("meta2");
                }
                try (DocumentContext documentContext = appender.writingDocument(true)) {
                    documentContext.wire().getValueOut().text("meta3");
                }
                try (DocumentContext documentContext = appender.writingDocument()) {
                    documentContext.wire().getValueOut().text("four");
                }
            }
            {
                ExcerptTailer tailer = q.createTailer();
                try (DocumentContext documentContext2 = tailer.readingDocument()) {
                    Assert.assertEquals(0, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(false, documentContext2.isMetaData());
                    Assert.assertEquals("one", documentContext2.wire().getValueIn().text());
                }
                try (DocumentContext documentContext2 = tailer.readingDocument(true)) {
                    Assert.assertEquals(1, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(false, documentContext2.isMetaData());
                    Assert.assertEquals("two", documentContext2.wire().getValueIn().text());
                }
                try (DocumentContext documentContext2 = tailer.readingDocument(true)) {
                    Assert.assertEquals(2, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(true, documentContext2.isMetaData());
                    Assert.assertEquals("meta1", documentContext2.wire().getValueIn().text());
                }
                try (DocumentContext documentContext2 = tailer.readingDocument(true)) {
                    Assert.assertEquals(2, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(false, documentContext2.isMetaData());
                    Assert.assertEquals("three", documentContext2.wire().getValueIn().text());
                }
                try (DocumentContext documentContext2 = tailer.readingDocument(true)) {
                    Assert.assertEquals(3, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(true, documentContext2.isMetaData());
                    Assert.assertEquals("meta2", documentContext2.wire().getValueIn().text());
                }
                try (DocumentContext documentContext2 = tailer.readingDocument(true)) {
                    Assert.assertEquals(3, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(true, documentContext2.isMetaData());
                    Assert.assertEquals("meta3", documentContext2.wire().getValueIn().text());
                }
                try (DocumentContext documentContext2 = tailer.readingDocument(true)) {
                    Assert.assertEquals(3, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(false, documentContext2.isMetaData());
                    Assert.assertEquals("four", documentContext2.wire().getValueIn().text());
                }
            }
            {
                ExcerptTailer tailer = q.createTailer();
                try (DocumentContext documentContext2 = tailer.readingDocument()) {
                    Assert.assertEquals(0, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(false, documentContext2.isMetaData());
                    Assert.assertEquals("one", documentContext2.wire().getValueIn().text());
                }
                try (DocumentContext documentContext2 = tailer.readingDocument(false)) {
                    Assert.assertEquals(1, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(false, documentContext2.isMetaData());
                    Assert.assertEquals("two", documentContext2.wire().getValueIn().text());
                }
                try (DocumentContext documentContext2 = tailer.readingDocument(false)) {
                    Assert.assertEquals(2, toSeq(q, documentContext2.index()));
                    Assert.assertEquals(false, documentContext2.isMetaData());
                    Assert.assertEquals("three", documentContext2.wire().getValueIn().text());
                }
            }
        }
    }

    @Test
    public void testLastWritten() throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        try {
            final SetTimeProvider tp = new SetTimeProvider();
            try (ChronicleQueue outQueue = builder(getTmpDir(), wireType).rollCycle(RollCycles.TEST_SECONDLY).sourceId(1).timeProvider(tp).build()) {
                File inQueueTmpDir = getTmpDir();
                try (ChronicleQueue inQueue = builder(inQueueTmpDir, wireType).rollCycle(RollCycles.TEST_SECONDLY).sourceId(2).timeProvider(tp).build()) {
                    // write some initial data to the inqueue
                    final SingleChronicleQueueTest.Msg msg = inQueue.acquireAppender().methodWriterBuilder(SingleChronicleQueueTest.Msg.class).recordHistory(true).get();
                    msg.msg("somedata-0");
                    Assert.assertEquals(1, inQueueTmpDir.listFiles(( file) -> file.getName().endsWith("cq4")).length);
                    tp.advanceMillis(1000);
                    // write data into the inQueue
                    msg.msg("somedata-1");
                    Assert.assertEquals(2, inQueueTmpDir.listFiles(( file) -> file.getName().endsWith("cq4")).length);
                    // read a message on the in queue and write it to the out queue
                    {
                        SingleChronicleQueueTest.Msg out = outQueue.acquireAppender().methodWriterBuilder(SingleChronicleQueueTest.Msg.class).recordHistory(true).get();
                        MethodReader methodReader = inQueue.createTailer().methodReader(((SingleChronicleQueueTest.Msg) (out::msg)));
                        // reads the somedata-0
                        methodReader.readOne();
                        // reads the somedata-1
                        methodReader.readOne();
                        Assert.assertFalse(methodReader.readOne());
                        tp.advanceMillis(1000);
                        Assert.assertFalse(methodReader.readOne());
                    }
                    Assert.assertEquals("trying to read should not create a file", 2, inQueueTmpDir.listFiles(( file) -> file.getName().endsWith("cq4")).length);
                    // write data into the inQueue
                    msg.msg("somedata-2");
                    Assert.assertEquals(3, inQueueTmpDir.listFiles(( file) -> file.getName().endsWith("cq4")).length);
                    // advance 2 cycles - we will end up with a missing file
                    tp.advanceMillis(2000);
                    msg.msg("somedata-3");
                    msg.msg("somedata-4");
                    Assert.assertEquals("Should be a missing cycle file", 4, inQueueTmpDir.listFiles(( file) -> file.getName().endsWith("cq4")).length);
                    AtomicReference<String> actualValue = new AtomicReference<>();
                    // check that we are able to pick up from where we left off, in other words the next read should be somedata-2
                    {
                        ExcerptTailer excerptTailer = inQueue.createTailer().afterLastWritten(outQueue);
                        MethodReader methodReader = excerptTailer.methodReader(((SingleChronicleQueueTest.Msg) (actualValue::set)));
                        methodReader.readOne();
                        Assert.assertEquals("somedata-2", actualValue.get());
                        methodReader.readOne();
                        Assert.assertEquals("somedata-3", actualValue.get());
                        methodReader.readOne();
                        Assert.assertEquals("somedata-4", actualValue.get());
                        Assert.assertFalse(methodReader.readOne());
                    }
                }
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.SECONDS);
            executorService.shutdownNow();
        }
    }

    @Test
    public void shouldAllowDirectoryToBeDeletedWhenQueueIsClosed() throws IOException {
        if (OS.isWindows()) {
            System.err.println("#460 Cannot test deleting after close on windows");
            return;
        }
        final File dir = DirectoryUtils.tempDir("to-be-deleted");
        try (final ChronicleQueue queue = builder(dir, wireType).testBlockSize().build()) {
            try (final DocumentContext dc = queue.acquireAppender().writingDocument()) {
                dc.wire().write().text("foo");
            }
            try (final DocumentContext dc = queue.createTailer().readingDocument()) {
                Assert.assertEquals("foo", dc.wire().read().text());
            }
        }
        Files.walk(dir.toPath()).forEach(( p) -> {
            if (!(Files.isDirectory(p))) {
                Assert.assertTrue(p.toString(), p.toFile().delete());
            }
        });
        Assert.assertTrue(dir.delete());
    }

    @Test
    public void testReadingLessBytesThanWritten() {
        try (final ChronicleQueue queue = builder(getTmpDir(), wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            final Bytes<byte[]> expected = Bytes.wrapForRead("some long message".getBytes(StandardCharsets.ISO_8859_1));
            for (int i = 0; i < 10; i++) {
                appender.writeBytes(expected);
            }
            final ExcerptTailer tailer = queue.createTailer();
            // Sequential read
            for (int i = 0; i < 10; i++) {
                Bytes b = Bytes.allocateDirect(8);
                tailer.readBytes(b);
                Assert.assertEquals(expected.readInt(0), b.readInt(0));
                b.release();
            }
        }
    }

    @Test
    public void testAppendAndRead() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            final int cycle = appender.cycle();
            for (int i = 0; i < 10; i++) {
                final int n = i;
                appender.writeDocument(( w) -> w.write(TestKey.test).int32(n));
                Assert.assertEquals(n, queue.rollCycle().toSequenceNumber(appender.lastIndexAppended()));
            }
            final ExcerptTailer tailer = queue.createTailer();
            // Sequential read
            for (int i = 0; i < 10; i++) {
                final int n = i;
                Assert.assertTrue(tailer.readDocument(( r) -> assertEquals(n, r.read(TestKey.test).int32())));
                Assert.assertEquals((n + 1), queue.rollCycle().toSequenceNumber(tailer.index()));
            }
            // Random read
            for (int i = 0; i < 10; i++) {
                final int n = i;
                Assert.assertTrue(("n: " + n), tailer.moveToIndex(queue.rollCycle().toIndex(cycle, n)));
                Assert.assertTrue(("n: " + n), tailer.readDocument(( r) -> assertEquals(n, r.read(TestKey.test).int32())));
                Assert.assertEquals((n + 1), queue.rollCycle().toSequenceNumber(tailer.index()));
            }
        }
    }

    @Test
    public void testReadAndAppend() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).build()) {
            int[] results = new int[2];
            Thread t = new Thread(() -> {
                try {
                    final ExcerptTailer tailer = queue.createTailer();
                    for (int i = 0; i < 2;) {
                        boolean read = tailer.readDocument(( r) -> {
                            int result = r.read(TestKey.test).int32();
                            results[result] = result;
                        });
                        if (read) {
                            i++;
                        } else {
                            // Pause for a little
                            Jvm.pause(10);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.assertTrue(false);
                }
            });
            t.setDaemon(true);
            t.start();
            // Give the tailer thread enough time to initialise before send
            // the messages
            Jvm.pause(500);// TODO: latch

            final ExcerptAppender appender = queue.acquireAppender();
            for (int i = 0; i < 2; i++) {
                final int n = i;
                appender.writeDocument(( w) -> w.write(TestKey.test).int32(n));
            }
            Jvm.pause(500);
            Assert.assertArrayEquals(new int[]{ 0, 1 }, results);
        }
    }

    @Test
    public void testCheckIndexWithWritingDocument() {
        doTestCheckIndex(( appender, n) -> {
            try (final DocumentContext dc = appender.writingDocument()) {
                dc.wire().writeEventName("").object(("" + n));
            }
        });
    }

    @Test
    public void testCheckIndexWithWritingDocument2() {
        doTestCheckIndex(( appender, n) -> {
            try (final DocumentContext dc = appender.writingDocument()) {
                // float also supported.
                dc.wire().bytes().writeUtf8("Hello").writeStopBit(12345).writeStopBit(1.2).writeInt(1);
            }
        });
    }

    @Test
    public void testCheckIndexWithWriteBytes() {
        doTestCheckIndex(( appender, n) -> appender.writeBytes(Bytes.from(("Message-" + n))));
    }

    @Test
    public void testCheckIndexWithWriteBytes2() {
        doTestCheckIndex(( appender, n) -> appender.writeBytes(( b) -> b.append8bit("Message-").append(n)));
    }

    @Test
    public void testCheckIndexWithWriteBytes3() {
        doTestCheckIndex(( appender, n) -> appender.writeBytes(( b) -> // float also supported.
        b.writeUtf8("Hello").writeStopBit(12345).writeStopBit(1.2).writeInt(1)));
    }

    @Test
    public void testCheckIndexWithWriteMap() {
        doTestCheckIndex(( appender, n) -> appender.writeMap(new HashMap<String, String>() {
            {
                put("key", ("Message-" + n));
            }
        }));
    }

    @Test
    public void testCheckIndexWithWriteText() {
        doTestCheckIndex(( appender, n) -> appender.writeText(("Message-" + n)));
    }

    @Test
    public void testAppendAndReadWithRollingB() {
        SetTimeProvider stp = new SetTimeProvider();
        stp.currentTimeMillis(((System.currentTimeMillis()) - (3 * 86400000L)));
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).rollCycle(TEST_DAILY).timeProvider(stp).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            Assume.assumeFalse(((appender.padToCacheAlignMode()) == (WORD)));
            appender.writeDocument(( w) -> w.write(TestKey.test).int32(0));
            appender.writeDocument(( w) -> w.write(TestKey.test2).int32(1000));
            int cycle = appender.cycle();
            for (int i = 1; i <= 5; i++) {
                stp.currentTimeMillis(((stp.currentTimeMillis()) + 86400000L));
                final int n = i;
                appender.writeDocument(( w) -> w.write(TestKey.test).int32(n));
                Assert.assertEquals((cycle + i), appender.cycle());
                appender.writeDocument(( w) -> w.write(TestKey.test2).int32((n + 1000)));
                Assert.assertEquals((cycle + i), appender.cycle());
            }
            /* Note this means the file has rolled
            --- !!not-ready-meta-data! #binary
            ...
             */
            Assume.assumeFalse(encryption);
            Assume.assumeFalse(((wireType) == (DEFAULT_ZERO_BINARY)));
            final ExcerptTailer tailer = queue.createTailer().toStart();
            for (int i = 0; i < 6; i++) {
                final int n = i;
                boolean condition = tailer.readDocument(( r) -> assertEquals(n, r.read(TestKey.test).int32()));
                Assert.assertTrue(("i : " + i), condition);
                Assert.assertEquals((cycle + i), tailer.cycle());
                boolean condition2 = tailer.readDocument(( r) -> assertEquals((n + 1000), r.read(TestKey.test2).int32()));
                Assert.assertTrue(("i2 : " + i), condition2);
                Assert.assertEquals((cycle + i), tailer.cycle());
            }
        }
    }

    @Test
    public void testAppendAndReadAtIndex() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).rollCycle(TEST2_DAILY).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            appender.cycle();
            for (int i = 0; i < 5; i++) {
                final int n = i;
                appender.writeDocument(( w) -> w.write(TestKey.test).int32(n));
                Assert.assertEquals(i, queue.rollCycle().toSequenceNumber(appender.lastIndexAppended()));
            }
            final ExcerptTailer tailer = queue.createTailer();
            for (int i = 0; i < 5; i++) {
                final long index = queue.rollCycle().toIndex(appender.cycle(), i);
                Assert.assertTrue(tailer.moveToIndex(index));
                final int n = i;
                Assert.assertTrue(tailer.readDocument(( r) -> assertEquals(n, queue.rollCycle().toSequenceNumber(r.read(TestKey.test).int32()))));
                long index2 = tailer.index();
                long sequenceNumber = queue.rollCycle().toSequenceNumber(index2);
                Assert.assertEquals((n + 1), sequenceNumber);
            }
        }
    }

    @Test
    public void testSimpleWire() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( wire) -> wire.write(() -> "FirstName").text("Steve"));
            appender.writeDocument(( wire) -> wire.write(() -> "Surname").text("Jobs"));
            StringBuilder first = new StringBuilder();
            StringBuilder surname = new StringBuilder();
            final ExcerptTailer tailer = chronicle.createTailer();
            tailer.readDocument(( wire) -> wire.read(() -> "FirstName").text(first));
            tailer.readDocument(( wire) -> wire.read(() -> "Surname").text(surname));
            Assert.assertEquals("Steve Jobs", ((first + " ") + surname));
        }
    }

    @Test
    public void testIndexWritingDocument() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            long index;
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write(() -> "FirstName").text("Quartilla");
                index = dc.index();
            }
            try (DocumentContext dc = appender.writingDocument(true)) {
                dc.wire().write(() -> "FirstName").text("Quartilla");
            }
            Assert.assertEquals(index, appender.lastIndexAppended());
        }
    }

    @Test
    public void testReadingWritingMarshallableDocument() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            SingleChronicleQueueTest.MyMarshable myMarshable = new SingleChronicleQueueTest.MyMarshable();
            final ExcerptAppender appender = chronicle.acquireAppender();
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write("myMarshable").typedMarshallable(myMarshable);
            }
            ExcerptTailer tailer = chronicle.createTailer();
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertEquals(myMarshable, dc.wire().read(() -> "myMarshable").typedMarshallable());
            }
        }
    }

    @Test
    public void testMetaData() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            try (DocumentContext dc = appender.writingDocument(true)) {
                dc.wire().write(() -> "FirstName").text("Quartilla");
            }
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write(() -> "FirstName").text("Rob");
            }
            try (DocumentContext dc = appender.writingDocument(true)) {
                dc.wire().write(() -> "FirstName").text("Steve");
            }
            final ExcerptTailer tailer = chronicle.createTailer();
            StringBuilder event = new StringBuilder();
            while (true) {
                try (DocumentContext dc = tailer.readingDocument(true)) {
                    Assert.assertTrue(dc.isMetaData());
                    ValueIn in = dc.wire().read(event);
                    // first we will pick up header, index etc.
                    if (!(StringUtils.isEqual(event, "FirstName")))
                        continue;

                    in.text("Quartilla", Assert::assertEquals);
                    break;
                }
            } 
            long robIndex;
            try (DocumentContext dc = tailer.readingDocument(true)) {
                Assert.assertTrue(dc.isData());
                robIndex = dc.index();
                dc.wire().read(() -> "FirstName").text("Rob", Assert::assertEquals);
            }
            while (true) {
                try (DocumentContext dc = tailer.readingDocument(true)) {
                    Assert.assertTrue(dc.isMetaData());
                    ValueIn in = dc.wire().read(event);
                    if (!(StringUtils.isEqual(event, "FirstName")))
                        continue;

                    in.text("Steve", Assert::assertEquals);
                    break;
                }
            } 
            Assert.assertTrue(tailer.moveToIndex(robIndex));
            try (DocumentContext dc = tailer.readingDocument(true)) {
                Assert.assertTrue(dc.isData());
                dc.wire().read(() -> "FirstName").text("Rob", Assert::assertEquals);
            }
        }
    }

    @Test
    public void testReadingSecondDocumentNotExist() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write(() -> "FirstName").text("Quartilla");
            }
            final ExcerptTailer tailer = chronicle.createTailer();
            try (DocumentContext dc = tailer.readingDocument()) {
                String text = dc.wire().read(() -> "FirstName").text();
                Assert.assertEquals("Quartilla", text);
            }
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertFalse(dc.isPresent());
            }
        }
    }

    @Test
    public void testDocumentIndexTest() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            try (DocumentContext dc = appender.writingDocument()) {
                long index = dc.index();
                Assert.assertEquals(0, chronicle.rollCycle().toSequenceNumber(index));
                dc.wire().write(() -> "FirstName").text("Quartilla");
            }
            try (DocumentContext dc = appender.writingDocument()) {
                Assert.assertEquals(1, chronicle.rollCycle().toSequenceNumber(dc.index()));
                dc.wire().write(() -> "FirstName").text("Rob");
            }
            try (DocumentContext dc = appender.writingDocument()) {
                Assert.assertEquals(2, chronicle.rollCycle().toSequenceNumber(dc.index()));
                dc.wire().write(() -> "FirstName").text("Rob");
            }
            ExcerptTailer tailer = chronicle.createTailer();
            try (DocumentContext dc = tailer.readingDocument()) {
                long index = dc.index();
                Assert.assertEquals(0, chronicle.rollCycle().toSequenceNumber(index));
            }
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertEquals(1, chronicle.rollCycle().toSequenceNumber(dc.index()));
            }
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertEquals(2, chronicle.rollCycle().toSequenceNumber(dc.index()));
            }
        }
    }

    @Test
    public void testReadingSecondDocumentNotExistIncludingMeta() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write(() -> "FirstName").text("Quartilla");
            }
            final ExcerptTailer tailer = chronicle.createTailer();
            StringBuilder event = new StringBuilder();
            while (true) {
                try (DocumentContext dc = tailer.readingDocument(true)) {
                    ValueIn in = dc.wire().read(event);
                    if (!(StringUtils.isEqual(event, "FirstName")))
                        continue;

                    in.text("Quartilla", Assert::assertEquals);
                    break;
                }
            } 
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertFalse(dc.isPresent());
            }
        }
    }

    @Test
    public void testSimpleByteTest() {
        Assume.assumeFalse(Jvm.isArm());
        try (final ChronicleQueue chronicle = builder(getTmpDir(), wireType).rollCycle(TEST2_DAILY).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            Bytes steve = Bytes.allocateDirect("Steve".getBytes());
            appender.writeBytes(steve);
            Bytes jobs = Bytes.allocateDirect("Jobs".getBytes());
            appender.writeBytes(jobs);
            final ExcerptTailer tailer = chronicle.createTailer();
            Bytes bytes = Bytes.elasticByteBuffer();
            try {
                tailer.readBytes(bytes);
                Assert.assertEquals("Steve", bytes.toString());
                bytes.clear();
                tailer.readBytes(bytes);
                Assert.assertEquals("Jobs", bytes.toString());
            } finally {
                steve.release();
                jobs.release();
                bytes.release();
            }
        }
    }

    @Test
    public void testReadAtIndex() {
        try (final RollingChronicleQueue queue = builder(getTmpDir(), wireType).indexCount(8).indexSpacing(8).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            // create 100 documents
            for (int i = 0; i < 100; i++) {
                final int j = i;
                try (final DocumentContext context = appender.writingDocument()) {
                    context.wire().write(() -> "key").text(("value=" + j));
                }
            }
            long lastIndex = appender.lastIndexAppended();
            final int cycle = queue.rollCycle().toCycle(lastIndex);
            Assert.assertEquals(queue.firstCycle(), cycle);
            Assert.assertEquals(queue.lastCycle(), cycle);
            final ExcerptTailer tailer = queue.createTailer();
            StringBuilder sb = new StringBuilder();
            for (int i : new int[]{ 0, 8, 7, 9, 64, 65, 66 }) {
                final long index = queue.rollCycle().toIndex(cycle, i);
                Assert.assertTrue(("i: " + i), tailer.moveToIndex(index));
                final DocumentContext context = tailer.readingDocument();
                Assert.assertThat(context.index(), CoreMatchers.is(index));
                context.wire().read(() -> "key").text(sb);
                Assert.assertEquals(("value=" + i), sb.toString());
            }
        }
    }

    @Test
    public void testLastWrittenIndexPerAppender() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            appender.writeDocument(( wire) -> wire.write(() -> "key").text("test"));
            Assert.assertEquals(0, queue.rollCycle().toSequenceNumber(appender.lastIndexAppended()));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testLastWrittenIndexPerAppenderNoData() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.lastIndexAppended();
            Assert.fail();
        }
    }

    // : no messages written
    @Test(expected = IllegalStateException.class)
    public void testNoMessagesWritten() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.lastIndexAppended();
        }
    }

    @Test
    public void testHeaderIndexReadAtIndex() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            final int cycle = appender.cycle();
            // create 100 documents
            for (int i = 0; i < 100; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
            }
            final ExcerptTailer tailer = queue.createTailer();
            Assert.assertTrue(tailer.moveToIndex(queue.rollCycle().toIndex(cycle, 0)));
            StringBuilder sb = new StringBuilder();
            tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
            Assert.assertEquals("value=0", sb.toString());
        }
    }

    /**
     * test that if we make EPOC the current time, then the cycle is == 0
     */
    @Test
    public void testEPOC() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).epoch(System.currentTimeMillis()).rollCycle(RollCycles.HOURLY).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( wire) -> wire.write(() -> "key").text("value=v"));
            Assert.assertTrue(((appender.cycle()) == 0));
        }
    }

    @Test
    public void shouldBeAbleToReadFromQueueWithNonZeroEpoch() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).epoch(System.currentTimeMillis()).rollCycle(RollCycles.DAILY).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( wire) -> wire.write(() -> "key").text("value=v"));
            Assert.assertTrue(((appender.cycle()) == 0));
            final ExcerptTailer excerptTailer = chronicle.createTailer().toStart();
            Assert.assertThat(excerptTailer.readingDocument().isPresent(), CoreMatchers.is(true));
        }
    }

    @Test
    public void shouldHandleLargeEpoch() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).epoch(System.currentTimeMillis()).epoch(1284739200000L).rollCycle(DAILY).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( wire) -> wire.write(() -> "key").text("value=v"));
            final ExcerptTailer excerptTailer = chronicle.createTailer().toStart();
            Assert.assertThat(excerptTailer.readingDocument().isPresent(), CoreMatchers.is(true));
        }
    }

    @Test
    public void testNegativeEPOC() {
        for (int h = -14; h <= 14; h++) {
            try (final ChronicleQueue chronicle = builder(getTmpDir(), wireType).epoch(TimeUnit.HOURS.toMillis(h)).build()) {
                final ExcerptAppender appender = chronicle.acquireAppender();
                appender.writeDocument(( wire) -> wire.write(() -> "key").text("value=v"));
                chronicle.createTailer().readDocument(( wire) -> {
                    assertEquals("value=v", wire.read("key").text());
                });
            }
        }
    }

    @Test
    public void testIndex() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).rollCycle(RollCycles.HOURLY).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            int cycle = appender.cycle();
            // create 100 documents
            for (int i = 0; i < 5; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
                if (i == 2) {
                    final long cycle1 = queue.rollCycle().toCycle(appender.lastIndexAppended());
                    Assert.assertEquals(cycle1, cycle);
                }
            }
            final ExcerptTailer tailer = queue.createTailer();
            Assert.assertTrue(tailer.moveToIndex(queue.rollCycle().toIndex(cycle, 2)));
            StringBuilder sb = new StringBuilder();
            tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
            Assert.assertEquals("value=2", sb.toString());
            tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
            Assert.assertEquals("value=3", sb.toString());
            tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
            Assert.assertEquals("value=4", sb.toString());
        }
    }

    @Test
    public void testReadingDocument() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).rollCycle(RollCycles.HOURLY).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            long cycle = appender.cycle();
            // create 100 documents
            for (int i = 0; i < 5; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
                if (i == 2) {
                    final long cycle1 = queue.rollCycle().toCycle(appender.lastIndexAppended());
                    Assert.assertEquals(cycle1, cycle);
                }
            }
            final ExcerptTailer tailer = queue.createTailer();
            final StringBuilder sb = Wires.acquireStringBuilder();
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert dc.isPresent();
                assert dc.isData();
                dc.wire().read(() -> "key").text(sb);
                Assert.assertEquals("value=0", sb.toString());
            }
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert dc.isPresent();
                assert dc.isData();
                dc.wire().read(() -> "key").text(sb);
                Assert.assertEquals("value=1", sb.toString());
            }
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert dc.isPresent();
                assert dc.isData();
                dc.wire().read(() -> "key").text(sb);
                Assert.assertEquals("value=2", sb.toString());
            }
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert dc.isPresent();
                assert dc.isData();
                dc.wire().read(() -> "key").text(sb);
                Assert.assertEquals("value=3", sb.toString());
            }
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert dc.isPresent();
                assert dc.isData();
                dc.wire().read(() -> "key").text(sb);
                Assert.assertEquals("value=4", sb.toString());
            }
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert !(dc.isPresent());
                assert !(dc.isData());
                assert !(dc.isMetaData());
            }
        }
    }

    @Test
    public void testReadingDocumentWithFirstAMove() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).rollCycle(RollCycles.HOURLY).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            int cycle = appender.cycle();
            // create 100 documents
            for (int i = 0; i < 5; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
                if (i == 2) {
                    final long cycle1 = queue.rollCycle().toCycle(appender.lastIndexAppended());
                    Assert.assertEquals(cycle1, cycle);
                }
            }
            final ExcerptTailer tailer = queue.createTailer();
            Assert.assertTrue(tailer.moveToIndex(queue.rollCycle().toIndex(cycle, 2)));
            final StringBuilder sb = Wires.acquireStringBuilder();
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert dc.isPresent();
                assert dc.isData();
                dc.wire().read(() -> "key").text(sb);
                Assert.assertEquals("value=2", sb.toString());
            }
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert dc.isPresent();
                assert dc.isData();
                dc.wire().read(() -> "key").text(sb);
                Assert.assertEquals("value=3", sb.toString());
            }
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert dc.isPresent();
                assert dc.isData();
                dc.wire().read(() -> "key").text(sb);
                Assert.assertEquals("value=4", sb.toString());
            }
            try (final DocumentContext dc = tailer.readingDocument()) {
                assert !(dc.isPresent());
                assert !(dc.isData());
                assert !(dc.isMetaData());
            }
        }
    }

    @Test
    public void testReadingDocumentWithFirstAMoveWithEpoch() {
        Instant hourly = Instant.parse("2018-02-12T00:59:59.999Z");
        Instant minutely = Instant.parse("2018-02-12T00:00:59.999Z");
        Date epochHourlyFirstCycle = Date.from(hourly);
        Date epochMinutelyFirstCycle = Date.from(minutely);
        Date epochHourlySecondCycle = Date.from(hourly.plusMillis(1));
        Date epochMinutelySecondCycle = Date.from(minutely.plusMillis(1));
        doTestEpochMove(epochHourlyFirstCycle.getTime(), MINUTELY);
        doTestEpochMove(epochHourlySecondCycle.getTime(), MINUTELY);
        doTestEpochMove(epochHourlyFirstCycle.getTime(), RollCycles.HOURLY);
        doTestEpochMove(epochHourlySecondCycle.getTime(), RollCycles.HOURLY);
        doTestEpochMove(epochHourlyFirstCycle.getTime(), RollCycles.DAILY);
        doTestEpochMove(epochHourlySecondCycle.getTime(), RollCycles.DAILY);
        doTestEpochMove(epochMinutelyFirstCycle.getTime(), MINUTELY);
        doTestEpochMove(epochMinutelySecondCycle.getTime(), MINUTELY);
        doTestEpochMove(epochMinutelyFirstCycle.getTime(), RollCycles.HOURLY);
        doTestEpochMove(epochMinutelySecondCycle.getTime(), RollCycles.HOURLY);
        doTestEpochMove(epochMinutelyFirstCycle.getTime(), RollCycles.DAILY);
        doTestEpochMove(epochMinutelySecondCycle.getTime(), RollCycles.DAILY);
    }

    @Test
    public void testAppendedBeforeToEnd() {
        File dir = getTmpDir();
        try (ChronicleQueue chronicle = builder(dir, this.wireType).rollCycle(RollCycles.TEST_SECONDLY).build();ChronicleQueue chronicle2 = builder(dir, this.wireType).rollCycle(RollCycles.TEST_SECONDLY).build()) {
            ExcerptTailer tailer = chronicle.createTailer();
            ExcerptAppender append = chronicle2.acquireAppender();
            append.writeDocument(( w) -> w.write(() -> "test").text("text"));
            while ((tailer.state()) == (UNINITIALISED))
                tailer.toEnd();

            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertFalse((((tailer.index()) + " ") + (tailer.state())), dc.isPresent());
            }
            append.writeDocument(( w) -> w.write(() -> "test").text("text2"));
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertTrue(dc.isPresent());
                Assert.assertEquals("text2", dc.wire().read("test").text());
            }
        }
    }

    @Test(expected = AssertionError.class)
    public void testReentrant() {
        boolean assertsEnabled = false;
        // noinspection ConstantConditions
        assert assertsEnabled = true;
        // noinspection ConstantConditions
        Assume.assumeTrue(assertsEnabled);
        File tmpDir = DirectoryUtils.tempDir("testReentrant");
        try (final ChronicleQueue queue = binary(tmpDir).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build()) {
            ExcerptAppender appender = queue.acquireAppender();
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write("some").text("data");
                try (DocumentContext dc2 = appender.writingDocument()) {
                    dc2.wire().write("some2").text("other");
                }
            }
        }
    }

    @Test
    public void testToEnd() throws InterruptedException {
        File dir = getTmpDir();
        try (ChronicleQueue queue = builder(dir, wireType).rollCycle(RollCycles.HOURLY).build()) {
            ExcerptTailer tailer = queue.createTailer();
            // move to the end even though it doesn't exist yet.
            tailer.toEnd();
            try (ChronicleQueue chronicle2 = builder(dir, wireType).rollCycle(RollCycles.HOURLY).build()) {
                ExcerptAppender append = chronicle2.acquireAppender();
                append.writeDocument(( w) -> w.write(() -> "test").text("text"));
            }
            // this is needed to avoid caching of first and last cycle, see SingleChronicleQueue#setFirstAndLastCycle
            Thread.sleep(1);
            try (DocumentContext dc = tailer.readingDocument()) {
                String message = "dump: " + (builder(dir, wireType).rollCycle(RollCycles.HOURLY).build().dump());
                Assert.assertTrue(message, dc.isPresent());
                Assert.assertEquals(message, "text", dc.wire().read("test").text());
            }
        }
    }

    @Test
    public void testToEnd2() {
        File dir = getTmpDir();
        try (ChronicleQueue chronicle = builder(dir, wireType).build();ChronicleQueue chronicle2 = builder(dir, wireType).build()) {
            ExcerptAppender append = chronicle2.acquireAppender();
            append.writeDocument(( w) -> w.write(() -> "test").text("before text"));
            ExcerptTailer tailer = chronicle.createTailer();
            // move to the end even though it doesn't exist yet.
            tailer.toEnd();
            append.writeDocument(( w) -> w.write(() -> "test").text("text"));
            Assert.assertTrue(tailer.readDocument(( w) -> w.read(() -> "test").text("text", Assert.Assert::assertEquals)));
        }
    }

    @Test
    public void testToEndOnDeletedQueueFiles() throws IOException {
        if (OS.isWindows()) {
            System.err.println("#460 Cannot test delete after close on windows");
            return;
        }
        File dir = getTmpDir();
        try (ChronicleQueue q = builder(dir, wireType).build()) {
            ExcerptAppender append = q.acquireAppender();
            append.writeDocument(( w) -> w.write(() -> "test").text("before text"));
            ExcerptTailer tailer = q.createTailer();
            // move to the end even though it doesn't exist yet.
            tailer.toEnd();
            append.writeDocument(( w) -> w.write(() -> "test").text("text"));
            Assert.assertTrue(tailer.readDocument(( w) -> w.read(() -> "test").text("text", Assert.Assert::assertEquals)));
            Files.find(dir.toPath(), 1, ( p, basicFileAttributes) -> p.toString().endsWith("cq4"), FileVisitOption.FOLLOW_LINKS).forEach(( path) -> Assert.assertTrue(path.toFile().delete()));
            ChronicleQueue q2 = builder(dir, wireType).build();
            tailer = q2.createTailer();
            tailer.toEnd();
            Assert.assertEquals(UNINITIALISED, tailer.state());
            append = q2.acquireAppender();
            append.writeDocument(( w) -> w.write(() -> "test").text("before text"));
            Assert.assertTrue(tailer.readDocument(( w) -> w.read(() -> "test").text("before text", Assert.Assert::assertEquals)));
        }
    }

    @Test
    public void testReadWrite() {
        File dir = getTmpDir();
        try (ChronicleQueue chronicle = builder(dir, wireType).rollCycle(RollCycles.HOURLY).testBlockSize().build();ChronicleQueue chronicle2 = builder(dir, wireType).rollCycle(RollCycles.HOURLY).testBlockSize().build()) {
            ExcerptAppender append = chronicle2.acquireAppender();
            int runs = 50000;
            for (int i = 0; i < runs; i++) {
                append.writeDocument(( w) -> w.write(() -> "test - message").text("text"));
            }
            ExcerptTailer tailer = chronicle.createTailer();
            ExcerptTailer tailer2 = chronicle.createTailer();
            ExcerptTailer tailer3 = chronicle.createTailer();
            ExcerptTailer tailer4 = chronicle.createTailer();
            for (int i = 0; i < runs; i++) {
                if ((i % 10000) == 0)
                    System.gc();

                if ((i % 2) == 0)
                    Assert.assertTrue(tailer2.readDocument(( w) -> w.read(() -> "test - message").text("text", Assert.Assert::assertEquals)));

                if ((i % 3) == 0)
                    Assert.assertTrue(tailer3.readDocument(( w) -> w.read(() -> "test - message").text("text", Assert.Assert::assertEquals)));

                if ((i % 4) == 0)
                    Assert.assertTrue(tailer4.readDocument(( w) -> w.read(() -> "test - message").text("text", Assert.Assert::assertEquals)));

                Assert.assertTrue(tailer.readDocument(( w) -> w.read(() -> "test - message").text("text", Assert.Assert::assertEquals)));
            }
        }
    }

    @Test
    public void testReadingDocumentForEmptyQueue() {
        File dir = getTmpDir();
        try (ChronicleQueue chronicle = builder(dir, this.wireType).rollCycle(RollCycles.HOURLY).build()) {
            ExcerptTailer tailer = chronicle.createTailer();
            // DocumentContext is empty as we have no queue and don't know what the wire type will be.
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertFalse(dc.isPresent());
            }
            try (ChronicleQueue chronicle2 = builder(dir, this.wireType).rollCycle(RollCycles.HOURLY).build()) {
                ExcerptAppender appender = chronicle2.acquireAppender();
                appender.writeDocument(( w) -> w.write(() -> "test - message").text("text"));
                while ((tailer.state()) == (UNINITIALISED))
                    tailer.toStart();

                // DocumentContext should not be empty as we know what the wire type will be.
                try (DocumentContext dc = tailer.readingDocument()) {
                    Assert.assertTrue(dc.isPresent());
                    dc.wire().read(() -> "test - message").text("text", Assert::assertEquals);
                }
            }
        }
    }

    @Test
    public void testMetaData6() {
        try (final ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).rollCycle(TEST2_DAILY).build()) {
            final ExcerptAppender appender = chronicle.acquireAppender();
            try (DocumentContext dc = appender.writingDocument(true)) {
                dc.wire().write(() -> "FirstName").text("Quartilla");
            }
            try (DocumentContext dc = appender.writingDocument()) {
                Assert.assertFalse(dc.isMetaData());
                dc.wire().write(() -> "FirstName").text("Helen");
            }
            try (DocumentContext dc = appender.writingDocument(true)) {
                dc.wire().write(() -> "FirstName").text("Steve");
            }
            final ExcerptTailer tailer = chronicle.createTailer();
            StringBuilder event = new StringBuilder();
            while (true) {
                try (DocumentContext dc = tailer.readingDocument(true)) {
                    Assert.assertTrue(dc.isMetaData());
                    ValueIn in = dc.wire().read(event);
                    if (!(StringUtils.isEqual(event, "FirstName")))
                        continue;

                    in.text("Quartilla", Assert::assertEquals);
                    break;
                }
            } 
            try (DocumentContext dc = tailer.readingDocument(true)) {
                Assert.assertTrue(dc.isData());
                Assert.assertTrue(dc.isPresent());
                dc.wire().read(() -> "FirstName").text("Helen", Assert::assertEquals);
            }
            while (true) {
                try (DocumentContext dc = tailer.readingDocument(true)) {
                    Assert.assertTrue(dc.isMetaData());
                    ValueIn in = dc.wire().read(event);
                    if (!(StringUtils.isEqual(event, "FirstName")))
                        continue;

                    in.text("Steve", Assert::assertEquals);
                    break;
                }
            } 
            Assert.assertEquals(expectedMetaDataTest2(), chronicle.dump());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void dontPassQueueToReader() {
        try (ChronicleQueue queue = binary(getTmpDir()).build()) {
            queue.createTailer().afterLastWritten(queue).methodReader();
        }
    }

    @Test
    public void testToEndBeforeWrite() {
        try (ChronicleQueue chronicle = builder(getTmpDir(), wireType).rollCycle(TEST2_DAILY).build()) {
            ExcerptAppender appender = chronicle.acquireAppender();
            ExcerptTailer tailer = chronicle.createTailer();
            int entries = ((chronicle.rollCycle().defaultIndexSpacing()) * 2) + 2;
            for (int i = 0; i < entries; i++) {
                tailer.toEnd();
                int finalI = i;
                appender.writeDocument(( w) -> w.writeEventName("hello").text(("world" + finalI)));
                tailer.readDocument(( w) -> w.read().text(("world" + finalI), Assert.Assert::assertEquals));
            }
        }
    }

    @Test
    public void testSomeMessages() {
        try (ChronicleQueue chronicle = builder(getTmpDir(), wireType).rollCycle(TEST2_DAILY).build()) {
            ExcerptAppender appender = chronicle.acquireAppender();
            ExcerptTailer tailer = chronicle.createTailer();
            int entries = ((chronicle.rollCycle().defaultIndexSpacing()) * 2) + 2;
            for (long i = 0; i < entries; i++) {
                long finalI = i;
                appender.writeDocument(( w) -> w.writeEventName("hello").int64(finalI));
                long seq = chronicle.rollCycle().toSequenceNumber(appender.lastIndexAppended());
                Assert.assertEquals(i, seq);
                // System.out.println(chronicle.dump());
                tailer.readDocument(( w) -> w.read().int64(finalI, ( a, b) -> Assert.Assert.assertEquals(((long) (a)), b)));
            }
        }
    }

    @Test
    public void testForwardFollowedBackBackwardTailer() {
        try (ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).rollCycle(TEST2_DAILY).build()) {
            ExcerptAppender appender = chronicle.acquireAppender();
            int entries = (chronicle.rollCycle().defaultIndexSpacing()) + 2;
            for (int i = 0; i < entries; i++) {
                int finalI = i;
                appender.writeDocument(( w) -> w.writeEventName("hello").text(("world" + finalI)));
            }
            for (int i = 0; i < 3; i++) {
                readForward(chronicle, entries);
                readBackward(chronicle, entries);
            }
        }
    }

    @Test
    public void shouldReadBackwardFromEndOfQueueWhenDirectionIsSetAfterMoveToEnd() {
        try (final ChronicleQueue queue = builder(getTmpDir(), this.wireType).rollCycle(TEST2_DAILY).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            appender.writeDocument(( w) -> w.writeEventName("hello").text("world"));
            final ExcerptTailer tailer = queue.createTailer();
            tailer.toEnd();
            tailer.direction(BACKWARD);
            Assert.assertThat(tailer.readingDocument().isPresent(), CoreMatchers.is(true));
        }
    }

    @Test
    public void testOverreadForwardFromFutureCycleThenReadBackwardTailer() {
        RollCycle cycle = TEST2_DAILY;
        // when "forwardToFuture" flag is set, go one cycle to the future
        AtomicBoolean forwardToFuture = new AtomicBoolean(false);
        TimeProvider timeProvider = () -> forwardToFuture.get() ? (System.currentTimeMillis()) + (TimeUnit.MILLISECONDS.toDays(1)) : System.currentTimeMillis();
        try (ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).rollCycle(cycle).timeProvider(timeProvider).build()) {
            ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( w) -> w.writeEventName("hello").text("world"));
            // go to the cycle next to the one the write was made on
            forwardToFuture.set(true);
            ExcerptTailer forwardTailer = chronicle.createTailer().direction(FORWARD).toStart();
            try (DocumentContext context = forwardTailer.readingDocument()) {
                Assert.assertTrue(context.isPresent());
            }
            try (DocumentContext context = forwardTailer.readingDocument()) {
                Assert.assertFalse(context.isPresent());
            }
            ExcerptTailer backwardTailer = chronicle.createTailer().direction(BACKWARD).toEnd();
            try (DocumentContext context = backwardTailer.readingDocument()) {
                Assert.assertTrue(context.isPresent());
            }
        }
    }

    @Test
    public void testLastIndexAppended() {
        try (ChronicleQueue chronicle = builder(getTmpDir(), this.wireType).build()) {
            ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( w) -> w.writeEventName("hello").text("world0"));
            final long nextIndexToWrite = (appender.lastIndexAppended()) + 1;
            appender.writeDocument(( w) -> w.getValueOut().bytes(new byte[0]));
            // System.out.println(chronicle.dump());
            Assert.assertEquals(nextIndexToWrite, appender.lastIndexAppended());
        }
    }

    @Test
    public void testZeroLengthMessage() {
        try (ChronicleQueue chronicle = builder(getTmpDir(), wireType).rollCycle(TEST_DAILY).build()) {
            ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( w) -> {
            });
            // System.out.println(chronicle.dump());
            ExcerptTailer tailer = chronicle.createTailer();
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertFalse(dc.wire().hasMore());
            }
        }
    }

    @Test
    public void testMoveToWithAppender() {
        try (ChronicleQueue syncQ = builder(getTmpDir(), this.wireType).build()) {
            InternalAppender sync = ((InternalAppender) (syncQ.acquireAppender()));
            File name2 = DirectoryUtils.tempDir(testName.getMethodName());
            try (ChronicleQueue chronicle = builder(name2, this.wireType).build()) {
                ExcerptAppender appender = chronicle.acquireAppender();
                appender.writeDocument(( w) -> w.writeEventName("hello").text("world0"));
                appender.writeDocument(( w) -> w.writeEventName("hello").text("world1"));
                appender.writeDocument(( w) -> w.writeEventName("hello").text("world2"));
                ExcerptTailer tailer = chronicle.createTailer();
                try (DocumentContext documentContext = tailer.readingDocument()) {
                    sync.writeBytes(documentContext.index(), documentContext.wire().bytes());
                }
                try (DocumentContext documentContext = tailer.readingDocument()) {
                    String text = documentContext.wire().read().text();
                    Assert.assertEquals("world1", text);
                }
            }
        }
    }

    @Test
    public void testMapWrapper() {
        try (ChronicleQueue syncQ = builder(getTmpDir(), this.wireType).build()) {
            File name2 = DirectoryUtils.tempDir(testName.getMethodName());
            try (ChronicleQueue chronicle = builder(name2, this.wireType).build()) {
                ExcerptAppender appender = chronicle.acquireAppender();
                SingleChronicleQueueTest.MapWrapper myMap = new SingleChronicleQueueTest.MapWrapper();
                myMap.map.put("hello", 1.2);
                appender.writeDocument(( w) -> w.write().object(myMap));
                ExcerptTailer tailer = chronicle.createTailer();
                try (DocumentContext documentContext = tailer.readingDocument()) {
                    SingleChronicleQueueTest.MapWrapper object = documentContext.wire().read().object(SingleChronicleQueueTest.MapWrapper.class);
                    Assert.assertEquals(1.2, object.map.get("hello"), 0.0);
                }
            }
        }
    }

    /**
     * if one appender if much further ahead than the other, then the new append should jump straight to the end rather than attempting to write a
     * positions that are already occupied
     */
    @Test
    public void testAppendedSkipToEnd() {
        try (ChronicleQueue q = builder(getTmpDir(), this.wireType).build()) {
            ExcerptAppender appender = q.acquireAppender();
            ExcerptAppender appender2 = q.acquireAppender();
            int indexCount = 100;
            for (int i = 0; i < indexCount; i++) {
                try (DocumentContext dc = appender.writingDocument()) {
                    dc.wire().write("key").text(("some more " + 1));
                    Assert.assertEquals(i, q.rollCycle().toSequenceNumber(dc.index()));
                }
            }
            try (DocumentContext dc = appender2.writingDocument()) {
                dc.wire().write("key").text(("some data " + indexCount));
                Assert.assertEquals(indexCount, q.rollCycle().toSequenceNumber(dc.index()));
            }
        }
    }

    @Test
    public void testAppendedSkipToEndMultiThreaded() throws InterruptedException {
        // some text to simulate load.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++)
            sb.append(UUID.randomUUID());

        String text = sb.toString();
        try (ChronicleQueue q = builder(getTmpDir(), this.wireType).rollCycle(TEST_SECONDLY).build()) {
            System.err.println(q.file());
            final ThreadLocal<ExcerptAppender> tl = ThreadLocal.withInitial(q::acquireAppender);
            int size = 50000;
            int threadCount = 8;
            int sizePerThread = size / threadCount;
            CountDownLatch latch = new CountDownLatch(threadCount);
            for (int j = 0; j < threadCount; j++) {
                new Thread(() -> {
                    for (int i = 0; i < sizePerThread; i++)
                        writeTestDocument(tl, text);

                    latch.countDown();
                }).start();
            }
            latch.await();
            ExcerptTailer tailer = q.createTailer();
            for (int i = 0; i < size; i++) {
                try (DocumentContext dc = tailer.readingDocument(false)) {
                    long index = dc.index();
                    long actual = dc.wire().read(() -> "key").int64();
                    Assert.assertEquals(toTextIndex(q, index), toTextIndex(q, actual));
                }
            }
        }
    }

    @Test
    public void testToEndPrevCycleEOF() {
        final AtomicLong clock = new AtomicLong(System.currentTimeMillis());
        File dir = getTmpDir();
        try (ChronicleQueue q = builder(dir, wireType).rollCycle(TEST_SECONDLY).timeProvider(clock::get).build()) {
            q.acquireAppender().writeText("first");
        }
        clock.addAndGet(1100);
        // this will write an EOF
        try (ChronicleQueue q = builder(dir, wireType).rollCycle(TEST_SECONDLY).timeProvider(clock::get).build()) {
            ExcerptTailer tailer = q.createTailer();
            Assert.assertEquals("first", tailer.readText());
            Assert.assertEquals(null, tailer.readText());
        }
        try (ChronicleQueue q = builder(dir, wireType).rollCycle(TEST_SECONDLY).timeProvider(clock::get).build()) {
            ExcerptTailer tailer = q.createTailer().toEnd();
            try (DocumentContext documentContext = tailer.readingDocument()) {
                Assert.assertFalse(documentContext.isPresent());
            }
            try (DocumentContext documentContext = tailer.readingDocument()) {
                Assert.assertFalse(documentContext.isPresent());
            }
        }
        clock.addAndGet(50L);
        try (ChronicleQueue q = builder(dir, wireType).rollCycle(TEST_SECONDLY).timeProvider(clock::get).build()) {
            ExcerptTailer excerptTailerBeforeAppend = q.createTailer().toEnd();
            q.acquireAppender().writeText("more text");
            ExcerptTailer excerptTailerAfterAppend = q.createTailer().toEnd();
            q.acquireAppender().writeText("even more text");
            Assert.assertEquals("more text", excerptTailerBeforeAppend.readText());
            Assert.assertEquals("even more text", excerptTailerAfterAppend.readText());
            Assert.assertEquals("even more text", excerptTailerBeforeAppend.readText());
        }
    }

    @Test
    public void shouldNotGenerateGarbageReadingDocumentAfterEndOfFile() {
        final AtomicLong clock = new AtomicLong(System.currentTimeMillis());
        File dir = getTmpDir();
        try (ChronicleQueue q = builder(dir, wireType).rollCycle(TEST_SECONDLY).timeProvider(clock::get).build()) {
            q.acquireAppender().writeText("first");
        }
        clock.addAndGet(1100);
        // this will write an EOF
        try (ChronicleQueue q = builder(dir, wireType).rollCycle(TEST_SECONDLY).timeProvider(clock::get).build()) {
            ExcerptTailer tailer = q.createTailer();
            Assert.assertEquals("first", tailer.readText());
            GcControls.waitForGcCycle();
            final long startCollectionCount = GcControls.getGcCount();
            // allow a few GCs due to possible side-effect or re-used JVM
            final long maxAllowedGcCycles = 6;
            final long endCollectionCount = GcControls.getGcCount();
            final long actualGcCycles = endCollectionCount - startCollectionCount;
            Assert.assertTrue(String.format("Too many GC cycles. Expected <= %d, but was %d", maxAllowedGcCycles, actualGcCycles), (actualGcCycles <= maxAllowedGcCycles));
        }
    }

    @Test
    public void testTailerWhenCyclesWhereSkippedOnWrite() {
        SetTimeProvider timeProvider = new SetTimeProvider();
        try (final ChronicleQueue queue = binary(getTmpDir()).rollCycle(RollCycles.TEST_SECONDLY).timeProvider(timeProvider).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            final ExcerptTailer tailer = queue.createTailer();
            final List<String> stringsToPut = Arrays.asList("one", "two", "three");
            // writes two strings immediately and one string with 2 seconds delay
            {
                try (DocumentContext writingContext = appender.writingDocument()) {
                    writingContext.wire().write().bytes(stringsToPut.get(0).getBytes());
                }
                try (DocumentContext writingContext = appender.writingDocument()) {
                    writingContext.wire().write().bytes(stringsToPut.get(1).getBytes());
                }
                timeProvider.advanceMillis(2100);
                try (DocumentContext writingContext = appender.writingDocument()) {
                    writingContext.wire().write().bytes(stringsToPut.get(2).getBytes());
                }
            }
            for (String expected : stringsToPut) {
                try (DocumentContext readingContext = tailer.readingDocument()) {
                    if (!(readingContext.isPresent()))
                        Assert.fail();

                    String text = readingContext.wire().read().text();
                    Assert.assertEquals(expected, text);
                }
            }
        }
    }

    @Test
    public void testMultipleAppenders() {
        try (ChronicleQueue syncQ = builder(getTmpDir(), this.wireType).rollCycle(TEST_DAILY).build()) {
            ExcerptAppender syncA = syncQ.acquireAppender();
            Assume.assumeFalse(((syncA.padToCacheAlignMode()) == (WORD)));
            ExcerptAppender syncB = syncQ.acquireAppender();
            ExcerptAppender syncC = syncQ.acquireAppender();
            int count = 0;
            for (int i = 0; i < 3; i++) {
                syncA.writeText(("hello A" + i));
                Assert.assertEquals((count++), ((int) (syncA.lastIndexAppended())));
                syncB.writeText(("hello B" + i));
                Assert.assertEquals((count++), ((int) (syncB.lastIndexAppended())));
                try (DocumentContext dc = syncC.writingDocument(true)) {
                    dc.wire().getValueOut().text(("some meta " + i));
                }
            }
            String expected = expectedMultipleAppenders();
            Assert.assertEquals(expected, syncQ.dump());
        }
    }

    @Test
    public void testCountExceptsBetweenCycles() {
        SetTimeProvider timeProvider = new SetTimeProvider();
        final SingleChronicleQueueBuilder builder = binary(getTmpDir()).rollCycle(RollCycles.TEST_SECONDLY).timeProvider(timeProvider);
        final RollingChronicleQueue queue = builder.build();
        final ExcerptAppender appender = queue.acquireAppender();
        long[] indexs = new long[10];
        for (int i = 0; i < (indexs.length); i++) {
            System.out.println(".");
            try (DocumentContext writingContext = appender.writingDocument()) {
                writingContext.wire().write().text(("some-text-" + i));
                indexs[i] = writingContext.index();
            }
            // we add the pause times to vary the test, to ensure it can handle when cycles are
            // skipped
            if (((i + 1) % 5) == 0)
                timeProvider.advanceMillis(2000);
            else
                if (((i + 1) % 3) == 0)
                    timeProvider.advanceMillis(1000);


        }
        for (int lower = 0; lower < (indexs.length); lower++) {
            for (int upper = lower; upper < (indexs.length); upper++) {
                System.out.println(((("lower=" + lower) + ",upper=") + upper));
                Assert.assertEquals((upper - lower), queue.countExcerpts(indexs[lower], indexs[upper]));
            }
        }
        // check the base line of the test below
        Assert.assertEquals(6, queue.countExcerpts(indexs[0], indexs[6]));
        // / check for the case when the last index has a sequence number of -1
        Assert.assertEquals(queue.rollCycle().toSequenceNumber(indexs[6]), 0);
        Assert.assertEquals(5, queue.countExcerpts(indexs[0], ((indexs[6]) - 1)));
        // / check for the case when the first index has a sequence number of -1
        Assert.assertEquals(7, queue.countExcerpts(((indexs[0]) - 1), indexs[6]));
    }

    @Test
    public void testReadingWritingWhenNextCycleIsInSequence() {
        SetTimeProvider timeProvider = new SetTimeProvider();
        final File dir = DirectoryUtils.tempDir(testName.getMethodName());
        final RollCycles.RollCycles rollCycle = TEST_SECONDLY;
        // write first message
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
            queue.acquireAppender().writeText("first message");
        }
        timeProvider.advanceMillis(1100);
        // write second message
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
            queue.acquireAppender().writeText("second message");
        }
        // read both messages
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
            ExcerptTailer tailer = queue.createTailer();
            Assert.assertEquals("first message", tailer.readText());
            Assert.assertEquals("second message", tailer.readText());
        }
    }

    @Test
    public void testReadingWritingWhenCycleIsSkipped() {
        SetTimeProvider timeProvider = new SetTimeProvider();
        final File dir = DirectoryUtils.tempDir(testName.getMethodName());
        final RollCycles.RollCycles rollCycle = TEST_SECONDLY;
        // write first message
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
            queue.acquireAppender().writeText("first message");
        }
        timeProvider.advanceMillis(2100);
        // write second message
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
            queue.acquireAppender().writeText("second message");
        }
        // read both messages
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
            ExcerptTailer tailer = queue.createTailer();
            Assert.assertEquals("first message", tailer.readText());
            Assert.assertEquals("second message", tailer.readText());
        }
    }

    @Test
    public void testReadingWritingWhenCycleIsSkippedBackwards() throws Exception {
        final File dir = DirectoryUtils.tempDir(testName.getMethodName());
        final RollCycles.RollCycles rollCycle = TEST_SECONDLY;
        // write first message
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).build()) {
            queue.acquireAppender().writeText("first message");
        }
        // TODO: this test fails when converted to use a TimeProvider. Need to work out why
        Thread.sleep(2100);
        // write second message
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).build()) {
            queue.acquireAppender().writeText("second message");
        }
        // read both messages
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).build()) {
            ExcerptTailer tailer = queue.createTailer();
            ExcerptTailer excerptTailer = tailer.direction(BACKWARD).toEnd();
            Assert.assertEquals("second message", excerptTailer.readText());
            Assert.assertEquals("first message", excerptTailer.readText());
        }
    }

    @Test
    public void testReadWritingWithTimeProvider() {
        final File dir = DirectoryUtils.tempDir(testName.getMethodName());
        long time = System.currentTimeMillis();
        SetTimeProvider timeProvider = new SetTimeProvider();
        timeProvider.currentTimeMillis(time);
        try (ChronicleQueue q1 = binary(dir).timeProvider(timeProvider).build()) {
            try (ChronicleQueue q2 = binary(dir).timeProvider(timeProvider).build()) {
                final ExcerptAppender appender2 = q2.acquireAppender();
                final ExcerptTailer tailer1 = q1.createTailer();
                final ExcerptTailer tailer2 = q2.createTailer();
                try (final DocumentContext dc = appender2.writingDocument()) {
                    dc.wire().write().text("some data");
                }
                try (DocumentContext dc = tailer2.readingDocument()) {
                    Assert.assertTrue(dc.isPresent());
                }
                Assert.assertTrue(q1.file().equals(q2.file()));
                // this is required for queue to re-request last/first cycle
                timeProvider.advanceMillis(1);
                for (int i = 0; i < 10; i++) {
                    try (DocumentContext dc = tailer1.readingDocument()) {
                        if (dc.isPresent())
                            return;

                    }
                    Jvm.pause(1);
                }
                Assert.fail();
            }
        }
    }

    @Test
    public void testTailerSnappingRollWithNewAppender() throws Exception {
        SetTimeProvider timeProvider = new SetTimeProvider();
        timeProvider.currentTimeMillis(((System.currentTimeMillis()) - 2000));
        final File dir = DirectoryUtils.tempDir(testName.getMethodName());
        final RollCycles.RollCycles rollCycle = TEST_SECONDLY;
        // write first message
        try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
            ExcerptAppender excerptAppender = queue.acquireAppender();
            excerptAppender.writeText("someText");
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            Future f1 = executorService.submit(() -> {
                try (ChronicleQueue queue2 = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
                    queue2.acquireAppender().writeText("someText more");
                }
                timeProvider.advanceMillis(1100);
                try (ChronicleQueue queue2 = binary(dir).rollCycle(rollCycle).build()) {
                    queue2.acquireAppender().writeText("someText more");
                }
            });
            Future f2 = executorService.submit(() -> {
                // write second message
                try (ChronicleQueue queue2 = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
                    for (int i = 0; i < 5; i++) {
                        queue2.acquireAppender().writeText("someText more");
                        timeProvider.advanceMillis(400);
                    }
                }
            });
            f1.get(10, TimeUnit.SECONDS);
            System.out.println(queue.dump());
            f2.get(10, TimeUnit.SECONDS);
            executorService.shutdownNow();
        }
    }

    @Test
    public void testLongLivingTailerAppenderReAcquiredEachSecond() {
        SetTimeProvider timeProvider = new SetTimeProvider();
        final File dir = DirectoryUtils.tempDir(testName.getMethodName());
        final RollCycles.RollCycles rollCycle = TEST_SECONDLY;
        try (ChronicleQueue queuet = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
            final ExcerptTailer tailer = queuet.createTailer();
            // write first message
            try (ChronicleQueue queue = binary(dir).rollCycle(rollCycle).timeProvider(timeProvider).build()) {
                for (int i = 0; i < 5; i++) {
                    final ExcerptAppender appender = queue.acquireAppender();
                    timeProvider.advanceMillis(1100);
                    try (final DocumentContext dc = appender.writingDocument()) {
                        dc.wire().write("some").int32(i);
                    }
                    try (final DocumentContext dc = tailer.readingDocument()) {
                        Assert.assertEquals(i, dc.wire().read("some").int32());
                    }
                }
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCountExceptsWithRubbishData() {
        try (final RollingChronicleQueue queue = binary(getTmpDir()).rollCycle(RollCycles.TEST_SECONDLY).build()) {
            // rubbish data
            queue.countExcerpts(6309354155219615744L, 5949066185029976064L);
        }
    }

    // @Ignore("todo fix fails with enterprise queue")
    @Test
    public void testFromSizePrefixedBlobs() {
        try (final ChronicleQueue queue = binary(getTmpDir()).build()) {
            try (DocumentContext dc = queue.acquireAppender().writingDocument()) {
                dc.wire().write("some").text("data");
            }
            String s = null;
            DocumentContext dc0;
            try (DocumentContext dc = queue.createTailer().readingDocument()) {
                s = Wires.fromSizePrefixedBlobs(dc);
                if (!(encryption))
                    Assert.assertTrue(s.contains("some: data"));

                dc0 = dc;
            }
            String out = Wires.fromSizePrefixedBlobs(dc0);
            Assert.assertEquals(s, out);
        }
    }

    @Test
    public void tailerRollBackTest() {
        final File source = DirectoryUtils.tempDir("testCopyQueue-source");
        try (final ChronicleQueue q = binary(source).build()) {
            try (DocumentContext dc = q.acquireAppender().writingDocument()) {
                dc.wire().write("hello").text("hello-world");
            }
            try (DocumentContext dc = q.acquireAppender().writingDocument()) {
                dc.wire().write("hello2").text("hello-world-2");
            }
        }
    }

    @Test
    public void testCopyQueue() {
        final File source = DirectoryUtils.tempDir("testCopyQueue-source");
        final File target = DirectoryUtils.tempDir("testCopyQueue-target");
        {
            try (final ChronicleQueue q = binary(source).build()) {
                ExcerptAppender excerptAppender = q.acquireAppender();
                excerptAppender.writeMessage(() -> "one", 1);
                excerptAppender.writeMessage(() -> "two", 2);
                excerptAppender.writeMessage(() -> "three", 3);
                excerptAppender.writeMessage(() -> "four", 4);
            }
        }
        {
            try (final ChronicleQueue s = binary(source).build();final ChronicleQueue t = binary(target).build()) {
                ExcerptTailer sourceTailer = s.createTailer();
                ExcerptAppender appender = t.acquireAppender();
                for (; ;) {
                    try (DocumentContext rdc = sourceTailer.readingDocument()) {
                        if (!(rdc.isPresent()))
                            break;

                        try (DocumentContext wdc = appender.writingDocument()) {
                            final Bytes<?> bytes = rdc.wire().bytes();
                            wdc.wire().bytes().write(bytes);
                        }
                    }
                }
            }
        }
    }

    /**
     * see https://github.com/OpenHFT/Chronicle-Queue/issues/299
     */
    @Test
    public void testIncorrectExcerptTailerReadsAfterSwitchingTailerDirection() {
        final ChronicleQueue queue = binary(getTmpDir()).rollCycle(RollCycles.TEST_SECONDLY).build();
        int value = 0;
        long cycle = 0;
        long startIndex = 0;
        for (int i = 0; i < 56; i++) {
            try (final DocumentContext dc = queue.acquireAppender().writingDocument()) {
                if (cycle == 0)
                    cycle = queue.rollCycle().toCycle(dc.index());

                final long index = dc.index();
                final long seq = queue.rollCycle().toSequenceNumber(index);
                if (seq == 52)
                    startIndex = dc.index();

                if (seq >= 52) {
                    final int v = value++;
                    dc.wire().write("value").int64(v);
                } else {
                    dc.wire().write("value").int64(0);
                }
            }
        }
        ExcerptTailer tailer = queue.createTailer();
        Assert.assertTrue(tailer.moveToIndex(startIndex));
        tailer = tailer.direction(FORWARD);
        Assert.assertEquals(0, action(tailer, queue.rollCycle()));
        Assert.assertEquals(1, action(tailer, queue.rollCycle()));
        tailer = tailer.direction(BACKWARD);
        Assert.assertEquals(2, action(tailer, queue.rollCycle()));
        Assert.assertEquals(1, action(tailer, queue.rollCycle()));
        tailer = tailer.direction(FORWARD);
        Assert.assertEquals(0, action(tailer, queue.rollCycle()));
        Assert.assertEquals(1, action(tailer, queue.rollCycle()));
    }

    @Test
    public void checkReferenceCountingAndCheckFileDeletion() {
        MappedFile mappedFile;
        try (ChronicleQueue queue = binary(getTmpDir()).rollCycle(RollCycles.TEST_SECONDLY).build()) {
            ExcerptAppender appender = queue.acquireAppender();
            try (DocumentContext documentContext1 = appender.writingDocument()) {
                documentContext1.wire().write().text("some text");
            }
            try (DocumentContext documentContext = queue.createTailer().readingDocument()) {
                mappedFile = toMappedFile(documentContext);
                Assert.assertEquals("some text", documentContext.wire().read().text());
            }
        }
        SingleChronicleQueueTest.waitFor(mappedFile::isClosed, "mappedFile is not closed");
        if (OS.isWindows()) {
            System.err.println("#460 Cannot test delete after close on windows");
            return;
        }
        // this used to fail on windows
        Assert.assertTrue(mappedFile.file().delete());
    }

    @Test
    public void checkReferenceCountingWhenRollingAndCheckFileDeletion() {
        SetTimeProvider timeProvider = new SetTimeProvider();
        @SuppressWarnings("unused")
        MappedFile mappedFile1;
        @SuppressWarnings("unused")
        MappedFile mappedFile2;
        @SuppressWarnings("unused")
        MappedFile mappedFile3;
        @SuppressWarnings("unused")
        MappedFile mappedFile4;
        try (ChronicleQueue queue = binary(getTmpDir()).rollCycle(RollCycles.TEST_SECONDLY).timeProvider(timeProvider).build()) {
            ExcerptAppender appender = queue.acquireAppender();
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write().text("some text");
                mappedFile1 = toMappedFile(dc);
            }
            timeProvider.advanceMillis(1100);
            try (DocumentContext dc = appender.writingDocument()) {
                dc.wire().write().text("some more text");
                mappedFile2 = toMappedFile(dc);
            }
            ExcerptTailer tailer = queue.createTailer();
            try (DocumentContext documentContext = tailer.readingDocument()) {
                mappedFile3 = toMappedFile(documentContext);
                Assert.assertEquals("some text", documentContext.wire().read().text());
            }
            try (DocumentContext documentContext = tailer.readingDocument()) {
                mappedFile4 = toMappedFile(documentContext);
                Assert.assertEquals("some more text", documentContext.wire().read().text());
            }
        }
        SingleChronicleQueueTest.waitFor(mappedFile1::isClosed, "mappedFile1 is not closed");
        SingleChronicleQueueTest.waitFor(mappedFile2::isClosed, "mappedFile2 is not closed");
        if (OS.isWindows()) {
            System.err.println("#460 Cannot test delete after close on windows");
            return;
        }
        // this used to fail on windows
        Assert.assertTrue(mappedFile1.file().delete());
        Assert.assertTrue(mappedFile2.file().delete());
    }

    @Test
    public void testWritingDocumentIsAtomic() {
        final int threadCount = 8;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        // remove change of cycle roll in test, cross-cycle atomicity is covered elsewhere
        final AtomicLong fixedClock = new AtomicLong(System.currentTimeMillis());
        try {
            ChronicleQueue queue = ChronicleQueue.singleBuilder(getTmpDir()).rollCycle(RollCycles.TEST_SECONDLY).timeoutMS(3000).timeProvider(fixedClock::get).testBlockSize().build();
            final int iterationsPerThread = (Short.MAX_VALUE) / 8;
            final int totalIterations = iterationsPerThread * threadCount;
            final int[] nonAtomicCounter = new int[]{ 0 };
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        ExcerptAppender excerptAppender = queue.acquireAppender();
                        try (DocumentContext dc = excerptAppender.writingDocument()) {
                            int value = (nonAtomicCounter[0])++;
                            dc.wire().write("some key").int64(value);
                        }
                    }
                });
            }
            long timeout = 20000 + (System.currentTimeMillis());
            ExcerptTailer tailer = queue.createTailer();
            for (int expected = 0; expected < totalIterations; expected++) {
                for (; ;) {
                    if ((System.currentTimeMillis()) > timeout)
                        Assert.fail(((("Timed out, having read " + expected) + " documents of ") + totalIterations));

                    try (DocumentContext dc = tailer.readingDocument()) {
                        if (!(dc.isPresent())) {
                            Thread.yield();
                            continue;
                        }
                        long justRead = dc.wire().read("some key").int64();
                        Assert.assertEquals(expected, justRead);
                        break;
                    }
                }
            }
        } finally {
            executorService.shutdownNow();
            try {
                executorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    @Test
    public void shouldBeAbleToLoadQueueFromReadOnlyFiles() throws Exception {
        if (OS.isWindows()) {
            System.err.println("#460 Cannot test read only mode on windows");
            return;
        }
        final File queueDir = getTmpDir();
        try (final ChronicleQueue queue = builder(queueDir, wireType).testBlockSize().build()) {
            queue.acquireAppender().writeDocument("foo", ( v, t) -> {
                v.text(t);
            });
        }
        Files.list(queueDir.toPath()).filter(( p) -> p.endsWith(SUFFIX)).forEach(( p) -> Assert.assertTrue(p.toFile().setWritable(false)));
        try (final ChronicleQueue queue = builder(queueDir, wireType).readOnly(true).testBlockSize().build()) {
            Assert.assertTrue(queue.createTailer().readingDocument().isPresent());
        }
    }

    @Test
    public void shouldCreateQueueInCurrentDirectory() {
        if (OS.isWindows()) {
            System.err.println("#460 Cannot test delete after close on windows");
            return;
        }
        try (final ChronicleQueue ignored = builder(new File(""), wireType).testBlockSize().build()) {
        }
        Assert.assertThat(new File(SingleChronicleQueue.QUEUE_METADATA_FILE).delete(), CoreMatchers.is(true));
    }

    @Test
    public void writeBytesAndIndexFiveTimesWithOverwriteTest() {
        Assume.assumeFalse(Jvm.isArm());
        try (final ChronicleQueue sourceQueue = builder(DirectoryUtils.tempDir("to-be-deleted"), wireType).testBlockSize().build()) {
            for (int i = 0; i < 5; i++) {
                ExcerptAppender excerptAppender = sourceQueue.acquireAppender();
                try (DocumentContext dc = excerptAppender.writingDocument()) {
                    dc.wire().write("hello").text(("world" + i));
                }
            }
            ExcerptTailer tailer = sourceQueue.createTailer();
            try (final ChronicleQueue queue = builder(DirectoryUtils.tempDir("to-be-deleted"), wireType).testBlockSize().build()) {
                ExcerptAppender appender0 = queue.acquireAppender();
                if (!(appender0 instanceof InternalAppender))
                    return;

                InternalAppender appender = ((InternalAppender) (appender0));
                if (!(appender instanceof StoreAppender))
                    return;

                List<SingleChronicleQueueTest.BytesWithIndex> bytesWithIndies = new ArrayList<>();
                try {
                    for (int i = 0; i < 5; i++) {
                        bytesWithIndies.add(bytes(tailer));
                    }
                    for (int i = 0; i < 4; i++) {
                        SingleChronicleQueueTest.BytesWithIndex b = bytesWithIndies.get(i);
                        appender.writeBytes(b.index, b.bytes);
                    }
                    for (int i = 0; i < 4; i++) {
                        SingleChronicleQueueTest.BytesWithIndex b = bytesWithIndies.get(i);
                        appender.writeBytes(b.index, b.bytes);
                    }
                    SingleChronicleQueueTest.BytesWithIndex b = bytesWithIndies.get(4);
                    appender.writeBytes(b.index, b.bytes);
                    checkWritePositionHeaderNumber();
                    appender0.writeText("hello");
                } finally {
                    closeQuietly(bytesWithIndies);
                }
                Assert.assertTrue(queue.dump(), queue.dump().contains(("# position: 768, header: 0\n" + (((((((((((((((("--- !!data #binary\n" + "hello: world0\n") + "# position: 785, header: 1\n") + "--- !!data #binary\n") + "hello: world1\n") + "# position: 802, header: 2\n") + "--- !!data #binary\n") + "hello: world2\n") + "# position: 819, header: 3\n") + "--- !!data #binary\n") + "hello: world3\n") + "# position: 836, header: 4\n") + "--- !!data #binary\n") + "hello: world4\n") + "# position: 853, header: 5\n") + "--- !!data #binary\n") + "hello\n"))));
            }
        }
    }

    @Test
    public void writeBytesAndIndexFiveTimesTest() {
        Assume.assumeFalse(Jvm.isArm());
        try (final ChronicleQueue sourceQueue = builder(DirectoryUtils.tempDir("to-be-deleted"), wireType).testBlockSize().build()) {
            for (int i = 0; i < 5; i++) {
                ExcerptAppender excerptAppender = sourceQueue.acquireAppender();
                try (DocumentContext dc = excerptAppender.writingDocument()) {
                    dc.wire().write("hello").text(("world" + i));
                }
            }
            String before = sourceQueue.dump();
            ExcerptTailer tailer = sourceQueue.createTailer();
            try (final ChronicleQueue queue = builder(DirectoryUtils.tempDir("to-be-deleted"), wireType).testBlockSize().build()) {
                ExcerptAppender appender = queue.acquireAppender();
                if (!(appender instanceof StoreAppender))
                    return;

                for (int i = 0; i < 5; i++) {
                    try (final SingleChronicleQueueTest.BytesWithIndex b = bytes(tailer)) {
                        ((InternalAppender) (appender)).writeBytes(b.index, b.bytes);
                    }
                }
                String dump = queue.dump();
                Assert.assertEquals(before, dump);
                Assert.assertTrue(dump, dump.contains(("# position: 768, header: 0\n" + ((((((((((((("--- !!data #binary\n" + "hello: world0\n") + "# position: 785, header: 1\n") + "--- !!data #binary\n") + "hello: world1\n") + "# position: 802, header: 2\n") + "--- !!data #binary\n") + "hello: world2\n") + "# position: 819, header: 3\n") + "--- !!data #binary\n") + "hello: world3\n") + "# position: 836, header: 4\n") + "--- !!data #binary\n") + "hello: world4"))));
            }
        }
    }

    @Test
    public void rollbackTest() {
        File file = DirectoryUtils.tempDir("to-be-deleted");
        try (final ChronicleQueue sourceQueue = builder(file, wireType).testBlockSize().build()) {
            ExcerptAppender excerptAppender = sourceQueue.acquireAppender();
            try (DocumentContext dc = excerptAppender.writingDocument()) {
                dc.wire().write("hello").text("world1");
            }
            try (DocumentContext dc = excerptAppender.writingDocument()) {
                dc.wire().write("hello2").text("world2");
            }
            try (DocumentContext dc = excerptAppender.writingDocument()) {
                dc.wire().write("hello3").text("world3");
            }
        }
        try (final ChronicleQueue queue = builder(file, wireType).testBlockSize().build()) {
            ExcerptTailer tailer1 = queue.createTailer();
            StringBuilder sb = Wires.acquireStringBuilder();
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                documentContext.wire().readEventName(sb);
                Assert.assertEquals("hello", sb.toString());
                documentContext.rollbackOnClose();
            }
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                documentContext.wire().readEventName(sb);
                Assert.assertEquals("hello", sb.toString());
            }
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                documentContext.wire().readEventName(sb);
                documentContext.rollbackOnClose();
                Assert.assertEquals("hello2", sb.toString());
            }
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                Bytes<?> bytes = documentContext.wire().bytes();
                long rp = bytes.readPosition();
                long wp = bytes.writePosition();
                long wl = bytes.writeLimit();
                try {
                    documentContext.wire().readEventName(sb);
                    Assert.assertEquals("hello2", sb.toString());
                    documentContext.rollbackOnClose();
                } finally {
                    bytes.readPosition(rp).writePosition(wp).writeLimit(wl);
                }
            }
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                documentContext.wire().readEventName(sb);
                Assert.assertEquals("hello2", sb.toString());
            }
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                documentContext.wire().readEventName(sb);
                Assert.assertEquals("hello3", sb.toString());
                documentContext.rollbackOnClose();
            }
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                Assert.assertTrue(documentContext.isPresent());
                documentContext.wire().readEventName(sb);
                Assert.assertEquals("hello3", sb.toString());
            }
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                Assert.assertFalse(documentContext.isPresent());
                documentContext.rollbackOnClose();
            }
            try (DocumentContext documentContext = tailer1.readingDocument()) {
                Assert.assertFalse(documentContext.isPresent());
            }
        }
    }

    @Test
    public void mappedSegmentsShouldBeUnmappedAsCycleRolls() throws Exception {
        Assume.assumeTrue("this test is slow and does not depend on wire type", ((wireType) == (BINARY)));
        long now = System.currentTimeMillis();
        long ONE_HOUR_IN_MILLIS = (60 * 60) * 1000;
        long ONE_DAY_IN_MILLIS = ONE_HOUR_IN_MILLIS * 24;
        long midnight = now - (now % ONE_DAY_IN_MILLIS);
        AtomicLong clock = new AtomicLong(now);
        StringBuilder builder = new StringBuilder();
        boolean passed = doMappedSegmentUnmappedRollTest(clock, builder);
        passed = passed && (doMappedSegmentUnmappedRollTest(setTime(clock, midnight), builder));
        for (int i = 1; i < 24; i += 2)
            passed = passed && (doMappedSegmentUnmappedRollTest(setTime(clock, (midnight + (i * ONE_HOUR_IN_MILLIS))), builder));

        if (!passed) {
            Assert.fail(builder.toString());
        }
    }

    interface Msg {
        void msg(String s);
    }

    private static class MapWrapper extends AbstractMarshallable {
        final Map<CharSequence, Double> map = new HashMap<>();
    }

    static class MyMarshable extends AbstractMarshallable implements Demarshallable {
        @UsedViaReflection
        String name;

        @UsedViaReflection
        public MyMarshable(@NotNull
        WireIn wire) {
            readMarshallable(wire);
        }

        public MyMarshable() {
        }
    }

    private static class BytesWithIndex implements Closeable {
        private BytesStore bytes;

        private long index;

        public BytesWithIndex(Bytes<?> bytes, long index) {
            this.bytes = Bytes.allocateElasticDirect(bytes.readRemaining()).write(bytes);
            this.index = index;
        }

        @Override
        public void close() {
            bytes.release();
        }
    }
}

