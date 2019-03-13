/**
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.protocols.raft.storage.snapshot;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


/**
 * Snapshot file test.
 */
public class SnapshotFileTest {
    /**
     * Tests creating a snapshot file name.
     */
    @Test
    public void testCreateSnapshotFileName() throws Exception {
        Assert.assertEquals("test-1.snapshot", SnapshotFile.createSnapshotFileName("test", 1));
        Assert.assertEquals("test-2.snapshot", SnapshotFile.createSnapshotFileName("test", 2));
    }

    /**
     * Tests determining whether a file is a snapshot file.
     */
    @Test
    public void testCreateValidateSnapshotFile() throws Exception {
        Assert.assertTrue(SnapshotFile.isSnapshotFile(SnapshotFile.createSnapshotFile(new File(System.getProperty("user.dir")), "foo", 1)));
        Assert.assertTrue(SnapshotFile.isSnapshotFile(SnapshotFile.createSnapshotFile(new File(System.getProperty("user.dir")), "foo-bar", 1)));
        Assert.assertFalse(SnapshotFile.isSnapshotFile(new File(((System.getProperty("user.dir")) + "/foo"))));
        Assert.assertFalse(SnapshotFile.isSnapshotFile(new File(((System.getProperty("user.dir")) + "/foo.bar"))));
        Assert.assertFalse(SnapshotFile.isSnapshotFile(new File(((System.getProperty("user.dir")) + "/foo.snapshot"))));
        Assert.assertFalse(SnapshotFile.isSnapshotFile(new File(((System.getProperty("user.dir")) + "/foo-bar.snapshot"))));
    }

    @Test
    public void testParseSnapshotName() throws Exception {
        Assert.assertEquals("foo", SnapshotFile.parseName("foo-1-2.snapshot"));
        Assert.assertEquals("foo-bar", SnapshotFile.parseName("foo-bar-1-2.snapshot"));
    }
}

