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


import Journal.JournalEntry;
import PropertyKey.MASTER_JOURNAL_CHECKPOINT_PERIOD_ENTRIES;
import alluxio.conf.ServerConfiguration;
import alluxio.master.MockMaster;
import alluxio.proto.journal.Journal;
import alluxio.underfs.UnderFileSystem;
import alluxio.util.CommonUtils;
import alluxio.util.WaitForOptions;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link alluxio.master.journal.ufs.UfsJournalCheckpointThread}.
 */
public final class UfsJournalCheckpointThreadTest {
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    private UfsJournal mJournal;

    private UnderFileSystem mUfs;

    /**
     * The checkpoint thread replays all the logs and checkpoints periodically if not shutdown.
     */
    @Test
    public void checkpointBeforeShutdown() throws Exception {
        ServerConfiguration.set(MASTER_JOURNAL_CHECKPOINT_PERIOD_ENTRIES, "2");
        buildCompletedLog(0, 10);
        buildIncompleteLog(10, 15);
        MockMaster mockMaster = new MockMaster();
        UfsJournalCheckpointThread checkpointThread = new UfsJournalCheckpointThread(mockMaster, mJournal);
        checkpointThread.start();
        CommonUtils.waitFor("checkpoint", () -> {
            try {
                UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
                if ((!(snapshot.getCheckpoints().isEmpty())) && ((snapshot.getCheckpoints().get(((snapshot.getCheckpoints().size()) - 1)).getEnd()) == 10)) {
                    return true;
                }
            } catch ( e) {
                return false;
            }
            return false;
        }, WaitForOptions.defaults().setTimeoutMs(20000));
        UfsJournalSnapshot snapshot = UfsJournalSnapshot.getSnapshot(mJournal);
        Assert.assertEquals(1, snapshot.getCheckpoints().size());
        Assert.assertEquals(10, snapshot.getCheckpoints().get(0).getEnd());
        checkpointThread.awaitTermination(true);
    }

    /**
     * The checkpoint thread replays all the logs before shutting down.
     */
    @Test
    public void checkpointAfterShutdown() throws Exception {
        ServerConfiguration.set(MASTER_JOURNAL_CHECKPOINT_PERIOD_ENTRIES, "2");
        buildCompletedLog(0, 10);
        buildIncompleteLog(10, 15);
        MockMaster mockMaster = new MockMaster();
        UfsJournalCheckpointThread checkpointThread = new UfsJournalCheckpointThread(mockMaster, mJournal);
        checkpointThread.start();
        checkpointThread.awaitTermination(true);
        // Make sure all the journal entries have been processed. Note that it is not necessary that
        // the they are checkpointed.
        Iterator<Journal.JournalEntry> it = mockMaster.getJournalEntryIterator();
        Assert.assertEquals(10, Iterators.size(it));
    }
}

