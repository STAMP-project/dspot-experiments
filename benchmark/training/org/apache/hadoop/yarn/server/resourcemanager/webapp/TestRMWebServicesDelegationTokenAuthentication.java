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
package org.apache.hadoop.yarn.server.resourcemanager.webapp;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import RMWebServices.DELEGATION_TOKEN_HEADER;
import Status.FORBIDDEN;
import Status.OK;
import Status.UNAUTHORIZED;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.authentication.KerberosTestUtils;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenAuthenticator;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationSubmissionContextInfo;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestRMWebServicesDelegationTokenAuthentication {
    private static final File testRootDir = new File("target", ((TestRMWebServicesDelegationTokenAuthentication.class.getName()) + "-root"));

    private static File httpSpnegoKeytabFile = new File(KerberosTestUtils.getKeytabFile());

    private static final String SUN_SECURITY_KRB5_RCACHE_KEY = "sun.security.krb5.rcache";

    private static String httpSpnegoPrincipal = KerberosTestUtils.getServerPrincipal();

    private static boolean miniKDCStarted = false;

    private static MiniKdc testMiniKDC;

    private static MockRM rm;

    private static String sunSecurityKrb5RcacheValue;

    String delegationTokenHeader;

    // use published header name
    static final String OldDelegationTokenHeader = "Hadoop-YARN-Auth-Delegation-Token";

    // alternate header name
    static final String NewDelegationTokenHeader = DelegationTokenAuthenticator.DELEGATION_TOKEN_HEADER;

    public TestRMWebServicesDelegationTokenAuthentication(String header) throws Exception {
        super();
        this.delegationTokenHeader = header;
    }

    // Test that you can authenticate with only delegation tokens
    // 1. Get a delegation token using Kerberos auth(this ends up
    // testing the fallback authenticator)
    // 2. Submit an app without kerberos or delegation-token
    // - we should get an UNAUTHORIZED response
    // 3. Submit same app with delegation-token
    // - we should get OK response
    // - confirm owner of the app is the user whose
    // delegation-token we used
    @Test
    public void testDelegationTokenAuth() throws Exception {
        final String token = getDelegationToken("test");
        ApplicationSubmissionContextInfo app = new ApplicationSubmissionContextInfo();
        String appid = "application_123_0";
        app.setApplicationId(appid);
        String requestBody = TestRMWebServicesDelegationTokenAuthentication.getMarshalledAppInfo(app);
        URL url = new URL("http://localhost:8088/ws/v1/cluster/apps");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "POST", "application/xml", requestBody);
        // this should fail with unauthorized because only
        // auth is kerberos or delegation token
        try {
            conn.getInputStream();
            Assert.fail("we should not be here");
        } catch (IOException e) {
            Assert.assertEquals(UNAUTHORIZED.getStatusCode(), conn.getResponseCode());
        }
        conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestProperty(delegationTokenHeader, token);
        TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "POST", APPLICATION_XML, requestBody);
        // this should not fail
        try {
            conn.getInputStream();
        } catch (IOException ie) {
            InputStream errorStream = conn.getErrorStream();
            String error = "";
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(errorStream, "UTF8"));
            for (String line; (line = reader.readLine()) != null;) {
                error += line;
            }
            reader.close();
            errorStream.close();
            Assert.fail(((("Response " + (conn.getResponseCode())) + "; ") + error));
        }
        boolean appExists = getRMContext().getRMApps().containsKey(ApplicationId.fromString(appid));
        Assert.assertTrue(appExists);
        RMApp actualApp = getRMContext().getRMApps().get(ApplicationId.fromString(appid));
        String owner = actualApp.getUser();
        Assert.assertEquals("client", owner);
    }

    // Test to make sure that cancelled delegation tokens
    // are rejected
    @Test
    public void testCancelledDelegationToken() throws Exception {
        String token = getDelegationToken("client");
        cancelDelegationToken(token);
        ApplicationSubmissionContextInfo app = new ApplicationSubmissionContextInfo();
        String appid = "application_123_0";
        app.setApplicationId(appid);
        String requestBody = TestRMWebServicesDelegationTokenAuthentication.getMarshalledAppInfo(app);
        URL url = new URL("http://localhost:8088/ws/v1/cluster/apps");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestProperty(delegationTokenHeader, token);
        TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "POST", APPLICATION_XML, requestBody);
        // this should fail with unauthorized because only
        // auth is kerberos or delegation token
        try {
            conn.getInputStream();
            Assert.fail("Authentication should fail with expired delegation tokens");
        } catch (IOException e) {
            Assert.assertEquals(FORBIDDEN.getStatusCode(), conn.getResponseCode());
        }
    }

    // Test to make sure that we can't do delegation token
    // functions using just delegation token auth
    @Test
    public void testDelegationTokenOps() throws Exception {
        String token = getDelegationToken("client");
        String createRequest = "{\"renewer\":\"test\"}";
        String renewRequest = ("{\"token\": \"" + token) + "\"}";
        // first test create and renew
        String[] requests = new String[]{ createRequest, renewRequest };
        for (String requestBody : requests) {
            URL url = new URL("http://localhost:8088/ws/v1/cluster/delegation-token");
            HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
            conn.setRequestProperty(delegationTokenHeader, token);
            TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "POST", APPLICATION_JSON, requestBody);
            try {
                conn.getInputStream();
                Assert.fail(("Creation/Renewing delegation tokens should not be " + "allowed with token auth"));
            } catch (IOException e) {
                Assert.assertEquals(FORBIDDEN.getStatusCode(), conn.getResponseCode());
            }
        }
        // test cancel
        URL url = new URL("http://localhost:8088/ws/v1/cluster/delegation-token");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestProperty(delegationTokenHeader, token);
        conn.setRequestProperty(DELEGATION_TOKEN_HEADER, token);
        TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "DELETE", null, null);
        try {
            conn.getInputStream();
            Assert.fail("Cancelling delegation tokens should not be allowed with token auth");
        } catch (IOException e) {
            Assert.assertEquals(FORBIDDEN.getStatusCode(), conn.getResponseCode());
        }
    }

    // Superuser "client" should be able to get a delegation token
    // for user "client2" when authenticated using Kerberos
    // The request shouldn't work when authenticated using DelegationTokens
    @Test
    public void testDoAs() throws Exception {
        KerberosTestUtils.doAsClient(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                String token = "";
                String owner = "";
                String renewer = "renewer";
                String body = ("{\"renewer\":\"" + renewer) + "\"}";
                URL url = new URL("http://localhost:8088/ws/v1/cluster/delegation-token?doAs=client2");
                HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
                TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "POST", APPLICATION_JSON, body);
                InputStream response = conn.getInputStream();
                Assert.assertEquals(OK.getStatusCode(), conn.getResponseCode());
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response, "UTF8"));
                    for (String line; (line = reader.readLine()) != null;) {
                        JSONObject obj = new JSONObject(line);
                        if (obj.has("token")) {
                            token = obj.getString("token");
                        }
                        if (obj.has("owner")) {
                            owner = obj.getString("owner");
                        }
                    }
                } finally {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(response);
                }
                Assert.assertEquals("client2", owner);
                Token<RMDelegationTokenIdentifier> realToken = new Token<RMDelegationTokenIdentifier>();
                realToken.decodeFromUrlString(token);
                Assert.assertEquals("client2", realToken.decodeIdentifier().getOwner().toString());
                return null;
            }
        });
        // this should not work
        final String token = getDelegationToken("client");
        String renewer = "renewer";
        String body = ("{\"renewer\":\"" + renewer) + "\"}";
        URL url = new URL("http://localhost:8088/ws/v1/cluster/delegation-token?doAs=client2");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.setRequestProperty(delegationTokenHeader, token);
        TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "POST", APPLICATION_JSON, body);
        try {
            conn.getInputStream();
            Assert.fail("Client should not be allowed to impersonate using delegation tokens");
        } catch (IOException ie) {
            Assert.assertEquals(FORBIDDEN.getStatusCode(), conn.getResponseCode());
        }
        // this should also fail due to client2 not being a super user
        KerberosTestUtils.doAs("client2@EXAMPLE.COM", new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                String renewer = "renewer";
                String body = ("{\"renewer\":\"" + renewer) + "\"}";
                URL url = new URL("http://localhost:8088/ws/v1/cluster/delegation-token?doAs=client");
                HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
                TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "POST", APPLICATION_JSON, body);
                try {
                    conn.getInputStream();
                    Assert.fail("Non superuser client should not be allowed to carry out doAs");
                } catch (IOException ie) {
                    Assert.assertEquals(FORBIDDEN.getStatusCode(), conn.getResponseCode());
                }
                return null;
            }
        });
    }
}

