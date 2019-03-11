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
package net.openhft.chronicle.queue;


import java.io.File;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rob Austin.
 */
@RequiredForClient
public class LastIndexAppendedTest {
    @Test
    public void testLastIndexAppendedAcrossRestarts() {
        String path = ((((OS.TARGET) + "/") + (getClass().getSimpleName())) + "-") + (System.nanoTime());
        for (int i = 0; i < 5; i++) {
            try (ChronicleQueue queue = SingleChronicleQueueBuilder.single(path).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build()) {
                ExcerptAppender appender = queue.acquireAppender();
                try (DocumentContext documentContext = appender.writingDocument()) {
                    int index = ((int) (documentContext.index()));
                    Assert.assertEquals(i, index);
                    documentContext.wire().write().text("hello world");
                }
                Assert.assertEquals(i, ((int) (appender.lastIndexAppended())));
            }
        }
        try {
            IOTools.deleteDirWithFiles(path, 2);
        } catch (Exception index) {
        }
    }

    @Test
    public void testTwoAppenders() throws Exception {
        File path = DirectoryUtils.tempDir("testTwoAppenders");
        long a_index;
        try (ChronicleQueue appender_queue = SingleChronicleQueueBuilder.single(path).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build()) {
            ExcerptAppender appender = appender_queue.acquireAppender();
            for (int i = 0; i < 5; i++) {
                appender.writeDocument(( wireOut) -> wireOut.write("log").marshallable(( m) -> m.write("msg").text("hello world ")));
            }
            a_index = appender.lastIndexAppended();
        }
        ChronicleQueue tailer_queue = SingleChronicleQueueBuilder.single(path).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build();
        ExcerptTailer tailer = tailer_queue.createTailer();
        tailer = tailer.toStart();
        long t_index;
        t_index = doRead(tailer, 5);
        Assert.assertEquals(a_index, t_index);
        System.out.println("Continue appending");
        try (ChronicleQueue appender_queue = // .buffered(false)
        SingleChronicleQueueBuilder.single(path).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build()) {
            ExcerptAppender appender = appender_queue.acquireAppender();
            for (int i = 0; i < 5; i++) {
                appender.writeDocument(( wireOut) -> wireOut.write("log").marshallable(( m) -> m.write("msg").text("hello world2 ")));
            }
            a_index = appender.lastIndexAppended();
            Assert.assertTrue((a_index > t_index));
        }
        // if the tailer continues as well it should see the 5 new messages
        System.out.println("Reading messages added");
        t_index = doRead(tailer, 5);
        Assert.assertEquals(a_index, t_index);
        // if the tailer is expecting to read all the message again
        System.out.println("Reading all the messages again");
        tailer.toStart();
        t_index = doRead(tailer, 10);
        Assert.assertEquals(a_index, t_index);
        try {
            IOTools.deleteDirWithFiles(path, 2);
        } catch (Exception index) {
        }
    }
}

