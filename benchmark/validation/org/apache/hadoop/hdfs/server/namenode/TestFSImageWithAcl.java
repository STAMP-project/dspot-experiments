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
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;


public class TestFSImageWithAcl {
    private static Configuration conf;

    private static MiniDFSCluster cluster;

    @Test
    public void testPersistAcl() throws IOException {
        testAcl(true);
    }

    @Test
    public void testAclEditLog() throws IOException {
        testAcl(false);
    }

    @Test
    public void testFsImageDefaultAclNewChildren() throws IOException {
        doTestDefaultAclNewChildren(true);
    }

    @Test
    public void testEditLogDefaultAclNewChildren() throws IOException {
        doTestDefaultAclNewChildren(false);
    }

    @Test
    public void testRootACLAfterLoadingFsImage() throws IOException {
        DistributedFileSystem fs = TestFSImageWithAcl.cluster.getFileSystem();
        Path rootdir = new Path("/");
        AclEntry e1 = new AclEntry.Builder().setName("foo").setPermission(ALL).setScope(ACCESS).setType(GROUP).build();
        AclEntry e2 = new AclEntry.Builder().setName("bar").setPermission(READ).setScope(ACCESS).setType(GROUP).build();
        fs.modifyAclEntries(rootdir, Lists.newArrayList(e1, e2));
        AclStatus s = TestFSImageWithAcl.cluster.getNamesystem().getAclStatus(rootdir.toString());
        AclEntry[] returned = Lists.newArrayList(s.getEntries()).toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, "bar", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, "foo", ALL) }, returned);
        // restart - hence save and load from fsimage
        restart(fs, true);
        s = TestFSImageWithAcl.cluster.getNamesystem().getAclStatus(rootdir.toString());
        returned = Lists.newArrayList(s.getEntries()).toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, "bar", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, "foo", ALL) }, returned);
    }
}

