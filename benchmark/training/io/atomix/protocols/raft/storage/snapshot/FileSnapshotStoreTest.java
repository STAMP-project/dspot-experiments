/**
 * Copyright 2015-present Open Networking Foundation
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
package io.atomix.protocols.raft.storage.snapshot;


import io.atomix.utils.time.WallClockTimestamp;
import org.junit.Assert;
import org.junit.Test;


/**
 * File snapshot store test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class FileSnapshotStoreTest extends AbstractSnapshotStoreTest {
    private String testId;

    /**
     * Tests storing and loading snapshots.
     */
    @Test
    public void testStoreLoadSnapshot() {
        SnapshotStore store = createSnapshotStore();
        Snapshot snapshot = store.newSnapshot(2, new WallClockTimestamp());
        try (SnapshotWriter writer = snapshot.openWriter()) {
            writer.writeLong(10);
        }
        snapshot.complete();
        Assert.assertNotNull(store.getSnapshot(2));
        store.close();
        store = createSnapshotStore();
        Assert.assertNotNull(store.getSnapshot(2));
        Assert.assertEquals(2, store.getSnapshot(2).index());
        try (SnapshotReader reader = snapshot.openReader()) {
            Assert.assertEquals(10, reader.readLong());
        }
    }

    /**
     * Tests persisting and loading snapshots.
     */
    @Test
    public void testPersistLoadSnapshot() {
        SnapshotStore store = createSnapshotStore();
        Snapshot snapshot = store.newTemporarySnapshot(2, new WallClockTimestamp());
        try (SnapshotWriter writer = snapshot.openWriter()) {
            writer.writeLong(10);
        }
        snapshot = snapshot.persist();
        Assert.assertTrue(snapshot.isPersisted());
        Assert.assertNull(store.getSnapshot(2));
        snapshot.complete();
        Assert.assertNotNull(store.getSnapshot(2));
        try (SnapshotReader reader = snapshot.openReader()) {
            Assert.assertEquals(10, reader.readLong());
        }
        store.close();
        store = createSnapshotStore();
        Assert.assertNotNull(store.getSnapshot(2));
        Assert.assertEquals(2, store.getSnapshot(2).index());
        snapshot = store.getSnapshot(2);
        try (SnapshotReader reader = snapshot.openReader()) {
            Assert.assertEquals(10, reader.readLong());
        }
    }

    /**
     * Tests writing multiple times to a snapshot designed to mimic chunked snapshots from leaders.
     */
    @Test
    public void testStreamSnapshot() throws Exception {
        SnapshotStore store = createSnapshotStore();
        Snapshot snapshot = store.newSnapshot(1, new WallClockTimestamp());
        for (long i = 1; i <= 10; i++) {
            try (SnapshotWriter writer = snapshot.openWriter()) {
                writer.writeLong(i);
            }
        }
        snapshot.complete();
        snapshot = store.getSnapshot(1);
        try (SnapshotReader reader = snapshot.openReader()) {
            for (long i = 1; i <= 10; i++) {
                Assert.assertEquals(i, reader.readLong());
            }
        }
    }
}

