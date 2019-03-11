/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package io.aeron.cluster;


import ConsensusModule.Configuration.SERVICE_ID;
import RecordingLog.Entry;
import RecordingLog.Snapshot;
import io.aeron.Aeron;
import java.io.File;
import java.util.ArrayList;
import org.agrona.IoUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RecordingLogTest {
    private static final File TEMP_DIR = new File(IoUtil.tmpDirName());

    private boolean ignoreMissingRecordingFile = false;

    @Test
    public void shouldCreateNewIndex() {
        try (RecordingLog recordingLog = new RecordingLog(RecordingLogTest.TEMP_DIR)) {
            Assert.assertThat(recordingLog.entries().size(), CoreMatchers.is(0));
        }
    }

    @Test
    public void shouldAppendAndThenReloadLatestSnapshot() {
        final RecordingLog.Entry entry = new RecordingLog.Entry(1, 3, 2, 777, 4, Aeron.NULL_VALUE, RecordingLog.ENTRY_TYPE_SNAPSHOT, 0);
        try (RecordingLog recordingLog = new RecordingLog(RecordingLogTest.TEMP_DIR)) {
            recordingLog.appendSnapshot(entry.recordingId, entry.leadershipTermId, entry.termBaseLogPosition, 777, entry.timestamp, Configuration.SERVICE_ID);
        }
        try (RecordingLog recordingLog = new RecordingLog(RecordingLogTest.TEMP_DIR)) {
            Assert.assertThat(recordingLog.entries().size(), CoreMatchers.is(1));
            final RecordingLog.Entry snapshot = recordingLog.getLatestSnapshot(Configuration.SERVICE_ID);
            Assert.assertEquals(entry.toString(), snapshot.toString());
        }
    }

    @Test
    public void shouldAppendAndThenCommitTermPosition() {
        final long newPosition = 9999L;
        try (RecordingLog recordingLog = new RecordingLog(RecordingLogTest.TEMP_DIR)) {
            final long recordingId = 1L;
            final long leadershipTermId = 1111L;
            final long logPosition = 2222L;
            final long timestamp = 3333L;
            recordingLog.appendTerm(recordingId, leadershipTermId, logPosition, timestamp);
            recordingLog.commitLogPosition(leadershipTermId, newPosition);
        }
        try (RecordingLog recordingLog = new RecordingLog(RecordingLogTest.TEMP_DIR)) {
            Assert.assertThat(recordingLog.entries().size(), CoreMatchers.is(1));
            final RecordingLog.Entry actualEntry = recordingLog.entries().get(0);
            Assert.assertEquals(newPosition, actualEntry.logPosition);
        }
    }

    @Test
    public void shouldTombstoneEntry() {
        try (RecordingLog recordingLog = new RecordingLog(RecordingLogTest.TEMP_DIR)) {
            final RecordingLog.Entry entryOne = new RecordingLog.Entry(1L, 3, 2, NULL_POSITION, 4, 0, RecordingLog.ENTRY_TYPE_TERM, 0);
            recordingLog.appendTerm(entryOne.recordingId, entryOne.leadershipTermId, entryOne.termBaseLogPosition, entryOne.timestamp);
            final RecordingLog.Entry entryTwo = new RecordingLog.Entry(1L, 4, 3, NULL_POSITION, 5, 0, RecordingLog.ENTRY_TYPE_TERM, 0);
            recordingLog.appendTerm(entryTwo.recordingId, entryTwo.leadershipTermId, entryTwo.termBaseLogPosition, entryTwo.timestamp);
            recordingLog.tombstoneEntry(entryTwo.leadershipTermId, ((recordingLog.nextEntryIndex()) - 1));
        }
        try (RecordingLog recordingLog = new RecordingLog(RecordingLogTest.TEMP_DIR)) {
            Assert.assertThat(recordingLog.entries().size(), CoreMatchers.is(1));
            Assert.assertThat(recordingLog.nextEntryIndex(), CoreMatchers.is(2));
        }
    }

    @Test
    public void shouldCorrectlyOrderSnapshots() {
        ignoreMissingRecordingFile = true;
        final ArrayList<RecordingLog.Snapshot> snapshots = new ArrayList<>();
        final ArrayList<RecordingLog.Entry> entries = new ArrayList<>();
        RecordingLogTest.addRecordingLogEntry(entries, SERVICE_ID, 0, RecordingLog.ENTRY_TYPE_TERM);
        RecordingLogTest.addRecordingLogEntry(entries, 2, 4, RecordingLog.ENTRY_TYPE_SNAPSHOT);
        RecordingLogTest.addRecordingLogEntry(entries, 1, 5, RecordingLog.ENTRY_TYPE_SNAPSHOT);
        RecordingLogTest.addRecordingLogEntry(entries, 0, 6, RecordingLog.ENTRY_TYPE_SNAPSHOT);
        RecordingLogTest.addRecordingLogEntry(entries, SERVICE_ID, 7, RecordingLog.ENTRY_TYPE_SNAPSHOT);
        RecordingLog.addSnapshots(snapshots, entries, 3, ((entries.size()) - 1));
        Assert.assertThat(snapshots.size(), CoreMatchers.is(4));
        Assert.assertThat(snapshots.get(0).serviceId, CoreMatchers.is(SERVICE_ID));
        Assert.assertThat(snapshots.get(1).serviceId, CoreMatchers.is(0));
        Assert.assertThat(snapshots.get(2).serviceId, CoreMatchers.is(1));
        Assert.assertThat(snapshots.get(3).serviceId, CoreMatchers.is(2));
    }
}

