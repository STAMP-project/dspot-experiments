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
package org.apache.hadoop.hdfs;


import CreateEncryptionZoneFlag.NO_TRASH;
import java.io.File;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.client.CreateEncryptionZoneFlag;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests interaction of encryption zones with HA failover.
 */
public class TestEncryptionZonesWithHA {
    private Configuration conf;

    private MiniDFSCluster cluster;

    private NameNode nn0;

    private NameNode nn1;

    private DistributedFileSystem fs;

    private HdfsAdmin dfsAdmin0;

    private HdfsAdmin dfsAdmin1;

    private FileSystemTestHelper fsHelper;

    private File testRootDir;

    private final String TEST_KEY = "test_key";

    protected static final EnumSet<CreateEncryptionZoneFlag> NO_TRASH = EnumSet.of(CreateEncryptionZoneFlag.NO_TRASH);

    /**
     * Test that encryption zones are properly tracked by the standby.
     */
    @Test(timeout = 60000)
    public void testEncryptionZonesTrackedOnStandby() throws Exception {
        final int len = 8196;
        final Path dir = new Path("/enc");
        final Path dirChild = new Path(dir, "child");
        final Path dirFile = new Path(dir, "file");
        fs.mkdir(dir, FsPermission.getDirDefault());
        dfsAdmin0.createEncryptionZone(dir, TEST_KEY, TestEncryptionZonesWithHA.NO_TRASH);
        fs.mkdir(dirChild, FsPermission.getDirDefault());
        DFSTestUtil.createFile(fs, dirFile, len, ((short) (1)), 65261);
        String contents = DFSTestUtil.readFile(fs, dirFile);
        // Failover the current standby to active.
        HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
        cluster.shutdownNameNode(0);
        cluster.transitionToActive(1);
        Assert.assertEquals("Got unexpected ez path", dir.toString(), dfsAdmin1.getEncryptionZoneForPath(dir).getPath().toString());
        Assert.assertEquals("Got unexpected ez path", dir.toString(), dfsAdmin1.getEncryptionZoneForPath(dirChild).getPath().toString());
        Assert.assertEquals("File contents after failover were changed", contents, DFSTestUtil.readFile(fs, dirFile));
    }
}

