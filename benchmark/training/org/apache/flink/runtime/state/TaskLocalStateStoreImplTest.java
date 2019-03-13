/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import java.io.File;
import java.util.List;
import java.util.SortedMap;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TaskLocalStateStoreImplTest {
    private SortedMap<Long, TaskStateSnapshot> internalSnapshotMap;

    private Object internalLock;

    private TemporaryFolder temporaryFolder;

    private File[] allocationBaseDirs;

    private TaskLocalStateStoreImpl taskLocalStateStore;

    /**
     * Test that the instance delivers a correctly configured LocalRecoveryDirectoryProvider.
     */
    @Test
    public void getLocalRecoveryRootDirectoryProvider() {
        LocalRecoveryConfig directoryProvider = taskLocalStateStore.getLocalRecoveryConfig();
        Assert.assertEquals(allocationBaseDirs.length, directoryProvider.getLocalStateDirectoryProvider().allocationBaseDirsCount());
        for (int i = 0; i < (allocationBaseDirs.length); ++i) {
            Assert.assertEquals(allocationBaseDirs[i], directoryProvider.getLocalStateDirectoryProvider().selectAllocationBaseDirectory(i));
        }
    }

    /**
     * Tests basic store/retrieve of local state.
     */
    @Test
    public void storeAndRetrieve() throws Exception {
        final int chkCount = 3;
        for (int i = 0; i < chkCount; ++i) {
            Assert.assertNull(taskLocalStateStore.retrieveLocalState(i));
        }
        List<TaskStateSnapshot> taskStateSnapshots = storeStates(chkCount);
        checkStoredAsExpected(taskStateSnapshots, 0, chkCount);
        Assert.assertNull(taskLocalStateStore.retrieveLocalState((chkCount + 1)));
    }

    /**
     * Test checkpoint pruning.
     */
    @Test
    public void pruneCheckpoints() throws Exception {
        final int chkCount = 3;
        List<TaskStateSnapshot> taskStateSnapshots = storeStates(chkCount);
        // test retrieve with pruning
        taskLocalStateStore.pruneMatchingCheckpoints((long chk) -> chk != (chkCount - 1));
        for (int i = 0; i < (chkCount - 1); ++i) {
            Assert.assertNull(taskLocalStateStore.retrieveLocalState(i));
        }
        checkStoredAsExpected(taskStateSnapshots, (chkCount - 1), chkCount);
    }

    /**
     * Tests pruning of previous checkpoints if a new checkpoint is confirmed.
     */
    @Test
    public void confirmCheckpoint() throws Exception {
        final int chkCount = 3;
        final int confirmed = chkCount - 1;
        List<TaskStateSnapshot> taskStateSnapshots = storeStates(chkCount);
        taskLocalStateStore.confirmCheckpoint(confirmed);
        checkPrunedAndDiscarded(taskStateSnapshots, 0, confirmed);
        checkStoredAsExpected(taskStateSnapshots, confirmed, chkCount);
    }

    /**
     * Tests that disposal of a {@link TaskLocalStateStoreImpl} works and discards all local states.
     */
    @Test
    public void dispose() throws Exception {
        final int chkCount = 3;
        final int confirmed = chkCount - 1;
        List<TaskStateSnapshot> taskStateSnapshots = storeStates(chkCount);
        taskLocalStateStore.confirmCheckpoint(confirmed);
        taskLocalStateStore.dispose();
        checkPrunedAndDiscarded(taskStateSnapshots, 0, chkCount);
    }
}

