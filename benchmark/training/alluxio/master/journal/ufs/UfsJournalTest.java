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


import UfsJournal.UNKNOWN_SEQUENCE_NUMBER;
import alluxio.exception.status.UnavailableException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link UfsJournal}.
 */
public final class UfsJournalTest {
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    private UfsJournal mJournal;

    /**
     * Tests formatting journal.
     */
    @Test
    public void format() throws Exception {
        mJournal.getUfs().create(UfsJournalFile.encodeCheckpointFileLocation(mJournal, 18).toString()).close();
        mJournal.getUfs().create(UfsJournalFile.encodeTemporaryCheckpointFileLocation(mJournal).toString()).close();
        long start = 17;
        for (int i = 0; i < 10; i++) {
            String l = UfsJournalFile.encodeLogFileLocation(mJournal, (start + i), ((start + i) + 2)).toString();
            mJournal.getUfs().create(l).close();
            start = (start + i) + 2;
        }
        String currentLog = UfsJournalFile.encodeLogFileLocation(mJournal, start, UNKNOWN_SEQUENCE_NUMBER).toString();
        mJournal.getUfs().create(currentLog).close();
        // Write a malformed log file which should be skipped.
        mJournal.getUfs().create(((UfsJournalFile.encodeLogFileLocation(mJournal, 16, 256).toString()) + ".tmp")).close();
        mJournal.format();
        Assert.assertTrue(mJournal.isFormatted());
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        Assert.assertTrue(snapshot.getCheckpoints().isEmpty());
        Assert.assertTrue(snapshot.getLogs().isEmpty());
        Assert.assertTrue(snapshot.getTemporaryCheckpoints().isEmpty());
    }

    @Test
    public void unavailableAfterClose() throws Exception {
        mJournal.start();
        mJournal.close();
        mThrown.expect(UnavailableException.class);
        mJournal.createJournalContext();
    }
}

