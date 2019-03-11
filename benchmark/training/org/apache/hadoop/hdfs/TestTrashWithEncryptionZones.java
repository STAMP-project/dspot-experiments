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
import CreateEncryptionZoneFlag.PROVISION_TRASH;
import java.io.File;
import java.security.PrivilegedExceptionAction;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.FileSystemTestWrapper;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.hdfs.client.CreateEncryptionZoneFlag;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests Trash functionality in Encryption Zones.
 */
public class TestTrashWithEncryptionZones {
    private Configuration conf;

    private FileSystemTestHelper fsHelper;

    private MiniDFSCluster cluster;

    private HdfsAdmin dfsAdmin;

    private DistributedFileSystem fs;

    private File testRootDir;

    private static final String TEST_KEY = "test_key";

    private FileSystemTestWrapper fsWrapper;

    private static Configuration clientConf;

    private static FsShell shell;

    private static AtomicInteger zoneCounter = new AtomicInteger(1);

    private static AtomicInteger fileCounter = new AtomicInteger(1);

    private static final int LEN = 8192;

    private static final EnumSet<CreateEncryptionZoneFlag> NO_TRASH = EnumSet.of(CreateEncryptionZoneFlag.NO_TRASH);

    private static final EnumSet<CreateEncryptionZoneFlag> PROVISION_TRASH = EnumSet.of(CreateEncryptionZoneFlag.PROVISION_TRASH);

    @Test
    public void testDeleteWithinEncryptionZone() throws Exception {
        final Path zone = new Path("/zones");
        fs.mkdirs(zone);
        final Path zone1 = new Path(("/zones/zone" + (TestTrashWithEncryptionZones.zoneCounter.getAndIncrement())));
        fs.mkdirs(zone1);
        dfsAdmin.createEncryptionZone(zone1, TestTrashWithEncryptionZones.TEST_KEY, TestTrashWithEncryptionZones.PROVISION_TRASH);
        final Path encFile1 = new Path(zone1, ("encFile" + (TestTrashWithEncryptionZones.fileCounter.getAndIncrement())));
        DFSTestUtil.createFile(fs, encFile1, TestTrashWithEncryptionZones.LEN, ((short) (1)), 65261);
        // Verify file deletion
        DFSTestUtil.verifyDelete(TestTrashWithEncryptionZones.shell, fs, encFile1, true);
        // Verify directory deletion
        DFSTestUtil.verifyDelete(TestTrashWithEncryptionZones.shell, fs, zone1, true);
    }

    @Test
    public void testDeleteEZWithMultipleUsers() throws Exception {
        final Path zone = new Path("/zones");
        fs.mkdirs(zone);
        final Path zone1 = new Path(("/zones/zone" + (TestTrashWithEncryptionZones.zoneCounter.getAndIncrement())));
        fs.mkdirs(zone1);
        dfsAdmin.createEncryptionZone(zone1, TestTrashWithEncryptionZones.TEST_KEY, TestTrashWithEncryptionZones.NO_TRASH);
        fsWrapper.setPermission(zone1, new org.apache.hadoop.fs.permission.FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL));
        final Path encFile1 = new Path(zone1, ("encFile" + (TestTrashWithEncryptionZones.fileCounter.getAndIncrement())));
        DFSTestUtil.createFile(fs, encFile1, TestTrashWithEncryptionZones.LEN, ((short) (1)), 65261);
        // create a non-privileged user
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        final Path encFile2 = new Path(zone1, ("encFile" + (TestTrashWithEncryptionZones.fileCounter.getAndIncrement())));
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                // create a file /zones/zone1/encFile2 in EZ
                // this file is owned by user:mygroup
                FileSystem fs2 = FileSystem.get(cluster.getConfiguration(0));
                DFSTestUtil.createFile(fs2, encFile2, TestTrashWithEncryptionZones.LEN, ((short) (1)), 65261);
                // Delete /zones/zone1/encFile2, which moves the file to
                // /zones/zone1/.Trash/user/Current/zones/zone1/encFile2
                DFSTestUtil.verifyDelete(TestTrashWithEncryptionZones.shell, fs, encFile2, true);
                // Delete /zones/zone1 should not succeed as current user is not admin
                String[] argv = new String[]{ "-rm", "-r", zone1.toString() };
                int res = ToolRunner.run(TestTrashWithEncryptionZones.shell, argv);
                Assert.assertEquals((("Non-admin could delete an encryption zone with multiple" + " users : ") + zone1), 1, res);
                return null;
            }
        });
        TestTrashWithEncryptionZones.shell = new FsShell(TestTrashWithEncryptionZones.clientConf);
        DFSTestUtil.verifyDelete(TestTrashWithEncryptionZones.shell, fs, zone1, true);
    }
}

