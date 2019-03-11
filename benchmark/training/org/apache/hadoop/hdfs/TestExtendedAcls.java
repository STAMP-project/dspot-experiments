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


import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclEntryScope;
import org.apache.hadoop.fs.permission.AclEntryType;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.server.namenode.AclTestHelpers;
import org.junit.Assert;
import org.junit.Test;


/**
 * A class for testing the behavior of HDFS directory and file ACL.
 */
public class TestExtendedAcls {
    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static final short REPLICATION = 3;

    private static DistributedFileSystem hdfs;

    /**
     * Set default ACL to a directory.
     * Create subdirectory, it must have default acls set.
     * Create sub file and it should have default acls.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDefaultAclNewChildDirFile() throws IOException {
        Path parent = new Path("/testDefaultAclNewChildDirFile");
        List<AclEntry> acls = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.ALL));
        TestExtendedAcls.hdfs.mkdirs(parent);
        TestExtendedAcls.hdfs.setAcl(parent, acls);
        // create sub directory
        Path childDir = new Path(parent, "childDir");
        TestExtendedAcls.hdfs.mkdirs(childDir);
        // the sub directory should have the default acls
        AclEntry[] childDirExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.MASK, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.OTHER, FsAction.READ_EXECUTE) };
        AclStatus childDirAcl = TestExtendedAcls.hdfs.getAclStatus(childDir);
        Assert.assertArrayEquals(childDirExpectedAcl, childDirAcl.getEntries().toArray());
        // create sub file
        Path childFile = new Path(parent, "childFile");
        TestExtendedAcls.hdfs.create(childFile).close();
        // the sub file should have the default acls
        AclEntry[] childFileExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE) };
        AclStatus childFileAcl = TestExtendedAcls.hdfs.getAclStatus(childFile);
        Assert.assertArrayEquals(childFileExpectedAcl, childFileAcl.getEntries().toArray());
        TestExtendedAcls.hdfs.delete(parent, true);
    }

    /**
     * Set default ACL to a directory and make sure existing sub dirs/files
     * does not have default acl.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDefaultAclExistingDirFile() throws Exception {
        Path parent = new Path("/testDefaultAclExistingDirFile");
        TestExtendedAcls.hdfs.mkdirs(parent);
        // the old acls
        List<AclEntry> acls1 = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.ALL));
        // the new acls
        List<AclEntry> acls2 = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.READ_EXECUTE));
        // set parent to old acl
        TestExtendedAcls.hdfs.setAcl(parent, acls1);
        Path childDir = new Path(parent, "childDir");
        TestExtendedAcls.hdfs.mkdirs(childDir);
        // the sub directory should also have the old acl
        AclEntry[] childDirExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.MASK, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.OTHER, FsAction.READ_EXECUTE) };
        AclStatus childDirAcl = TestExtendedAcls.hdfs.getAclStatus(childDir);
        Assert.assertArrayEquals(childDirExpectedAcl, childDirAcl.getEntries().toArray());
        Path childFile = new Path(childDir, "childFile");
        // the sub file should also have the old acl
        TestExtendedAcls.hdfs.create(childFile).close();
        AclEntry[] childFileExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE) };
        AclStatus childFileAcl = TestExtendedAcls.hdfs.getAclStatus(childFile);
        Assert.assertArrayEquals(childFileExpectedAcl, childFileAcl.getEntries().toArray());
        // now change parent to new acls
        TestExtendedAcls.hdfs.setAcl(parent, acls2);
        // sub directory and sub file should still have the old acls
        childDirAcl = TestExtendedAcls.hdfs.getAclStatus(childDir);
        Assert.assertArrayEquals(childDirExpectedAcl, childDirAcl.getEntries().toArray());
        childFileAcl = TestExtendedAcls.hdfs.getAclStatus(childFile);
        Assert.assertArrayEquals(childFileExpectedAcl, childFileAcl.getEntries().toArray());
        // now remove the parent acls
        TestExtendedAcls.hdfs.removeAcl(parent);
        // sub directory and sub file should still have the old acls
        childDirAcl = TestExtendedAcls.hdfs.getAclStatus(childDir);
        Assert.assertArrayEquals(childDirExpectedAcl, childDirAcl.getEntries().toArray());
        childFileAcl = TestExtendedAcls.hdfs.getAclStatus(childFile);
        Assert.assertArrayEquals(childFileExpectedAcl, childFileAcl.getEntries().toArray());
        // check changing the access mode of the file
        // mask out the access of group other for testing
        TestExtendedAcls.hdfs.setPermission(childFile, new FsPermission(((short) (416))));
        boolean canAccess = tryAccess(childFile, "other", new String[]{ "other" }, FsAction.READ);
        Assert.assertFalse(canAccess);
        TestExtendedAcls.hdfs.delete(parent, true);
    }

    /**
     * Verify that access acl does not get inherited on newly created subdir/file.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testAccessAclNotInherited() throws IOException {
        Path parent = new Path("/testAccessAclNotInherited");
        TestExtendedAcls.hdfs.mkdirs(parent);
        // parent have both access acl and default acl
        List<AclEntry> acls = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "bar", FsAction.ALL));
        TestExtendedAcls.hdfs.setAcl(parent, acls);
        Path childDir = new Path(parent, "childDir");
        TestExtendedAcls.hdfs.mkdirs(childDir);
        // subdirectory should only have the default acl inherited
        AclEntry[] childDirExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.MASK, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.OTHER, FsAction.READ) };
        AclStatus childDirAcl = TestExtendedAcls.hdfs.getAclStatus(childDir);
        Assert.assertArrayEquals(childDirExpectedAcl, childDirAcl.getEntries().toArray());
        Path childFile = new Path(parent, "childFile");
        TestExtendedAcls.hdfs.create(childFile).close();
        // sub file should only have the default acl inherited
        AclEntry[] childFileExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ) };
        AclStatus childFileAcl = TestExtendedAcls.hdfs.getAclStatus(childFile);
        Assert.assertArrayEquals(childFileExpectedAcl, childFileAcl.getEntries().toArray());
        TestExtendedAcls.hdfs.delete(parent, true);
    }

    /**
     * Create a parent dir and set default acl to allow foo read/write access.
     * Create a sub dir and set default acl to allow bar group read/write access.
     * parent dir/file can not be viewed/appended by bar group.
     * parent dir/child dir/file can be viewed/appended by bar group.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGradSubdirMoreAccess() throws Exception {
        Path parent = new Path("/testGradSubdirMoreAccess");
        TestExtendedAcls.hdfs.mkdirs(parent);
        List<AclEntry> aclsParent = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.READ_EXECUTE));
        List<AclEntry> aclsChild = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, "bar", FsAction.READ_WRITE));
        TestExtendedAcls.hdfs.setAcl(parent, aclsParent);
        AclEntry[] parentDirExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.MASK, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.OTHER, FsAction.READ_EXECUTE) };
        AclStatus parentAcl = TestExtendedAcls.hdfs.getAclStatus(parent);
        Assert.assertArrayEquals(parentDirExpectedAcl, parentAcl.getEntries().toArray());
        Path childDir = new Path(parent, "childDir");
        TestExtendedAcls.hdfs.mkdirs(childDir);
        TestExtendedAcls.hdfs.modifyAclEntries(childDir, aclsChild);
        // child dir should inherit the default acls from parent, plus bar group
        AclEntry[] childDirExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, "bar", FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.MASK, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.OTHER, FsAction.READ_EXECUTE) };
        AclStatus childDirAcl = TestExtendedAcls.hdfs.getAclStatus(childDir);
        Assert.assertArrayEquals(childDirExpectedAcl, childDirAcl.getEntries().toArray());
        Path parentFile = new Path(parent, "parentFile");
        TestExtendedAcls.hdfs.create(parentFile).close();
        TestExtendedAcls.hdfs.setPermission(parentFile, new FsPermission(((short) (416))));
        // parent dir/parent file allows foo to access but not bar group
        AclEntry[] parentFileExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE) };
        AclStatus parentFileAcl = TestExtendedAcls.hdfs.getAclStatus(parentFile);
        Assert.assertArrayEquals(parentFileExpectedAcl, parentFileAcl.getEntries().toArray());
        Path childFile = new Path(childDir, "childFile");
        TestExtendedAcls.hdfs.create(childFile).close();
        TestExtendedAcls.hdfs.setPermission(childFile, new FsPermission(((short) (416))));
        // child dir/child file allows foo user and bar group to access
        AclEntry[] childFileExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, "bar", FsAction.READ_WRITE) };
        AclStatus childFileAcl = TestExtendedAcls.hdfs.getAclStatus(childFile);
        Assert.assertArrayEquals(childFileExpectedAcl, childFileAcl.getEntries().toArray());
        // parent file should not be accessible for bar group
        Assert.assertFalse(tryAccess(parentFile, "barUser", new String[]{ "bar" }, FsAction.READ));
        // child file should be accessible for bar group
        Assert.assertTrue(tryAccess(childFile, "barUser", new String[]{ "bar" }, FsAction.READ));
        // parent file should be accessible for foo user
        Assert.assertTrue(tryAccess(parentFile, "foo", new String[]{ "fooGroup" }, FsAction.READ));
        // child file should be accessible for foo user
        Assert.assertTrue(tryAccess(childFile, "foo", new String[]{ "fooGroup" }, FsAction.READ));
        TestExtendedAcls.hdfs.delete(parent, true);
    }

    /**
     * Verify that sub directory can restrict acl with acl inherited from parent.
     * Create a parent dir and set default to allow foo and bar full access
     * Create a sub dir and set default to restrict bar to empty access
     *
     * parent dir/file can be viewed by foo
     * parent dir/child dir/file can be viewed by foo
     * parent dir/child dir/file can not be viewed by bar
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testRestrictAtSubDir() throws Exception {
        Path parent = new Path("/testRestrictAtSubDir");
        TestExtendedAcls.hdfs.mkdirs(parent);
        List<AclEntry> aclsParent = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, "bar", FsAction.ALL));
        TestExtendedAcls.hdfs.setAcl(parent, aclsParent);
        AclEntry[] parentDirExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, "bar", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.MASK, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.OTHER, FsAction.READ_EXECUTE) };
        AclStatus parentAcl = TestExtendedAcls.hdfs.getAclStatus(parent);
        Assert.assertArrayEquals(parentDirExpectedAcl, parentAcl.getEntries().toArray());
        Path parentFile = new Path(parent, "parentFile");
        TestExtendedAcls.hdfs.create(parentFile).close();
        TestExtendedAcls.hdfs.setPermission(parentFile, new FsPermission(((short) (416))));
        AclEntry[] parentFileExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, "bar", FsAction.ALL) };
        AclStatus parentFileAcl = TestExtendedAcls.hdfs.getAclStatus(parentFile);
        Assert.assertArrayEquals(parentFileExpectedAcl, parentFileAcl.getEntries().toArray());
        Path childDir = new Path(parent, "childDir");
        TestExtendedAcls.hdfs.mkdirs(childDir);
        List<AclEntry> newAclsChild = Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, "bar", FsAction.NONE));
        TestExtendedAcls.hdfs.modifyAclEntries(childDir, newAclsChild);
        AclEntry[] childDirExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, "bar", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, "bar", FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.MASK, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.OTHER, FsAction.READ_EXECUTE) };
        AclStatus childDirAcl = TestExtendedAcls.hdfs.getAclStatus(childDir);
        Assert.assertArrayEquals(childDirExpectedAcl, childDirAcl.getEntries().toArray());
        Path childFile = new Path(childDir, "childFile");
        TestExtendedAcls.hdfs.create(childFile).close();
        TestExtendedAcls.hdfs.setPermission(childFile, new FsPermission(((short) (416))));
        AclEntry[] childFileExpectedAcl = new AclEntry[]{ AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "foo", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, "bar", FsAction.NONE) };
        AclStatus childFileAcl = TestExtendedAcls.hdfs.getAclStatus(childFile);
        Assert.assertArrayEquals(childFileExpectedAcl, childFileAcl.getEntries().toArray());
        // child file should not be accessible for bar group
        Assert.assertFalse(tryAccess(childFile, "barUser", new String[]{ "bar" }, FsAction.READ));
        // child file should be accessible for foo user
        Assert.assertTrue(tryAccess(childFile, "foo", new String[]{ "fooGroup" }, FsAction.READ));
        // parent file should be accessible for bar group
        Assert.assertTrue(tryAccess(parentFile, "barUser", new String[]{ "bar" }, FsAction.READ));
        // parent file should be accessible for foo user
        Assert.assertTrue(tryAccess(parentFile, "foo", new String[]{ "fooGroup" }, FsAction.READ));
        TestExtendedAcls.hdfs.delete(parent, true);
    }
}

