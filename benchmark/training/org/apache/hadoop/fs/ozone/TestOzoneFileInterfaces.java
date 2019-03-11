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
package org.apache.hadoop.fs.ozone;


import GlobalStorageStatistics.INSTANCE;
import OzoneConsts.OZONE_URI_SCHEME;
import StorageStatistics.CommonStatisticNames.OP_CREATE;
import StorageStatistics.CommonStatisticNames.OP_GET_FILE_STATUS;
import StorageStatistics.CommonStatisticNames.OP_OPEN;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.web.interfaces.StorageHandler;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test OzoneFileSystem Interfaces.
 *
 * This test will test the various interfaces i.e.
 * create, read, write, getFileStatus
 */
@RunWith(Parameterized.class)
public class TestOzoneFileInterfaces {
    private String rootPath;

    private String userName;

    private boolean setDefaultFs;

    private boolean useAbsolutePath;

    private static MiniOzoneCluster cluster = null;

    private static FileSystem fs;

    private static OzoneFileSystem o3fs;

    private static StorageHandler storageHandler;

    private OzoneFSStorageStatistics statistics;

    public TestOzoneFileInterfaces(boolean setDefaultFs, boolean useAbsolutePath) {
        this.setDefaultFs = setDefaultFs;
        this.useAbsolutePath = useAbsolutePath;
        INSTANCE.reset();
    }

    @Test
    public void testFileSystemInit() throws IOException {
        if (setDefaultFs) {
            Assert.assertTrue(("The initialized file system is not OzoneFileSystem but " + (TestOzoneFileInterfaces.fs.getClass())), ((TestOzoneFileInterfaces.fs) instanceof OzoneFileSystem));
            Assert.assertEquals(OZONE_URI_SCHEME, TestOzoneFileInterfaces.fs.getUri().getScheme());
            Assert.assertEquals(OZONE_URI_SCHEME, statistics.getScheme());
        }
    }

    @Test
    public void testOzFsReadWrite() throws IOException {
        long currentTime = Time.now();
        int stringLen = 20;
        String data = RandomStringUtils.randomAlphanumeric(stringLen);
        String filePath = RandomStringUtils.randomAlphanumeric(5);
        Path path = createPath(("/" + filePath));
        try (FSDataOutputStream stream = TestOzoneFileInterfaces.fs.create(path)) {
            stream.writeBytes(data);
        }
        Assert.assertEquals(statistics.getLong(OP_CREATE).longValue(), 1);
        Assert.assertEquals(statistics.getLong("objects_created").longValue(), 1);
        FileStatus status = TestOzoneFileInterfaces.fs.getFileStatus(path);
        Assert.assertEquals(statistics.getLong(OP_GET_FILE_STATUS).longValue(), 2);
        Assert.assertEquals(statistics.getLong("objects_query").longValue(), 1);
        // The timestamp of the newly created file should always be greater than
        // the time when the test was started
        Assert.assertTrue(("Modification time has not been recorded: " + status), ((status.getModificationTime()) > currentTime));
        Assert.assertFalse(status.isDirectory());
        Assert.assertEquals(FsPermission.getFileDefault(), status.getPermission());
        verifyOwnerGroup(status);
        try (FSDataInputStream inputStream = TestOzoneFileInterfaces.fs.open(path)) {
            byte[] buffer = new byte[stringLen];
            // This read will not change the offset inside the file
            int readBytes = inputStream.read(0, buffer, 0, buffer.length);
            String out = new String(buffer, 0, buffer.length, StandardCharsets.UTF_8);
            Assert.assertEquals(data, out);
            Assert.assertEquals(readBytes, buffer.length);
            Assert.assertEquals(0, inputStream.getPos());
            // The following read will change the internal offset
            readBytes = inputStream.read(buffer, 0, buffer.length);
            Assert.assertEquals(data, out);
            Assert.assertEquals(readBytes, buffer.length);
            Assert.assertEquals(buffer.length, inputStream.getPos());
        }
        Assert.assertEquals(statistics.getLong(OP_OPEN).longValue(), 1);
        Assert.assertEquals(statistics.getLong("objects_read").longValue(), 1);
    }

    @Test
    public void testDirectory() throws IOException {
        String dirPath = RandomStringUtils.randomAlphanumeric(5);
        Path path = createPath(("/" + dirPath));
        Assert.assertTrue(("Makedirs returned with false for the path " + path), TestOzoneFileInterfaces.fs.mkdirs(path));
        FileStatus status = TestOzoneFileInterfaces.fs.getFileStatus(path);
        Assert.assertTrue("The created path is not directory.", status.isDirectory());
        Assert.assertTrue(status.isDirectory());
        Assert.assertEquals(FsPermission.getDirDefault(), status.getPermission());
        verifyOwnerGroup(status);
        Assert.assertEquals(0, status.getLen());
        FileStatus[] statusList = TestOzoneFileInterfaces.fs.listStatus(createPath("/"));
        Assert.assertEquals(1, statusList.length);
        Assert.assertEquals(status, statusList[0]);
        FileStatus statusRoot = TestOzoneFileInterfaces.fs.getFileStatus(createPath("/"));
        Assert.assertTrue("Root dir (/) is not a directory.", status.isDirectory());
        Assert.assertEquals(0, status.getLen());
    }

    @Test
    public void testPathToKey() throws Exception {
        Assert.assertEquals("a/b/1", TestOzoneFileInterfaces.o3fs.pathToKey(new Path("/a/b/1")));
        Assert.assertEquals((("user/" + (getCurrentUser())) + "/key1/key2"), TestOzoneFileInterfaces.o3fs.pathToKey(new Path("key1/key2")));
        Assert.assertEquals("key1/key2", TestOzoneFileInterfaces.o3fs.pathToKey(new Path("o3fs://test1/key1/key2")));
    }
}

