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


import DistCpConstants.ACLS_NOT_SUPPORTED;
import DistCpConstants.SUCCESS;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.util.Progressable;
import org.junit.Test;


/**
 * Tests distcp in combination with HDFS ACLs.
 */
public class TestDistCpWithAcls {
    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static FileSystem fs;

    @Test
    public void testPreserveAcls() throws Exception {
        TestDistCpWithAcls.assertRunDistCp(SUCCESS, "/dstPreserveAcls");
        TestDistCpWithAcls.assertAclEntries("/dstPreserveAcls/dir1", new AclEntry[]{ TestDistCpWithAcls.aclEntry(DEFAULT, USER, ALL), TestDistCpWithAcls.aclEntry(DEFAULT, USER, "bruce", ALL), TestDistCpWithAcls.aclEntry(DEFAULT, GROUP, READ_EXECUTE), TestDistCpWithAcls.aclEntry(DEFAULT, MASK, ALL), TestDistCpWithAcls.aclEntry(DEFAULT, OTHER, READ_EXECUTE) });
        TestDistCpWithAcls.assertPermission("/dstPreserveAcls/dir1", ((short) (493)));
        TestDistCpWithAcls.assertAclEntries("/dstPreserveAcls/dir1/subdir1", new AclEntry[]{  });
        TestDistCpWithAcls.assertPermission("/dstPreserveAcls/dir1/subdir1", ((short) (493)));
        TestDistCpWithAcls.assertAclEntries("/dstPreserveAcls/dir2", new AclEntry[]{  });
        TestDistCpWithAcls.assertPermission("/dstPreserveAcls/dir2", ((short) (493)));
        TestDistCpWithAcls.assertAclEntries("/dstPreserveAcls/dir2/file2", new AclEntry[]{ TestDistCpWithAcls.aclEntry(ACCESS, GROUP, READ), TestDistCpWithAcls.aclEntry(ACCESS, GROUP, "sales", NONE) });
        TestDistCpWithAcls.assertPermission("/dstPreserveAcls/dir2/file2", ((short) (420)));
        TestDistCpWithAcls.assertAclEntries("/dstPreserveAcls/dir2/file3", new AclEntry[]{  });
        TestDistCpWithAcls.assertPermission("/dstPreserveAcls/dir2/file3", ((short) (432)));
        TestDistCpWithAcls.assertAclEntries("/dstPreserveAcls/dir3sticky", new AclEntry[]{  });
        TestDistCpWithAcls.assertPermission("/dstPreserveAcls/dir3sticky", ((short) (1023)));
        TestDistCpWithAcls.assertAclEntries("/dstPreserveAcls/file1", new AclEntry[]{ TestDistCpWithAcls.aclEntry(ACCESS, USER, "diana", READ), TestDistCpWithAcls.aclEntry(ACCESS, GROUP, READ) });
        TestDistCpWithAcls.assertPermission("/dstPreserveAcls/file1", ((short) (420)));
    }

    @Test
    public void testAclsNotEnabled() throws Exception {
        try {
            TestDistCpWithAcls.restart(false);
            TestDistCpWithAcls.assertRunDistCp(ACLS_NOT_SUPPORTED, "/dstAclsNotEnabled");
        } finally {
            TestDistCpWithAcls.restart(true);
        }
    }

    @Test
    public void testAclsNotImplemented() throws Exception {
        TestDistCpWithAcls.assertRunDistCp(ACLS_NOT_SUPPORTED, "stubfs://dstAclsNotImplemented");
    }

    /**
     * Stub FileSystem implementation used for testing the case of attempting
     * distcp with ACLs preserved on a file system that does not support ACLs.
     * The base class implementation throws UnsupportedOperationException for the
     * ACL methods, so we don't need to override them.
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

