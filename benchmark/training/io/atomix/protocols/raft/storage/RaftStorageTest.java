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
package io.atomix.protocols.raft.storage;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;


/**
 * Raft storage test.
 */
public class RaftStorageTest {
    private static final Path PATH = Paths.get("target/test-logs/");

    @Test
    public void testDefaultConfiguration() throws Exception {
        RaftStorage storage = RaftStorage.builder().build();
        Assert.assertEquals("atomix", storage.prefix());
        Assert.assertEquals(new File(System.getProperty("user.dir")), storage.directory());
        Assert.assertEquals(((1024 * 1024) * 32), storage.maxLogSegmentSize());
        Assert.assertEquals((1024 * 1024), storage.maxLogEntriesPerSegment());
        Assert.assertTrue(storage.dynamicCompaction());
        Assert.assertEquals(0.2, storage.freeDiskBuffer(), 0.01);
        Assert.assertTrue(storage.isFlushOnCommit());
        Assert.assertFalse(storage.isRetainStaleSnapshots());
        Assert.assertTrue(((storage.statistics().getFreeMemory()) > 0));
    }

    @Test
    public void testCustomConfiguration() throws Exception {
        RaftStorage storage = RaftStorage.builder().withPrefix("foo").withDirectory(new File(RaftStorageTest.PATH.toFile(), "foo")).withMaxSegmentSize((1024 * 1024)).withMaxEntriesPerSegment(1024).withDynamicCompaction(false).withFreeDiskBuffer(0.5).withFlushOnCommit(false).withRetainStaleSnapshots().build();
        Assert.assertEquals("foo", storage.prefix());
        Assert.assertEquals(new File(RaftStorageTest.PATH.toFile(), "foo"), storage.directory());
        Assert.assertEquals((1024 * 1024), storage.maxLogSegmentSize());
        Assert.assertEquals(1024, storage.maxLogEntriesPerSegment());
        Assert.assertFalse(storage.dynamicCompaction());
        Assert.assertEquals(0.5, storage.freeDiskBuffer(), 0.01);
        Assert.assertFalse(storage.isFlushOnCommit());
        Assert.assertTrue(storage.isRetainStaleSnapshots());
    }

    @Test
    public void testCustomConfiguration2() throws Exception {
        RaftStorage storage = RaftStorage.builder().withDirectory(((RaftStorageTest.PATH.toString()) + "/baz")).withDynamicCompaction().withFlushOnCommit().build();
        Assert.assertEquals(new File(RaftStorageTest.PATH.toFile(), "baz"), storage.directory());
        Assert.assertTrue(storage.dynamicCompaction());
        Assert.assertTrue(storage.isFlushOnCommit());
    }

    @Test
    public void testStorageLock() throws Exception {
        RaftStorage storage1 = RaftStorage.builder().withDirectory(RaftStorageTest.PATH.toFile()).withPrefix("test").build();
        Assert.assertTrue(storage1.lock("a"));
        RaftStorage storage2 = RaftStorage.builder().withDirectory(RaftStorageTest.PATH.toFile()).withPrefix("test").build();
        Assert.assertFalse(storage2.lock("b"));
        RaftStorage storage3 = RaftStorage.builder().withDirectory(RaftStorageTest.PATH.toFile()).withPrefix("test").build();
        Assert.assertTrue(storage3.lock("a"));
    }
}

