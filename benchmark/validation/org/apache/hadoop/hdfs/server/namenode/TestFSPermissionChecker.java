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
package org.apache.hadoop.hdfs.server.namenode;


import java.io.IOException;
import org.apache.hadoop.fs.permission.AclEntryScope;
import org.apache.hadoop.fs.permission.AclEntryType;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;


/**
 * Unit tests covering FSPermissionChecker.  All tests in this suite have been
 * cross-validated against Linux setfacl/getfacl to check for consistency of the
 * HDFS implementation.
 */
public class TestFSPermissionChecker {
    private static final long PREFERRED_BLOCK_SIZE = (128 * 1024) * 1024;

    private static final short REPLICATION = 3;

    private static final String SUPERGROUP = "supergroup";

    private static final String SUPERUSER = "superuser";

    private static final UserGroupInformation BRUCE = UserGroupInformation.createUserForTesting("bruce", new String[]{  });

    private static final UserGroupInformation DIANA = UserGroupInformation.createUserForTesting("diana", new String[]{ "sales" });

    private static final UserGroupInformation CLARK = UserGroupInformation.createUserForTesting("clark", new String[]{ "execs" });

    private FSDirectory dir;

    private INodeDirectory inodeRoot;

    @Test
    public void testAclOwner() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "execs", ((short) (416)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.NONE));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.READ);
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.WRITE);
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.BRUCE, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.EXECUTE);
    }

    @Test
    public void testAclNamedUser() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "execs", ((short) (416)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "diana", FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.NONE));
        assertPermissionGranted(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.ALL);
    }

    @Test
    public void testAclNamedUserDeny() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "execs", ((short) (420)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "diana", FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ);
    }

    @Test
    public void testAclNamedUserTraverseDeny() throws IOException {
        INodeDirectory inodeDir = TestFSPermissionChecker.createINodeDirectory(inodeRoot, "dir1", "bruce", "execs", ((short) (493)));
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeDir, "file1", "bruce", "execs", ((short) (420)));
        addAcl(inodeDir, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "diana", FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ_EXECUTE));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/dir1/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.ALL);
    }

    @Test
    public void testAclNamedUserMask() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "execs", ((short) (400)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "diana", FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.NONE));
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.ALL);
    }

    @Test
    public void testAclGroup() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "execs", ((short) (416)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.NONE));
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.ALL);
    }

    @Test
    public void testAclGroupDeny() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "sales", ((short) (388)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.ALL);
    }

    @Test
    public void testAclGroupTraverseDeny() throws IOException {
        INodeDirectory inodeDir = TestFSPermissionChecker.createINodeDirectory(inodeRoot, "dir1", "bruce", "execs", ((short) (493)));
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeDir, "file1", "bruce", "execs", ((short) (420)));
        addAcl(inodeDir, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ_EXECUTE));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/dir1/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.ALL);
    }

    @Test
    public void testAclGroupTraverseDenyOnlyDefaultEntries() throws IOException {
        INodeDirectory inodeDir = TestFSPermissionChecker.createINodeDirectory(inodeRoot, "dir1", "bruce", "execs", ((short) (493)));
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeDir, "file1", "bruce", "execs", ((short) (420)));
        addAcl(inodeDir, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, "sales", FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.GROUP, FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, AclEntryType.OTHER, FsAction.READ_EXECUTE));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/dir1/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.ALL);
    }

    @Test
    public void testAclGroupMask() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "execs", ((short) (420)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.ALL);
    }

    @Test
    public void testAclNamedGroup() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "execs", ((short) (416)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, "sales", FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.NONE));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ);
        assertPermissionGranted(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.ALL);
    }

    @Test
    public void testAclNamedGroupDeny() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "sales", ((short) (420)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, "execs", FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.ALL);
    }

    @Test
    public void testAclNamedGroupTraverseDeny() throws IOException {
        INodeDirectory inodeDir = TestFSPermissionChecker.createINodeDirectory(inodeRoot, "dir1", "bruce", "execs", ((short) (493)));
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeDir, "file1", "bruce", "execs", ((short) (420)));
        addAcl(inodeDir, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, "sales", FsAction.NONE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ_EXECUTE));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/dir1/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/dir1/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/dir1/file1", FsAction.ALL);
    }

    @Test
    public void testAclNamedGroupMask() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "execs", ((short) (420)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, "sales", FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.READ), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.READ_WRITE);
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ);
        assertPermissionGranted(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.DIANA, "/file1", FsAction.ALL);
    }

    @Test
    public void testAclOther() throws IOException {
        INodeFile inodeFile = TestFSPermissionChecker.createINodeFile(inodeRoot, "file1", "bruce", "sales", ((short) (508)));
        addAcl(inodeFile, AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, "diana", FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.GROUP, FsAction.READ_WRITE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.MASK, FsAction.ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.OTHER, FsAction.READ));
        assertPermissionGranted(TestFSPermissionChecker.BRUCE, "/file1", FsAction.ALL);
        assertPermissionGranted(TestFSPermissionChecker.DIANA, "/file1", FsAction.ALL);
        assertPermissionGranted(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ_WRITE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.READ_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.WRITE_EXECUTE);
        assertPermissionDenied(TestFSPermissionChecker.CLARK, "/file1", FsAction.ALL);
    }
}

