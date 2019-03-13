/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master.journal.ufs;


import ExceptionMessage.JOURNAL_ENTRY_MISSING;
import PropertyKey.MASTER_JOURNAL_LOG_SIZE_BYTES_MAX;
import RuntimeConstants.ALLUXIO_DEBUG_DOCS_URL;
import UfsJournal.UNKNOWN_SEQUENCE_NUMBER;
import alluxio.conf.ServerConfiguration;
import alluxio.underfs.UnderFileSystem;
import alluxio.util.URIUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link UfsJournalLogWriter}.
 */
public final class UfsJournalLogWriterTest {
    private static final String INJECTED_IO_ERROR_MESSAGE = "injected I/O error";

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    private UfsJournal mJournal;

    private UnderFileSystem mUfs;

    /**
     * A new journal writer completes the current log.
     */
    @Test
    public void completeCurrentLog() throws Exception {
        long startSN = 16;
        long endSN = 32;
        close();
        close();
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        String expectedLog = URIUtils.appendPathOrDie(mJournal.getLogDir(), String.format("0x%x-0x%x", startSN, endSN)).toString();
        Assert.assertEquals(1, snapshot.getLogs().size());
        Assert.assertEquals(expectedLog, snapshot.getLogs().get(0).getLocation().toString());
        Assert.assertTrue(((UfsJournalSnapshot.getCurrentLog(mJournal)) == null));
    }

    /**
     * The completed log corresponds to the current log exists.
     */
    @Test
    public void duplicateCompletedLog() throws Exception {
        long startSN = 16;
        long endSN = 32;
        close();
        close();
        close();
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        String expectedLog = URIUtils.appendPathOrDie(mJournal.getLogDir(), String.format("0x%x-0x%x", startSN, endSN)).toString();
        Assert.assertEquals(1, snapshot.getLogs().size());
        Assert.assertEquals(expectedLog, snapshot.getLogs().get(0).getLocation().toString());
        Assert.assertTrue(((UfsJournalSnapshot.getCurrentLog(mJournal)) == null));
    }

    /**
     * Writes journal entries while the UFS has flush supported.
     */
    @Test
    public void writeJournalEntryUfsHasFlush() throws Exception {
        Mockito.when(mUfs.supportsFlush()).thenReturn(true);
        long nextSN = 32;
        UfsJournalLogWriter writer = new UfsJournalLogWriter(mJournal, nextSN);
        for (int i = 0; i < 10; i++) {
            writer.write(newEntry(nextSN));
            nextSN++;
            if ((i % 5) == 0) {
                writer.flush();
            }
        }
        writer.close();
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        Assert.assertTrue(snapshot.getCheckpoints().isEmpty());
        // 0x20 - 0x2a
        Assert.assertEquals(1, snapshot.getLogs().size());
        Assert.assertEquals(UfsJournalFile.encodeLogFileLocation(mJournal, 32, 42), snapshot.getLogs().get(0).getLocation());
    }

    /**
     * Writes journal entries while the UFS has no flush supported.
     */
    @Test
    public void writeJournalEntryUfsNoFlush() throws Exception {
        Mockito.when(mUfs.supportsFlush()).thenReturn(false);
        long nextSN = 32;
        UfsJournalLogWriter writer = new UfsJournalLogWriter(mJournal, nextSN);
        for (int i = 0; i < 10; i++) {
            writer.write(newEntry(nextSN));
            nextSN++;
            if ((i % 5) == 0) {
                writer.flush();
            }
        }
        writer.close();
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        Assert.assertTrue(snapshot.getCheckpoints().isEmpty());
        // 0x20 - 0x21, 0x21 - 0x26, 0x26 - 0x2a
        Assert.assertEquals(3, snapshot.getLogs().size());
        Assert.assertEquals(UfsJournalFile.encodeLogFileLocation(mJournal, 32, 33), snapshot.getLogs().get(0).getLocation());
        Assert.assertEquals(UfsJournalFile.encodeLogFileLocation(mJournal, 33, 38), snapshot.getLogs().get(1).getLocation());
        Assert.assertEquals(UfsJournalFile.encodeLogFileLocation(mJournal, 38, 42), snapshot.getLogs().get(2).getLocation());
    }

    /**
     * Tests journal rotation.
     */
    @Test
    public void writeJournalEntryRotate() throws Exception {
        Mockito.when(mUfs.supportsFlush()).thenReturn(true);
        ServerConfiguration.set(MASTER_JOURNAL_LOG_SIZE_BYTES_MAX, "1");
        long nextSN = 32;
        UfsJournalLogWriter writer = new UfsJournalLogWriter(mJournal, nextSN);
        for (int i = 0; i < 10; i++) {
            writer.write(newEntry(nextSN));
            nextSN++;
            writer.flush();
        }
        writer.close();
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        Assert.assertTrue(snapshot.getCheckpoints().isEmpty());
        Assert.assertEquals(10, snapshot.getLogs().size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(UfsJournalFile.encodeLogFileLocation(mJournal, (32 + i), (33 + i)), snapshot.getLogs().get(i).getLocation());
        }
    }

    /**
     * Tests that (1) {@link UfsJournalLogWriter#mJournalOutputStream} is reset when an exception
     * is thrown, and (2) the {@link UfsJournalLogWriter} recovers during the next write. It should
     * write out all entries, including the one that initially failed.
     */
    @Test
    public void recoverFromUfsFailure() throws Exception {
        long startSN = 16;
        UfsJournalLogWriter writer = new UfsJournalLogWriter(mJournal, startSN);
        final int numberOfEntriesWrittenBeforeFailure = 10;
        long nextSN = writeJournalEntries(writer, startSN, numberOfEntriesWrittenBeforeFailure);
        DataOutputStream badOut = createMockDataOutputStream(writer);
        Mockito.doThrow(new IOException(UfsJournalLogWriterTest.INJECTED_IO_ERROR_MESSAGE)).when(badOut).write(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
        tryWriteAndExpectToFail(writer, nextSN);
        // UfsJournalLogWriter will perform recovery before the write operation.
        writer.write(newEntry(nextSN));
        nextSN++;
        writer.close();
        checkJournalEntries(startSN, nextSN);
    }

    /**
     * Tests that {@link UfsJournalLogWriter#flush()} can recover after Ufs failure.
     *
     * This test has the following steps.
     * 1. Write several journal entries, flush and close the journal. This will create a complete
     *    journal file, i.e. <startSN>-<endSN>.
     * 2. Write another journal entry, do NOT flush it, and inject an I/O error.
     * 3. Attempt to write another journal entry, which is expected to fail. After the failure,
     *    the newly created incomplete journal file may or may not have valid content.
     * 4. The UFS does not guarantee the consistency of the incomplete journal. To
     *    test how {@link UfsJournalLogWriter} recovers from Ufs failure before
     *    {@link UfsJournalLogWriter#flush()}, we simply delete the possibly created incomplete
     *    journal.
     */
    @Test
    public void flushAfterUfsFailure() throws Exception {
        Mockito.when(mUfs.supportsFlush()).thenReturn(true);
        // Write several journal entries, creating and closing journal file.
        // This file is complete.
        long startSN = 16;
        UfsJournalLogWriter writer = new UfsJournalLogWriter(mJournal, startSN);
        final int numberOfEntriesWrittenBeforeFailure = 10;
        long nextSN = writeJournalEntries(writer, startSN, numberOfEntriesWrittenBeforeFailure);
        writer.flush();
        writer.close();
        // Write another entry without flushing.
        writer = new UfsJournalLogWriter(mJournal, nextSN);
        nextSN = writeJournalEntries(writer, nextSN, 1);
        DataOutputStream badOut = createMockDataOutputStream(writer);
        Mockito.doThrow(new IOException(UfsJournalLogWriterTest.INJECTED_IO_ERROR_MESSAGE)).when(badOut).write(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
        tryWriteAndExpectToFail(writer, nextSN);
        // Delete the incomplete journal file to simulate the case in which
        // journal entries that have not been explicitly flushed will be lost.
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        UfsJournalFile journalFile = snapshot.getCurrentLog(mJournal);
        File file = new File(journalFile.getLocation().toString());
        file.delete();
        // Flush the journal, recovering from the Ufs failure.
        writer.flush();
        checkJournalEntries(startSN, nextSN);
        writer.close();
    }

    /**
     * Tests that {@link UfsJournalLogWriter} can detect the failure in which some flushed journal
     * entries are missing from the journal during recovery.
     */
    @Test
    public void missingJournalEntries() throws Exception {
        Mockito.when(mUfs.supportsFlush()).thenReturn(true);
        long startSN = 16;
        long nextSN = startSN;
        UfsJournalLogWriter writer = new UfsJournalLogWriter(mJournal, nextSN);
        long truncateSize = 0;
        long firstCorruptedEntrySeq = startSN + 4;
        for (int i = 0; i < 5; i++) {
            writer.write(newEntry(nextSN));
            nextSN++;
            if (i == 3) {
                UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
                UfsJournalFile journalFile = snapshot.getCurrentLog(mJournal);
                File file = new File(journalFile.getLocation().toString());
                truncateSize = file.length();
            }
        }
        writer.flush();
        writer.write(newEntry(nextSN));
        long seqOfFirstEntryToFlush = nextSN;
        nextSN++;
        Assert.assertNotNull(writer.getJournalOutputStream());
        DataOutputStream badOut = createMockDataOutputStream(writer);
        Mockito.doThrow(new IOException(UfsJournalLogWriterTest.INJECTED_IO_ERROR_MESSAGE)).when(badOut).write(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
        tryWriteAndExpectToFail(writer, nextSN);
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        UfsJournalFile journalFile = snapshot.getCurrentLog(mJournal);
        File file = new File(journalFile.getLocation().toString());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true);FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.truncate(truncateSize);
        }
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(JOURNAL_ENTRY_MISSING.getMessageWithUrl(ALLUXIO_DEBUG_DOCS_URL, firstCorruptedEntrySeq, seqOfFirstEntryToFlush));
        writer.write(newEntry(nextSN));
        writer.close();
    }

    @Test
    public void missingIncompleteJournalFile() throws Exception {
        long startSN = 16;
        UfsJournalLogWriter writer = new UfsJournalLogWriter(mJournal, startSN);
        long nextSN = writeJournalEntries(writer, startSN, 5);
        DataOutputStream badOut = createMockDataOutputStream(writer);
        Mockito.doThrow(new IOException(UfsJournalLogWriterTest.INJECTED_IO_ERROR_MESSAGE)).when(badOut).write(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
        tryWriteAndExpectToFail(writer, nextSN);
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        UfsJournalFile journalFile = snapshot.getCurrentLog(mJournal);
        File file = new File(journalFile.getLocation().toString());
        file.delete();
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(JOURNAL_ENTRY_MISSING.getMessageWithUrl(ALLUXIO_DEBUG_DOCS_URL, 0, 16));
        writer.write(newEntry(nextSN));
        writer.close();
    }
}

