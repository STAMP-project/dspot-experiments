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


import com.google.common.collect.Lists;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclEntryScope;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.AclTestHelpers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify ACL through ViewFileSystem functionality.
 */
public class TestViewFileSystemWithAcls {
    private static MiniDFSCluster cluster;

    private static Configuration clusterConf = new Configuration();

    private static FileSystem fHdfs;

    private static FileSystem fHdfs2;

    private FileSystem fsView;

    private Configuration fsViewConf;

    private FileSystem fsTarget;

    private FileSystem fsTarget2;

    private Path targetTestRoot;

    private Path targetTestRoot2;

    private Path mountOnNn1;

    private Path mountOnNn2;

    private FileSystemTestHelper fileSystemTestHelper = new FileSystemTestHelper("/tmp/TestViewFileSystemWithAcls");

    /**
     * Verify a ViewFs wrapped over multiple federated NameNodes will
     * dispatch the ACL operations to the correct NameNode.
     */
    @Test
    public void testAclOnMountEntry() throws Exception {
        // Set ACLs on the first namespace and verify they are correct
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, OTHER, NONE));
        fsView.setAcl(mountOnNn1, aclSpec);
        AclEntry[] expected = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ) };
        Assert.assertArrayEquals(expected, aclEntryArray(fsView.getAclStatus(mountOnNn1)));
        // Double-check by getting ACL status using FileSystem
        // instead of ViewFs
        Assert.assertArrayEquals(expected, aclEntryArray(TestViewFileSystemWithAcls.fHdfs.getAclStatus(targetTestRoot)));
        // Modify the ACL entries on the first namespace
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, USER, "foo", READ));
        fsView.modifyAclEntries(mountOnNn1, aclSpec);
        expected = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, USER, READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, USER, "foo", READ), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, GROUP, READ), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, MASK, READ), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, OTHER, NONE) };
        Assert.assertArrayEquals(expected, aclEntryArray(fsView.getAclStatus(mountOnNn1)));
        fsView.removeDefaultAcl(mountOnNn1);
        expected = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ) };
        Assert.assertArrayEquals(expected, aclEntryArray(fsView.getAclStatus(mountOnNn1)));
        Assert.assertArrayEquals(expected, aclEntryArray(TestViewFileSystemWithAcls.fHdfs.getAclStatus(targetTestRoot)));
        // Paranoid check: verify the other namespace does not
        // have ACLs set on the same path.
        Assert.assertEquals(0, fsView.getAclStatus(mountOnNn2).getEntries().size());
        Assert.assertEquals(0, TestViewFileSystemWithAcls.fHdfs2.getAclStatus(targetTestRoot2).getEntries().size());
        // Remove the ACL entries on the first namespace
        fsView.removeAcl(mountOnNn1);
        Assert.assertEquals(0, fsView.getAclStatus(mountOnNn1).getEntries().size());
        Assert.assertEquals(0, TestViewFileSystemWithAcls.fHdfs.getAclStatus(targetTestRoot).getEntries().size());
        // Now set ACLs on the second namespace
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "bar", READ));
        fsView.modifyAclEntries(mountOnNn2, aclSpec);
        expected = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "bar", READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ_EXECUTE) };
        Assert.assertArrayEquals(expected, aclEntryArray(fsView.getAclStatus(mountOnNn2)));
        Assert.assertArrayEquals(expected, aclEntryArray(TestViewFileSystemWithAcls.fHdfs2.getAclStatus(targetTestRoot2)));
        // Remove the ACL entries on the second namespace
        fsView.removeAclEntries(mountOnNn2, Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "bar", READ)));
        expected = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ_EXECUTE) };
        Assert.assertArrayEquals(expected, aclEntryArray(TestViewFileSystemWithAcls.fHdfs2.getAclStatus(targetTestRoot2)));
        fsView.removeAcl(mountOnNn2);
        Assert.assertEquals(0, fsView.getAclStatus(mountOnNn2).getEntries().size());
        Assert.assertEquals(0, TestViewFileSystemWithAcls.fHdfs2.getAclStatus(targetTestRoot2).getEntries().size());
    }
}

