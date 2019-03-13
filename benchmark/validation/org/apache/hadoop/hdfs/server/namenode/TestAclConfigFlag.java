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


import com.google.common.collect.Lists;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests that the configuration flag that controls support for ACLs is off by
 * default and causes all attempted operations related to ACLs to fail.  The
 * NameNode can still load ACLs from fsimage or edits.
 */
public class TestAclConfigFlag {
    private static final Path PATH = new Path("/path");

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testModifyAclEntries() throws Exception {
        initCluster(true, false);
        fs.mkdirs(TestAclConfigFlag.PATH);
        expectException();
        fs.modifyAclEntries(TestAclConfigFlag.PATH, Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_WRITE)));
    }

    @Test
    public void testRemoveAclEntries() throws Exception {
        initCluster(true, false);
        fs.mkdirs(TestAclConfigFlag.PATH);
        expectException();
        fs.removeAclEntries(TestAclConfigFlag.PATH, Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_WRITE)));
    }

    @Test
    public void testRemoveDefaultAcl() throws Exception {
        initCluster(true, false);
        fs.mkdirs(TestAclConfigFlag.PATH);
        expectException();
        fs.removeAclEntries(TestAclConfigFlag.PATH, Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_WRITE)));
    }

    @Test
    public void testRemoveAcl() throws Exception {
        initCluster(true, false);
        fs.mkdirs(TestAclConfigFlag.PATH);
        expectException();
        fs.removeAcl(TestAclConfigFlag.PATH);
    }

    @Test
    public void testSetAcl() throws Exception {
        initCluster(true, false);
        fs.mkdirs(TestAclConfigFlag.PATH);
        expectException();
        fs.setAcl(TestAclConfigFlag.PATH, Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_WRITE)));
    }

    @Test
    public void testGetAclStatus() throws Exception {
        initCluster(true, false);
        fs.mkdirs(TestAclConfigFlag.PATH);
        expectException();
        fs.getAclStatus(TestAclConfigFlag.PATH);
    }

    @Test
    public void testEditLog() throws Exception {
        // With ACLs enabled, set an ACL.
        initCluster(true, true);
        fs.mkdirs(TestAclConfigFlag.PATH);
        fs.setAcl(TestAclConfigFlag.PATH, Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_WRITE)));
        // Restart with ACLs disabled.  Expect successful restart.
        restart(false, false);
    }

    @Test
    public void testFsImage() throws Exception {
        // With ACLs enabled, set an ACL.
        initCluster(true, true);
        fs.mkdirs(TestAclConfigFlag.PATH);
        fs.setAcl(TestAclConfigFlag.PATH, Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_WRITE)));
        // Save a new checkpoint and restart with ACLs still enabled.
        restart(true, true);
        // Restart with ACLs disabled.  Expect successful restart.
        restart(false, false);
    }
}

