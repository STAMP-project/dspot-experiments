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


import RollCycles.TEST_DAILY;
import java.io.File;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.bytes.ref.BinaryLongArrayReference;
import net.openhft.chronicle.bytes.ref.BinaryLongReference;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Marshallable;
import net.openhft.chronicle.wire.WireOut;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class NotCompleteTest {
    /**
     * tests that when flags are set to not complete we are able to recover
     */
    @Test
    public void testUsingANotCompleteQueue() throws InterruptedException {
        BinaryLongReference.startCollecting();
        File tmpDir = DirectoryUtils.tempDir("testUsingANotCompleteQueue");
        try {
            try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().rollCycle(TEST_DAILY).build()) {
                ExcerptAppender appender = queue.acquireAppender();
                try (DocumentContext dc = appender.writingDocument()) {
                    dc.wire().write("some").text("data");
                }
                Thread.sleep(100);
                // System.out.println(queue.dump());
                // this is what will corrupt the queue
                BinaryLongReference.forceAllToNotCompleteState();
            }
            try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().timeoutMS(500).build()) {
                // System.out.println(queue.dump());
                ExcerptTailer tailer = queue.createTailer();
                try (DocumentContext dc = tailer.readingDocument()) {
                    Assert.assertEquals("data", dc.wire().read(() -> "some").text());
                }
            }
        } finally {
            IOTools.deleteDirWithFiles(tmpDir, 20);
        }
    }

    @Test
    public void testUsingANotCompleteArrayQueue() throws InterruptedException {
        BinaryLongArrayReference.startCollecting();
        File tmpDir = DirectoryUtils.tempDir("testUsingANotCompleteArrayQueue");
        try {
            try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().rollCycle(TEST_DAILY).build()) {
                ExcerptAppender appender = queue.acquireAppender();
                try (DocumentContext dc = appender.writingDocument()) {
                    dc.wire().write("some").text("data");
                }
                Thread.sleep(100);
                // System.out.println(queue.dump());
                // this is what will corrupt the queue
                BinaryLongArrayReference.forceAllToNotCompleteState();
            }
            try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().timeoutMS(500).build()) {
                // System.out.println(queue.dump());
                ExcerptTailer tailer = queue.createTailer();
                try (DocumentContext dc = tailer.readingDocument()) {
                    Assert.assertEquals("data", dc.wire().read(() -> "some").text());
                }
            }
        } finally {
            IOTools.deleteDirWithFiles(tmpDir, 20);
        }
    }

    @Test
    public void testMessageNotLeftIncomplete() {
        File tmpDir = DirectoryUtils.tempDir("testMessageNotLeftIncomplete");
        try {
            try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().rollCycle(TEST_DAILY).build()) {
                ExcerptAppender appender = queue.acquireAppender();
                // start a message which was not completed.
                DocumentContext dc = appender.writingDocument();
                dc.wire().write("some").text("data");
                // didn't call dc.close();
            }
            try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().build()) {
                ExcerptTailer tailer = queue.createTailer();
                try (DocumentContext dc = tailer.readingDocument()) {
                    Assert.assertFalse(dc.isPresent());
                }
                String expectedEager = "--- !!meta-data #binary\n" + (((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    0,\n") + "    0\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 8,\n") + "    indexSpacing: 1,\n") + "    index2Index: 184,\n") + "    lastIndex: 0\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 8, used: 1\n") + "  288,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 288, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 8, used: 0\n") + "  0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "...\n") + "# 327292 bytes remaining\n");
                Assert.assertEquals(expectedEager, queue.dump());
            }
            try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().timeoutMS(500).build()) {
                ExcerptAppender appender = queue.acquireAppender();
                try (DocumentContext dc = appender.writingDocument()) {
                    dc.wire().write("some").text("data");
                }
                String expected = ("--- !!meta-data #binary\n" + ((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    384,\n") + "    1649267441664\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 8,\n") + "    indexSpacing: 1,\n") + "    index2Index: 184,\n") + "    lastIndex: 1\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 8, used: 1\n") + "  288,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 288, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 8, used: 1\n") + "  384,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 384, header: 0\n") + "--- !!data #binary\n") + "some: data\n") + "...\n")) + (Jvm.isArm() ? "# 327276 bytes remaining\n" : "# 327278 bytes remaining\n");
                Assert.assertEquals(expected, queue.dump());
            }
        } finally {
            try {
                IOTools.deleteDirWithFiles(tmpDir, 2);
            } catch (Exception e) {
                if ((e instanceof AccessDeniedException) && (OS.isWindows()))
                    return;

                throw e;
            }
        }
    }

    @Test
    public void testInterruptedDuringSerialisation() throws InterruptedException {
        final File tmpDir = DirectoryUtils.tempDir("testInterruptedDuringSerialisation");
        try {
            DirectoryUtils.deleteDir(tmpDir);
            tmpDir.mkdirs();
            final List<String> names = Collections.synchronizedList(new ArrayList<>());
            final NotCompleteTest.Person person1 = new NotCompleteTest.Person(40, "Terry");
            final NotCompleteTest.Person interrupter = new NotCompleteTest.Person(50, NotCompleteTest.Person.INTERRUPT);
            final NotCompleteTest.Person thrower = new NotCompleteTest.Person(80, NotCompleteTest.Person.THROW);
            final NotCompleteTest.Person person2 = new NotCompleteTest.Person(90, "Bert");
            try (final ChronicleQueue queueReader = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().rollCycle(TEST_DAILY).timeoutMS(500).build();final ChronicleQueue queueWriter = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().rollCycle(TEST_DAILY).build()) {
                ExcerptTailer tailer = queueReader.createTailer();
                MethodReader reader = tailer.methodReader(((NotCompleteTest.PersonListener) (( person) -> names.add(person.name))));
                final StringBuilder queueDumpBeforeInterruptedWrite = new StringBuilder();
                // set up
                doWrite(queueWriter, ( proxy, queue) -> {
                    proxy.accept(person1);
                    queueDumpBeforeInterruptedWrite.append(queue.dump());
                });
                String cleanedQueueDump = cleanQueueDump(queueDumpBeforeInterruptedWrite.toString());
                // start up writer thread
                Thread writerThread = new Thread(() -> doWrite(queueWriter, ( proxy, queue) -> {
                    // thread is interrupted during this
                    proxy.accept(interrupter);
                }));
                writerThread.start();
                writerThread.join();
                try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().rollCycle(TEST_DAILY).build()) {
                    String dump = cleanQueueDump(queue.dump());
                    Assert.assertEquals("queue should be unchanged by the interrupted write", cleanedQueueDump, dump);
                }
                // check only 1 written
                Assert.assertTrue(reader.readOne());
                Assert.assertEquals(1, names.size());
                Assert.assertEquals(person1.name, names.get(0));
                Assert.assertFalse(reader.readOne());
                // do a write that throws an exception
                doWrite(queueWriter, ( proxy, queue) -> {
                    try {
                        proxy.accept(thrower);
                    } catch ( npe) {
                        // ignore
                    }
                });
                try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().rollCycle(TEST_DAILY).build()) {
                    String dump = cleanQueueDump(queue.dump());
                    Assert.assertEquals("queue should be unchanged by the failed write", cleanedQueueDump, dump);
                    System.err.println(queue.dump());
                }
                // check nothing else written
                Assert.assertFalse(reader.readOne());
                // do an empty write
                ExcerptAppender appender = queueWriter.acquireAppender();
                DocumentContext wd = appender.writingDocument();
                wd.rollbackOnClose();
                wd.close();
                // check queue unchanged
                String dump = cleanQueueDump(queueWriter.dump());
                Assert.assertEquals("queue should be unchanged by the failed write", cleanedQueueDump, dump);
                // check nothing else written
                Assert.assertFalse(reader.readOne());
                // write another person to same queue in this thread
                doWrite(queueWriter, ( proxy, queue) -> proxy.accept(person2));
                Assert.assertTrue(reader.readOne());
                Assert.assertEquals(2, names.size());
                Assert.assertEquals(person2.name, names.get(1));
                Assert.assertFalse(reader.readOne());
            }
        } finally {
            try {
                IOTools.deleteDirWithFiles(tmpDir, 2);
            } catch (Exception e) {
                if ((e instanceof AccessDeniedException) && (OS.isWindows()))
                    return;

                throw e;
            }
        }
    }

    @Test
    public void testSkipSafeLengthOverBlock() {
        File tmpDir = DirectoryUtils.tempDir("testSkipSafeLengthOverBlock");
        try {
            // 3rd time will do it
            for (int i = 0; i < 8; i++) {
                try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().rollCycle(TEST_DAILY).timeoutMS(1).build()) {
                    ExcerptAppender appender = queue.acquireAppender();
                    // start a message which won't be completed.
                    DocumentContext dc = appender.writingDocument();
                    // 2nd and subsequent times we call this will invoke recovery
                    dc.wire().write("some").text("data");
                    // don't call dc.close();
                }
            }
            try (final ChronicleQueue queue = SingleChronicleQueueBuilder.binary(tmpDir).testBlockSize().build()) {
                ExcerptTailer tailer = queue.createTailer();
                try (DocumentContext dc = tailer.readingDocument()) {
                    Assert.assertFalse(dc.isPresent());
                }
            }
        } finally {
            IOTools.deleteDirWithFiles(tmpDir, 20);
        }
    }

    private interface PersonListener {
        void accept(NotCompleteTest.Person name);
    }

    private class Person implements Marshallable {
        static final String INTERRUPT = "Arthur";

        static final String THROW = "Thrower";

        final int age;

        final String name;

        Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        @Override
        public void writeMarshallable(@NotNull
        WireOut wire) {
            wire.write("age").int32(age);
            // interrupt half way through writing
            if (NotCompleteTest.Person.INTERRUPT.equals(name)) {
                Thread.currentThread().interrupt();
            } else
                if (NotCompleteTest.Person.THROW.equals(name)) {
                    throw new NullPointerException();
                } else {
                    wire.write("name").text(name);
                }

        }

        @Override
        public String toString() {
            return ((((("Person{" + "age=") + (age)) + ", name='") + (name)) + '\'') + '}';
        }
    }
}

