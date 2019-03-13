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


import DelegationTokenAuthenticator.DELEGATION_TOKEN_JSON;
import DelegationTokenAuthenticator.DELEGATION_TOKEN_URL_STRING_JSON;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import org.apache.hadoop.fs.http.client.HttpFSFileSystem;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.security.authentication.client.AuthenticatedURL;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.HFSTestCase;
import org.apache.hadoop.test.KerberosTestUtils;
import org.apache.hadoop.test.TestDir;
import org.apache.hadoop.test.TestHdfs;
import org.apache.hadoop.test.TestJetty;
import org.apache.hadoop.test.TestJettyHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;


// TODO: WebHdfsFilesystem does work with ProxyUser HDFS-3509
// @Test
// @TestDir
// @TestJetty
// @TestHdfs
// public void testDelegationTokenWithWebhdfsFileSystemProxyUser()
// throws Exception {
// testDelegationTokenWithinDoAs(WebHdfsFileSystem.class, true);
// }
public class TestHttpFSWithKerberos extends HFSTestCase {
    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testValidHttpFSAccess() throws Exception {
        createHttpFSServer();
        KerberosTestUtils.doAsClient(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                URL url = new URL(TestJettyHelper.getJettyURL(), "/webhdfs/v1/?op=GETHOMEDIRECTORY");
                AuthenticatedURL aUrl = new AuthenticatedURL();
                AuthenticatedURL.Token aToken = new AuthenticatedURL.Token();
                HttpURLConnection conn = aUrl.openConnection(url, aToken);
                Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
                return null;
            }
        });
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testInvalidadHttpFSAccess() throws Exception {
        createHttpFSServer();
        URL url = new URL(TestJettyHelper.getJettyURL(), "/webhdfs/v1/?op=GETHOMEDIRECTORY");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDelegationTokenHttpFSAccess() throws Exception {
        createHttpFSServer();
        KerberosTestUtils.doAsClient(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // get delegation token doing SPNEGO authentication
                URL url = new URL(TestJettyHelper.getJettyURL(), "/webhdfs/v1/?op=GETDELEGATIONTOKEN");
                AuthenticatedURL aUrl = new AuthenticatedURL();
                AuthenticatedURL.Token aToken = new AuthenticatedURL.Token();
                HttpURLConnection conn = aUrl.openConnection(url, aToken);
                Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
                JSONObject json = ((JSONObject) (new JSONParser().parse(new InputStreamReader(conn.getInputStream()))));
                json = ((JSONObject) (json.get(DELEGATION_TOKEN_JSON)));
                String tokenStr = ((String) (json.get(DELEGATION_TOKEN_URL_STRING_JSON)));
                // access httpfs using the delegation token
                url = new URL(TestJettyHelper.getJettyURL(), ("/webhdfs/v1/?op=GETHOMEDIRECTORY&delegation=" + tokenStr));
                conn = ((HttpURLConnection) (url.openConnection()));
                Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
                // try to renew the delegation token without SPNEGO credentials
                url = new URL(TestJettyHelper.getJettyURL(), ("/webhdfs/v1/?op=RENEWDELEGATIONTOKEN&token=" + tokenStr));
                conn = ((HttpURLConnection) (url.openConnection()));
                conn.setRequestMethod("PUT");
                Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_UNAUTHORIZED);
                // renew the delegation token with SPNEGO credentials
                url = new URL(TestJettyHelper.getJettyURL(), ("/webhdfs/v1/?op=RENEWDELEGATIONTOKEN&token=" + tokenStr));
                conn = aUrl.openConnection(url, aToken);
                conn.setRequestMethod("PUT");
                Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
                // cancel delegation token, no need for SPNEGO credentials
                url = new URL(TestJettyHelper.getJettyURL(), ("/webhdfs/v1/?op=CANCELDELEGATIONTOKEN&token=" + tokenStr));
                conn = ((HttpURLConnection) (url.openConnection()));
                conn.setRequestMethod("PUT");
                Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
                // try to access httpfs with the canceled delegation token
                url = new URL(TestJettyHelper.getJettyURL(), ("/webhdfs/v1/?op=GETHOMEDIRECTORY&delegation=" + tokenStr));
                conn = ((HttpURLConnection) (url.openConnection()));
                Assert.assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_UNAUTHORIZED);
                return null;
            }
        });
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDelegationTokenWithHttpFSFileSystem() throws Exception {
        testDelegationTokenWithinDoAs(HttpFSFileSystem.class, false);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDelegationTokenWithWebhdfsFileSystem() throws Exception {
        testDelegationTokenWithinDoAs(WebHdfsFileSystem.class, false);
    }

    @Test
    @TestDir
    @TestJetty
    @TestHdfs
    public void testDelegationTokenWithHttpFSFileSystemProxyUser() throws Exception {
        testDelegationTokenWithinDoAs(HttpFSFileSystem.class, true);
    }
}

