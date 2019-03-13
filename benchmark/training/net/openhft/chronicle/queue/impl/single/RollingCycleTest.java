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


import RollCycles.TEST_DAILY;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.ReadBytesMarshallable;
import net.openhft.chronicle.bytes.WriteBytesMarshallable;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;


public class RollingCycleTest {
    @Test
    public void testRollCycle() {
        SetTimeProvider stp = new SetTimeProvider();
        long start = (System.currentTimeMillis()) - (3 * 86400000);
        stp.currentTimeMillis(start);
        String basePath = ((OS.TARGET) + "/testRollCycle") + (System.nanoTime());
        try (final ChronicleQueue queue = SingleChronicleQueueBuilder.single(basePath).testBlockSize().timeoutMS(5).rollCycle(TEST_DAILY).timeProvider(stp).build()) {
            final ExcerptAppender appender = queue.acquireAppender();
            int numWritten = 0;
            for (int h = 0; h < 3; h++) {
                stp.currentTimeMillis((start + (TimeUnit.DAYS.toMillis(h))));
                for (int i = 0; i < 3; i++) {
                    appender.writeBytes(new RollingCycleTest.TestBytesMarshallable(i));
                    numWritten++;
                }
            }
            String expectedEagerFile1 = (Jvm.isArm()) ? "--- !!meta-data #binary\n" + ((((((((((((((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    480,\n") + "    2061584302082\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 8,\n") + "    indexSpacing: 1,\n") + "    index2Index: 184,\n") + "    lastIndex: 3\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 8, used: 1\n") + "  288,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 288, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 8, used: 3\n") + "  384,\n") + "  432,\n") + "  480,\n") + "  0, 0, 0, 0, 0\n") + "]\n") + "# position: 384, header: 0\n") + "--- !!data #binary\n") + "00000180             10 6e 61 6d  65 5f 2d 31 31 35 35 34     \u00b7nam e_-11554\n") + "00000190 38 34 35 37 36 7a cb 93  3d 38 51 d9 d4 f6 c9 2d 84576z\u00b7\u00b7 =8Q\u00b7\u00b7\u00b7\u00b7-\n") + "000001a0 a3 bd 70 39 9b b7 70 e9  8c 39 f0 1d 4f 8f 8f 8f \u00b7\u00b7p9\u00b7\u00b7p\u00b7 \u00b79\u00b7\u00b7O\u00b7\u00b7\u00b7\n") + "# position: 432, header: 1\n") + "--- !!data #binary\n") + "000001b0             10 6e 61 6d  65 5f 2d 31 31 35 35 38     \u00b7nam e_-11558\n") + "000001c0 36 39 33 32 35 6f 0e fb  68 d8 9c b8 19 fc cc 2c 69325o\u00b7\u00b7 h\u00b7\u00b7\u00b7\u00b7\u00b7\u00b7,\n") + "000001d0 35 92 f9 4d 68 e5 f1 2c  55 f0 b8 46 09 8f 8f 8f 5\u00b7\u00b7Mh\u00b7\u00b7, U\u00b7\u00b7F\u00b7\u00b7\u00b7\u00b7\n") + "# position: 480, header: 2\n") + "--- !!data #binary\n") + "000001e0             10 6e 61 6d  65 5f 2d 31 31 35 34 37     \u00b7nam e_-11547\n") + "000001f0 31 35 30 37 39 90 45 c5  e6 f7 b9 1a 4b ea c3 2f 15079\u00b7E\u00b7 \u00b7\u00b7\u00b7\u00b7K\u00b7\u00b7/\n") + "00000200 7f 17 5f 10 01 5c 6e 62  fc cc 5e cc da 8f 8f 8f \u00b7\u00b7_\u00b7\u00b7\\nb \u00b7\u00b7^\u00b7\u00b7\u00b7\u00b7\u00b7\n") + "# position: 528, header: 2 EOF\n") : "--- !!meta-data #binary\n" + (((((((((((((((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    474,\n") + "    2035814498306\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 8,\n") + "    indexSpacing: 1,\n") + "    index2Index: 184,\n") + "    lastIndex: 3\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 8, used: 1\n") + "  288,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 288, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 8, used: 3\n") + "  384,\n") + "  429,\n") + "  474,\n") + "  0, 0, 0, 0, 0\n") + "]\n") + "# position: 384, header: 0\n") + "--- !!data #binary\n") + "00000180             10 6e 61 6d  65 5f 2d 31 31 35 35 34     \u00b7nam e_-11554\n") + "00000190 38 34 35 37 36 7a cb 93  3d 38 51 d9 d4 f6 c9 2d 84576z\u00b7\u00b7 =8Q\u00b7\u00b7\u00b7\u00b7-\n") + "000001a0 a3 bd 70 39 9b b7 70 e9  8c 39 f0 1d 4f          \u00b7\u00b7p9\u00b7\u00b7p\u00b7 \u00b79\u00b7\u00b7O   \n") + "# position: 429, header: 1\n") + "--- !!data #binary\n") + "000001b0    10 6e 61 6d 65 5f 2d  31 31 35 35 38 36 39 33  \u00b7name_- 11558693\n") + "000001c0 32 35 6f 0e fb 68 d8 9c  b8 19 fc cc 2c 35 92 f9 25o\u00b7\u00b7h\u00b7\u00b7 \u00b7\u00b7\u00b7\u00b7,5\u00b7\u00b7\n") + "000001d0 4d 68 e5 f1 2c 55 f0 b8  46 09                   Mh\u00b7\u00b7,U\u00b7\u00b7 F\u00b7      \n") + "# position: 474, header: 2\n") + "--- !!data #binary\n") + "000001d0                                            10 6e                \u00b7n\n") + "000001e0 61 6d 65 5f 2d 31 31 35  34 37 31 35 30 37 39 90 ame_-115 4715079\u00b7\n") + "000001f0 45 c5 e6 f7 b9 1a 4b ea  c3 2f 7f 17 5f 10 01 5c E\u00b7\u00b7\u00b7\u00b7\u00b7K\u00b7 \u00b7/\u00b7\u00b7_\u00b7\u00b7\\\n") + "00000200 6e 62 fc cc 5e cc da                             nb\u00b7\u00b7^\u00b7\u00b7          \n") + "# position: 519, header: 2 EOF\n");
            String expectedEagerFile2 = (Jvm.isArm()) ? "--- !!meta-data #binary\n" + ((((((((((((((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    480,\n") + "    2061584302082\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 8,\n") + "    indexSpacing: 1,\n") + "    index2Index: 184,\n") + "    lastIndex: 3\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 8, used: 1\n") + "  288,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 288, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 8, used: 3\n") + "  384,\n") + "  432,\n") + "  480,\n") + "  0, 0, 0, 0, 0\n") + "]\n") + "# position: 384, header: 0\n") + "--- !!data #binary\n") + "00000180             10 6e 61 6d  65 5f 2d 31 31 35 35 34     \u00b7nam e_-11554\n") + "00000190 38 34 35 37 36 7a cb 93  3d 38 51 d9 d4 f6 c9 2d 84576z\u00b7\u00b7 =8Q\u00b7\u00b7\u00b7\u00b7-\n") + "000001a0 a3 bd 70 39 9b b7 70 e9  8c 39 f0 1d 4f 8f 8f 8f \u00b7\u00b7p9\u00b7\u00b7p\u00b7 \u00b79\u00b7\u00b7O\u00b7\u00b7\u00b7\n") + "# position: 432, header: 1\n") + "--- !!data #binary\n") + "000001b0             10 6e 61 6d  65 5f 2d 31 31 35 35 38     \u00b7nam e_-11558\n") + "000001c0 36 39 33 32 35 6f 0e fb  68 d8 9c b8 19 fc cc 2c 69325o\u00b7\u00b7 h\u00b7\u00b7\u00b7\u00b7\u00b7\u00b7,\n") + "000001d0 35 92 f9 4d 68 e5 f1 2c  55 f0 b8 46 09 8f 8f 8f 5\u00b7\u00b7Mh\u00b7\u00b7, U\u00b7\u00b7F\u00b7\u00b7\u00b7\u00b7\n") + "# position: 480, header: 2\n") + "--- !!data #binary\n") + "000001e0             10 6e 61 6d  65 5f 2d 31 31 35 34 37     \u00b7nam e_-11547\n") + "000001f0 31 35 30 37 39 90 45 c5  e6 f7 b9 1a 4b ea c3 2f 15079\u00b7E\u00b7 \u00b7\u00b7\u00b7\u00b7K\u00b7\u00b7/\n") + "00000200 7f 17 5f 10 01 5c 6e 62  fc cc 5e cc da 8f 8f 8f \u00b7\u00b7_\u00b7\u00b7\\nb \u00b7\u00b7^\u00b7\u00b7\u00b7\u00b7\u00b7\n") + "# position: 528, header: 2 EOF\n") : "--- !!meta-data #binary\n" + (((((((((((((((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    474,\n") + "    2035814498306\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 8,\n") + "    indexSpacing: 1,\n") + "    index2Index: 184,\n") + "    lastIndex: 3\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 8, used: 1\n") + "  288,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 288, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 8, used: 3\n") + "  384,\n") + "  429,\n") + "  474,\n") + "  0, 0, 0, 0, 0\n") + "]\n") + "# position: 384, header: 0\n") + "--- !!data #binary\n") + "00000180             10 6e 61 6d  65 5f 2d 31 31 35 35 34     \u00b7nam e_-11554\n") + "00000190 38 34 35 37 36 7a cb 93  3d 38 51 d9 d4 f6 c9 2d 84576z\u00b7\u00b7 =8Q\u00b7\u00b7\u00b7\u00b7-\n") + "000001a0 a3 bd 70 39 9b b7 70 e9  8c 39 f0 1d 4f          \u00b7\u00b7p9\u00b7\u00b7p\u00b7 \u00b79\u00b7\u00b7O   \n") + "# position: 429, header: 1\n") + "--- !!data #binary\n") + "000001b0    10 6e 61 6d 65 5f 2d  31 31 35 35 38 36 39 33  \u00b7name_- 11558693\n") + "000001c0 32 35 6f 0e fb 68 d8 9c  b8 19 fc cc 2c 35 92 f9 25o\u00b7\u00b7h\u00b7\u00b7 \u00b7\u00b7\u00b7\u00b7,5\u00b7\u00b7\n") + "000001d0 4d 68 e5 f1 2c 55 f0 b8  46 09                   Mh\u00b7\u00b7,U\u00b7\u00b7 F\u00b7      \n") + "# position: 474, header: 2\n") + "--- !!data #binary\n") + "000001d0                                            10 6e                \u00b7n\n") + "000001e0 61 6d 65 5f 2d 31 31 35  34 37 31 35 30 37 39 90 ame_-115 4715079\u00b7\n") + "000001f0 45 c5 e6 f7 b9 1a 4b ea  c3 2f 7f 17 5f 10 01 5c E\u00b7\u00b7\u00b7\u00b7\u00b7K\u00b7 \u00b7/\u00b7\u00b7_\u00b7\u00b7\\\n") + "00000200 6e 62 fc cc 5e cc da                             nb\u00b7\u00b7^\u00b7\u00b7          \n") + "# position: 519, header: 2 EOF\n");
            String expectedEagerFile3 = (Jvm.isArm()) ? "--- !!meta-data #binary\n" + (((((((((((((((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    480,\n") + "    2061584302082\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 8,\n") + "    indexSpacing: 1,\n") + "    index2Index: 184,\n") + "    lastIndex: 3\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 8, used: 1\n") + "  288,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 288, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 8, used: 3\n") + "  384,\n") + "  432,\n") + "  480,\n") + "  0, 0, 0, 0, 0\n") + "]\n") + "# position: 384, header: 0\n") + "--- !!data #binary\n") + "00000180             10 6e 61 6d  65 5f 2d 31 31 35 35 34     \u00b7nam e_-11554\n") + "00000190 38 34 35 37 36 7a cb 93  3d 38 51 d9 d4 f6 c9 2d 84576z\u00b7\u00b7 =8Q\u00b7\u00b7\u00b7\u00b7-\n") + "000001a0 a3 bd 70 39 9b b7 70 e9  8c 39 f0 1d 4f 8f 8f 8f \u00b7\u00b7p9\u00b7\u00b7p\u00b7 \u00b79\u00b7\u00b7O\u00b7\u00b7\u00b7\n") + "# position: 432, header: 1\n") + "--- !!data #binary\n") + "000001b0             10 6e 61 6d  65 5f 2d 31 31 35 35 38     \u00b7nam e_-11558\n") + "000001c0 36 39 33 32 35 6f 0e fb  68 d8 9c b8 19 fc cc 2c 69325o\u00b7\u00b7 h\u00b7\u00b7\u00b7\u00b7\u00b7\u00b7,\n") + "000001d0 35 92 f9 4d 68 e5 f1 2c  55 f0 b8 46 09 8f 8f 8f 5\u00b7\u00b7Mh\u00b7\u00b7, U\u00b7\u00b7F\u00b7\u00b7\u00b7\u00b7\n") + "# position: 480, header: 2\n") + "--- !!data #binary\n") + "000001e0             10 6e 61 6d  65 5f 2d 31 31 35 34 37     \u00b7nam e_-11547\n") + "000001f0 31 35 30 37 39 90 45 c5  e6 f7 b9 1a 4b ea c3 2f 15079\u00b7E\u00b7 \u00b7\u00b7\u00b7\u00b7K\u00b7\u00b7/\n") + "00000200 7f 17 5f 10 01 5c 6e 62  fc cc 5e cc da 8f 8f 8f \u00b7\u00b7_\u00b7\u00b7\\nb \u00b7\u00b7^\u00b7\u00b7\u00b7\u00b7\u00b7\n") + "...\n") + "# 130540 bytes remaining\n") : "--- !!meta-data #binary\n" + ((((((((((((((((((((((((((((((((((((((((((((("header: !SCQStore {\n" + "  writePosition: [\n") + "    474,\n") + "    2035814498306\n") + "  ],\n") + "  indexing: !SCQSIndexing {\n") + "    indexCount: 8,\n") + "    indexSpacing: 1,\n") + "    index2Index: 184,\n") + "    lastIndex: 3\n") + "  }\n") + "}\n") + "# position: 184, header: -1\n") + "--- !!meta-data #binary\n") + "index2index: [\n") + "  # length: 8, used: 1\n") + "  288,\n") + "  0, 0, 0, 0, 0, 0, 0\n") + "]\n") + "# position: 288, header: -1\n") + "--- !!meta-data #binary\n") + "index: [\n") + "  # length: 8, used: 3\n") + "  384,\n") + "  429,\n") + "  474,\n") + "  0, 0, 0, 0, 0\n") + "]\n") + "# position: 384, header: 0\n") + "--- !!data #binary\n") + "00000180             10 6e 61 6d  65 5f 2d 31 31 35 35 34     \u00b7nam e_-11554\n") + "00000190 38 34 35 37 36 7a cb 93  3d 38 51 d9 d4 f6 c9 2d 84576z\u00b7\u00b7 =8Q\u00b7\u00b7\u00b7\u00b7-\n") + "000001a0 a3 bd 70 39 9b b7 70 e9  8c 39 f0 1d 4f          \u00b7\u00b7p9\u00b7\u00b7p\u00b7 \u00b79\u00b7\u00b7O   \n") + "# position: 429, header: 1\n") + "--- !!data #binary\n") + "000001b0    10 6e 61 6d 65 5f 2d  31 31 35 35 38 36 39 33  \u00b7name_- 11558693\n") + "000001c0 32 35 6f 0e fb 68 d8 9c  b8 19 fc cc 2c 35 92 f9 25o\u00b7\u00b7h\u00b7\u00b7 \u00b7\u00b7\u00b7\u00b7,5\u00b7\u00b7\n") + "000001d0 4d 68 e5 f1 2c 55 f0 b8  46 09                   Mh\u00b7\u00b7,U\u00b7\u00b7 F\u00b7      \n") + "# position: 474, header: 2\n") + "--- !!data #binary\n") + "000001d0                                            10 6e                \u00b7n\n") + "000001e0 61 6d 65 5f 2d 31 31 35  34 37 31 35 30 37 39 90 ame_-115 4715079\u00b7\n") + "000001f0 45 c5 e6 f7 b9 1a 4b ea  c3 2f 7f 17 5f 10 01 5c E\u00b7\u00b7\u00b7\u00b7\u00b7K\u00b7 \u00b7/\u00b7\u00b7_\u00b7\u00b7\\\n") + "00000200 6e 62 fc cc 5e cc da                             nb\u00b7\u00b7^\u00b7\u00b7          \n") + "...\n") + "# 130549 bytes remaining\n");
            System.out.println((("Wrote: " + numWritten) + " messages"));
            long numRead = 0;
            final RollingCycleTest.TestBytesMarshallable reusableData = new RollingCycleTest.TestBytesMarshallable(0);
            final ExcerptTailer currentPosTailer = queue.createTailer().toStart();
            final ExcerptTailer endPosTailer = queue.createTailer().toEnd();
            while ((currentPosTailer.index()) < (endPosTailer.index())) {
                try {
                    Assert.assertTrue(currentPosTailer.readBytes(reusableData));
                } catch (AssertionError e) {
                    System.err.println(((((((((("Could not read data at index: " + numRead) + " ") + (Long.toHexString(currentPosTailer.cycle()))) + " ") + (Long.toHexString(currentPosTailer.index()))) + " ") + (e.getMessage())) + " ") + e));
                    throw e;
                }
                numRead++;
            } 
            Assert.assertFalse(currentPosTailer.readBytes(reusableData));
            System.out.println(((("Wrote " + numWritten) + " Read ") + numRead));
            Assert.assertTrue(queue.dump().contains(expectedEagerFile1));
            Assert.assertTrue(queue.dump().contains(expectedEagerFile2));
            Assert.assertTrue(queue.dump().contains(expectedEagerFile3));
            try {
                IOTools.deleteDirWithFiles(basePath, 2);
            } catch (IORuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private static class TestBytesMarshallable implements ReadBytesMarshallable , WriteBytesMarshallable {
        @Nullable
        String _name;

        long _value1;

        long _value2;

        long _value3;

        public TestBytesMarshallable(int i) {
            final Random rand = new Random(i);
            _name = "name_" + (rand.nextInt());
            _value1 = rand.nextLong();
            _value2 = rand.nextLong();
            _value3 = rand.nextLong();
        }

        @Override
        public void writeMarshallable(@NotNull
        BytesOut bytes) {
            bytes.writeUtf8(_name);
            bytes.writeLong(_value1);
            bytes.writeLong(_value2);
            bytes.writeLong(_value3);
        }

        @Override
        public void readMarshallable(@NotNull
        BytesIn bytes) throws IORuntimeException {
            _name = bytes.readUtf8();
            _value1 = bytes.readLong();
            _value2 = bytes.readLong();
            _value3 = bytes.readLong();
        }
    }
}

