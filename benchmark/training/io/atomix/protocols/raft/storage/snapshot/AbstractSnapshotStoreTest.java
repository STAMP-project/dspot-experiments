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
 * Snapshot store test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public abstract class AbstractSnapshotStoreTest {
    /**
     * Tests writing a snapshot.
     */
    @Test
    public void testWriteSnapshotChunks() {
        SnapshotStore store = createSnapshotStore();
        WallClockTimestamp timestamp = new WallClockTimestamp();
        Snapshot snapshot = store.newSnapshot(2, timestamp);
        Assert.assertEquals(2, snapshot.index());
        Assert.assertEquals(timestamp, snapshot.timestamp());
        Assert.assertNull(store.getSnapshot(2));
        try (SnapshotWriter writer = snapshot.openWriter()) {
            writer.writeLong(10);
        }
        Assert.assertNull(store.getSnapshot(2));
        try (SnapshotWriter writer = snapshot.openWriter()) {
            writer.writeLong(11);
        }
        Assert.assertNull(store.getSnapshot(2));
        try (SnapshotWriter writer = snapshot.openWriter()) {
            writer.writeLong(12);
        }
        Assert.assertNull(store.getSnapshot(2));
        snapshot.complete();
        Assert.assertEquals(2, store.getSnapshot(2).index());
        try (SnapshotReader reader = store.getSnapshot(2).openReader()) {
            Assert.assertEquals(10, reader.readLong());
            Assert.assertEquals(11, reader.readLong());
            Assert.assertEquals(12, reader.readLong());
        }
    }
}

