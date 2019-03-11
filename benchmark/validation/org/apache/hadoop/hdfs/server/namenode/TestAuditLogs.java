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


import WebHdfsConstants.WEBHDFS_SCHEME;
import java.io.InputStream;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.hdfs.web.WebHdfsTestUtil;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.PathUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * A JUnit test that audit logs are generated
 */
@RunWith(Parameterized.class)
public class TestAuditLogs {
    static final String auditLogFile = (PathUtils.getTestDirName(TestAuditLogs.class)) + "/TestAuditLogs-audit.log";

    final boolean useAsyncLog;

    final boolean useAsyncEdits;

    public TestAuditLogs(boolean useAsyncLog, boolean useAsyncEdits) {
        this.useAsyncLog = useAsyncLog;
        this.useAsyncEdits = useAsyncEdits;
    }

    // Pattern for:
    // allowed=(true|false) ugi=name ip=/address cmd={cmd} src={path} dst=null perm=null
    static final Pattern auditPattern = Pattern.compile(("allowed=.*?\\s" + ((("ugi=.*?\\s" + "ip=/\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s") + "cmd=.*?\\ssrc=.*?\\sdst=null\\s") + "perm=.*?")));

    static final Pattern successPattern = Pattern.compile(".*allowed=true.*");

    static final Pattern webOpenPattern = Pattern.compile(".*cmd=open.*proto=webhdfs.*");

    static final String username = "bob";

    static final String[] groups = new String[]{ "group1" };

    static final String fileName = "/srcdat";

    DFSTestUtil util;

    MiniDFSCluster cluster;

    FileSystem fs;

    String[] fnames;

    Configuration conf;

    UserGroupInformation userGroupInfo;

    /**
     * test that allowed operation puts proper entry in audit log
     */
    @Test
    public void testAuditAllowed() throws Exception {
        final Path file = new Path(fnames[0]);
        FileSystem userfs = DFSTestUtil.getFileSystemAs(userGroupInfo, conf);
        setupAuditLogs();
        InputStream istream = userfs.open(file);
        int val = istream.read();
        istream.close();
        verifyAuditLogs(true);
        Assert.assertTrue("failed to read from file", (val >= 0));
    }

    /**
     * test that allowed stat puts proper entry in audit log
     */
    @Test
    public void testAuditAllowedStat() throws Exception {
        final Path file = new Path(fnames[0]);
        FileSystem userfs = DFSTestUtil.getFileSystemAs(userGroupInfo, conf);
        setupAuditLogs();
        FileStatus st = userfs.getFileStatus(file);
        verifyAuditLogs(true);
        Assert.assertTrue("failed to stat file", ((st != null) && (st.isFile())));
    }

    /**
     * test that denied operation puts proper entry in audit log
     */
    @Test
    public void testAuditDenied() throws Exception {
        final Path file = new Path(fnames[0]);
        FileSystem userfs = DFSTestUtil.getFileSystemAs(userGroupInfo, conf);
        fs.setPermission(file, new FsPermission(((short) (384))));
        fs.setOwner(file, "root", null);
        setupAuditLogs();
        try {
            userfs.open(file);
            Assert.fail("open must not succeed");
        } catch (AccessControlException e) {
            System.out.println("got access denied, as expected.");
        }
        verifyAuditLogs(false);
    }

    /**
     * test that access via webhdfs puts proper entry in audit log
     */
    @Test
    public void testAuditWebHdfs() throws Exception {
        final Path file = new Path(fnames[0]);
        fs.setPermission(file, new FsPermission(((short) (420))));
        fs.setOwner(file, "root", null);
        setupAuditLogs();
        WebHdfsFileSystem webfs = WebHdfsTestUtil.getWebHdfsFileSystemAs(userGroupInfo, conf, WEBHDFS_SCHEME);
        InputStream istream = webfs.open(file);
        int val = istream.read();
        istream.close();
        verifyAuditLogsRepeat(true, 3);
        Assert.assertTrue("failed to read from file", (val >= 0));
    }

    /**
     * test that stat via webhdfs puts proper entry in audit log
     */
    @Test
    public void testAuditWebHdfsStat() throws Exception {
        final Path file = new Path(fnames[0]);
        fs.setPermission(file, new FsPermission(((short) (420))));
        fs.setOwner(file, "root", null);
        setupAuditLogs();
        WebHdfsFileSystem webfs = WebHdfsTestUtil.getWebHdfsFileSystemAs(userGroupInfo, conf, WEBHDFS_SCHEME);
        FileStatus st = webfs.getFileStatus(file);
        verifyAuditLogs(true);
        Assert.assertTrue("failed to stat file", ((st != null) && (st.isFile())));
    }

    /**
     * test that denied access via webhdfs puts proper entry in audit log
     */
    @Test
    public void testAuditWebHdfsDenied() throws Exception {
        final Path file = new Path(fnames[0]);
        fs.setPermission(file, new FsPermission(((short) (384))));
        fs.setOwner(file, "root", null);
        setupAuditLogs();
        try {
            WebHdfsFileSystem webfs = WebHdfsTestUtil.getWebHdfsFileSystemAs(userGroupInfo, conf, WEBHDFS_SCHEME);
            InputStream istream = webfs.open(file);
            int val = istream.read();
            Assert.fail(("open+read must not succeed, got " + val));
        } catch (AccessControlException E) {
            System.out.println("got access denied, as expected.");
        }
        verifyAuditLogsRepeat(false, 2);
    }

    /**
     * test that open via webhdfs puts proper entry in audit log
     */
    @Test
    public void testAuditWebHdfsOpen() throws Exception {
        final Path file = new Path(fnames[0]);
        fs.setPermission(file, new FsPermission(((short) (420))));
        fs.setOwner(file, "root", null);
        setupAuditLogs();
        WebHdfsFileSystem webfs = WebHdfsTestUtil.getWebHdfsFileSystemAs(userGroupInfo, conf, WEBHDFS_SCHEME);
        webfs.open(file).read();
        verifyAuditLogsCheckPattern(true, 3, TestAuditLogs.webOpenPattern);
    }

    /**
     * make sure that "\r\n" isn't made into a newline in audit log
     */
    @Test
    public void testAuditCharacterEscape() throws Exception {
        final Path file = new Path(("foo" + ("\r\n" + "bar")));
        setupAuditLogs();
        fs.create(file);
        verifyAuditLogsRepeat(true, 1);
    }
}

