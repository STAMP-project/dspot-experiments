/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.actions.cache;


import com.google.devtools.build.lib.clock.Clock;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for the PersistentStringIndexer class.
 */
@RunWith(JUnit4.class)
public class PersistentStringIndexerTest {
    private static class ManualClock implements Clock {
        private long currentTime = 0L;

        ManualClock() {
        }

        @Override
        public long currentTimeMillis() {
            throw new AssertionError("unexpected method call");
        }

        @Override
        public long nanoTime() {
            return currentTime;
        }

        void advance(long time) {
            currentTime += time;
        }
    }

    private PersistentStringIndexer psi;

    private Map<Integer, String> mappings = new ConcurrentHashMap<>();

    private Scratch scratch = new Scratch();

    private PersistentStringIndexerTest.ManualClock clock = new PersistentStringIndexerTest.ManualClock();

    private Path dataPath;

    private Path journalPath;

    @Test
    public void testNormalOperation() throws Exception {
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        setupTestContent();
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        clock.advance(4);
        assertIndex(9, "xyzqwerty");// This should flush journal to disk.

        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isTrue();
        psi.save();// Successful save will remove journal file.

        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isFalse();
        // Now restore data from file and verify it.
        psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
        assertThat(journalPath.exists()).isFalse();
        clock.advance(4);
        assertSize(10);
        assertContent();
        assertThat(journalPath.exists()).isFalse();
    }

    @Test
    public void testJournalRecoveryWithoutMainDataFile() throws Exception {
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        setupTestContent();
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        clock.advance(4);
        assertIndex(9, "abc1234");// This should flush journal to disk.

        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isTrue();
        // Now restore data from file and verify it. All data should be restored from journal;
        psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isFalse();
        clock.advance(4);
        assertSize(10);
        assertContent();
        assertThat(journalPath.exists()).isFalse();
    }

    @Test
    public void testJournalRecovery() throws Exception {
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        setupTestContent();
        psi.save();
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isFalse();
        long oldDataFileLen = dataPath.getFileSize();
        clock.advance(4);
        assertIndex(9, "another record");// This should flush journal to disk.

        assertSize(10);
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isTrue();
        // Now restore data from file and verify it. All data should be restored from journal;
        psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isFalse();
        assertThat(dataPath.getFileSize()).isGreaterThan(oldDataFileLen);// data file should have been updated

        clock.advance(4);
        assertSize(10);
        assertContent();
        assertThat(journalPath.exists()).isFalse();
    }

    @Test
    public void testConcurrentWritesJournalRecovery() throws Exception {
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        setupTestContent();
        psi.save();
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isFalse();
        long oldDataFileLen = dataPath.getFileSize();
        int size = psi.size();
        int numToWrite = 50000;
        writeLotsOfEntriesConcurrently(numToWrite);
        assertThat(journalPath.exists()).isFalse();
        clock.advance(4);
        assertIndex((size + numToWrite), "another record");// This should flush journal to disk.

        assertSize(((size + numToWrite) + 1));
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isTrue();
        // Now restore data from file and verify it. All data should be restored from journal;
        psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isFalse();
        assertThat(dataPath.getFileSize()).isGreaterThan(oldDataFileLen);// data file should have been updated

        clock.advance(4);
        assertSize(((size + numToWrite) + 1));
        assertContent();
        assertThat(journalPath.exists()).isFalse();
    }

    @Test
    public void testCorruptedJournal() throws Exception {
        FileSystemUtils.createDirectoryAndParents(journalPath.getParentDirectory());
        FileSystemUtils.writeContentAsLatin1(journalPath, "bogus content");
        try {
            psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
            Assert.fail();
        } catch (IOException e) {
            assertThat(e).hasMessageThat().contains("too short: Only 13 bytes");
        }
        journalPath.delete();
        setupTestContent();
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        clock.advance(4);
        assertIndex(9, "abc1234");// This should flush journal to disk.

        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isTrue();
        byte[] journalContent = FileSystemUtils.readContent(journalPath);
        // Now restore data from file and verify it. All data should be restored from journal;
        psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isFalse();
        // Now put back truncated journal. We should get an error.
        assertThat(dataPath.delete()).isTrue();
        FileSystemUtils.writeContent(journalPath, Arrays.copyOf(journalContent, ((journalContent.length) - 1)));
        try {
            psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
            Assert.fail();
        } catch (EOFException e) {
            // Expected.
        }
        // Corrupt the journal with a negative size value.
        byte[] journalCopy = journalContent.clone();
        // Flip this bit to make the key size negative.
        journalCopy[95] = -2;
        FileSystemUtils.writeContent(journalPath, journalCopy);
        try {
            psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
            Assert.fail();
        } catch (IOException e) {
            // Expected.
            assertThat(e).hasMessageThat().contains("corrupt key length");
        }
        // Now put back corrupted journal. We should get an error.
        journalContent[((journalContent.length) - 13)] = 100;
        FileSystemUtils.writeContent(journalPath, journalContent);
        try {
            psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
            Assert.fail();
        } catch (IOException e) {
            // Expected.
        }
    }

    @Test
    public void testDupeIndexCorruption() throws Exception {
        setupTestContent();
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        assertIndex(9, "abc1234");// This should flush journal to disk.

        psi.save();
        assertThat(dataPath.exists()).isTrue();
        assertThat(journalPath.exists()).isFalse();
        byte[] content = FileSystemUtils.readContent(dataPath);
        // We remove the data file, and instead create a corrupt journal.
        // 
        // The journal has a header followed by a sequence of (String, int) pairs, where each int is a
        // unique value. The String is encoded by the length (as an int), and the int is simply encoded
        // as an int. Note that the DataOutputStream class uses big endian by default, so the low-order
        // bits are at the end.
        // 
        // For the purpose of this test, we want to make the journal contain two entries with the same
        // index (which is illegal). The PersistentStringIndexer assigns int values in the usual order,
        // starting with zero, and it now contains 9 entries. We simply change the last entry to an
        // index that is guaranteed to already exist. If it is the index 1, we change it to 2, otherwise
        // we change it to 1 - in both cases, the code currently guarantees that the duplicate comes
        // earlier in the stream.
        assertThat(dataPath.delete()).isTrue();
        content[((content.length) - 1)] = ((content[((content.length) - 1)]) == 1) ? ((byte) (2)) : ((byte) (1));
        FileSystemUtils.writeContent(journalPath, content);
        try {
            psi = PersistentStringIndexer.newPersistentStringIndexer(dataPath, clock);
            Assert.fail();
        } catch (IOException e) {
            // Expected.
            assertThat(e).hasMessageThat().contains("Corrupted filename index has duplicate entry");
        }
    }

    @Test
    public void testDeferredIOFailure() throws Exception {
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        setupTestContent();
        assertThat(dataPath.exists()).isFalse();
        assertThat(journalPath.exists()).isFalse();
        // Ensure that journal cannot be saved.
        FileSystemUtils.createDirectoryAndParents(journalPath);
        clock.advance(4);
        assertIndex(9, "abc1234");// This should flush journal to disk (and fail at that).

        assertThat(dataPath.exists()).isFalse();
        // Subsequent updates should succeed even though journaling is disabled at this point.
        clock.advance(4);
        assertIndex(10, "another record");
        try {
            // Save should actually save main data file but then return us deferred IO failure
            // from failed journal write.
            psi.save();
            Assert.fail();
        } catch (IOException e) {
            assertThat(e).hasMessageThat().contains(((journalPath.getPathString()) + " (Is a directory)"));
        }
    }
}

