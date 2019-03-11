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
package org.apache.hadoop.hdfs.web;


import FsAction.READ_WRITE;
import GetOpParam.Op.CHECKACCESS;
import GetOpParam.Op.GETDELEGATIONTOKEN;
import GetOpParam.Op.GETFILESTATUS;
import GetOpParam.Op.LISTSTATUS_BATCH;
import PutOpParam.Op.CANCELDELEGATIONTOKEN;
import PutOpParam.Op.CREATE;
import PutOpParam.Op.RENEWDELEGATIONTOKEN;
import WebHdfs.SCHEME;
import WebHdfsFileSystem.SPECIAL_FILENAME_CHARACTERS_REGEX;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.web.resources.DelegationParam;
import org.apache.hadoop.hdfs.web.resources.FsActionParam;
import org.apache.hadoop.hdfs.web.resources.StartAfterParam;
import org.apache.hadoop.hdfs.web.resources.TokenArgumentParam;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;

import static WebHdfsConstants.WEBHDFS_SCHEME;
import static WebHdfsFileSystem.PATH_PREFIX;


public class TestWebHdfsUrl {
    // NOTE: port is never used
    final URI uri = URI.create((((WEBHDFS_SCHEME) + "://") + "127.0.0.1:0"));

    @Test(timeout = 60000)
    public void testEncodedPathUrl() throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        final WebHdfsFileSystem webhdfs = ((WebHdfsFileSystem) (FileSystem.get(uri, conf)));
        // Construct a file path that contains percentage-encoded string
        String pathName = "/hdtest010%2C60020%2C1371000602151.1371058984668";
        Path fsPath = new Path(pathName);
        URL encodedPathUrl = webhdfs.toUrl(CREATE, fsPath);
        // We should get back the original file path after cycling back and decoding
        Assert.assertEquals(((PATH_PREFIX) + pathName), encodedPathUrl.toURI().getPath());
    }

    @Test(timeout = 60000)
    public void testSimpleAuthParamsInUrl() throws IOException {
        Configuration conf = new Configuration();
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        UserGroupInformation.setLoginUser(ugi);
        WebHdfsFileSystem webhdfs = getWebHdfsFileSystem(ugi, conf);
        Path fsPath = new Path("/");
        // send user+token
        URL fileStatusUrl = webhdfs.toUrl(GETFILESTATUS, fsPath);
        checkQueryParams(new String[]{ GETFILESTATUS.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getShortUserName()).toString() }, fileStatusUrl);
    }

    @Test(timeout = 60000)
    public void testSimpleProxyAuthParamsInUrl() throws IOException {
        Configuration conf = new Configuration();
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        ugi = UserGroupInformation.createProxyUser("test-proxy-user", ugi);
        UserGroupInformation.setLoginUser(ugi);
        WebHdfsFileSystem webhdfs = getWebHdfsFileSystem(ugi, conf);
        Path fsPath = new Path("/");
        // send real+effective
        URL fileStatusUrl = webhdfs.toUrl(GETFILESTATUS, fsPath);
        checkQueryParams(new String[]{ GETFILESTATUS.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getRealUser().getShortUserName()).toString(), new org.apache.hadoop.hdfs.web.resources.DoAsParam(ugi.getShortUserName()).toString() }, fileStatusUrl);
    }

    @Test(timeout = 60000)
    public void testSecureAuthParamsInUrl() throws IOException {
        Configuration conf = new Configuration();
        // fake turning on security so api thinks it should use tokens
        SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, conf);
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        ugi.setAuthenticationMethod(AuthenticationMethod.KERBEROS);
        UserGroupInformation.setLoginUser(ugi);
        WebHdfsFileSystem webhdfs = getWebHdfsFileSystem(ugi, conf);
        Path fsPath = new Path("/");
        String tokenString = webhdfs.getDelegationToken().encodeToUrlString();
        // send user
        URL getTokenUrl = webhdfs.toUrl(GETDELEGATIONTOKEN, fsPath);
        checkQueryParams(new String[]{ GETDELEGATIONTOKEN.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getShortUserName()).toString() }, getTokenUrl);
        // send user
        URL renewTokenUrl = webhdfs.toUrl(RENEWDELEGATIONTOKEN, fsPath, new TokenArgumentParam(tokenString));
        checkQueryParams(new String[]{ RENEWDELEGATIONTOKEN.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getShortUserName()).toString(), new TokenArgumentParam(tokenString).toString() }, renewTokenUrl);
        // send token
        URL cancelTokenUrl = webhdfs.toUrl(CANCELDELEGATIONTOKEN, fsPath, new TokenArgumentParam(tokenString));
        checkQueryParams(new String[]{ CANCELDELEGATIONTOKEN.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getShortUserName()).toString(), new TokenArgumentParam(tokenString).toString() }, cancelTokenUrl);
        // send token
        URL fileStatusUrl = webhdfs.toUrl(GETFILESTATUS, fsPath);
        checkQueryParams(new String[]{ GETFILESTATUS.toQueryString(), new DelegationParam(tokenString).toString() }, fileStatusUrl);
        // wipe out internal token to simulate auth always required
        webhdfs.setDelegationToken(null);
        // send user
        cancelTokenUrl = webhdfs.toUrl(CANCELDELEGATIONTOKEN, fsPath, new TokenArgumentParam(tokenString));
        checkQueryParams(new String[]{ CANCELDELEGATIONTOKEN.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getShortUserName()).toString(), new TokenArgumentParam(tokenString).toString() }, cancelTokenUrl);
        // send user
        fileStatusUrl = webhdfs.toUrl(GETFILESTATUS, fsPath);
        checkQueryParams(new String[]{ GETFILESTATUS.toQueryString(), new DelegationParam(tokenString).toString() }, fileStatusUrl);
    }

    @Test(timeout = 60000)
    public void testSecureProxyAuthParamsInUrl() throws IOException {
        Configuration conf = new Configuration();
        // fake turning on security so api thinks it should use tokens
        SecurityUtil.setAuthenticationMethod(AuthenticationMethod.KERBEROS, conf);
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        ugi.setAuthenticationMethod(AuthenticationMethod.KERBEROS);
        ugi = UserGroupInformation.createProxyUser("test-proxy-user", ugi);
        UserGroupInformation.setLoginUser(ugi);
        WebHdfsFileSystem webhdfs = getWebHdfsFileSystem(ugi, conf);
        Path fsPath = new Path("/");
        String tokenString = webhdfs.getDelegationToken().encodeToUrlString();
        // send real+effective
        URL getTokenUrl = webhdfs.toUrl(GETDELEGATIONTOKEN, fsPath);
        checkQueryParams(new String[]{ GETDELEGATIONTOKEN.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getRealUser().getShortUserName()).toString(), new org.apache.hadoop.hdfs.web.resources.DoAsParam(ugi.getShortUserName()).toString() }, getTokenUrl);
        // send real+effective
        URL renewTokenUrl = webhdfs.toUrl(RENEWDELEGATIONTOKEN, fsPath, new TokenArgumentParam(tokenString));
        checkQueryParams(new String[]{ RENEWDELEGATIONTOKEN.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getRealUser().getShortUserName()).toString(), new org.apache.hadoop.hdfs.web.resources.DoAsParam(ugi.getShortUserName()).toString(), new TokenArgumentParam(tokenString).toString() }, renewTokenUrl);
        // send token
        URL cancelTokenUrl = webhdfs.toUrl(CANCELDELEGATIONTOKEN, fsPath, new TokenArgumentParam(tokenString));
        checkQueryParams(new String[]{ CANCELDELEGATIONTOKEN.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getRealUser().getShortUserName()).toString(), new org.apache.hadoop.hdfs.web.resources.DoAsParam(ugi.getShortUserName()).toString(), new TokenArgumentParam(tokenString).toString() }, cancelTokenUrl);
        // send token
        URL fileStatusUrl = webhdfs.toUrl(GETFILESTATUS, fsPath);
        checkQueryParams(new String[]{ GETFILESTATUS.toQueryString(), new DelegationParam(tokenString).toString() }, fileStatusUrl);
        // wipe out internal token to simulate auth always required
        webhdfs.setDelegationToken(null);
        // send real+effective
        cancelTokenUrl = webhdfs.toUrl(CANCELDELEGATIONTOKEN, fsPath, new TokenArgumentParam(tokenString));
        checkQueryParams(new String[]{ CANCELDELEGATIONTOKEN.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getRealUser().getShortUserName()).toString(), new org.apache.hadoop.hdfs.web.resources.DoAsParam(ugi.getShortUserName()).toString(), new TokenArgumentParam(tokenString).toString() }, cancelTokenUrl);
        // send real+effective
        fileStatusUrl = webhdfs.toUrl(GETFILESTATUS, fsPath);
        checkQueryParams(new String[]{ GETFILESTATUS.toQueryString(), new DelegationParam(tokenString).toString() }, fileStatusUrl);
    }

    @Test(timeout = 60000)
    public void testCheckAccessUrl() throws IOException {
        Configuration conf = new Configuration();
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        UserGroupInformation.setLoginUser(ugi);
        WebHdfsFileSystem webhdfs = getWebHdfsFileSystem(ugi, conf);
        Path fsPath = new Path("/p1");
        URL checkAccessUrl = webhdfs.toUrl(CHECKACCESS, fsPath, new FsActionParam(FsAction.READ_WRITE));
        checkQueryParams(new String[]{ CHECKACCESS.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getShortUserName()).toString(), ((FsActionParam.NAME) + "=") + (READ_WRITE.SYMBOL) }, checkAccessUrl);
    }

    @Test(timeout = 60000)
    public void testBatchedListingUrl() throws Exception {
        Configuration conf = new Configuration();
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        UserGroupInformation.setLoginUser(ugi);
        WebHdfsFileSystem webhdfs = getWebHdfsFileSystem(ugi, conf);
        Path fsPath = new Path("/p1");
        final StartAfterParam startAfter = new StartAfterParam("last");
        URL url = webhdfs.toUrl(LISTSTATUS_BATCH, fsPath, startAfter);
        checkQueryParams(new String[]{ LISTSTATUS_BATCH.toQueryString(), new org.apache.hadoop.hdfs.web.resources.UserParam(ugi.getShortUserName()).toString(), ((StartAfterParam.NAME) + "=") + "last" }, url);
    }

    private static final String SPECIAL_CHARACTER_FILENAME = "specialFile ?\"\\()[]_-=&+;,{}#%\'`~!@$^*|<>.";

    @Test
    public void testWebHdfsSpecialCharacterFile() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        ugi.setAuthenticationMethod(AuthenticationMethod.KERBEROS);
        UserGroupInformation.setLoginUser(ugi);
        final Configuration conf = WebHdfsTestUtil.createConf();
        final Path dir = new Path("/testWebHdfsSpecialCharacterFile");
        final short numDatanodes = 1;
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDatanodes).build();
        try {
            cluster.waitActive();
            final FileSystem fs = WebHdfsTestUtil.getWebHdfsFileSystem(conf, SCHEME);
            // create a file
            final long length = 1L << 10;
            final Path file1 = new Path(dir, TestWebHdfsUrl.SPECIAL_CHARACTER_FILENAME);
            DFSTestUtil.createFile(fs, file1, length, numDatanodes, 20120406L);
            // get file status and check that it was written properly.
            final FileStatus s1 = fs.getFileStatus(file1);
            Assert.assertEquals(("Write failed for file " + file1), length, s1.getLen());
            boolean found = false;
            RemoteIterator<LocatedFileStatus> statusRemoteIterator = fs.listFiles(dir, false);
            while (statusRemoteIterator.hasNext()) {
                LocatedFileStatus locatedFileStatus = statusRemoteIterator.next();
                if ((locatedFileStatus.isFile()) && (TestWebHdfsUrl.SPECIAL_CHARACTER_FILENAME.equals(locatedFileStatus.getPath().getName()))) {
                    found = true;
                }
            } 
            Assert.assertFalse("Could not find file with special character", (!found));
        } finally {
            cluster.shutdown();
        }
    }

    private static final String BACKWARD_COMPATIBLE_SPECIAL_CHARACTER_FILENAME = "specialFile ?\"\\()[]_-=&,{}#\'`~!@$^*|<>.";

    @Test
    public void testWebHdfsBackwardCompatibleSpecialCharacterFile() throws Exception {
        Assert.assertFalse(TestWebHdfsUrl.BACKWARD_COMPATIBLE_SPECIAL_CHARACTER_FILENAME.matches(SPECIAL_FILENAME_CHARACTERS_REGEX));
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser("test-user");
        ugi.setAuthenticationMethod(AuthenticationMethod.KERBEROS);
        UserGroupInformation.setLoginUser(ugi);
        final Configuration conf = WebHdfsTestUtil.createConf();
        final Path dir = new Path("/testWebHdfsSpecialCharacterFile");
        final short numDatanodes = 1;
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDatanodes).build();
        try {
            cluster.waitActive();
            final FileSystem fs = WebHdfsTestUtil.getWebHdfsFileSystem(conf, SCHEME);
            // create a file
            final long length = 1L << 10;
            final Path file1 = new Path(dir, TestWebHdfsUrl.BACKWARD_COMPATIBLE_SPECIAL_CHARACTER_FILENAME);
            DFSTestUtil.createFile(fs, file1, length, numDatanodes, 20120406L);
            // get file status and check that it was written properly.
            final FileStatus s1 = fs.getFileStatus(file1);
            Assert.assertEquals(("Write failed for file " + file1), length, s1.getLen());
            boolean found = false;
            RemoteIterator<LocatedFileStatus> statusRemoteIterator = fs.listFiles(dir, false);
            while (statusRemoteIterator.hasNext()) {
                LocatedFileStatus locatedFileStatus = statusRemoteIterator.next();
                if ((locatedFileStatus.isFile()) && (TestWebHdfsUrl.BACKWARD_COMPATIBLE_SPECIAL_CHARACTER_FILENAME.equals(locatedFileStatus.getPath().getName()))) {
                    found = true;
                }
            } 
            Assert.assertFalse("Could not find file with special character", (!found));
        } finally {
            cluster.shutdown();
        }
    }
}

