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
package org.apache.hadoop.tools;


import DistCpConstants.SUCCESS;
import DistCpConstants.XATTRS_NOT_SUPPORTED;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.tools.util.DistCpTestUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.Test;


/**
 * Tests distcp in combination with HDFS XAttrs.
 */
public class TestDistCpWithXAttrs {
    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static FileSystem fs;

    // XAttrs
    private static final String name1 = "user.a1";

    private static final byte[] value1 = new byte[]{ 49, 50, 51 };

    private static final String name2 = "trusted.a2";

    private static final byte[] value2 = new byte[]{ 55, 56, 57 };

    private static final String name3 = "user.a3";

    private static final byte[] value3 = null;

    private static final String name4 = "user.a4";

    private static final byte[] value4 = null;

    private static final Path dir1 = new Path("/src/dir1");

    private static final Path subDir1 = new Path(TestDistCpWithXAttrs.dir1, "subdir1");

    private static final Path file1 = new Path("/src/file1");

    private static final Path dir2 = new Path("/src/dir2");

    private static final Path file2 = new Path(TestDistCpWithXAttrs.dir2, "file2");

    private static final Path file3 = new Path(TestDistCpWithXAttrs.dir2, "file3");

    private static final Path file4 = new Path(TestDistCpWithXAttrs.dir2, "file4");

    private static final Path dstDir1 = new Path("/dstPreserveXAttrs/dir1");

    private static final Path dstSubDir1 = new Path(TestDistCpWithXAttrs.dstDir1, "subdir1");

    private static final Path dstFile1 = new Path("/dstPreserveXAttrs/file1");

    private static final Path dstDir2 = new Path("/dstPreserveXAttrs/dir2");

    private static final Path dstFile2 = new Path(TestDistCpWithXAttrs.dstDir2, "file2");

    private static final Path dstFile3 = new Path(TestDistCpWithXAttrs.dstDir2, "file3");

    private static final Path dstFile4 = new Path(TestDistCpWithXAttrs.dstDir2, "file4");

    private static final String rootedSrcName = "/src";

    @Test
    public void testPreserveXAttrs() throws Exception {
        DistCpTestUtils.assertRunDistCp(SUCCESS, TestDistCpWithXAttrs.rootedSrcName, "/dstPreserveXAttrs", "-px", TestDistCpWithXAttrs.conf);
        // dstDir1
        Map<String, byte[]> xAttrs = Maps.newHashMap();
        xAttrs.put(TestDistCpWithXAttrs.name1, TestDistCpWithXAttrs.value1);
        xAttrs.put(TestDistCpWithXAttrs.name2, TestDistCpWithXAttrs.value2);
        DistCpTestUtils.assertXAttrs(TestDistCpWithXAttrs.dstDir1, TestDistCpWithXAttrs.fs, xAttrs);
        // dstSubDir1
        xAttrs.clear();
        xAttrs.put(TestDistCpWithXAttrs.name1, TestDistCpWithXAttrs.value1);
        xAttrs.put(TestDistCpWithXAttrs.name3, new byte[0]);
        DistCpTestUtils.assertXAttrs(TestDistCpWithXAttrs.dstSubDir1, TestDistCpWithXAttrs.fs, xAttrs);
        // dstFile1
        xAttrs.clear();
        xAttrs.put(TestDistCpWithXAttrs.name1, TestDistCpWithXAttrs.value1);
        xAttrs.put(TestDistCpWithXAttrs.name2, TestDistCpWithXAttrs.value2);
        xAttrs.put(TestDistCpWithXAttrs.name3, new byte[0]);
        DistCpTestUtils.assertXAttrs(TestDistCpWithXAttrs.dstFile1, TestDistCpWithXAttrs.fs, xAttrs);
        // dstDir2
        xAttrs.clear();
        xAttrs.put(TestDistCpWithXAttrs.name2, TestDistCpWithXAttrs.value2);
        DistCpTestUtils.assertXAttrs(TestDistCpWithXAttrs.dstDir2, TestDistCpWithXAttrs.fs, xAttrs);
        // dstFile2
        xAttrs.clear();
        xAttrs.put(TestDistCpWithXAttrs.name1, TestDistCpWithXAttrs.value1);
        xAttrs.put(TestDistCpWithXAttrs.name4, new byte[0]);
        DistCpTestUtils.assertXAttrs(TestDistCpWithXAttrs.dstFile2, TestDistCpWithXAttrs.fs, xAttrs);
        // dstFile3
        xAttrs.clear();
        xAttrs.put(TestDistCpWithXAttrs.name3, new byte[0]);
        xAttrs.put(TestDistCpWithXAttrs.name4, new byte[0]);
        DistCpTestUtils.assertXAttrs(TestDistCpWithXAttrs.dstFile3, TestDistCpWithXAttrs.fs, xAttrs);
        // dstFile4
        xAttrs.clear();
        DistCpTestUtils.assertXAttrs(TestDistCpWithXAttrs.dstFile4, TestDistCpWithXAttrs.fs, xAttrs);
    }

    @Test
    public void testXAttrsNotEnabled() throws Exception {
        try {
            TestDistCpWithXAttrs.restart(false);
            DistCpTestUtils.assertRunDistCp(XATTRS_NOT_SUPPORTED, TestDistCpWithXAttrs.rootedSrcName, "/dstXAttrsNotEnabled", "-px", TestDistCpWithXAttrs.conf);
        } finally {
            TestDistCpWithXAttrs.restart(true);
        }
    }

    @Test
    public void testXAttrsNotImplemented() throws Exception {
        DistCpTestUtils.assertRunDistCp(XATTRS_NOT_SUPPORTED, TestDistCpWithXAttrs.rootedSrcName, "stubfs://dstXAttrsNotImplemented", "-px", TestDistCpWithXAttrs.conf);
    }

    /**
     * Stub FileSystem implementation used for testing the case of attempting
     * distcp with XAttrs preserved on a file system that does not support XAttrs.
     * The base class implementation throws UnsupportedOperationException for
     * the XAttr methods, so we don't need to override them.
     */
    public static class StubFileSystem extends FileSystem {
        @Override
        public FSDataOutputStream append(Path f, int bufferSize, Progressable progress) throws IOException {
            return null;
        }

        @Override
        public FSDataOutputStream create(Path f, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress) throws IOException {
            return null;
        }

        @Override
        public boolean delete(Path f, boolean recursive) throws IOException {
            return false;
        }

        @Override
        public FileStatus getFileStatus(Path f) throws IOException {
            return null;
        }

        @Override
        public URI getUri() {
            return URI.create("stubfs:///");
        }

        @Override
        public Path getWorkingDirectory() {
            return new Path(Path.SEPARATOR);
        }

        @Override
        public FileStatus[] listStatus(Path f) throws IOException {
            return new FileStatus[0];
        }

        @Override
        public boolean mkdirs(Path f, FsPermission permission) throws IOException {
            return false;
        }

        @Override
        public FSDataInputStream open(Path f, int bufferSize) throws IOException {
            return null;
        }

        @Override
        public boolean rename(Path src, Path dst) throws IOException {
            return false;
        }

        @Override
        public void setWorkingDirectory(Path dir) {
        }
    }
}

