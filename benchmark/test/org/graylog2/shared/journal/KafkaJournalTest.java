/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.shared.journal;


import DateTimeZone.UTC;
import Journal.Entry;
import Journal.JournalReadEntry;
import Lifecycle.RUNNING;
import Lifecycle.THROTTLED;
import com.codahale.metrics.MetricRegistry;
import com.github.joschi.jadconfig.util.Size;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import kafka.common.KafkaException;
import kafka.log.LogSegment;
import kafka.message.Message;
import kafka.message.MessageSet;
import kafka.utils.FileLock;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.graylog2.plugin.InstantMillisProvider;
import org.graylog2.plugin.ServerStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class KafkaJournalTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ServerStatus serverStatus;

    private ScheduledThreadPoolExecutor scheduler;

    private File journalDirectory;

    @Test
    public void writeAndRead() throws IOException {
        final Journal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, Size.megabytes(100L), Duration.standardHours(1), Size.megabytes(5L), Duration.standardHours(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
        final byte[] idBytes = "id".getBytes(StandardCharsets.UTF_8);
        final byte[] messageBytes = "message".getBytes(StandardCharsets.UTF_8);
        final long position = journal.write(idBytes, messageBytes);
        final List<Journal.JournalReadEntry> messages = journal.read(1);
        final Journal.JournalReadEntry firstMessage = Iterators.getOnlyElement(messages.iterator());
        Assert.assertEquals("message", new String(firstMessage.getPayload(), StandardCharsets.UTF_8));
    }

    @Test
    public void readAtLeastOne() throws Exception {
        final Journal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, Size.megabytes(100L), Duration.standardHours(1), Size.megabytes(5L), Duration.standardHours(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
        final byte[] idBytes = "id".getBytes(StandardCharsets.UTF_8);
        final byte[] messageBytes = "message1".getBytes(StandardCharsets.UTF_8);
        final long position = journal.write(idBytes, messageBytes);
        // Trying to read 0 should always read at least 1 entry.
        final List<Journal.JournalReadEntry> messages = journal.read(0);
        final Journal.JournalReadEntry firstMessage = Iterators.getOnlyElement(messages.iterator());
        Assert.assertEquals("message1", new String(firstMessage.getPayload(), StandardCharsets.UTF_8));
    }

    @Test
    public void maxSegmentSize() throws Exception {
        final Size segmentSize = Size.kilobytes(1L);
        final KafkaJournal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, segmentSize, Duration.standardHours(1), Size.kilobytes(10L), Duration.standardDays(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
        long size = 0L;
        long maxSize = segmentSize.toBytes();
        final List<Journal.Entry> list = Lists.newArrayList();
        while (size <= maxSize) {
            final byte[] idBytes = "the1-id".getBytes(StandardCharsets.UTF_8);
            final byte[] messageBytes = "the1-message".getBytes(StandardCharsets.UTF_8);
            size += (idBytes.length) + (messageBytes.length);
            list.add(journal.createEntry(idBytes, messageBytes));
        } 
        // Make sure all messages have been written
        assertThat(journal.write(list)).isEqualTo(((list.size()) - 1));
    }

    @Test
    public void maxMessageSize() throws Exception {
        final Size segmentSize = Size.kilobytes(1L);
        final KafkaJournal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, segmentSize, Duration.standardHours(1), Size.kilobytes(10L), Duration.standardDays(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
        long size = 0L;
        long maxSize = segmentSize.toBytes();
        final List<Journal.Entry> list = Lists.newArrayList();
        final String largeMessage1 = randomAlphanumeric(Ints.saturatedCast(((segmentSize.toBytes()) * 2)));
        list.add(journal.createEntry(randomAlphanumeric(6).getBytes(StandardCharsets.UTF_8), largeMessage1.getBytes(StandardCharsets.UTF_8)));
        final byte[] idBytes0 = randomAlphanumeric(6).getBytes(StandardCharsets.UTF_8);
        // Build a message that has exactly the max segment size
        final String largeMessage2 = randomAlphanumeric(Ints.saturatedCast(((((segmentSize.toBytes()) - (MessageSet.LogOverhead())) - (Message.MessageOverhead())) - (idBytes0.length))));
        list.add(journal.createEntry(idBytes0, largeMessage2.getBytes(StandardCharsets.UTF_8)));
        while (size <= maxSize) {
            final byte[] idBytes = randomAlphanumeric(6).getBytes(StandardCharsets.UTF_8);
            final byte[] messageBytes = "the-message".getBytes(StandardCharsets.UTF_8);
            size += (idBytes.length) + (messageBytes.length);
            list.add(journal.createEntry(idBytes, messageBytes));
        } 
        // Make sure all messages but the large one have been written
        assertThat(journal.write(list)).isEqualTo(((list.size()) - 2));
    }

    @Test
    public void segmentRotation() throws Exception {
        final Size segmentSize = Size.kilobytes(1L);
        final KafkaJournal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, segmentSize, Duration.standardHours(1), Size.kilobytes(10L), Duration.standardDays(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
        createBulkChunks(journal, segmentSize, 3);
        final File[] files = journalDirectory.listFiles();
        Assert.assertNotNull(files);
        Assert.assertTrue("there should be files in the journal directory", ((files.length) > 0));
        final File[] messageJournalDir = journalDirectory.listFiles(((FileFilter) (FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), FileFilterUtils.nameFileFilter("messagejournal-0")))));
        Assert.assertTrue(((messageJournalDir.length) == 1));
        final File[] logFiles = messageJournalDir[0].listFiles(((FileFilter) (FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".log")))));
        Assert.assertEquals("should have two journal segments", 3, logFiles.length);
    }

    @Test
    public void segmentSizeCleanup() throws Exception {
        final Size segmentSize = Size.kilobytes(1L);
        final KafkaJournal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, segmentSize, Duration.standardHours(1), Size.kilobytes(1L), Duration.standardDays(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
        final File messageJournalDir = new File(journalDirectory, "messagejournal-0");
        Assert.assertTrue(messageJournalDir.exists());
        // create enough chunks so that we exceed the maximum journal size we configured
        createBulkChunks(journal, segmentSize, 3);
        // make sure everything is on disk
        journal.flushDirtyLogs();
        Assert.assertEquals(3, countSegmentsInDir(messageJournalDir));
        final int cleanedLogs = journal.cleanupLogs();
        Assert.assertEquals(1, cleanedLogs);
        final int numberOfSegments = countSegmentsInDir(messageJournalDir);
        Assert.assertEquals(2, numberOfSegments);
    }

    @Test
    public void segmentAgeCleanup() throws Exception {
        final InstantMillisProvider clock = new InstantMillisProvider(DateTime.now(UTC));
        DateTimeUtils.setCurrentMillisProvider(clock);
        try {
            final Size segmentSize = Size.kilobytes(1L);
            final KafkaJournal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, segmentSize, Duration.standardHours(1), Size.kilobytes(10L), Duration.standardMinutes(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
            final File messageJournalDir = new File(journalDirectory, "messagejournal-0");
            Assert.assertTrue(messageJournalDir.exists());
            // we need to fix up the last modified times of the actual files.
            long[] lastModifiedTs = new long[2];
            // create two chunks, 30 seconds apart
            createBulkChunks(journal, segmentSize, 1);
            journal.flushDirtyLogs();
            lastModifiedTs[0] = clock.getMillis();
            clock.tick(Period.seconds(30));
            createBulkChunks(journal, segmentSize, 1);
            journal.flushDirtyLogs();
            lastModifiedTs[1] = clock.getMillis();
            int i = 0;
            for (final LogSegment segment : journal.getSegments()) {
                Assert.assertTrue((i < 2));
                segment.lastModified_$eq(lastModifiedTs[i]);
                i++;
            }
            int cleanedLogs = journal.cleanupLogs();
            Assert.assertEquals("no segments should've been cleaned", 0, cleanedLogs);
            Assert.assertEquals("two segments segment should remain", 2, countSegmentsInDir(messageJournalDir));
            // move clock beyond the retention period and clean again
            clock.tick(Period.seconds(120));
            cleanedLogs = journal.cleanupLogs();
            Assert.assertEquals("two segments should've been cleaned (only one will actually be removed...)", 2, cleanedLogs);
            Assert.assertEquals("one segment should remain", 1, countSegmentsInDir(messageJournalDir));
        } finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    @Test
    public void segmentCommittedCleanup() throws Exception {
        final Size segmentSize = Size.kilobytes(1L);
        final KafkaJournal journal = // never clean by size in this test
        new KafkaJournal(journalDirectory.toPath(), scheduler, segmentSize, Duration.standardHours(1), Size.petabytes(1L), Duration.standardDays(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
        final File messageJournalDir = new File(journalDirectory, "messagejournal-0");
        Assert.assertTrue(messageJournalDir.exists());
        final int bulkSize = createBulkChunks(journal, segmentSize, 3);
        // make sure everything is on disk
        journal.flushDirtyLogs();
        Assert.assertEquals(3, countSegmentsInDir(messageJournalDir));
        // we haven't committed any offsets, this should not touch anything.
        final int cleanedLogs = journal.cleanupLogs();
        Assert.assertEquals(0, cleanedLogs);
        final int numberOfSegments = countSegmentsInDir(messageJournalDir);
        Assert.assertEquals(3, numberOfSegments);
        // mark first half of first segment committed, should not clean anything
        journal.markJournalOffsetCommitted((bulkSize / 2));
        Assert.assertEquals("should not touch segments", 0, journal.cleanupLogs());
        Assert.assertEquals(3, countSegmentsInDir(messageJournalDir));
        journal.markJournalOffsetCommitted((bulkSize + 1));
        Assert.assertEquals("first segment should've been purged", 1, journal.cleanupLogs());
        Assert.assertEquals(2, countSegmentsInDir(messageJournalDir));
        journal.markJournalOffsetCommitted((bulkSize * 4));
        Assert.assertEquals("only purge one segment, not the active one", 1, journal.cleanupLogs());
        Assert.assertEquals(1, countSegmentsInDir(messageJournalDir));
    }

    @Test
    public void lockedJournalDir() throws Exception {
        // Grab the lock before starting the KafkaJournal.
        final File file = new File(journalDirectory, ".lock");
        Assume.assumeTrue(file.createNewFile());
        final FileLock fileLock = new FileLock(file);
        Assume.assumeTrue(fileLock.tryLock());
        try {
            new KafkaJournal(journalDirectory.toPath(), scheduler, Size.megabytes(100L), Duration.standardHours(1), Size.megabytes(5L), Duration.standardHours(1), 1000000, Duration.standardMinutes(1), 100, new MetricRegistry(), serverStatus);
            Assert.fail("Expected exception");
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class).hasMessageStartingWith("kafka.common.KafkaException: Failed to acquire lock on file .lock in").hasCauseExactlyInstanceOf(KafkaException.class);
        }
    }

    @Test
    public void serverStatusThrottledIfJournalUtilizationIsHigherThanThreshold() throws Exception {
        serverStatus.running();
        final Size segmentSize = Size.kilobytes(1L);
        final KafkaJournal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, segmentSize, Duration.standardSeconds(1L), Size.kilobytes(4L), Duration.standardHours(1L), 1000000, Duration.standardSeconds(1L), 90, new MetricRegistry(), serverStatus);
        createBulkChunks(journal, segmentSize, 4);
        journal.flushDirtyLogs();
        journal.cleanupLogs();
        assertThat(serverStatus.getLifecycle()).isEqualTo(THROTTLED);
    }

    @Test
    public void serverStatusUnthrottledIfJournalUtilizationIsLowerThanThreshold() throws Exception {
        serverStatus.throttle();
        final Size segmentSize = Size.kilobytes(1L);
        final KafkaJournal journal = new KafkaJournal(journalDirectory.toPath(), scheduler, segmentSize, Duration.standardSeconds(1L), Size.kilobytes(4L), Duration.standardHours(1L), 1000000, Duration.standardSeconds(1L), 90, new MetricRegistry(), serverStatus);
        journal.flushDirtyLogs();
        journal.cleanupLogs();
        assertThat(serverStatus.getLifecycle()).isEqualTo(RUNNING);
    }
}

