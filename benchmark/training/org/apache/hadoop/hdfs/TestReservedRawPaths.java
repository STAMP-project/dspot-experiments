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
import DirOp.READ;
import FSDirectory.DOT_INODES_STRING;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContextTestWrapper;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.FileSystemTestWrapper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.client.CreateEncryptionZoneFlag;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.server.namenode.FSDirectory;
import org.apache.hadoop.hdfs.server.namenode.INodesInPath;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestReservedRawPaths {
    private Configuration conf;

    private FileSystemTestHelper fsHelper;

    private MiniDFSCluster cluster;

    private HdfsAdmin dfsAdmin;

    private DistributedFileSystem fs;

    private final String TEST_KEY = "test_key";

    protected FileSystemTestWrapper fsWrapper;

    protected FileContextTestWrapper fcWrapper;

    protected static final EnumSet<CreateEncryptionZoneFlag> NO_TRASH = EnumSet.of(CreateEncryptionZoneFlag.NO_TRASH);

    /**
     * Verify resolving path will return an iip that tracks if the original
     * path was a raw path.
     */
    @Test(timeout = 120000)
    public void testINodesInPath() throws IOException {
        FSDirectory fsd = cluster.getNamesystem().getFSDirectory();
        final String path = "/path";
        INodesInPath iip = fsd.resolvePath(null, path, READ);
        Assert.assertFalse(iip.isRaw());
        Assert.assertEquals(path, iip.getPath());
        iip = fsd.resolvePath(null, ("/.reserved/raw" + path), READ);
        Assert.assertTrue(iip.isRaw());
        Assert.assertEquals(path, iip.getPath());
    }

    /**
     * Basic read/write tests of raw files.
     * Create a non-encrypted file
     * Create an encryption zone
     * Verify that non-encrypted file contents and decrypted file in EZ are equal
     * Compare the raw encrypted bytes of the file with the decrypted version to
     *   ensure they're different
     * Compare the raw and non-raw versions of the non-encrypted file to ensure
     *   they're the same.
     */
    @Test(timeout = 120000)
    public void testReadWriteRaw() throws Exception {
        // Create a base file for comparison
        final Path baseFile = new Path("/base");
        final int len = 8192;
        DFSTestUtil.createFile(fs, baseFile, len, ((short) (1)), 65261);
        // Create the first enc file
        final Path zone = new Path("/zone");
        fs.mkdirs(zone);
        dfsAdmin.createEncryptionZone(zone, TEST_KEY, TestReservedRawPaths.NO_TRASH);
        final Path encFile1 = new Path(zone, "myfile");
        DFSTestUtil.createFile(fs, encFile1, len, ((short) (1)), 65261);
        // Read them back in and compare byte-by-byte
        DFSTestUtil.verifyFilesEqual(fs, baseFile, encFile1, len);
        // Raw file should be different from encrypted file
        final Path encFile1Raw = new Path(zone, "/.reserved/raw/zone/myfile");
        DFSTestUtil.verifyFilesNotEqual(fs, encFile1Raw, encFile1, len);
        // Raw file should be same as /base which is not in an EZ
        final Path baseFileRaw = new Path(zone, "/.reserved/raw/base");
        DFSTestUtil.verifyFilesEqual(fs, baseFile, baseFileRaw, len);
    }

    /**
     * Tests that getFileStatus on raw and non raw resolve to the same
     * file.
     */
    @Test(timeout = 120000)
    public void testGetFileStatus() throws Exception {
        final Path zone = new Path("zone");
        final Path slashZone = new Path("/", zone);
        fs.mkdirs(slashZone);
        dfsAdmin.createEncryptionZone(slashZone, TEST_KEY, TestReservedRawPaths.NO_TRASH);
        final Path base = new Path("base");
        final Path reservedRaw = new Path("/.reserved/raw");
        final Path baseRaw = new Path(reservedRaw, base);
        final int len = 8192;
        DFSTestUtil.createFile(fs, baseRaw, len, ((short) (1)), 65261);
        assertPathEquals(new Path("/", base), baseRaw);
        /* Repeat the test for a file in an ez. */
        final Path ezEncFile = new Path(slashZone, base);
        final Path ezRawEncFile = new Path(new Path(reservedRaw, zone), base);
        DFSTestUtil.createFile(fs, ezEncFile, len, ((short) (1)), 65261);
        assertPathEquals(ezEncFile, ezRawEncFile);
    }

    @Test(timeout = 120000)
    public void testReservedRoot() throws Exception {
        final Path root = new Path("/");
        final Path rawRoot = new Path("/.reserved/raw");
        final Path rawRootSlash = new Path("/.reserved/raw/");
        assertPathEquals(root, rawRoot);
        assertPathEquals(root, rawRootSlash);
    }

    /* Verify mkdir works ok in .reserved/raw directory. */
    @Test(timeout = 120000)
    public void testReservedRawMkdir() throws Exception {
        final Path zone = new Path("zone");
        final Path slashZone = new Path("/", zone);
        fs.mkdirs(slashZone);
        dfsAdmin.createEncryptionZone(slashZone, TEST_KEY, TestReservedRawPaths.NO_TRASH);
        final Path rawRoot = new Path("/.reserved/raw");
        final Path dir1 = new Path("dir1");
        final Path rawDir1 = new Path(rawRoot, dir1);
        fs.mkdirs(rawDir1);
        assertPathEquals(rawDir1, new Path("/", dir1));
        fs.delete(rawDir1, true);
        final Path rawZone = new Path(rawRoot, zone);
        final Path rawDir1EZ = new Path(rawZone, dir1);
        fs.mkdirs(rawDir1EZ);
        assertPathEquals(rawDir1EZ, new Path(slashZone, dir1));
        fs.delete(rawDir1EZ, true);
    }

    @Test(timeout = 120000)
    public void testRelativePathnames() throws Exception {
        final Path baseFileRaw = new Path("/.reserved/raw/base");
        final int len = 8192;
        DFSTestUtil.createFile(fs, baseFileRaw, len, ((short) (1)), 65261);
        final Path root = new Path("/");
        final Path rawRoot = new Path("/.reserved/raw");
        assertPathEquals(root, new Path(rawRoot, "../raw"));
        assertPathEquals(root, new Path(rawRoot, "../../.reserved/raw"));
        assertPathEquals(baseFileRaw, new Path(rawRoot, "../raw/base"));
        assertPathEquals(baseFileRaw, new Path(rawRoot, "../../.reserved/raw/base"));
        assertPathEquals(baseFileRaw, new Path(rawRoot, "../../.reserved/raw/base/../base"));
        assertPathEquals(baseFileRaw, new Path("/.reserved/../.reserved/raw/../raw/base"));
    }

    @Test(timeout = 120000)
    public void testUserReadAccessOnly() throws Exception {
        final Path zone = new Path("zone");
        final Path slashZone = new Path("/", zone);
        fs.mkdirs(slashZone);
        dfsAdmin.createEncryptionZone(slashZone, TEST_KEY, TestReservedRawPaths.NO_TRASH);
        final Path base = new Path("base");
        final Path reservedRaw = new Path("/.reserved/raw");
        final int len = 8192;
        /* Test failure of create file in reserved/raw as non admin */
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final DistributedFileSystem fs = cluster.getFileSystem();
                try {
                    final Path ezRawEncFile = new Path(new Path(reservedRaw, zone), base);
                    DFSTestUtil.createFile(fs, ezRawEncFile, len, ((short) (1)), 65261);
                    Assert.fail("access to /.reserved/raw is superuser-only operation");
                } catch (AccessControlException e) {
                    GenericTestUtils.assertExceptionContains("Superuser privilege is required", e);
                }
                return null;
            }
        });
        /* Test success of getFileStatus in reserved/raw as non admin since
        read is allowed.
         */
        final Path ezRawEncFile = new Path(new Path(reservedRaw, zone), base);
        DFSTestUtil.createFile(fs, ezRawEncFile, len, ((short) (1)), 65261);
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final DistributedFileSystem fs = cluster.getFileSystem();
                fs.getFileStatus(ezRawEncFile);
                return null;
            }
        });
        /* Test success of listStatus in reserved/raw as non admin since read is
        allowed.
         */
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final DistributedFileSystem fs = cluster.getFileSystem();
                fs.listStatus(ezRawEncFile);
                return null;
            }
        });
        fs.setPermission(new Path("/"), new FsPermission(((short) (511))));
        /* Test failure of mkdir in reserved/raw as non admin */
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final DistributedFileSystem fs = cluster.getFileSystem();
                final Path d1 = new Path(reservedRaw, "dir1");
                try {
                    fs.mkdirs(d1);
                    Assert.fail("access to /.reserved/raw is superuser-only operation");
                } catch (AccessControlException e) {
                    GenericTestUtils.assertExceptionContains("Superuser privilege is required", e);
                }
                return null;
            }
        });
    }

    @Test(timeout = 120000)
    public void testListDotReserved() throws Exception {
        // Create a base file for comparison
        final Path baseFileRaw = new Path("/.reserved/raw/base");
        final int len = 8192;
        DFSTestUtil.createFile(fs, baseFileRaw, len, ((short) (1)), 65261);
        /* Ensure that you can list /.reserved, with results: raw and .inodes */
        FileStatus[] stats = fs.listStatus(new Path("/.reserved"));
        Assert.assertEquals(2, stats.length);
        Assert.assertEquals(DOT_INODES_STRING, stats[0].getPath().getName());
        Assert.assertEquals("raw", stats[1].getPath().getName());
        try {
            fs.listStatus(new Path("/.reserved/.inodes"));
            Assert.fail("expected FNFE");
        } catch (FileNotFoundException e) {
            GenericTestUtils.assertExceptionContains("/.reserved/.inodes does not exist", e);
        }
        final FileStatus[] fileStatuses = fs.listStatus(new Path("/.reserved/raw"));
        Assert.assertEquals("expected 1 entry", fileStatuses.length, 1);
        GenericTestUtils.assertMatches(fileStatuses[0].getPath().toString(), "/.reserved/raw/base");
    }

    @Test(timeout = 120000)
    public void testListRecursive() throws Exception {
        Path rootPath = new Path("/");
        Path p = rootPath;
        for (int i = 0; i < 3; i++) {
            p = new Path(p, ("dir" + i));
            fs.mkdirs(p);
        }
        Path curPath = new Path("/.reserved/raw");
        int cnt = 0;
        FileStatus[] fileStatuses = fs.listStatus(curPath);
        while ((fileStatuses != null) && ((fileStatuses.length) > 0)) {
            FileStatus f = fileStatuses[0];
            GenericTestUtils.assertMatches(f.getPath().toString(), "/.reserved/raw");
            curPath = Path.getPathWithoutSchemeAndAuthority(f.getPath());
            cnt++;
            fileStatuses = fs.listStatus(curPath);
        } 
        Assert.assertEquals(3, cnt);
    }
}

