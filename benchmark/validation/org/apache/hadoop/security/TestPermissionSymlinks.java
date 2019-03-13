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
package org.apache.hadoop.security;


import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestWrapper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.AclTestHelpers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestPermissionSymlinks {
    private static final Logger LOG = LoggerFactory.getLogger(TestPermissionSymlinks.class);

    private static final Configuration conf = new HdfsConfiguration();

    // Non-super user to run commands with
    private static final UserGroupInformation user = UserGroupInformation.createRemoteUser("myuser");

    private static final Path linkParent = new Path("/symtest1");

    private static final Path targetParent = new Path("/symtest2");

    private static final Path link = new Path(TestPermissionSymlinks.linkParent, "link");

    private static final Path target = new Path(TestPermissionSymlinks.targetParent, "target");

    private static MiniDFSCluster cluster;

    private static FileSystem fs;

    private static FileSystemTestWrapper wrapper;

    @Test(timeout = 5000)
    public void testDelete() throws Exception {
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.linkParent, new FsPermission(((short) (365))));
        doDeleteLinkParentNotWritable();
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.linkParent, new FsPermission(((short) (511))));
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.targetParent, new FsPermission(((short) (365))));
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.target, new FsPermission(((short) (365))));
        doDeleteTargetParentAndTargetNotWritable();
    }

    @Test
    public void testAclDelete() throws Exception {
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.linkParent, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        doDeleteLinkParentNotWritable();
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.linkParent, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.targetParent, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.target, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        doDeleteTargetParentAndTargetNotWritable();
    }

    @Test(timeout = 5000)
    public void testReadWhenTargetNotReadable() throws Exception {
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.target, new FsPermission(((short) (0))));
        doReadTargetNotReadable();
    }

    @Test
    public void testAclReadTargetNotReadable() throws Exception {
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.target, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), NONE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, READ)));
        doReadTargetNotReadable();
    }

    @Test(timeout = 5000)
    public void testFileStatus() throws Exception {
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.target, new FsPermission(((short) (0))));
        doGetFileLinkStatusTargetNotReadable();
    }

    @Test
    public void testAclGetFileLinkStatusTargetNotReadable() throws Exception {
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.target, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), NONE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, READ)));
        doGetFileLinkStatusTargetNotReadable();
    }

    @Test(timeout = 5000)
    public void testRenameLinkTargetNotWritableFC() throws Exception {
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.target, new FsPermission(((short) (365))));
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.targetParent, new FsPermission(((short) (365))));
        doRenameLinkTargetNotWritableFC();
    }

    @Test
    public void testAclRenameTargetNotWritableFC() throws Exception {
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.target, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.targetParent, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        doRenameLinkTargetNotWritableFC();
    }

    @Test(timeout = 5000)
    public void testRenameSrcNotWritableFC() throws Exception {
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.linkParent, new FsPermission(((short) (365))));
        doRenameSrcNotWritableFC();
    }

    @Test
    public void testAclRenameSrcNotWritableFC() throws Exception {
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.linkParent, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        doRenameSrcNotWritableFC();
    }

    // Need separate FileSystem tests since the server-side impl is different
    // See {@link ClientProtocol#rename} and {@link ClientProtocol#rename2}.
    @Test(timeout = 5000)
    public void testRenameLinkTargetNotWritableFS() throws Exception {
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.target, new FsPermission(((short) (365))));
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.targetParent, new FsPermission(((short) (365))));
        doRenameLinkTargetNotWritableFS();
    }

    @Test
    public void testAclRenameTargetNotWritableFS() throws Exception {
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.target, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.targetParent, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        doRenameLinkTargetNotWritableFS();
    }

    @Test(timeout = 5000)
    public void testRenameSrcNotWritableFS() throws Exception {
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.linkParent, new FsPermission(((short) (365))));
        doRenameSrcNotWritableFS();
    }

    @Test
    public void testAclRenameSrcNotWritableFS() throws Exception {
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.linkParent, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getUserName(), READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ALL)));
        doRenameSrcNotWritableFS();
    }

    @Test
    public void testAccess() throws Exception {
        TestPermissionSymlinks.fs.setPermission(TestPermissionSymlinks.target, new FsPermission(((short) (2))));
        TestPermissionSymlinks.fs.setAcl(TestPermissionSymlinks.target, Arrays.asList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, USER, TestPermissionSymlinks.user.getShortUserName(), WRITE), AclTestHelpers.aclEntry(ACCESS, OTHER, WRITE)));
        FileContext myfc = TestPermissionSymlinks.user.doAs(new PrivilegedExceptionAction<FileContext>() {
            @Override
            public FileContext run() throws IOException {
                return FileContext.getFileContext(TestPermissionSymlinks.conf);
            }
        });
        // Path to targetChild via symlink
        myfc.access(TestPermissionSymlinks.link, FsAction.WRITE);
        try {
            myfc.access(TestPermissionSymlinks.link, FsAction.ALL);
            Assert.fail("The access call should have failed.");
        } catch (AccessControlException e) {
            // expected
        }
        Path badPath = new Path(TestPermissionSymlinks.link, "bad");
        try {
            myfc.access(badPath, FsAction.READ);
            Assert.fail("The access call should have failed");
        } catch (AccessControlException ace) {
            // expected
            String message = ace.getMessage();
            Assert.assertTrue(message, message.contains("is not a directory"));
            Assert.assertTrue(message.contains(TestPermissionSymlinks.target.toString()));
            Assert.assertFalse(message.contains(badPath.toString()));
        }
    }
}

