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
import alluxio.util.URIUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link UfsJournalFile}.
 */
public final class UfsJournalFileTest {
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    private URI mLocation;

    private UfsJournal mJournal;

    @Test
    public void createCheckpointFile() {
        UfsJournalFile file = UfsJournalFile.createCheckpointFile(mLocation, 256);
        Assert.assertEquals(0, file.getStart());
        Assert.assertEquals(256, file.getEnd());
        Assert.assertEquals(mLocation, file.getLocation());
        Assert.assertTrue(file.isCheckpoint());
        Assert.assertFalse(file.isIncompleteLog());
        Assert.assertFalse(file.isCompletedLog());
        Assert.assertFalse(file.isTmpCheckpoint());
    }

    @Test
    public void createCompletedLogFile() {
        UfsJournalFile file = UfsJournalFile.createLogFile(mLocation, 16, 256);
        Assert.assertEquals(16, file.getStart());
        Assert.assertEquals(256, file.getEnd());
        Assert.assertEquals(mLocation, file.getLocation());
        Assert.assertFalse(file.isCheckpoint());
        Assert.assertFalse(file.isIncompleteLog());
        Assert.assertTrue(file.isCompletedLog());
        Assert.assertFalse(file.isTmpCheckpoint());
    }

    @Test
    public void createIncompleteLogFile() {
        UfsJournalFile file = UfsJournalFile.createLogFile(mLocation, 16, UNKNOWN_SEQUENCE_NUMBER);
        Assert.assertEquals(16, file.getStart());
        Assert.assertEquals(UNKNOWN_SEQUENCE_NUMBER, file.getEnd());
        Assert.assertEquals(mLocation, file.getLocation());
        Assert.assertFalse(file.isCheckpoint());
        Assert.assertTrue(file.isIncompleteLog());
        Assert.assertFalse(file.isCompletedLog());
        Assert.assertFalse(file.isTmpCheckpoint());
    }

    @Test
    public void createTmpCheckpointFile() {
        UfsJournalFile file = UfsJournalFile.createTmpCheckpointFile(mLocation);
        Assert.assertEquals(UNKNOWN_SEQUENCE_NUMBER, file.getStart());
        Assert.assertEquals(UNKNOWN_SEQUENCE_NUMBER, file.getEnd());
        Assert.assertEquals(mLocation, file.getLocation());
        Assert.assertFalse(file.isCheckpoint());
        Assert.assertFalse(file.isIncompleteLog());
        Assert.assertFalse(file.isCompletedLog());
        Assert.assertTrue(file.isTmpCheckpoint());
    }

    @Test
    public void sort() {
        List<UfsJournalFile> logs = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            logs.add(UfsJournalFile.createLogFile(mLocation, random.nextInt(), (i + 1)));
        }
        Collections.shuffle(logs);
        Collections.sort(logs);
        for (int i = 0; i < (logs.size()); i++) {
            Assert.assertEquals((i + 1), logs.get(i).getEnd());
        }
    }

    /**
     * Encodes/decodes completed log file names.
     */
    @Test
    public void completedLogFilename() throws Exception {
        String location = UfsJournalFile.encodeLogFileLocation(mJournal, 16, 256).toString();
        Assert.assertEquals(URIUtils.appendPathOrDie(mJournal.getLogDir(), "0x10-0x100").toString(), location);
        UfsJournalFile file = UfsJournalFile.decodeLogFile(mJournal, "0x10-0x100");
        Assert.assertTrue(file.isCompletedLog());
        Assert.assertEquals(16, file.getStart());
        Assert.assertEquals(256, file.getEnd());
        Assert.assertEquals(location, file.getLocation().toString());
    }

    /**
     * Encodes/decodes incomplete log file names.
     */
    @Test
    public void incompleteLogFilename() throws Exception {
        String location = UfsJournalFile.encodeLogFileLocation(mJournal, 16, UNKNOWN_SEQUENCE_NUMBER).toString();
        String expectedFilename = "0x10-0x" + (Long.toHexString(UNKNOWN_SEQUENCE_NUMBER));
        Assert.assertEquals(URIUtils.appendPathOrDie(mJournal.getLogDir(), expectedFilename).toString(), location);
        UfsJournalFile file = UfsJournalFile.decodeLogFile(mJournal, expectedFilename);
        Assert.assertTrue(file.isIncompleteLog());
        Assert.assertEquals(16, file.getStart());
        Assert.assertEquals(UNKNOWN_SEQUENCE_NUMBER, file.getEnd());
        Assert.assertEquals(location, file.getLocation().toString());
    }

    /**
     * Encodes/decodes checkpoint filename.
     */
    @Test
    public void checkpointFilename() throws Exception {
        String location = UfsJournalFile.encodeCheckpointFileLocation(mJournal, 16).toString();
        String expectedFilename = "0x0-0x10";
        Assert.assertEquals(URIUtils.appendPathOrDie(mJournal.getCheckpointDir(), expectedFilename).toString(), location);
        UfsJournalFile file = UfsJournalFile.decodeCheckpointFile(mJournal, expectedFilename);
        Assert.assertTrue(file.isCheckpoint());
        Assert.assertEquals(0, file.getStart());
        Assert.assertEquals(16, file.getEnd());
        Assert.assertEquals(location, file.getLocation().toString());
    }

    /**
     * Encodes/decodes temporary checkpoint filename.
     */
    @Test
    public void temporaryCheckpointFilename() throws Exception {
        String location = UfsJournalFile.encodeTemporaryCheckpointFileLocation(mJournal).toString();
        Assert.assertTrue(location.startsWith(mJournal.getTmpDir().toString()));
        UfsJournalFile file = UfsJournalFile.decodeTemporaryCheckpointFile(mJournal, location.substring(((location.lastIndexOf('/')) + 1)));
        Assert.assertTrue(file.isTmpCheckpoint());
        Assert.assertEquals(UNKNOWN_SEQUENCE_NUMBER, file.getStart());
        Assert.assertEquals(UNKNOWN_SEQUENCE_NUMBER, file.getEnd());
        Assert.assertEquals(location, file.getLocation().toString());
    }
}

