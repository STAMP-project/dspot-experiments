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
package org.apache.hadoop.fs.http.server;


import AclEntryScope.DEFAULT;
import AclEntryType.USER;
import DataParam.NAME;
import FsAction.NONE;
import FsAction.READ_WRITE;
import HdfsClientConfigKeys.DFS_WEBHDFS_ACL_PERMISSION_PATTERN_KEY;
import HdfsClientConfigKeys.DFS_WEBHDFS_USER_PATTERN_KEY;
import HttpMethod.GET;
import HttpMethod.PUT;
import MediaType.APPLICATION_OCTET_STREAM;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport;
import org.apache.hadoop.hdfs.web.JsonUtil;
import org.apache.hadoop.lib.server.Server.Status;
import org.apache.hadoop.lib.server.Service;
import org.apache.hadoop.lib.server.ServiceException;
import org.apache.hadoop.lib.service.FileSystemAccess;
import org.apache.hadoop.lib.service.Groups;
import org.apache.hadoop.test.HFSTestCase;
import org.apache.hadoop.test.HadoopUsersConfTestHelper;
import org.apache.hadoop.test.TestDir;
import org.apache.hadoop.test.TestDirHelper;
import org.apache.hadoop.test.TestHdfs;
import org.apache.hadoop.test.TestHdfsHelper;
import org.apache.hadoop.test.TestJetty;
import org.apache.hadoop.test.TestJettyHelper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.org.apache.hadoop.lib.server.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;


/**
 * Main test class for HttpFSServer.
 */
public class TestHttpFSServer extends HFSTestCase {
    @Test
    @TestDir
    @TestJetty
    public void server() throws Exception {
        String dir = TestDirHelper.getTestDir().getAbsolutePath();
        Configuration httpfsConf = new Configuration(false);
        HttpFSServerWebApp server = new HttpFSServerWebApp(dir, dir, dir, dir, httpfsConf);
        server.init();
        server.destroy();
    }

    /**
     * Mock groups.
     */
    public static class MockGroups implements Service , Groups {
        @Override
        public void init(org.apache.hadoop.lib.server.Server server) throws ServiceException {
        }

        @Override
        public void postInit() throws ServiceException {
        }

        @Override
        public void destroy() {
        }

        @Override
        public Class[] getServiceDependencies() {
            return new Class[0];
        }

        @Override
        public Class getInterface() {
            return Groups.class;
        }

        @Override
        public void serverStatusChange(Status oldStatus, Status newStatus) throws ServiceException {
        }

        @Override
        public List<String> getGroups(String user) throws IOException {
            return Arrays.asList(HadoopUsersConfTestHelper.getHadoopUserGroups(user));
        }
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void instrumentation() throws Exception {
        createHttpFSServer(false, false);
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1?user.name={0}&op=instrumentation", "nobody"));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_UNAUTHORIZED);
        url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1?user.name={0}&op=instrumentation", HadoopUsersConfTestHelper.getHadoopUsers()[0]));
        conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = reader.readLine();
        reader.close();
        Assert.assertTrue(line.contains("\"counters\":{"));
        url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1/foo?user.name={0}&op=instrumentation", HadoopUsersConfTestHelper.getHadoopUsers()[0]));
        conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testHdfsAccess() throws Exception {
        createHttpFSServer(false, false);
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1/?user.name={0}&op=liststatus", user));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        reader.readLine();
        reader.close();
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testMkdirs() throws Exception {
        createHttpFSServer(false, false);
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1/tmp/sub-tmp?user.name={0}&op=MKDIRS", user));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod("PUT");
        conn.connect();
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        getStatus("/tmp/sub-tmp", "LISTSTATUS");
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testGlobFilter() throws Exception {
        createHttpFSServer(false, false);
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        fs.mkdirs(new Path("/tmp"));
        fs.create(new Path("/tmp/foo.txt")).close();
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1/tmp?user.name={0}&op=liststatus&filter=f*", user));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        reader.readLine();
        reader.close();
    }

    /**
     * Validate that files are created with 755 permissions when no
     * 'permissions' attribute is specified, and when 'permissions'
     * is specified, that value is honored.
     */
    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testPerms() throws Exception {
        createHttpFSServer(false, false);
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        fs.mkdirs(new Path("/perm"));
        createWithHttp("/perm/none", null);
        String statusJson = getStatus("/perm/none", "GETFILESTATUS");
        Assert.assertTrue("755".equals(getPerms(statusJson)));
        createWithHttp("/perm/p-777", "777");
        statusJson = getStatus("/perm/p-777", "GETFILESTATUS");
        Assert.assertTrue("777".equals(getPerms(statusJson)));
        createWithHttp("/perm/p-654", "654");
        statusJson = getStatus("/perm/p-654", "GETFILESTATUS");
        Assert.assertTrue("654".equals(getPerms(statusJson)));
        createWithHttp("/perm/p-321", "321");
        statusJson = getStatus("/perm/p-321", "GETFILESTATUS");
        Assert.assertTrue("321".equals(getPerms(statusJson)));
    }

    /**
     * Validate XAttr get/set/remove calls.
     */
    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testXAttrs() throws Exception {
        final String name1 = "user.a1";
        final byte[] value1 = new byte[]{ 49, 50, 51 };
        final String name2 = "user.a2";
        final byte[] value2 = new byte[]{ 65, 66, 67 };
        final String dir = "/xattrTest";
        final String path = dir + "/file";
        createHttpFSServer(false, false);
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        fs.mkdirs(new Path(dir));
        createWithHttp(path, null);
        String statusJson = getStatus(path, "GETXATTRS");
        Map<String, byte[]> xAttrs = getXAttrs(statusJson);
        Assert.assertEquals(0, xAttrs.size());
        // Set two xattrs
        putCmd(path, "SETXATTR", TestHttpFSServer.setXAttrParam(name1, value1));
        putCmd(path, "SETXATTR", TestHttpFSServer.setXAttrParam(name2, value2));
        statusJson = getStatus(path, "GETXATTRS");
        xAttrs = getXAttrs(statusJson);
        Assert.assertEquals(2, xAttrs.size());
        Assert.assertArrayEquals(value1, xAttrs.get(name1));
        Assert.assertArrayEquals(value2, xAttrs.get(name2));
        // Remove one xattr
        putCmd(path, "REMOVEXATTR", ("xattr.name=" + name1));
        statusJson = getStatus(path, "GETXATTRS");
        xAttrs = getXAttrs(statusJson);
        Assert.assertEquals(1, xAttrs.size());
        Assert.assertArrayEquals(value2, xAttrs.get(name2));
        // Remove another xattr, then there is no xattr
        putCmd(path, "REMOVEXATTR", ("xattr.name=" + name2));
        statusJson = getStatus(path, "GETXATTRS");
        xAttrs = getXAttrs(statusJson);
        Assert.assertEquals(0, xAttrs.size());
    }

    /**
     * Validate the various ACL set/modify/remove calls.  General strategy is
     * to verify each of the following steps with GETFILESTATUS, LISTSTATUS,
     * and GETACLSTATUS:
     * <ol>
     *   <li>Create a file with no ACLs</li>
     *   <li>Add a user + group ACL</li>
     *   <li>Add another user ACL</li>
     *   <li>Remove the first user ACL</li>
     *   <li>Remove all ACLs</li>
     * </ol>
     */
    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testFileAcls() throws Exception {
        final String aclUser1 = "user:foo:rw-";
        final String remAclUser1 = "user:foo:";
        final String aclUser2 = "user:bar:r--";
        final String aclGroup1 = "group::r--";
        final String aclSpec = ((("aclspec=user::rwx," + aclUser1) + ",") + aclGroup1) + ",other::---";
        final String modAclSpec = "aclspec=" + aclUser2;
        final String remAclSpec = "aclspec=" + remAclUser1;
        final String dir = "/aclFileTest";
        final String path = dir + "/test";
        String statusJson;
        List<String> aclEntries;
        createHttpFSServer(false, false);
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        fs.mkdirs(new Path(dir));
        createWithHttp(path, null);
        /* getfilestatus and liststatus don't have 'aclBit' in their reply */
        statusJson = getStatus(path, "GETFILESTATUS");
        Assert.assertEquals((-1), statusJson.indexOf("aclBit"));
        statusJson = getStatus(dir, "LISTSTATUS");
        Assert.assertEquals((-1), statusJson.indexOf("aclBit"));
        /* getaclstatus works and returns no entries */
        statusJson = getStatus(path, "GETACLSTATUS");
        aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(((aclEntries.size()) == 0));
        /* Now set an ACL on the file.  (getfile|list)status have aclBit,
        and aclstatus has entries that looks familiar.
         */
        putCmd(path, "SETACL", aclSpec);
        statusJson = getStatus(path, "GETFILESTATUS");
        Assert.assertNotEquals((-1), statusJson.indexOf("aclBit"));
        statusJson = getStatus(dir, "LISTSTATUS");
        Assert.assertNotEquals((-1), statusJson.indexOf("aclBit"));
        statusJson = getStatus(path, "GETACLSTATUS");
        aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(((aclEntries.size()) == 2));
        Assert.assertTrue(aclEntries.contains(aclUser1));
        Assert.assertTrue(aclEntries.contains(aclGroup1));
        /* Modify acl entries to add another user acl */
        putCmd(path, "MODIFYACLENTRIES", modAclSpec);
        statusJson = getStatus(path, "GETACLSTATUS");
        aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(((aclEntries.size()) == 3));
        Assert.assertTrue(aclEntries.contains(aclUser1));
        Assert.assertTrue(aclEntries.contains(aclUser2));
        Assert.assertTrue(aclEntries.contains(aclGroup1));
        /* Remove the first user acl entry and verify */
        putCmd(path, "REMOVEACLENTRIES", remAclSpec);
        statusJson = getStatus(path, "GETACLSTATUS");
        aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(((aclEntries.size()) == 2));
        Assert.assertTrue(aclEntries.contains(aclUser2));
        Assert.assertTrue(aclEntries.contains(aclGroup1));
        /* Remove all acls and verify */
        putCmd(path, "REMOVEACL", null);
        statusJson = getStatus(path, "GETACLSTATUS");
        aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(((aclEntries.size()) == 0));
        statusJson = getStatus(path, "GETFILESTATUS");
        Assert.assertEquals((-1), statusJson.indexOf("aclBit"));
        statusJson = getStatus(dir, "LISTSTATUS");
        Assert.assertEquals((-1), statusJson.indexOf("aclBit"));
    }

    /**
     * Test ACL operations on a directory, including default ACLs.
     * General strategy is to use GETFILESTATUS and GETACLSTATUS to verify:
     * <ol>
     *   <li>Initial status with no ACLs</li>
     *   <li>The addition of a default ACL</li>
     *   <li>The removal of default ACLs</li>
     * </ol>
     *
     * @throws Exception
     * 		
     */
    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDirAcls() throws Exception {
        final String defUser1 = "default:user:glarch:r-x";
        final String defSpec1 = "aclspec=" + defUser1;
        final String dir = "/aclDirTest";
        String statusJson;
        List<String> aclEntries;
        createHttpFSServer(false, false);
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        fs.mkdirs(new Path(dir));
        /* getfilestatus and liststatus don't have 'aclBit' in their reply */
        statusJson = getStatus(dir, "GETFILESTATUS");
        Assert.assertEquals((-1), statusJson.indexOf("aclBit"));
        /* No ACLs, either */
        statusJson = getStatus(dir, "GETACLSTATUS");
        aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(((aclEntries.size()) == 0));
        /* Give it a default ACL and verify */
        putCmd(dir, "SETACL", defSpec1);
        statusJson = getStatus(dir, "GETFILESTATUS");
        Assert.assertNotEquals((-1), statusJson.indexOf("aclBit"));
        statusJson = getStatus(dir, "GETACLSTATUS");
        aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(((aclEntries.size()) == 5));
        /* 4 Entries are default:(user|group|mask|other):perm */
        Assert.assertTrue(aclEntries.contains(defUser1));
        /* Remove the default ACL and re-verify */
        putCmd(dir, "REMOVEDEFAULTACL", null);
        statusJson = getStatus(dir, "GETFILESTATUS");
        Assert.assertEquals((-1), statusJson.indexOf("aclBit"));
        statusJson = getStatus(dir, "GETACLSTATUS");
        aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(((aclEntries.size()) == 0));
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testCustomizedUserAndGroupNames() throws Exception {
        // Start server with default configuration
        Server server = createHttpFSServer(false, false);
        final Configuration conf = HttpFSServerWebApp.get().get(FileSystemAccess.class).getFileSystemConfiguration();
        // Change pattern config
        conf.set(DFS_WEBHDFS_USER_PATTERN_KEY, "^[A-Za-z0-9_][A-Za-z0-9._-]*[$]?$");
        conf.set(DFS_WEBHDFS_ACL_PERMISSION_PATTERN_KEY, ("^(default:)?(user|group|mask|other):" + (("[[0-9A-Za-z_][@A-Za-z0-9._-]]*:([rwx-]{3})?(,(default:)?" + "(user|group|mask|other):[[0-9A-Za-z_][@A-Za-z0-9._-]]*:") + "([rwx-]{3})?)*$")));
        // Save configuration to site file
        writeConf(conf, "hdfs-site.xml");
        // Restart the HttpFS server to apply new config
        server.stop();
        server.start();
        final String aclUser = "user:123:rw-";
        final String aclGroup = "group:foo@bar:r--";
        final String aclSpec = ((("aclspec=user::rwx," + aclUser) + ",group::rwx,") + aclGroup) + ",other::---";
        final String dir = "/aclFileTestCustom";
        final String path = dir + "/test";
        // Create test dir
        FileSystem fs = FileSystem.get(conf);
        fs.mkdirs(new Path(dir));
        createWithHttp(path, null);
        // Set ACL
        putCmd(path, "SETACL", aclSpec);
        // Verify ACL
        String statusJson = getStatus(path, "GETACLSTATUS");
        List<String> aclEntries = getAclEntries(statusJson);
        Assert.assertTrue(aclEntries.contains(aclUser));
        Assert.assertTrue(aclEntries.contains(aclGroup));
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testOpenOffsetLength() throws Exception {
        createHttpFSServer(false, false);
        byte[] array = new byte[]{ 0, 1, 2, 3 };
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        fs.mkdirs(new Path("/tmp"));
        OutputStream os = fs.create(new Path("/tmp/foo"));
        os.write(array);
        os.close();
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1/tmp/foo?user.name={0}&op=open&offset=1&length=2", user));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        InputStream is = conn.getInputStream();
        Assert.assertEquals(1, is.read());
        Assert.assertEquals(2, is.read());
        Assert.assertEquals((-1), is.read());
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testCreateFileWithUnmaskedPermissions() throws Exception {
        createHttpFSServer(false, false);
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        // Create a folder with a default acl default:user2:rw-
        fs.mkdirs(new Path("/tmp"));
        AclEntry acl = new AclEntry.Builder().setType(USER).setScope(DEFAULT).setName("user2").setPermission(READ_WRITE).build();
        fs.setAcl(new Path("/tmp"), new ArrayList<AclEntry>(Arrays.asList(acl)));
        String notUnmaskedFile = "/tmp/notUnmasked";
        String unmaskedFile = "/tmp/unmasked";
        // Create a file inside the folder. It should inherit the default acl
        // but the mask should affect the ACL permissions. The mask is controlled
        // by the group permissions, which are 0, and hence the mask will make
        // the effective permission of the inherited ACL be NONE.
        createWithHttp(notUnmaskedFile, "700");
        // Pull the relevant ACL from the FS object and check the mask has affected
        // its permissions.
        AclStatus aclStatus = fs.getAclStatus(new Path(notUnmaskedFile));
        AclEntry theAcl = findAclWithName(aclStatus, "user2");
        Assert.assertNotNull(theAcl);
        Assert.assertEquals(NONE, aclStatus.getEffectivePermission(theAcl));
        // Create another file, this time pass a mask of 777. Now the inherited
        // permissions should be as expected
        createWithHttp(unmaskedFile, "700", "777");
        aclStatus = fs.getAclStatus(new Path(unmaskedFile));
        theAcl = findAclWithName(aclStatus, "user2");
        Assert.assertNotNull(theAcl);
        Assert.assertEquals(READ_WRITE, aclStatus.getEffectivePermission(theAcl));
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testMkdirWithUnmaskedPermissions() throws Exception {
        createHttpFSServer(false, false);
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        // Create a folder with a default acl default:user2:rw-
        fs.mkdirs(new Path("/tmp"));
        AclEntry acl = new AclEntry.Builder().setType(USER).setScope(DEFAULT).setName("user2").setPermission(READ_WRITE).build();
        fs.setAcl(new Path("/tmp"), new ArrayList<AclEntry>(Arrays.asList(acl)));
        String notUnmaskedDir = "/tmp/notUnmaskedDir";
        String unmaskedDir = "/tmp/unmaskedDir";
        // Create a file inside the folder. It should inherit the default acl
        // but the mask should affect the ACL permissions. The mask is controlled
        // by the group permissions, which are 0, and hence the mask will make
        // the effective permission of the inherited ACL be NONE.
        createDirWithHttp(notUnmaskedDir, "700", null);
        // Pull the relevant ACL from the FS object and check the mask has affected
        // its permissions.
        AclStatus aclStatus = fs.getAclStatus(new Path(notUnmaskedDir));
        AclEntry theAcl = findAclWithName(aclStatus, "user2");
        Assert.assertNotNull(theAcl);
        Assert.assertEquals(NONE, aclStatus.getEffectivePermission(theAcl));
        // Create another file, this time pass a mask of 777. Now the inherited
        // permissions should be as expected
        createDirWithHttp(unmaskedDir, "700", "777");
        aclStatus = fs.getAclStatus(new Path(unmaskedDir));
        theAcl = findAclWithName(aclStatus, "user2");
        Assert.assertNotNull(theAcl);
        Assert.assertEquals(READ_WRITE, aclStatus.getEffectivePermission(theAcl));
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testPutNoOperation() throws Exception {
        createHttpFSServer(false, false);
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1/foo?user.name={0}", user));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testGetTrashRoot() throws Exception {
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        createHttpFSServer(false, false);
        String trashJson = getStatus("/", "GETTRASHROOT");
        String trashPath = getPath(trashJson);
        Path expectedPath = new Path(FileSystem.USER_HOME_PREFIX, new Path(user, FileSystem.TRASH_PREFIX));
        Assert.assertEquals(expectedPath.toUri().getPath(), trashPath);
        byte[] array = new byte[]{ 0, 1, 2, 3 };
        FileSystem fs = FileSystem.get(TestHdfsHelper.getHdfsConf());
        fs.mkdirs(new Path("/tmp"));
        OutputStream os = fs.create(new Path("/tmp/foo"));
        os.write(array);
        os.close();
        trashJson = getStatus("/tmp/foo", "GETTRASHROOT");
        trashPath = getPath(trashJson);
        Assert.assertEquals(expectedPath.toUri().getPath(), trashPath);
        // TestHdfsHelp has already set up EZ environment
        final Path ezFile = TestHdfsHelper.ENCRYPTED_FILE;
        final Path ezPath = TestHdfsHelper.ENCRYPTION_ZONE;
        trashJson = getStatus(ezFile.toUri().getPath(), "GETTRASHROOT");
        trashPath = getPath(trashJson);
        expectedPath = new Path(ezPath, new Path(FileSystem.TRASH_PREFIX, user));
        Assert.assertEquals(expectedPath.toUri().getPath(), trashPath);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDelegationTokenOperations() throws Exception {
        createHttpFSServer(true, false);
        delegationTokenCommonTests(false);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testAllowSnapshot() throws Exception {
        createHttpFSServer(false, false);
        // Create a test directory
        String pathString = "/tmp/tmp-snap-allow-test";
        createDirWithHttp(pathString, "700", null);
        Path path = new Path(pathString);
        DistributedFileSystem dfs = ((DistributedFileSystem) (FileSystem.get(path.toUri(), TestHdfsHelper.getHdfsConf())));
        // FileStatus should have snapshot enabled bit unset by default
        Assert.assertFalse(dfs.getFileStatus(path).isSnapshotEnabled());
        // Send a request with ALLOWSNAPSHOT API
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1{0}?user.name={1}&op=ALLOWSNAPSHOT", pathString, user));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod("PUT");
        conn.connect();
        // Should return HTTP_OK
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        // FileStatus should have snapshot enabled bit set
        Assert.assertTrue(dfs.getFileStatus(path).isSnapshotEnabled());
        // Clean up
        dfs.delete(path, true);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDisallowSnapshot() throws Exception {
        createHttpFSServer(false, false);
        // Create a test directory
        String pathString = "/tmp/tmp-snap-disallow-test";
        createDirWithHttp(pathString, "700", null);
        Path path = new Path(pathString);
        DistributedFileSystem dfs = ((DistributedFileSystem) (FileSystem.get(path.toUri(), TestHdfsHelper.getHdfsConf())));
        // Allow snapshot
        dfs.allowSnapshot(path);
        // FileStatus should have snapshot enabled bit set so far
        Assert.assertTrue(dfs.getFileStatus(path).isSnapshotEnabled());
        // Send a request with DISALLOWSNAPSHOT API
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1{0}?user.name={1}&op=DISALLOWSNAPSHOT", pathString, user));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod("PUT");
        conn.connect();
        // Should return HTTP_OK
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        // FileStatus should not have snapshot enabled bit set
        Assert.assertFalse(dfs.getFileStatus(path).isSnapshotEnabled());
        // Clean up
        dfs.delete(path, true);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDisallowSnapshotException() throws Exception {
        createHttpFSServer(false, false);
        // Create a test directory
        String pathString = "/tmp/tmp-snap-disallow-exception-test";
        createDirWithHttp(pathString, "700", null);
        Path path = new Path(pathString);
        DistributedFileSystem dfs = ((DistributedFileSystem) (FileSystem.get(path.toUri(), TestHdfsHelper.getHdfsConf())));
        // Allow snapshot
        dfs.allowSnapshot(path);
        // FileStatus should have snapshot enabled bit set so far
        Assert.assertTrue(dfs.getFileStatus(path).isSnapshotEnabled());
        // Create some snapshots
        dfs.createSnapshot(path, "snap-01");
        dfs.createSnapshot(path, "snap-02");
        // Send a request with DISALLOWSNAPSHOT API
        String user = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1{0}?user.name={1}&op=DISALLOWSNAPSHOT", pathString, user));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod("PUT");
        conn.connect();
        // Should not return HTTP_OK
        Assert.assertNotEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        // FileStatus should still have snapshot enabled bit set
        Assert.assertTrue(dfs.getFileStatus(path).isSnapshotEnabled());
        // Clean up
        dfs.deleteSnapshot(path, "snap-02");
        dfs.deleteSnapshot(path, "snap-01");
        dfs.delete(path, true);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testCreateSnapshot() throws Exception {
        createHttpFSServer(false, false);
        final HttpURLConnection conn = snapshotTestPreconditions("PUT", "CREATESNAPSHOT", "snapshotname=snap-with-name");
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String result = reader.readLine();
        // Validates if the content format is correct
        Assert.assertTrue(result.equals("{\"Path\":\"/tmp/tmp-snap-test/.snapshot/snap-with-name\"}"));
        // Validates if the snapshot is properly created under .snapshot folder
        result = getStatus("/tmp/tmp-snap-test/.snapshot", "LISTSTATUS");
        Assert.assertTrue(result.contains("snap-with-name"));
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testCreateSnapshotNoSnapshotName() throws Exception {
        createHttpFSServer(false, false);
        final HttpURLConnection conn = snapshotTestPreconditions("PUT", "CREATESNAPSHOT", "");
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String result = reader.readLine();
        // Validates if the content format is correct
        Assert.assertTrue(Pattern.matches(("(\\{\\\"Path\\\"\\:\\\"/tmp/tmp-snap-test/.snapshot/s)" + "(\\d{8})(-)(\\d{6})(\\.)(\\d{3})(\\\"\\})"), result));
        // Validates if the snapshot is properly created under .snapshot folder
        result = getStatus("/tmp/tmp-snap-test/.snapshot", "LISTSTATUS");
        Assert.assertTrue(Pattern.matches(("(.+)(\\\"pathSuffix\\\":\\\"s)" + "(\\d{8})(-)(\\d{6})(\\.)(\\d{3})(\\\")(.+)"), result));
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testRenameSnapshot() throws Exception {
        createHttpFSServer(false, false);
        HttpURLConnection conn = snapshotTestPreconditions("PUT", "CREATESNAPSHOT", "snapshotname=snap-to-rename");
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        conn = snapshotTestPreconditions("PUT", "RENAMESNAPSHOT", ("oldsnapshotname=snap-to-rename" + "&snapshotname=snap-renamed"));
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        // Validates the snapshot is properly renamed under .snapshot folder
        String result = getStatus("/tmp/tmp-snap-test/.snapshot", "LISTSTATUS");
        Assert.assertTrue(result.contains("snap-renamed"));
        // There should be no snapshot named snap-to-rename now
        Assert.assertFalse(result.contains("snap-to-rename"));
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDelegationTokenOperationsSsl() throws Exception {
        createHttpFSServer(true, true);
        delegationTokenCommonTests(true);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDeleteSnapshot() throws Exception {
        createHttpFSServer(false, false);
        HttpURLConnection conn = snapshotTestPreconditions("PUT", "CREATESNAPSHOT", "snapshotname=snap-to-delete");
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        conn = snapshotTestPreconditions("DELETE", "DELETESNAPSHOT", "snapshotname=snap-to-delete");
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        // Validates the snapshot is not under .snapshot folder anymore
        String result = getStatus("/tmp/tmp-snap-test/.snapshot", "LISTSTATUS");
        Assert.assertFalse(result.contains("snap-to-delete"));
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testGetSnapshotDiff() throws Exception {
        createHttpFSServer(false, false);
        // Create a test directory
        String pathStr = "/tmp/tmp-snap-diff-test";
        createDirWithHttp(pathStr, "700", null);
        Path path = new Path(pathStr);
        DistributedFileSystem dfs = ((DistributedFileSystem) (FileSystem.get(path.toUri(), TestHdfsHelper.getHdfsConf())));
        // Enable snapshot
        dfs.allowSnapshot(path);
        Assert.assertTrue(dfs.getFileStatus(path).isSnapshotEnabled());
        // Create a file and take a snapshot
        String file1 = pathStr + "/file1";
        createWithHttp(file1, null);
        dfs.createSnapshot(path, "snap1");
        // Create another file and take a snapshot
        String file2 = pathStr + "/file2";
        createWithHttp(file2, null);
        dfs.createSnapshot(path, "snap2");
        // Send a request with GETSNAPSHOTDIFF API
        HttpURLConnection conn = sendRequestGetSnapshotDiff(pathStr, "snap1", "snap2");
        // Should return HTTP_OK
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        // Verify the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        // The response should be a one-line JSON string.
        String result = reader.readLine();
        // Verify the content of diff with DFS API.
        SnapshotDiffReport dfsDiffReport = dfs.getSnapshotDiffReport(path, "snap1", "snap2");
        Assert.assertEquals(result, JsonUtil.toJsonString(dfsDiffReport));
        // Clean up
        dfs.deleteSnapshot(path, "snap2");
        dfs.deleteSnapshot(path, "snap1");
        dfs.delete(path, true);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testGetSnapshotDiffIllegalParam() throws Exception {
        createHttpFSServer(false, false);
        // Create a test directory
        String pathStr = "/tmp/tmp-snap-diff-exc-test";
        createDirWithHttp(pathStr, "700", null);
        Path path = new Path(pathStr);
        DistributedFileSystem dfs = ((DistributedFileSystem) (FileSystem.get(path.toUri(), TestHdfsHelper.getHdfsConf())));
        // Enable snapshot
        dfs.allowSnapshot(path);
        Assert.assertTrue(dfs.getFileStatus(path).isSnapshotEnabled());
        // Send requests with GETSNAPSHOTDIFF API
        // Snapshots snap1 and snap2 are not created, expect failures but not NPE
        HttpURLConnection conn = sendRequestGetSnapshotDiff(pathStr, "", "");
        Assert.assertNotEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        sendRequestGetSnapshotDiff(pathStr, "snap1", "");
        Assert.assertNotEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        sendRequestGetSnapshotDiff(pathStr, "", "snap2");
        Assert.assertNotEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        sendRequestGetSnapshotDiff(pathStr, "snap1", "snap2");
        Assert.assertNotEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
        // Clean up
        dfs.delete(path, true);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testGetSnapshottableDirectoryList() throws Exception {
        createHttpFSServer(false, false);
        // Create test directories
        String pathStr1 = "/tmp/tmp-snap-dirlist-test-1";
        createDirWithHttp(pathStr1, "700", null);
        Path path1 = new Path(pathStr1);
        String pathStr2 = "/tmp/tmp-snap-dirlist-test-2";
        createDirWithHttp(pathStr2, "700", null);
        Path path2 = new Path(pathStr2);
        DistributedFileSystem dfs = ((DistributedFileSystem) (FileSystem.get(path1.toUri(), TestHdfsHelper.getHdfsConf())));
        // Verify response when there is no snapshottable directory
        verifyGetSnapshottableDirectoryList(dfs);
        // Enable snapshot for path1
        dfs.allowSnapshot(path1);
        Assert.assertTrue(dfs.getFileStatus(path1).isSnapshotEnabled());
        // Verify response when there is one snapshottable directory
        verifyGetSnapshottableDirectoryList(dfs);
        // Enable snapshot for path2
        dfs.allowSnapshot(path2);
        Assert.assertTrue(dfs.getFileStatus(path2).isSnapshotEnabled());
        // Verify response when there are two snapshottable directories
        verifyGetSnapshottableDirectoryList(dfs);
        // Clean up and verify
        dfs.delete(path2, true);
        verifyGetSnapshottableDirectoryList(dfs);
        dfs.delete(path1, true);
        verifyGetSnapshottableDirectoryList(dfs);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testNoRedirect() throws Exception {
        createHttpFSServer(false, false);
        final String testContent = "Test content";
        final String path = "/testfile.txt";
        final String username = HadoopUsersConfTestHelper.getHadoopUsers()[0];
        // Trigger the creation of the file which shouldn't redirect
        URL url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1{0}?user.name={1}&op=CREATE&noredirect=true", path, username));
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod(PUT);
        conn.connect();
        // Verify that it returned the final write location
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        JSONObject json = ((JSONObject) (new JSONParser().parse(new InputStreamReader(conn.getInputStream()))));
        String location = ((String) (json.get("Location")));
        Assert.assertTrue(location.contains(NAME));
        Assert.assertTrue(location.contains(NoRedirectParam.NAME));
        Assert.assertTrue(location.contains("CREATE"));
        Assert.assertTrue(("Wrong location: " + location), location.startsWith(TestJettyHelper.getJettyURL().toString()));
        // Use the location to actually write the file
        url = new URL(location);
        conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod(PUT);
        conn.setRequestProperty("Content-Type", APPLICATION_OCTET_STREAM);
        conn.setDoOutput(true);
        conn.connect();
        OutputStream os = conn.getOutputStream();
        os.write(testContent.getBytes());
        os.close();
        // Verify that it created the file and returned the location
        Assert.assertEquals(HttpURLConnection.HTTP_CREATED, conn.getResponseCode());
        json = ((JSONObject) (new JSONParser().parse(new InputStreamReader(conn.getInputStream()))));
        location = ((String) (json.get("Location")));
        Assert.assertEquals((((TestJettyHelper.getJettyURL()) + "/webhdfs/v1") + path), location);
        // Read the file which shouldn't redirect
        url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1{0}?user.name={1}&op=OPEN&noredirect=true", path, username));
        conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod(GET);
        conn.connect();
        // Verify that we got the final location to read from
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        json = ((JSONObject) (new JSONParser().parse(new InputStreamReader(conn.getInputStream()))));
        location = ((String) (json.get("Location")));
        Assert.assertTrue((!(location.contains(NoRedirectParam.NAME))));
        Assert.assertTrue(location.contains("OPEN"));
        Assert.assertTrue(("Wrong location: " + location), location.startsWith(TestJettyHelper.getJettyURL().toString()));
        // Use the location to actually read
        url = new URL(location);
        conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod(GET);
        conn.connect();
        // Verify that we read what we wrote
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        String content = IOUtils.toString(conn.getInputStream(), Charset.defaultCharset());
        Assert.assertEquals(testContent, content);
        // Get the checksum of the file which shouldn't redirect
        url = new URL(TestJettyHelper.getJettyURL(), MessageFormat.format("/webhdfs/v1{0}?user.name={1}&op=GETFILECHECKSUM&noredirect=true", path, username));
        conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod(GET);
        conn.connect();
        // Verify that we got the final location to write to
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        json = ((JSONObject) (new JSONParser().parse(new InputStreamReader(conn.getInputStream()))));
        location = ((String) (json.get("Location")));
        Assert.assertTrue((!(location.contains(NoRedirectParam.NAME))));
        Assert.assertTrue(location.contains("GETFILECHECKSUM"));
        Assert.assertTrue(("Wrong location: " + location), location.startsWith(TestJettyHelper.getJettyURL().toString()));
        // Use the location to actually get the checksum
        url = new URL(location);
        conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestMethod(GET);
        conn.connect();
        // Verify that we read what we wrote
        Assert.assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
        json = ((JSONObject) (new JSONParser().parse(new InputStreamReader(conn.getInputStream()))));
        JSONObject checksum = ((JSONObject) (json.get("FileChecksum")));
        Assert.assertEquals("0000020000000000000000001b9c0a445fed3c0bf1e1aa7438d96b1500000000", checksum.get("bytes"));
        Assert.assertEquals(28L, checksum.get("length"));
        Assert.assertEquals("MD5-of-0MD5-of-512CRC32C", checksum.get("algorithm"));
    }
}

