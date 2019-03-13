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


import ChronicleQueue.TEST_BLOCK_SIZE;
import RollCycles.HOURLY;
import RollCycles.TEST4_DAILY;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import net.openhft.chronicle.bytes.MappedBytes;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.core.threads.ThreadDump;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.RollingChronicleQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import static SingleChronicleQueue.SUFFIX;
import static WireType.BINARY;
import static Wires.META_DATA;
import static Wires.NOT_COMPLETE;


/* Created by Peter Lawrey on 05/03/2016. */
public class SingleCQFormatTest {
    static {
        SingleChronicleQueueBuilder.addAliases();
    }

    private ThreadDump threadDump;

    @Test
    public void testEmptyDirectory() {
        @NotNull
        File dir = new File(OS.TARGET, (((getClass().getSimpleName()) + "-") + (System.nanoTime())));
        dir.mkdir();
        @NotNull
        RollingChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir).testBlockSize().build();
        Assert.assertEquals(Integer.MAX_VALUE, queue.firstCycle());
        Assert.assertEquals(Long.MAX_VALUE, queue.firstIndex());
        Assert.assertEquals(Integer.MIN_VALUE, queue.lastCycle());
        queue.close();
        IOTools.shallowDeleteDirWithFiles(dir.getAbsolutePath());
    }

    @Test
    public void testInvalidFile() throws FileNotFoundException {
        @NotNull
        File dir = new File((((OS.TARGET) + "/deleteme-") + (System.nanoTime())));
        dir.mkdir();
        try (@NotNull
        MappedBytes bytes = MappedBytes.mappedBytes(new File(dir, ("19700102" + (SUFFIX))), (64 << 10))) {
            bytes.write8bit("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
            try (@NotNull
            RollingChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir).rollCycle(TEST4_DAILY).testBlockSize().build()) {
                Assert.assertEquals(1, queue.firstCycle());
                Assert.assertEquals(1, queue.lastCycle());
                try {
                    @NotNull
                    ExcerptTailer tailer = queue.createTailer();
                    tailer.toEnd();
                    Assert.fail();
                } catch (Exception e) {
                    Assert.assertEquals("java.io.StreamCorruptedException: Unexpected magic number 783f3c37", e.toString());
                }
            }
        }
        System.gc();
        try {
            IOTools.shallowDeleteDirWithFiles(dir.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNoHeader() throws IOException {
        @NotNull
        File dir = new File((((OS.TARGET) + "/deleteme-") + (System.nanoTime())));
        dir.mkdir();
        File file = new File(dir, ("19700101" + (SUFFIX)));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] bytes = new byte[1024];
            for (int i = 0; i < 128; i++) {
                fos.write(bytes);
            }
        }
        @NotNull
        ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir).rollCycle(TEST4_DAILY).testBlockSize().build();
        testQueue(queue);
        queue.close();
        try {
            IOTools.shallowDeleteDirWithFiles(dir.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(expected = TimeoutException.class)
    public void testDeadHeader() throws IOException {
        @NotNull
        File dir = DirectoryUtils.tempDir("testDeadHeader");
        dir.mkdirs();
        File file = new File(dir, ("19700101" + (SUFFIX)));
        file.createNewFile();
        @NotNull
        MappedBytes bytes = MappedBytes.mappedBytes(file, TEST_BLOCK_SIZE);
        bytes.writeInt(((NOT_COMPLETE) | (META_DATA)));
        bytes.release();
        @Nullable
        ChronicleQueue queue = null;
        try {
            queue = SingleChronicleQueueBuilder.binary(dir).timeoutMS(1000L).testBlockSize().blockSize(TEST_BLOCK_SIZE).build();
            testQueue(queue);
        } finally {
            Closeable.closeQuietly(queue);
            IOTools.shallowDeleteDirWithFiles(dir.getAbsolutePath());
        }
    }

    @Test
    public void testCompleteHeader() throws FileNotFoundException {
        @NotNull
        File dir = DirectoryUtils.tempDir("testCompleteHeader");
        dir.mkdirs();
        @NotNull
        MappedBytes bytes = MappedBytes.mappedBytes(new File(dir, ("19700101" + (SUFFIX))), ((ChronicleQueue.TEST_BLOCK_SIZE) * 2));
        @NotNull
        Wire wire = new BinaryWire(bytes);
        try (DocumentContext dc = wire.writingDocument(true)) {
            dc.wire().writeEventName(() -> "header").typePrefix(SingleChronicleQueueStore.class).marshallable(( w) -> {
                w.write(() -> "wireType").object(WireType.BINARY);
                w.write(() -> "writePosition").int64forBinding(0);
                w.write(() -> "roll").typedMarshallable(new SCQRoll(RollCycles.TEST4_DAILY, 0, null, null));
                w.write(() -> "indexing").typedMarshallable(new SCQIndexing(WireType.BINARY, 32, 4));
                w.write(() -> "lastAcknowledgedIndexReplicated").int64forBinding(0);
            });
        }
        Assert.assertEquals(("--- !!meta-data #binary\n" + ((((((((((((((("header: !SCQStore {\n" + "  wireType: !WireType BINARY,\n") + "  writePosition: 0,\n") + "  roll: !SCQSRoll {\n") + "    length: !int 86400000,\n") + "    format: yyyyMMdd,\n") + "    epoch: 0\n") + "  },\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 32,\n") + "    indexSpacing: 4,\n") + "    index2Index: 0,\n") + "    lastIndex: 0\n") + "  },\n") + "  lastAcknowledgedIndexReplicated: 0\n") + "}\n")), Wires.fromSizePrefixedBlobs(bytes.readPosition(0)));
        bytes.release();
        @NotNull
        ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir).rollCycle(TEST4_DAILY).testBlockSize().build();
        testQueue(queue);
        queue.close();
        try {
            IOTools.shallowDeleteDirWithFiles(dir.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCompleteHeader2() throws FileNotFoundException {
        @NotNull
        File dir = new File(OS.TARGET, (((getClass().getSimpleName()) + "-") + (System.nanoTime())));
        dir.mkdir();
        @NotNull
        MappedBytes bytes = MappedBytes.mappedBytes(new File(dir, ("19700101-02" + (SUFFIX))), ((ChronicleQueue.TEST_BLOCK_SIZE) * 2));
        @NotNull
        Wire wire = new BinaryWire(bytes);
        try (DocumentContext dc = wire.writingDocument(true)) {
            dc.wire().writeEventName(() -> "header").typedMarshallable(new SingleChronicleQueueStore(RollCycles.HOURLY, BINARY, bytes, (4 << 10), 4));
        }
        Assert.assertEquals(("--- !!meta-data #binary\n" + ((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    0,\n") + "    0\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: !short 4096,\n") + "    indexSpacing: 4,\n") + "    index2Index: 0,\n") + "    lastIndex: 0\n") + "  }\n") + "}\n")), Wires.fromSizePrefixedBlobs(bytes.readPosition(0)));
        bytes.release();
        @NotNull
        RollingChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir).testBlockSize().rollCycle(HOURLY).build();
        testQueue(queue);
        Assert.assertEquals(2, queue.firstCycle());
        queue.close();
        try {
            IOTools.shallowDeleteDirWithFiles(dir.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncompleteHeader() throws FileNotFoundException {
        @NotNull
        File dir = new File(OS.TARGET, (((getClass().getSimpleName()) + "-") + (System.nanoTime())));
        dir.mkdir();
        @NotNull
        MappedBytes bytes = MappedBytes.mappedBytes(new File(dir, ("19700101" + (SUFFIX))), TEST_BLOCK_SIZE);
        @NotNull
        Wire wire = new BinaryWire(bytes);
        try (DocumentContext dc = wire.writingDocument(true)) {
            dc.wire().writeEventName(() -> "header").typePrefix(SingleChronicleQueueStore.class).marshallable(( w) -> w.write(() -> "wireType").object(WireType.BINARY));
        }
        bytes.release();
        try (@NotNull
        ChronicleQueue queue = SingleChronicleQueueBuilder.binary(dir).rollCycle(TEST4_DAILY).blockSize(TEST_BLOCK_SIZE).build()) {
            testQueue(queue);
            Assert.fail();
        } catch (Exception e) {
            // e.printStackTrace();
            Assert.assertEquals("net.openhft.chronicle.core.io.IORuntimeException: net.openhft.chronicle.core.io.IORuntimeException: field writePosition required", e.toString());
        }
        System.gc();
        try {
            IOTools.shallowDeleteDirWithFiles(dir.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

