/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.federation.router;


import HdfsFileStatus.EMPTY_NAME;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.DirectoryListing;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.apache.hadoop.hdfs.server.federation.resolver.MountTableResolver;
import org.apache.hadoop.hdfs.server.federation.store.records.MountTable;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test a router end-to-end including the MountTable.
 */
public class TestRouterMountTable {
    private static StateStoreDFSCluster cluster;

    private static MiniRouterDFSCluster.NamenodeContext nnContext;

    private static MiniRouterDFSCluster.RouterContext routerContext;

    private static MountTableResolver mountTable;

    private static ClientProtocol routerProtocol;

    @Test
    public void testReadOnly() throws Exception {
        // Add a read only entry
        MountTable readOnlyEntry = MountTable.newInstance("/readonly", Collections.singletonMap("ns0", "/testdir"));
        readOnlyEntry.setReadOnly(true);
        Assert.assertTrue(addMountTable(readOnlyEntry));
        // Add a regular entry
        MountTable regularEntry = MountTable.newInstance("/regular", Collections.singletonMap("ns0", "/testdir"));
        Assert.assertTrue(addMountTable(regularEntry));
        // Create a folder which should show in all locations
        final FileSystem nnFs = TestRouterMountTable.nnContext.getFileSystem();
        final FileSystem routerFs = TestRouterMountTable.routerContext.getFileSystem();
        Assert.assertTrue(routerFs.mkdirs(new Path("/regular/newdir")));
        FileStatus dirStatusNn = nnFs.getFileStatus(new Path("/testdir/newdir"));
        Assert.assertTrue(dirStatusNn.isDirectory());
        FileStatus dirStatusRegular = routerFs.getFileStatus(new Path("/regular/newdir"));
        Assert.assertTrue(dirStatusRegular.isDirectory());
        FileStatus dirStatusReadOnly = routerFs.getFileStatus(new Path("/readonly/newdir"));
        Assert.assertTrue(dirStatusReadOnly.isDirectory());
        // It should fail writing into a read only path
        try {
            routerFs.mkdirs(new Path("/readonly/newdirfail"));
            Assert.fail("We should not be able to write into a read only mount point");
        } catch (IOException ioe) {
            String msg = ioe.getMessage();
            Assert.assertTrue(msg.startsWith("/readonly/newdirfail is in a read only mount point"));
        }
    }

    /**
     * Verify that the file/dir listing contains correct date/time information.
     */
    @Test
    public void testListFilesTime() throws Exception {
        Long beforeCreatingTime = Time.now();
        // Add mount table entry
        MountTable addEntry = MountTable.newInstance("/testdir", Collections.singletonMap("ns0", "/testdir"));
        Assert.assertTrue(addMountTable(addEntry));
        addEntry = MountTable.newInstance("/testdir2", Collections.singletonMap("ns0", "/testdir2"));
        Assert.assertTrue(addMountTable(addEntry));
        addEntry = MountTable.newInstance("/testdir/subdir", Collections.singletonMap("ns0", "/testdir/subdir"));
        Assert.assertTrue(addMountTable(addEntry));
        addEntry = MountTable.newInstance("/testdir3/subdir1", Collections.singletonMap("ns0", "/testdir3"));
        Assert.assertTrue(addMountTable(addEntry));
        addEntry = MountTable.newInstance("/testA/testB/testC/testD", Collections.singletonMap("ns0", "/test"));
        Assert.assertTrue(addMountTable(addEntry));
        // Create test dir in NN
        final FileSystem nnFs = TestRouterMountTable.nnContext.getFileSystem();
        Assert.assertTrue(nnFs.mkdirs(new Path("/newdir")));
        Map<String, Long> pathModTime = new TreeMap<>();
        for (String mount : TestRouterMountTable.mountTable.getMountPoints("/")) {
            if ((TestRouterMountTable.mountTable.getMountPoint(("/" + mount))) != null) {
                pathModTime.put(mount, TestRouterMountTable.mountTable.getMountPoint(("/" + mount)).getDateModified());
            } else {
                List<MountTable> entries = TestRouterMountTable.mountTable.getMounts(("/" + mount));
                for (MountTable entry : entries) {
                    if (((pathModTime.get(mount)) == null) || ((pathModTime.get(mount)) < (entry.getDateModified()))) {
                        pathModTime.put(mount, entry.getDateModified());
                    }
                }
            }
        }
        FileStatus[] iterator = nnFs.listStatus(new Path("/"));
        for (FileStatus file : iterator) {
            pathModTime.put(file.getPath().getName(), file.getModificationTime());
        }
        // Fetch listing
        DirectoryListing listing = TestRouterMountTable.routerProtocol.getListing("/", EMPTY_NAME, false);
        Iterator<String> pathModTimeIterator = pathModTime.keySet().iterator();
        // Match date/time for each path returned
        for (HdfsFileStatus f : listing.getPartialListing()) {
            String fileName = pathModTimeIterator.next();
            String currentFile = f.getFullPath(new Path("/")).getName();
            Long currentTime = f.getModificationTime();
            Long expectedTime = pathModTime.get(currentFile);
            Assert.assertEquals(currentFile, fileName);
            Assert.assertTrue((currentTime > beforeCreatingTime));
            Assert.assertEquals(currentTime, expectedTime);
        }
        // Verify the total number of results found/matched
        Assert.assertEquals(pathModTime.size(), listing.getPartialListing().length);
    }
}

