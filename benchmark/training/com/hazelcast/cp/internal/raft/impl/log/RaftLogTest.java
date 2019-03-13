/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.cp.internal.raft.impl.log;


import com.hazelcast.core.Endpoint;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftLogTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private RaftLog log;

    @Test
    public void test_initialState() {
        Assert.assertEquals(0, log.lastLogOrSnapshotTerm());
        Assert.assertEquals(0, log.lastLogOrSnapshotIndex());
    }

    @Test
    public void test_appendEntries_withSameTerm() {
        log.appendEntries(new LogEntry(1, 1, null));
        log.appendEntries(new LogEntry(1, 2, null));
        LogEntry last = new LogEntry(1, 3, null);
        log.appendEntries(last);
        Assert.assertEquals(last.term(), log.lastLogOrSnapshotTerm());
        Assert.assertEquals(last.index(), log.lastLogOrSnapshotIndex());
    }

    @Test
    public void test_appendEntries_withHigherTerms() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        LogEntry last = new LogEntry(2, 4, null);
        log.appendEntries(last);
        Assert.assertEquals(last.term(), log.lastLogOrSnapshotTerm());
        Assert.assertEquals(last.index(), log.lastLogOrSnapshotIndex());
        LogEntry lastLogEntry = log.lastLogOrSnapshotEntry();
        Assert.assertEquals(last.term(), lastLogEntry.term());
        Assert.assertEquals(last.index(), lastLogEntry.index());
    }

    @Test
    public void test_appendEntries_withLowerTerm() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(2, 1, null), new LogEntry(2, 2, null), new LogEntry(2, 3, null) };
        log.appendEntries(entries);
        exception.expect(IllegalArgumentException.class);
        log.appendEntries(new LogEntry(1, 4, null));
    }

    @Test
    public void test_appendEntries_withLowerIndex() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(2, 1, null), new LogEntry(2, 2, null), new LogEntry(2, 3, null) };
        log.appendEntries(entries);
        exception.expect(IllegalArgumentException.class);
        log.appendEntries(new LogEntry(2, 2, null));
    }

    @Test
    public void test_appendEntries_withEqualIndex() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(2, 1, null), new LogEntry(2, 2, null), new LogEntry(2, 3, null) };
        log.appendEntries(entries);
        exception.expect(IllegalArgumentException.class);
        log.appendEntries(new LogEntry(2, 3, null));
    }

    @Test
    public void test_appendEntries_withGreaterIndex() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(2, 1, null), new LogEntry(2, 2, null), new LogEntry(2, 3, null) };
        log.appendEntries(entries);
        exception.expect(IllegalArgumentException.class);
        log.appendEntries(new LogEntry(2, 5, null));
    }

    @Test
    public void test_appendEntriesAfterSnapshot_withSameTerm() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(1, 3, null, 0, Collections.<Endpoint>emptySet()));
        LogEntry last = new LogEntry(1, 4, null);
        log.appendEntries(last);
        Assert.assertEquals(last.term(), log.lastLogOrSnapshotTerm());
        Assert.assertEquals(last.index(), log.lastLogOrSnapshotIndex());
    }

    @Test
    public void test_appendEntriesAfterSnapshot_withHigherTerm() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(1, 3, null, 0, Collections.<Endpoint>emptySet()));
        LogEntry last = new LogEntry(2, 4, null);
        log.appendEntries(last);
        Assert.assertEquals(last.term(), log.lastLogOrSnapshotTerm());
        Assert.assertEquals(last.index(), log.lastLogOrSnapshotIndex());
    }

    @Test
    public void test_appendEntriesAfterSnapshot_withLowerTerm() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(2, 1, null), new LogEntry(2, 2, null), new LogEntry(2, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(2, 3, null, 0, Collections.<Endpoint>emptySet()));
        exception.expect(IllegalArgumentException.class);
        log.appendEntries(new LogEntry(1, 4, null));
    }

    @Test
    public void test_appendEntriesAfterSnapshot_withLowerIndex() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(2, 1, null), new LogEntry(2, 2, null), new LogEntry(2, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(2, 3, null, 0, Collections.<Endpoint>emptySet()));
        exception.expect(IllegalArgumentException.class);
        log.appendEntries(new LogEntry(2, 2, null));
    }

    @Test
    public void test_appendEntriesAfterSnapshot_withEqualIndex() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(2, 1, null), new LogEntry(2, 2, null), new LogEntry(2, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(2, 3, null, 0, Collections.<Endpoint>emptySet()));
        exception.expect(IllegalArgumentException.class);
        log.appendEntries(new LogEntry(2, 3, null));
    }

    @Test
    public void test_appendEntriesAfterSnapshot_withGreaterIndex() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(2, 1, null), new LogEntry(2, 2, null), new LogEntry(2, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(2, 3, null, 0, Collections.<Endpoint>emptySet()));
        exception.expect(IllegalArgumentException.class);
        log.appendEntries(new LogEntry(2, 5, null));
    }

    @Test
    public void getEntry() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        for (int i = 1; i <= (log.lastLogOrSnapshotIndex()); i++) {
            LogEntry entry = log.getLogEntry(i);
            Assert.assertEquals(1, entry.term());
            Assert.assertEquals(i, entry.index());
        }
    }

    @Test
    public void getEntryAfterSnapshot() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(1, 3, null, 0, Collections.<Endpoint>emptySet()));
        log.appendEntries(new LogEntry(1, 4, null));
        log.appendEntries(new LogEntry(1, 5, null));
        for (int i = 1; i <= 3; i++) {
            Assert.assertNull(log.getLogEntry(i));
        }
        for (int i = 4; i <= (log.lastLogOrSnapshotIndex()); i++) {
            LogEntry entry = log.getLogEntry(i);
            Assert.assertEquals(1, entry.term());
            Assert.assertEquals(i, entry.index());
        }
    }

    @Test
    public void getEntry_withUnknownIndex() {
        Assert.assertNull(log.getLogEntry(1));
    }

    @Test
    public void getEntry_withZeroIndex() {
        exception.expect(IllegalArgumentException.class);
        log.getLogEntry(0);
    }

    @Test
    public void getEntriesBetween() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        LogEntry[] result = log.getEntriesBetween(1, 3);
        Assert.assertArrayEquals(entries, result);
        result = log.getEntriesBetween(1, 2);
        Assert.assertArrayEquals(Arrays.copyOfRange(entries, 0, 2), result);
        result = log.getEntriesBetween(2, 3);
        Assert.assertArrayEquals(Arrays.copyOfRange(entries, 1, 3), result);
    }

    @Test
    public void getEntriesBetweenAfterSnapshot() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(1, 2, null, 0, Collections.<Endpoint>emptySet()));
        LogEntry[] result = log.getEntriesBetween(3, 3);
        Assert.assertArrayEquals(Arrays.copyOfRange(entries, 2, 3), result);
    }

    @Test
    public void getEntriesBetweenBeforeSnapshotIndex() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(1, 2, null, 0, Collections.<Endpoint>emptySet()));
        exception.expect(IllegalArgumentException.class);
        log.getEntriesBetween(2, 3);
    }

    @Test
    public void truncateEntriesFrom() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null), new LogEntry(1, 4, null) };
        log.appendEntries(entries);
        List<LogEntry> truncated = log.truncateEntriesFrom(3);
        Assert.assertEquals(2, truncated.size());
        Assert.assertArrayEquals(Arrays.copyOfRange(entries, 2, 4), truncated.toArray());
        for (int i = 1; i <= 2; i++) {
            Assert.assertEquals(entries[(i - 1)], log.getLogEntry(i));
        }
        Assert.assertNull(log.getLogEntry(3));
    }

    @Test
    public void truncateEntriesFrom_afterSnapshot() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null), new LogEntry(1, 4, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(1, 2, null, 0, Collections.<Endpoint>emptySet()));
        List<LogEntry> truncated = log.truncateEntriesFrom(3);
        Assert.assertEquals(2, truncated.size());
        Assert.assertArrayEquals(Arrays.copyOfRange(entries, 2, 4), truncated.toArray());
        Assert.assertNull(log.getLogEntry(3));
    }

    @Test
    public void truncateEntriesFrom_outOfRange() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        exception.expect(IllegalArgumentException.class);
        log.truncateEntriesFrom(4);
    }

    @Test
    public void truncateEntriesFrom_beforeSnapshotIndex() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(1, 2, null, 0, Collections.<Endpoint>emptySet()));
        exception.expect(IllegalArgumentException.class);
        log.truncateEntriesFrom(1);
    }

    @Test
    public void setSnapshotAtLastLogIndex_forSingleEntryLog() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null) };
        log.appendEntries(entries);
        Object snapshot = new Object();
        log.setSnapshot(new SnapshotEntry(1, 1, snapshot, 0, Collections.<Endpoint>emptySet()));
        LogEntry lastLogEntry = log.lastLogOrSnapshotEntry();
        Assert.assertEquals(1, lastLogEntry.index());
        Assert.assertEquals(1, lastLogEntry.term());
        Assert.assertEquals(log.lastLogOrSnapshotIndex(), 1);
        Assert.assertEquals(log.lastLogOrSnapshotTerm(), 1);
        Assert.assertEquals(log.snapshotIndex(), 1);
        LogEntry snapshotEntry = log.snapshot();
        Assert.assertEquals(1, snapshotEntry.index());
        Assert.assertEquals(1, snapshotEntry.term());
        Assert.assertEquals(snapshot, snapshotEntry.operation());
    }

    @Test
    public void setSnapshotAtLastLogIndex_forMultiEntryLog() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null), new LogEntry(1, 4, null), new LogEntry(1, 5, null) };
        log.appendEntries(entries);
        log.setSnapshot(new SnapshotEntry(1, 5, null, 0, Collections.<Endpoint>emptySet()));
        LogEntry lastLogEntry = log.lastLogOrSnapshotEntry();
        Assert.assertEquals(5, lastLogEntry.index());
        Assert.assertEquals(1, lastLogEntry.term());
        Assert.assertEquals(log.lastLogOrSnapshotIndex(), 5);
        Assert.assertEquals(log.lastLogOrSnapshotTerm(), 1);
        Assert.assertEquals(log.snapshotIndex(), 5);
        LogEntry snapshot = log.snapshot();
        Assert.assertEquals(5, snapshot.index());
        Assert.assertEquals(1, snapshot.term());
    }

    @Test
    public void setSnapshot() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null), new LogEntry(1, 4, null), new LogEntry(1, 5, null) };
        log.appendEntries(entries);
        int truncated = log.setSnapshot(new SnapshotEntry(1, 3, null, 0, Collections.<Endpoint>emptySet()));
        Assert.assertEquals(3, truncated);
        for (int i = 1; i <= 3; i++) {
            Assert.assertFalse(log.containsLogEntry(i));
            Assert.assertNull(log.getLogEntry(i));
        }
        for (int i = 4; i <= 5; i++) {
            Assert.assertTrue(log.containsLogEntry(i));
            Assert.assertNotNull(log.getLogEntry(i));
        }
        LogEntry lastLogEntry = log.lastLogOrSnapshotEntry();
        Assert.assertEquals(5, lastLogEntry.index());
        Assert.assertEquals(1, lastLogEntry.term());
        Assert.assertSame(lastLogEntry, log.getLogEntry(lastLogEntry.index()));
        Assert.assertEquals(log.lastLogOrSnapshotIndex(), 5);
        Assert.assertEquals(log.lastLogOrSnapshotTerm(), 1);
        Assert.assertEquals(log.snapshotIndex(), 3);
        LogEntry snapshot = log.snapshot();
        Assert.assertEquals(3, snapshot.index());
        Assert.assertEquals(1, snapshot.term());
    }

    @Test
    public void setSnapshot_multipleTimes() {
        LogEntry[] entries = new LogEntry[]{ new LogEntry(1, 1, null), new LogEntry(1, 2, null), new LogEntry(1, 3, null), new LogEntry(1, 4, null), new LogEntry(1, 5, null) };
        log.appendEntries(entries);
        int truncated = log.setSnapshot(new SnapshotEntry(1, 2, null, 0, Collections.<Endpoint>emptySet()));
        Assert.assertEquals(2, truncated);
        for (int i = 1; i <= 2; i++) {
            Assert.assertFalse(log.containsLogEntry(i));
            Assert.assertNull(log.getLogEntry(i));
        }
        for (int i = 3; i <= 5; i++) {
            Assert.assertTrue(log.containsLogEntry(i));
            Assert.assertNotNull(log.getLogEntry(i));
        }
        Object snapshot = new Object();
        truncated = log.setSnapshot(new SnapshotEntry(1, 4, snapshot, 0, Collections.<Endpoint>emptySet()));
        Assert.assertEquals(2, truncated);
        for (int i = 1; i <= 4; i++) {
            Assert.assertFalse(log.containsLogEntry(i));
            Assert.assertNull(log.getLogEntry(i));
        }
        Assert.assertTrue(log.containsLogEntry(5));
        Assert.assertNotNull(log.getLogEntry(5));
        LogEntry lastLogEntry = log.lastLogOrSnapshotEntry();
        Assert.assertEquals(5, lastLogEntry.index());
        Assert.assertEquals(1, lastLogEntry.term());
        Assert.assertSame(lastLogEntry, log.getLogEntry(lastLogEntry.index()));
        Assert.assertEquals(log.lastLogOrSnapshotIndex(), 5);
        Assert.assertEquals(log.lastLogOrSnapshotTerm(), 1);
        Assert.assertEquals(log.snapshotIndex(), 4);
        LogEntry snapshotEntry = log.snapshot();
        Assert.assertEquals(4, snapshotEntry.index());
        Assert.assertEquals(1, snapshotEntry.term());
        Assert.assertEquals(snapshotEntry.operation(), snapshot);
    }
}

