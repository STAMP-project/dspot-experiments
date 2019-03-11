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


import RollCycles.HOURLY;
import RollCycles.TEST_SECONDLY;
import TailerDirection.BACKWARD;
import TailerDirection.FORWARD;
import TailerDirection.NONE;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Assert;
import org.junit.Test;


@RequiredForClient
public class TailerDirectionTest extends ChronicleQueueTestBase {
    private static final String TEST_MESSAGE_PREFIX = "Test entry: ";

    // 
    // Test procedure:
    // 1) Read in FORWARD direction 2 of the 4 entries
    // 2) Toggle Tailer direction and read in BACKWARD direction 2 entries
    // 3) Redo step 1)
    // 
    @Test
    public void testTailerForwardBackwardRead() throws Exception {
        String basePath = ((OS.TARGET) + "/tailerForwardBackward-") + (System.nanoTime());
        ChronicleQueue queue = ChronicleQueue.singleBuilder(basePath).testBlockSize().rollCycle(HOURLY).build();
        ExcerptAppender appender = queue.acquireAppender();
        ExcerptTailer tailer = queue.createTailer();
        // 
        // Prepare test messages in queue
        // 
        // Map of test messages with their queue index position
        HashMap<String, Long> msgIndexes = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            String msg = testMessage(i);
            long idx = appendEntry(appender, msg);
            msgIndexes.put(msg, idx);
        }
        Assert.assertEquals("[Forward 1] Wrong message 0", testMessage(0), readNextEntry(tailer));
        Assert.assertEquals("[Forward 1] Wrong Tailer index after reading msg 0", msgIndexes.get(testMessage(1)).longValue(), tailer.index());
        Assert.assertEquals("[Forward 1] Wrong message 1", testMessage(1), readNextEntry(tailer));
        Assert.assertEquals("[Forward 1] Wrong Tailer index after reading msg 1", msgIndexes.get(testMessage(2)).longValue(), tailer.index());
        tailer.direction(BACKWARD);
        Assert.assertEquals("[Backward] Wrong message 2", testMessage(2), readNextEntry(tailer));
        Assert.assertEquals("[Backward] Wrong Tailer index after reading msg 2", msgIndexes.get(testMessage(1)).longValue(), tailer.index());
        Assert.assertEquals("[Backward] Wrong message 1", testMessage(1), readNextEntry(tailer));
        Assert.assertEquals("[Backward] Wrong Tailer index after reading msg 1", msgIndexes.get(testMessage(0)).longValue(), tailer.index());
        Assert.assertEquals("[Backward] Wrong message 0", testMessage(0), readNextEntry(tailer));
        String res = readNextEntry(tailer);
        Assert.assertTrue(("Backward: res is" + res), (res == null));
        tailer.direction(FORWARD);
        res = readNextEntry(tailer);
        Assert.assertTrue(("Forward: res is" + res), (res == null));
        Assert.assertEquals("[Forward 2] Wrong message 0", testMessage(0), readNextEntry(tailer));
        Assert.assertEquals("[Forward 2] Wrong Tailer index after reading msg 0", msgIndexes.get(testMessage(1)).longValue(), tailer.index());
        Assert.assertEquals("[Forward 2] Wrong message 1", testMessage(1), readNextEntry(tailer));
        Assert.assertEquals("[Forward 2] Wrong Tailer index after reading msg 1", msgIndexes.get(testMessage(2)).longValue(), tailer.index());
        queue.close();
    }

    @Test
    public void uninitialisedTailerCreatedBeforeFirstAppendWithDirectionNoneShouldNotFindDocument() {
        final AtomicLong clock = new AtomicLong(System.currentTimeMillis());
        String path = ((((OS.TARGET) + "/") + (getClass().getSimpleName())) + "-") + (System.nanoTime());
        final ChronicleQueue queue = SingleChronicleQueueBuilder.single(path).timeProvider(clock::get).testBlockSize().rollCycle(TEST_SECONDLY).build();
        final ExcerptTailer tailer = queue.createTailer();
        tailer.direction(NONE);
        final ExcerptAppender excerptAppender = queue.acquireAppender();
        for (int i = 0; i < 10; i++) {
            excerptAppender.writeDocument(i, ( out, value) -> {
                out.int32(value);
            });
        }
        DocumentContext document = tailer.readingDocument();
        Assert.assertFalse(document.isPresent());
    }

    @Test
    public void testTailerBackwardsReadBeyondCycle() {
        File basePath = DirectoryUtils.tempDir("tailerForwardBackwardBeyondCycle");
        SetTimeProvider timeProvider = new SetTimeProvider();
        ChronicleQueue queue = ChronicleQueue.singleBuilder(basePath).testBlockSize().timeProvider(timeProvider).build();
        ExcerptAppender appender = queue.acquireAppender();
        // 
        // Prepare test messages in queue
        // 
        // List of test messages with their queue index position
        List<Long> indexes = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        for (int d = -7; d <= 0; d++) {
            if ((d == (-5)) || (d == (-4)))
                continue;
            // nothing on those days.

            timeProvider.currentTimeMillis(((System.currentTimeMillis()) + (TimeUnit.DAYS.toMillis(d))));
            for (int i = 0; i < 3; i++) {
                String msg = testMessage(indexes.size());
                long idx = appendEntry(appender, msg);
                messages.add(msg);
                indexes.add(idx);
            }
        }
        ExcerptTailer tailer = queue.createTailer().direction(BACKWARD).toEnd();
        for (int i = (indexes.size()) - 1; i >= 0; i--) {
            long index = indexes.get(i);
            String msg = messages.get(i);
            Assert.assertEquals(("[Backward] Wrong index " + i), index, tailer.index());
            Assert.assertEquals(("[Backward] Wrong message " + i), msg, readNextEntry(tailer));
        }
        queue.close();
    }
}

