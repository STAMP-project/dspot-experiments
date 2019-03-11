package net.openhft.chronicle.queue.impl;


import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.threads.InvalidEventHandlerException;
import net.openhft.chronicle.queue.ChronicleQueueTestBase;
import net.openhft.chronicle.queue.DirectoryUtils;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.single.Pretoucher;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Assert;
import org.junit.Test;


public class RollingChronicleQueueTest extends ChronicleQueueTestBase {
    @Test
    public void testCountExcerptsWhenTheCycleIsRolled() {
        final AtomicLong time = new AtomicLong();
        File name = DirectoryUtils.tempDir("testCountExcerptsWhenTheCycleIsRolled");
        try (final RollingChronicleQueue q = SingleChronicleQueueBuilder.binary(name).testBlockSize().timeProvider(time::get).rollCycle(RollCycles.TEST2_DAILY).build()) {
            final ExcerptAppender appender = q.acquireAppender();
            time.set(0);
            appender.writeText("1. some  text");
            long start = appender.lastIndexAppended();
            appender.writeText("2. some more text");
            appender.writeText("3. some more text");
            time.set(TimeUnit.DAYS.toMillis(1));
            appender.writeText("4. some text - first cycle");
            time.set(TimeUnit.DAYS.toMillis(2));
            time.set(TimeUnit.DAYS.toMillis(3));// large gap to miss a cycle file

            time.set(TimeUnit.DAYS.toMillis(4));
            appender.writeText("5. some text - second cycle");
            appender.writeText("some more text");
            long end = appender.lastIndexAppended();
            String expectedEagerFirstFile = (Jvm.isArm()) ? "--- !!meta-data #binary\n" + (((((((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    556,\n") + "    2388001816578\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 16,\n") + "    indexSpacing: 2,\n") + "    index2Index: 184,\n") + "    lastIndex: 4\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 16, used: 1\n") + "  352,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 352, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 16, used: 2\n") + "  512,\n") + "  556,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 512, header: 0\n") + "--- !!data #binary\n") + "\"1. some  text\"\n") + "# position: 532, header: 1\n") + "--- !!data #binary\n") + "\"2. some more text\"\n") + "# position: 556, header: 2\n") + "--- !!data #binary\n") + "\"3. some more text\"\n") + "# position: 580, header: 2 EOF\n") : "--- !!meta-data #binary\n" + (((((((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    552,\n") + "    2370821947394\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 16,\n") + "    indexSpacing: 2,\n") + "    index2Index: 184,\n") + "    lastIndex: 4\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 16, used: 1\n") + "  352,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 352, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 16, used: 2\n") + "  512,\n") + "  552,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 512, header: 0\n") + "--- !!data #binary\n") + "\"1. some  text\"\n") + "# position: 530, header: 1\n") + "--- !!data #binary\n") + "\"2. some more text\"\n") + "# position: 552, header: 2\n") + "--- !!data #binary\n") + "\"3. some more text\"\n") + "# position: 576, header: 2 EOF\n");
            String expectedEagerSecondFile = (Jvm.isArm()) ? "--- !!meta-data #binary\n" + (((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    512,\n") + "    2199023255552\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 16,\n") + "    indexSpacing: 2,\n") + "    index2Index: 184,\n") + "    lastIndex: 2\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 16, used: 1\n") + "  352,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 352, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 16, used: 1\n") + "  512,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 512, header: 0\n") + "--- !!data #binary\n") + "\"4. some text - first cycle\"\n") + "# position: 544, header: 0 EOF\n") + "--- !!not-ready-meta-data!\n") + "...\n") + "# 0 bytes remaining\n") : "--- !!meta-data #binary\n" + ((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    512,\n") + "    2199023255552\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 16,\n") + "    indexSpacing: 2,\n") + "    index2Index: 184,\n") + "    lastIndex: 2\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 16, used: 1\n") + "  352,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 352, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 16, used: 1\n") + "  512,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 512, header: 0\n") + "--- !!data #binary\n") + "\"4. some text - first cycle\"\n") + "# position: 543, header: 0 EOF\n");
            String expectedEagerThirdFile = (Jvm.isArm()) ? "--- !!meta-data #binary\n" + ((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    544,\n") + "    2336462209025\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 16,\n") + "    indexSpacing: 2,\n") + "    index2Index: 184,\n") + "    lastIndex: 2\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 16, used: 1\n") + "  352,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 352, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 16, used: 1\n") + "  512,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 512, header: 0\n") + "--- !!data #binary\n") + "\"5. some text - second cycle\"\n") + "# position: 544, header: 1\n") + "--- !!data #binary\n") + "some more text\n") : "--- !!meta-data #binary\n" + (((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    544,\n") + "    2336462209025\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 16,\n") + "    indexSpacing: 2,\n") + "    index2Index: 184,\n") + "    lastIndex: 2\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 16, used: 1\n") + "  352,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 352, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 16, used: 1\n") + "  512,\n") + "  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 512, header: 0\n") + "--- !!data #binary\n") + "\"5. some text - second cycle\"\n") + "# position: 544, header: 1\n") + "--- !!data #binary\n") + "some more text\n") + "...\n");
            Assert.assertEquals(5, q.countExcerpts(start, end));
            Thread.yield();
            String dump = q.dump();
            Assert.assertTrue(dump.contains(expectedEagerFirstFile));
            Assert.assertTrue(dump.contains(expectedEagerSecondFile));
            Assert.assertTrue(dump.contains(expectedEagerThirdFile));
        }
    }

    @Test
    public void testTailingWithEmptyCycles() {
        testTailing(( p) -> {
            try {
                p.execute();
            } catch (InvalidEventHandlerException e) {
                e.printStackTrace();
            }
            return 1;
        });
    }

    @Test
    public void testTailingWithMissingCycles() {
        testTailing(( p) -> 0);
    }
}

