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
package org.apache.hadoop.fs.viewfs;


import FsConstants.VIEWFS_URI;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Verify that viewfs propagates certain methods to the underlying fs
 */
public class TestViewFileSystemDelegation {
    // extends ViewFileSystemTestSetup {
    static Configuration conf;

    static FileSystem viewFs;

    static TestViewFileSystemDelegation.FakeFileSystem fs1;

    static TestViewFileSystemDelegation.FakeFileSystem fs2;

    @Test
    public void testSanity() {
        Assert.assertEquals("fs1:/", TestViewFileSystemDelegation.fs1.getUri().toString());
        Assert.assertEquals("fs2:/", TestViewFileSystemDelegation.fs2.getUri().toString());
    }

    @Test
    public void testVerifyChecksum() throws Exception {
        checkVerifyChecksum(false);
        checkVerifyChecksum(true);
    }

    /**
     * Tests that ViewFileSystem dispatches calls for every ACL method through the
     * mount table to the correct underlying FileSystem with all Path arguments
     * translated as required.
     */
    @Test
    public void testAclMethods() throws Exception {
        Configuration conf = ViewFileSystemTestSetup.createConfig();
        FileSystem mockFs1 = TestViewFileSystemDelegation.setupMockFileSystem(conf, new URI("mockfs1:/"));
        FileSystem mockFs2 = TestViewFileSystemDelegation.setupMockFileSystem(conf, new URI("mockfs2:/"));
        FileSystem viewFs = FileSystem.get(VIEWFS_URI, conf);
        Path viewFsPath1 = new Path("/mounts/mockfs1/a/b/c");
        Path mockFsPath1 = new Path("/a/b/c");
        Path viewFsPath2 = new Path("/mounts/mockfs2/d/e/f");
        Path mockFsPath2 = new Path("/d/e/f");
        List<AclEntry> entries = Collections.emptyList();
        viewFs.modifyAclEntries(viewFsPath1, entries);
        Mockito.verify(mockFs1).modifyAclEntries(mockFsPath1, entries);
        viewFs.modifyAclEntries(viewFsPath2, entries);
        Mockito.verify(mockFs2).modifyAclEntries(mockFsPath2, entries);
        viewFs.removeAclEntries(viewFsPath1, entries);
        Mockito.verify(mockFs1).removeAclEntries(mockFsPath1, entries);
        viewFs.removeAclEntries(viewFsPath2, entries);
        Mockito.verify(mockFs2).removeAclEntries(mockFsPath2, entries);
        viewFs.removeDefaultAcl(viewFsPath1);
        Mockito.verify(mockFs1).removeDefaultAcl(mockFsPath1);
        viewFs.removeDefaultAcl(viewFsPath2);
        Mockito.verify(mockFs2).removeDefaultAcl(mockFsPath2);
        viewFs.removeAcl(viewFsPath1);
        Mockito.verify(mockFs1).removeAcl(mockFsPath1);
        viewFs.removeAcl(viewFsPath2);
        Mockito.verify(mockFs2).removeAcl(mockFsPath2);
        viewFs.setAcl(viewFsPath1, entries);
        Mockito.verify(mockFs1).setAcl(mockFsPath1, entries);
        viewFs.setAcl(viewFsPath2, entries);
        Mockito.verify(mockFs2).setAcl(mockFsPath2, entries);
        viewFs.getAclStatus(viewFsPath1);
        Mockito.verify(mockFs1).getAclStatus(mockFsPath1);
        viewFs.getAclStatus(viewFsPath2);
        Mockito.verify(mockFs2).getAclStatus(mockFsPath2);
    }

    static class FakeFileSystem extends LocalFileSystem {
        boolean verifyChecksum = true;

        URI uri;

        @Override
        public void initialize(URI uri, Configuration conf) throws IOException {
            super.initialize(uri, conf);
            this.uri = uri;
        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public void setVerifyChecksum(boolean verifyChecksum) {
            this.verifyChecksum = verifyChecksum;
        }

        public boolean getVerifyChecksum() {
            return verifyChecksum;
        }
    }
}

