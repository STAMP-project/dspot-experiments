/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.metastore.utils;


import FsAction.EXECUTE;
import FsAction.READ;
import FsAction.WRITE;
import HdfsUtils.HadoopFileStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.security.auth.login.LoginException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category(MetastoreUnitTest.class)
public class TestHdfsUtils {
    private Random rand = new Random();

    @Test
    public void userReadWriteExecute() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE));
        UserGroupInformation ugi = SecurityUtils.getUGI();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), READ, ugi);
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), WRITE, ugi);
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), EXECUTE, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void userNoRead() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.NONE, FsAction.ALL, FsAction.ALL));
        UserGroupInformation ugi = SecurityUtils.getUGI();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), READ, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void userNoWrite() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.NONE, FsAction.ALL, FsAction.ALL));
        UserGroupInformation ugi = SecurityUtils.getUGI();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), WRITE, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void userNoExecute() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.NONE, FsAction.ALL, FsAction.ALL));
        UserGroupInformation ugi = SecurityUtils.getUGI();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), EXECUTE, ugi);
    }

    @Test
    public void groupReadWriteExecute() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.NONE, FsAction.ALL, FsAction.NONE));
        UserGroupInformation ugi = ugiInvalidUserValidGroups();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), READ, ugi);
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), WRITE, ugi);
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), EXECUTE, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void groupNoRead() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.ALL));
        UserGroupInformation ugi = ugiInvalidUserValidGroups();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), READ, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void groupNoWrite() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.ALL));
        UserGroupInformation ugi = ugiInvalidUserValidGroups();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), WRITE, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void groupNoExecute() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.ALL));
        UserGroupInformation ugi = ugiInvalidUserValidGroups();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), EXECUTE, ugi);
    }

    @Test
    public void otherReadWriteExecute() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.NONE, FsAction.NONE, FsAction.ALL));
        UserGroupInformation ugi = ugiInvalidUserInvalidGroups();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), READ, ugi);
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), WRITE, ugi);
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), EXECUTE, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void otherNoRead() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.NONE));
        UserGroupInformation ugi = ugiInvalidUserInvalidGroups();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), READ, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void otherNoWrite() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.NONE));
        UserGroupInformation ugi = ugiInvalidUserInvalidGroups();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), WRITE, ugi);
    }

    @Test(expected = AccessControlException.class)
    public void otherNoExecute() throws IOException, LoginException {
        FileSystem fs = FileSystem.get(makeConf());
        Path p = createFile(fs, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.NONE));
        UserGroupInformation ugi = ugiInvalidUserInvalidGroups();
        HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), EXECUTE, ugi);
    }

    @Test
    public void rootReadWriteExecute() throws IOException, LoginException {
        UserGroupInformation ugi = SecurityUtils.getUGI();
        FileSystem fs = FileSystem.get(new Configuration());
        String old = fs.getConf().get("dfs.permissions.supergroup");
        try {
            fs.getConf().set("dfs.permissions.supergroup", ugi.getPrimaryGroupName());
            Path p = createFile(fs, new FsPermission(FsAction.NONE, FsAction.NONE, FsAction.NONE));
            HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), READ, ugi);
            HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), WRITE, ugi);
            HdfsUtils.checkFileAccess(fs, fs.getFileStatus(p), EXECUTE, ugi);
        } finally {
            fs.getConf().set("dfs.permissions.supergroup", old);
        }
    }

    /**
     * Tests that {@link HdfsUtils#setFullFileStatus(Configuration, HdfsUtils.HadoopFileStatus, String, FileSystem, Path, boolean)}
     * does not throw an exception when setting the group and without recursion.
     */
    @Test
    public void testSetFullFileStatusFailInheritGroup() throws IOException {
        Configuration conf = new Configuration();
        conf.set("dfs.namenode.acls.enabled", "false");
        HdfsUtils.HadoopFileStatus mockHadoopFileStatus = Mockito.mock(HadoopFileStatus.class);
        FileStatus mockSourceStatus = Mockito.mock(FileStatus.class);
        FileSystem fs = Mockito.mock(FileSystem.class);
        Mockito.when(mockSourceStatus.getGroup()).thenReturn("fakeGroup1");
        Mockito.when(mockHadoopFileStatus.getFileStatus()).thenReturn(mockSourceStatus);
        Mockito.doThrow(RuntimeException.class).when(fs).setOwner(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));
        HdfsUtils.setFullFileStatus(conf, mockHadoopFileStatus, "fakeGroup2", fs, new Path("fakePath"), false);
        Mockito.verify(fs).setOwner(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));
    }

    /**
     * Tests that link HdfsUtils#setFullFileStatus
     * does not thrown an exception when setting ACLs and without recursion.
     */
    @Test
    public void testSetFullFileStatusFailInheritAcls() throws IOException {
        Configuration conf = new Configuration();
        conf.set("dfs.namenode.acls.enabled", "true");
        HdfsUtils.HadoopFileStatus mockHadoopFileStatus = Mockito.mock(HadoopFileStatus.class);
        FileStatus mockSourceStatus = Mockito.mock(FileStatus.class);
        AclStatus mockAclStatus = Mockito.mock(AclStatus.class);
        FileSystem mockFs = Mockito.mock(FileSystem.class);
        Mockito.when(mockSourceStatus.getPermission()).thenReturn(new FsPermission(((short) (777))));
        Mockito.when(mockAclStatus.toString()).thenReturn("");
        Mockito.when(mockHadoopFileStatus.getFileStatus()).thenReturn(mockSourceStatus);
        Mockito.when(mockHadoopFileStatus.getAclEntries()).thenReturn(new ArrayList());
        Mockito.when(mockHadoopFileStatus.getAclStatus()).thenReturn(mockAclStatus);
        Mockito.doThrow(RuntimeException.class).when(mockFs).setAcl(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(List.class));
        HdfsUtils.setFullFileStatus(conf, mockHadoopFileStatus, null, mockFs, new Path("fakePath"), false);
        Mockito.verify(mockFs).setAcl(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(List.class));
    }

    /**
     * Tests that HdfsUtils#setFullFileStatus
     * does not thrown an exception when setting permissions and without recursion.
     */
    @Test
    public void testSetFullFileStatusFailInheritPerms() throws IOException {
        Configuration conf = new Configuration();
        conf.set("dfs.namenode.acls.enabled", "false");
        HdfsUtils.HadoopFileStatus mockHadoopFileStatus = Mockito.mock(HadoopFileStatus.class);
        FileStatus mockSourceStatus = Mockito.mock(FileStatus.class);
        FileSystem mockFs = Mockito.mock(FileSystem.class);
        Mockito.when(mockSourceStatus.getPermission()).thenReturn(new FsPermission(((short) (777))));
        Mockito.when(mockHadoopFileStatus.getFileStatus()).thenReturn(mockSourceStatus);
        Mockito.doThrow(RuntimeException.class).when(mockFs).setPermission(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(FsPermission.class));
        HdfsUtils.setFullFileStatus(conf, mockHadoopFileStatus, null, mockFs, new Path("fakePath"), false);
        Mockito.verify(mockFs).setPermission(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(FsPermission.class));
    }

    /**
     * Tests that {@link HdfsUtils#setFullFileStatus(Configuration, HdfsUtils.HadoopFileStatus, String, FileSystem, Path, boolean)}
     * does not throw an exception when setting the group and with recursion.
     */
    @Test
    public void testSetFullFileStatusFailInheritGroupRecursive() throws Exception {
        Configuration conf = new Configuration();
        conf.set("dfs.namenode.acls.enabled", "false");
        String fakeSourceGroup = "fakeGroup1";
        String fakeTargetGroup = "fakeGroup2";
        Path fakeTarget = new Path("fakePath");
        HdfsUtils.HadoopFileStatus mockHadoopFileStatus = Mockito.mock(HadoopFileStatus.class);
        FileStatus mockSourceStatus = Mockito.mock(FileStatus.class);
        FsShell mockFsShell = Mockito.mock(FsShell.class);
        Mockito.when(mockSourceStatus.getGroup()).thenReturn(fakeSourceGroup);
        Mockito.when(mockHadoopFileStatus.getFileStatus()).thenReturn(mockSourceStatus);
        Mockito.doThrow(RuntimeException.class).when(mockFsShell).run(ArgumentMatchers.any(String[].class));
        HdfsUtils.setFullFileStatus(conf, mockHadoopFileStatus, fakeTargetGroup, Mockito.mock(FileSystem.class), fakeTarget, true, mockFsShell);
        Mockito.verify(mockFsShell).run(new String[]{ "-chgrp", "-R", fakeSourceGroup, fakeTarget.toString() });
    }

    /**
     * Tests that HdfsUtils#setFullFileStatus
     * does not thrown an exception when setting ACLs and with recursion.
     */
    @Test
    public void testSetFullFileStatusFailInheritAclsRecursive() throws Exception {
        Configuration conf = new Configuration();
        conf.set("dfs.namenode.acls.enabled", "true");
        Path fakeTarget = new Path("fakePath");
        HdfsUtils.HadoopFileStatus mockHadoopFileStatus = Mockito.mock(HadoopFileStatus.class);
        FileStatus mockSourceStatus = Mockito.mock(FileStatus.class);
        FsShell mockFsShell = Mockito.mock(FsShell.class);
        AclStatus mockAclStatus = Mockito.mock(AclStatus.class);
        Mockito.when(mockSourceStatus.getPermission()).thenReturn(new FsPermission(((short) (777))));
        Mockito.when(mockAclStatus.toString()).thenReturn("");
        Mockito.when(mockHadoopFileStatus.getFileStatus()).thenReturn(mockSourceStatus);
        Mockito.when(mockHadoopFileStatus.getAclEntries()).thenReturn(new ArrayList());
        Mockito.when(mockHadoopFileStatus.getAclStatus()).thenReturn(mockAclStatus);
        Mockito.doThrow(RuntimeException.class).when(mockFsShell).run(ArgumentMatchers.any(String[].class));
        HdfsUtils.setFullFileStatus(conf, mockHadoopFileStatus, "", Mockito.mock(FileSystem.class), fakeTarget, true, mockFsShell);
        Mockito.verify(mockFsShell).run(new String[]{ "-setfacl", "-R", "--set", ArgumentMatchers.any(String.class), fakeTarget.toString() });
    }

    /**
     * Tests that HdfsUtils#setFullFileStatus
     * does not thrown an exception when setting permissions and with recursion.
     */
    @Test
    public void testSetFullFileStatusFailInheritPermsRecursive() throws Exception {
        Configuration conf = new Configuration();
        conf.set("dfs.namenode.acls.enabled", "false");
        Path fakeTarget = new Path("fakePath");
        HdfsUtils.HadoopFileStatus mockHadoopFileStatus = Mockito.mock(HadoopFileStatus.class);
        FileStatus mockSourceStatus = Mockito.mock(FileStatus.class);
        FsShell mockFsShell = Mockito.mock(FsShell.class);
        Mockito.when(mockSourceStatus.getPermission()).thenReturn(new FsPermission(((short) (777))));
        Mockito.when(mockHadoopFileStatus.getFileStatus()).thenReturn(mockSourceStatus);
        Mockito.doThrow(RuntimeException.class).when(mockFsShell).run(ArgumentMatchers.any(String[].class));
        HdfsUtils.setFullFileStatus(conf, mockHadoopFileStatus, "", Mockito.mock(FileSystem.class), fakeTarget, true, mockFsShell);
        Mockito.verify(mockFsShell).run(new String[]{ "-chmod", "-R", ArgumentMatchers.any(String.class), fakeTarget.toString() });
    }
}

