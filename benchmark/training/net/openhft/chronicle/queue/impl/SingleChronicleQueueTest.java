/**
 * Copyright 2015 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.queue.impl;


import java.io.File;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.Wire;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SingleChronicleQueueTest {
    private final Class<? extends Wire> wireType;

    // *************************************************************************
    // 
    // *************************************************************************
    public SingleChronicleQueueTest(final Class<? extends Wire> wireType) {
        this.wireType = wireType;
    }

    // *************************************************************************
    // 
    // *************************************************************************
    @Test
    public void testSingleWire() {
        final File file = createTempFile("testSingleWire");
        try {
            final ChronicleQueue chronicle = createQueue(file);
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( wire) -> wire.write(() -> "FirstName").text("Steve"));
            appender.writeDocument(( wire) -> wire.write(() -> "Surname").text("Jobs"));
            StringBuilder first = new StringBuilder();
            StringBuilder surname = new StringBuilder();
            final ExcerptTailer tailer = chronicle.createTailer();
            tailer.readDocument(( wire) -> wire.read(() -> "FirstName").text(first));
            tailer.readDocument(( wire) -> wire.read(() -> "Surname").text(surname));
            Assert.assertEquals("Steve Jobs", ((first + " ") + surname));
        } finally {
            file.delete();
        }
    }

    @Test
    public void testSingleDirect() {
        final File file = createTempFile("testSingleDirect");
        try {
            final DirectChronicleQueue chronicle = createQueue(file);
            final ExcerptAppender appender = chronicle.acquireAppender();
            // create 100 documents
            for (int i = 0; i < 100; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
            }
            final ExcerptTailer tailer = chronicle.createTailer();
            final StringBuilder sb = new StringBuilder();
            for (int j = 0; j < (chronicle.lastIndex()); j++) {
                sb.setLength(0);
                tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
                Assert.assertEquals(("value=" + j), sb.toString());
            }
        } finally {
            file.delete();
        }
    }

    @Test
    public void testLastWrittenIndexPerAppender() {
        final File file = createTempFile("testLastWrittenIndexPerAppender");
        try {
            final ChronicleQueue chronicle = createQueue(file);
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( wire) -> wire.write(() -> "key").text("test"));
            Assert.assertEquals(0, appender.lastWrittenIndex());
        } finally {
            file.delete();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testLastWrittenIndexPerAppenderNoData() {
        final File file = createTempFile("testLastWrittenIndexPerAppenderNoData");
        try {
            final ChronicleQueue chronicle = createQueue(file);
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.lastWrittenIndex();
        } finally {
            file.delete();
        }
    }

    @Test
    public void testLastIndexPerChronicle() {
        final File file = createTempFile("testLastIndexPerChronicle");
        try {
            final DirectChronicleQueue chronicle = createQueue(file);
            final ExcerptAppender appender = chronicle.acquireAppender();
            appender.writeDocument(( wire) -> wire.write(() -> "key").text("test"));
            Assert.assertEquals(0, chronicle.lastIndex());
        } finally {
            file.delete();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testLastIndexPerChronicleNoData() {
        final File file = createTempFile("testLastIndexPerChronicleNoData");
        try {
            final DirectChronicleQueue chronicle = createQueue(file);
            Assert.assertEquals((-1), chronicle.lastIndex());
        } finally {
            file.delete();
        }
    }

    @Test
    public void testReadAtIndexSingle() {
        final File file = createTempFile("testReadAtIndexSingle");
        try {
            final DirectChronicleQueue chronicle = createQueue(file);
            final ExcerptAppender appender = chronicle.acquireAppender();
            // create 100 documents
            for (int i = 0; i < 100; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
            }
            final ExcerptTailer tailer = chronicle.createTailer();
            tailer.index(5);
            StringBuilder sb = new StringBuilder();
            tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
            Assert.assertEquals("value=5", sb.toString());
        } finally {
            file.delete();
        }
    }

    @Test
    public void testReadAtIndex() {
        final File file = createTempFile("testReadAtIndex");
        try {
            final DirectChronicleQueue chronicle = createQueue(file);
            final ExcerptAppender appender = chronicle.acquireAppender();
            // create 100 documents
            for (int i = 0; i < 100; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
            }
            final ExcerptTailer tailer = chronicle.createTailer();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                tailer.index(i);
                sb.setLength(0);
                tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
                Assert.assertEquals(("value=" + i), sb.toString());
            }
        } finally {
            file.delete();
        }
    }

    @Test
    public void testReadAtIndexWithIndexes() {
        final File file = createTempFile("testReadAtIndexWithIndexes");
        try {
            final SingleChronicleQueue chronicle = ((SingleChronicleQueue) (createQueue(file)));
            final ExcerptAppender appender = chronicle.acquireAppender();
            // create 100 documents
            for (int i = 0; i < 100; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
            }
            // creates the indexes
            index();
            final ExcerptTailer tailer = chronicle.createTailer();
            tailer.index(67);
            StringBuilder sb = new StringBuilder();
            tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
            Assert.assertEquals("value=67", sb.toString());
        } finally {
            file.delete();
        }
    }

    @Test
    public void testReadAtIndexWithIndexesAtStart() {
        final File file = createTempFile("testReadAtIndexWithIndexesAtStart");
        try {
            final SingleChronicleQueue chronicle = ((SingleChronicleQueue) (createQueue(file)));
            final ExcerptAppender appender = chronicle.acquireAppender();
            // create 100 documents
            for (int i = 0; i < 100; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
            }
            index();
            long index = 67;
            final ExcerptTailer tailer = chronicle.createTailer();
            tailer.index(index);
            StringBuilder sb = new StringBuilder();
            tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
            Assert.assertEquals(("value=" + index), sb.toString());
        } finally {
            file.delete();
        }
    }

    @Test
    public void testScanFromLastKnownIndex() {
        final File file = createTempFile("testScanFromLastKnownIndex");
        try {
            final SingleChronicleQueue chronicle = ((SingleChronicleQueue) (createQueue(file)));
            final ExcerptAppender appender = chronicle.acquireAppender();
            // create 100 documents
            for (int i = 0; i < 65; i++) {
                final int j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
            }
            // creates the indexes - index's 1 and 2 are created by the indexer
            index();
            // create 100 documents
            for (long i = (chronicle.lastIndex()) + 1; i < 200; i++) {
                final long j = i;
                appender.writeDocument(( wire) -> wire.write(() -> "key").text(("value=" + j)));
            }
            final ExcerptTailer tailer = chronicle.createTailer();
            {
                int expected = 150;
                tailer.index(expected);
                StringBuilder sb = new StringBuilder();
                tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
                Assert.assertEquals(("value=" + expected), sb.toString());
            }
            // read back earlier
            {
                int expected = 167;
                tailer.index(expected);
                StringBuilder sb = new StringBuilder();
                tailer.readDocument(( wire) -> wire.read(() -> "key").text(sb));
                Assert.assertEquals(("value=" + expected), sb.toString());
            }
        } finally {
            file.delete();
        }
    }
}

