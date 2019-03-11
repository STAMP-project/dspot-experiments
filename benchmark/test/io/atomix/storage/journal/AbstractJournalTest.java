/**
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.storage.journal;


import JournalReader.Mode.ALL;
import JournalReader.Mode.COMMITS;
import io.atomix.utils.serializer.Namespace;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Base journal test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@RunWith(Parameterized.class)
public abstract class AbstractJournalTest {
    private static final Namespace NAMESPACE = Namespace.builder().register(TestEntry.class).register(byte[].class).build();

    protected static final TestEntry ENTRY = new TestEntry(32);

    private static final Path PATH = Paths.get("target/test-logs/");

    private final int maxSegmentSize;

    private final int cacheSize;

    protected final int entriesPerSegment;

    protected AbstractJournalTest(int maxSegmentSize, int cacheSize) {
        this.maxSegmentSize = maxSegmentSize;
        this.cacheSize = cacheSize;
        int entryLength = (AbstractJournalTest.NAMESPACE.serialize(AbstractJournalTest.ENTRY).length) + 8;
        this.entriesPerSegment = (maxSegmentSize - 64) / entryLength;
    }

    @Test
    public void testCloseMultipleTimes() {
        // given
        final Journal<TestEntry> journal = createJournal();
        // when
        journal.close();
        // then
        journal.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWriteRead() throws Exception {
        try (Journal<TestEntry> journal = createJournal()) {
            JournalWriter<TestEntry> writer = journal.writer();
            JournalReader<TestEntry> reader = journal.openReader(1);
            // Append a couple entries.
            Indexed<TestEntry> indexed;
            Assert.assertEquals(1, writer.getNextIndex());
            indexed = writer.append(AbstractJournalTest.ENTRY);
            Assert.assertEquals(1, indexed.index());
            Assert.assertEquals(2, writer.getNextIndex());
            writer.append(new Indexed(2, AbstractJournalTest.ENTRY, 0));
            reader.reset(2);
            indexed = reader.next();
            Assert.assertEquals(2, indexed.index());
            Assert.assertFalse(reader.hasNext());
            // Test reading an entry
            Indexed<TestEntry> entry1;
            reader.reset();
            entry1 = ((Indexed) (reader.next()));
            Assert.assertEquals(1, entry1.index());
            Assert.assertEquals(entry1, reader.getCurrentEntry());
            Assert.assertEquals(1, reader.getCurrentIndex());
            // Test reading a second entry
            Indexed<TestEntry> entry2;
            Assert.assertTrue(reader.hasNext());
            Assert.assertEquals(2, reader.getNextIndex());
            entry2 = ((Indexed) (reader.next()));
            Assert.assertEquals(2, entry2.index());
            Assert.assertEquals(entry2, reader.getCurrentEntry());
            Assert.assertEquals(2, reader.getCurrentIndex());
            Assert.assertFalse(reader.hasNext());
            // Test opening a new reader and reading from the journal.
            reader = journal.openReader(1);
            Assert.assertTrue(reader.hasNext());
            entry1 = ((Indexed) (reader.next()));
            Assert.assertEquals(1, entry1.index());
            Assert.assertEquals(entry1, reader.getCurrentEntry());
            Assert.assertEquals(1, reader.getCurrentIndex());
            Assert.assertTrue(reader.hasNext());
            Assert.assertTrue(reader.hasNext());
            Assert.assertEquals(2, reader.getNextIndex());
            entry2 = ((Indexed) (reader.next()));
            Assert.assertEquals(2, entry2.index());
            Assert.assertEquals(entry2, reader.getCurrentEntry());
            Assert.assertEquals(2, reader.getCurrentIndex());
            Assert.assertFalse(reader.hasNext());
            // Reset the reader.
            reader.reset();
            // Test opening a new reader and reading from the journal.
            reader = journal.openReader(1);
            Assert.assertTrue(reader.hasNext());
            entry1 = ((Indexed) (reader.next()));
            Assert.assertEquals(1, entry1.index());
            Assert.assertEquals(entry1, reader.getCurrentEntry());
            Assert.assertEquals(1, reader.getCurrentIndex());
            Assert.assertTrue(reader.hasNext());
            Assert.assertTrue(reader.hasNext());
            Assert.assertEquals(2, reader.getNextIndex());
            entry2 = ((Indexed) (reader.next()));
            Assert.assertEquals(2, entry2.index());
            Assert.assertEquals(entry2, reader.getCurrentEntry());
            Assert.assertEquals(2, reader.getCurrentIndex());
            Assert.assertFalse(reader.hasNext());
            // Truncate the journal and write a different entry.
            writer.truncate(1);
            Assert.assertEquals(2, writer.getNextIndex());
            writer.append(new Indexed(2, AbstractJournalTest.ENTRY, 0));
            reader.reset(2);
            indexed = reader.next();
            Assert.assertEquals(2, indexed.index());
            // Reset the reader to a specific index and read the last entry again.
            reader.reset(2);
            Assert.assertNotNull(reader.getCurrentEntry());
            Assert.assertEquals(1, reader.getCurrentIndex());
            Assert.assertEquals(1, reader.getCurrentEntry().index());
            Assert.assertTrue(reader.hasNext());
            Assert.assertEquals(2, reader.getNextIndex());
            entry2 = ((Indexed) (reader.next()));
            Assert.assertEquals(2, entry2.index());
            Assert.assertEquals(entry2, reader.getCurrentEntry());
            Assert.assertEquals(2, reader.getCurrentIndex());
            Assert.assertFalse(reader.hasNext());
        }
    }

    @Test
    public void testResetTruncateZero() throws Exception {
        try (SegmentedJournal<TestEntry> journal = createJournal()) {
            JournalWriter<TestEntry> writer = journal.writer();
            JournalReader<TestEntry> reader = journal.openReader(1);
            Assert.assertEquals(0, writer.getLastIndex());
            writer.append(AbstractJournalTest.ENTRY);
            writer.append(AbstractJournalTest.ENTRY);
            writer.reset(1);
            Assert.assertEquals(0, writer.getLastIndex());
            writer.append(AbstractJournalTest.ENTRY);
            Assert.assertEquals(1, reader.next().index());
            writer.reset(1);
            Assert.assertEquals(0, writer.getLastIndex());
            writer.append(AbstractJournalTest.ENTRY);
            Assert.assertEquals(1, writer.getLastIndex());
            Assert.assertEquals(1, writer.getLastEntry().index());
            Assert.assertTrue(reader.hasNext());
            Assert.assertEquals(1, reader.next().index());
            writer.truncate(0);
            Assert.assertEquals(0, writer.getLastIndex());
            Assert.assertNull(writer.getLastEntry());
            writer.append(AbstractJournalTest.ENTRY);
            Assert.assertEquals(1, writer.getLastIndex());
            Assert.assertEquals(1, writer.getLastEntry().index());
            Assert.assertTrue(reader.hasNext());
            Assert.assertEquals(1, reader.next().index());
        }
    }

    @Test
    public void testTruncateRead() throws Exception {
        int i = 10;
        try (Journal<TestEntry> journal = createJournal()) {
            JournalWriter<TestEntry> writer = journal.writer();
            JournalReader<TestEntry> reader = journal.openReader(1);
            for (int j = 1; j <= i; j++) {
                Assert.assertEquals(j, writer.append(new TestEntry(32)).index());
            }
            for (int j = 1; j <= (i - 2); j++) {
                Assert.assertTrue(reader.hasNext());
                Assert.assertEquals(j, reader.next().index());
            }
            writer.truncate((i - 2));
            Assert.assertFalse(reader.hasNext());
            Assert.assertEquals((i - 1), writer.append(new TestEntry(32)).index());
            Assert.assertEquals(i, writer.append(new TestEntry(32)).index());
            Assert.assertTrue(reader.hasNext());
            Indexed<TestEntry> entry = reader.next();
            Assert.assertEquals((i - 1), entry.index());
            Assert.assertTrue(reader.hasNext());
            entry = reader.next();
            Assert.assertEquals(i, entry.index());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWriteReadEntries() throws Exception {
        try (Journal<TestEntry> journal = createJournal()) {
            JournalWriter<TestEntry> writer = journal.writer();
            JournalReader<TestEntry> reader = journal.openReader(1);
            for (int i = 1; i <= ((entriesPerSegment) * 5); i++) {
                writer.append(AbstractJournalTest.ENTRY);
                Assert.assertTrue(reader.hasNext());
                Indexed<TestEntry> entry;
                entry = ((Indexed) (reader.next()));
                Assert.assertEquals(i, entry.index());
                Assert.assertEquals(32, entry.entry().bytes().length);
                reader.reset(i);
                entry = ((Indexed) (reader.next()));
                Assert.assertEquals(i, entry.index());
                Assert.assertEquals(32, entry.entry().bytes().length);
                if (i > 6) {
                    reader.reset((i - 5));
                    Assert.assertNotNull(reader.getCurrentEntry());
                    Assert.assertEquals((i - 6), reader.getCurrentIndex());
                    Assert.assertEquals((i - 6), reader.getCurrentEntry().index());
                    Assert.assertEquals((i - 5), reader.getNextIndex());
                    reader.reset((i + 1));
                }
                writer.truncate((i - 1));
                writer.append(AbstractJournalTest.ENTRY);
                Assert.assertTrue(reader.hasNext());
                reader.reset(i);
                Assert.assertTrue(reader.hasNext());
                entry = ((Indexed) (reader.next()));
                Assert.assertEquals(i, entry.index());
                Assert.assertEquals(32, entry.entry().bytes().length);
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWriteReadCommittedEntries() throws Exception {
        try (Journal<TestEntry> journal = createJournal()) {
            JournalWriter<TestEntry> writer = journal.writer();
            JournalReader<TestEntry> reader = journal.openReader(1, COMMITS);
            for (int i = 1; i <= ((entriesPerSegment) * 5); i++) {
                writer.append(AbstractJournalTest.ENTRY);
                Assert.assertFalse(reader.hasNext());
                writer.commit(i);
                Assert.assertTrue(reader.hasNext());
                Indexed<TestEntry> entry;
                entry = ((Indexed) (reader.next()));
                Assert.assertEquals(i, entry.index());
                Assert.assertEquals(32, entry.entry().bytes().length);
                reader.reset(i);
                entry = ((Indexed) (reader.next()));
                Assert.assertEquals(i, entry.index());
                Assert.assertEquals(32, entry.entry().bytes().length);
            }
        }
    }

    @Test
    public void testReadAfterCompact() throws Exception {
        try (SegmentedJournal<TestEntry> journal = createJournal()) {
            JournalWriter<TestEntry> writer = journal.writer();
            JournalReader<TestEntry> uncommittedReader = journal.openReader(1, ALL);
            JournalReader<TestEntry> committedReader = journal.openReader(1, COMMITS);
            for (int i = 1; i <= ((entriesPerSegment) * 10); i++) {
                Assert.assertEquals(i, writer.append(AbstractJournalTest.ENTRY).index());
            }
            Assert.assertEquals(1, uncommittedReader.getNextIndex());
            Assert.assertTrue(uncommittedReader.hasNext());
            Assert.assertEquals(1, committedReader.getNextIndex());
            Assert.assertFalse(committedReader.hasNext());
            writer.commit(((entriesPerSegment) * 9));
            Assert.assertTrue(uncommittedReader.hasNext());
            Assert.assertTrue(committedReader.hasNext());
            for (int i = 1; i <= ((entriesPerSegment) * 2.5); i++) {
                Assert.assertEquals(i, uncommittedReader.next().index());
                Assert.assertEquals(i, committedReader.next().index());
            }
            journal.compact((((entriesPerSegment) * 5) + 1));
            Assert.assertNull(uncommittedReader.getCurrentEntry());
            Assert.assertEquals(0, uncommittedReader.getCurrentIndex());
            Assert.assertTrue(uncommittedReader.hasNext());
            Assert.assertEquals((((entriesPerSegment) * 5) + 1), uncommittedReader.getNextIndex());
            Assert.assertEquals((((entriesPerSegment) * 5) + 1), uncommittedReader.next().index());
            Assert.assertNull(committedReader.getCurrentEntry());
            Assert.assertEquals(0, committedReader.getCurrentIndex());
            Assert.assertTrue(committedReader.hasNext());
            Assert.assertEquals((((entriesPerSegment) * 5) + 1), committedReader.getNextIndex());
            Assert.assertEquals((((entriesPerSegment) * 5) + 1), committedReader.next().index());
        }
    }
}

