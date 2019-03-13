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


import Status.FORBIDDEN;
import Status.OK;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.authentication.KerberosTestUtils;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationSubmissionContextInfo;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class TestRMWebServicesHttpStaticUserPermissions {
    private static final File testRootDir = new File("target", ((TestRMWebServicesHttpStaticUserPermissions.class.getName()) + "-root"));

    private static File spnegoKeytabFile = new File(KerberosTestUtils.getKeytabFile());

    private static String spnegoPrincipal = KerberosTestUtils.getServerPrincipal();

    private static MiniKdc testMiniKDC;

    private static MockRM rm;

    static class Helper {
        String method;

        String requestBody;

        Helper(String method, String requestBody) {
            this.method = method;
            this.requestBody = requestBody;
        }
    }

    public TestRMWebServicesHttpStaticUserPermissions() throws Exception {
        super();
    }

    // Test that the http static user can't submit or kill apps
    // when secure mode is turned on
    @Test
    public void testWebServiceAccess() throws Exception {
        ApplicationSubmissionContextInfo app = new ApplicationSubmissionContextInfo();
        String appid = "application_123_0";
        app.setApplicationId(appid);
        String submitAppRequestBody = TestRMWebServicesDelegationTokenAuthentication.getMarshalledAppInfo(app);
        URL url = new URL("http://localhost:8088/ws/v1/cluster/apps");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        // we should be access the apps page with the static user
        TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, "GET", "", "");
        try {
            conn.getInputStream();
            Assert.assertEquals(OK.getStatusCode(), conn.getResponseCode());
        } catch (IOException e) {
            Assert.fail(((("Got " + (conn.getResponseCode())) + " instead of 200 accessing ") + (url.toString())));
        }
        conn.disconnect();
        // new-application, submit app and kill should fail with
        // forbidden
        Map<String, TestRMWebServicesHttpStaticUserPermissions.Helper> urlRequestMap = new HashMap<String, TestRMWebServicesHttpStaticUserPermissions.Helper>();
        String killAppRequestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + (("<appstate>\n" + "  <state>KILLED</state>\n") + "</appstate>");
        urlRequestMap.put("http://localhost:8088/ws/v1/cluster/apps", new TestRMWebServicesHttpStaticUserPermissions.Helper("POST", submitAppRequestBody));
        urlRequestMap.put("http://localhost:8088/ws/v1/cluster/apps/new-application", new TestRMWebServicesHttpStaticUserPermissions.Helper("POST", ""));
        urlRequestMap.put("http://localhost:8088/ws/v1/cluster/apps/app_123_1/state", new TestRMWebServicesHttpStaticUserPermissions.Helper("PUT", killAppRequestBody));
        for (Map.Entry<String, TestRMWebServicesHttpStaticUserPermissions.Helper> entry : urlRequestMap.entrySet()) {
            URL reqURL = new URL(entry.getKey());
            conn = ((HttpURLConnection) (reqURL.openConnection()));
            String method = entry.getValue().method;
            String body = entry.getValue().requestBody;
            TestRMWebServicesDelegationTokenAuthentication.setupConn(conn, method, "application/xml", body);
            try {
                conn.getInputStream();
                Assert.fail((("Request " + (entry.getKey())) + "succeeded but should have failed"));
            } catch (IOException e) {
                Assert.assertEquals(FORBIDDEN.getStatusCode(), conn.getResponseCode());
                InputStream errorStream = conn.getErrorStream();
                String error = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream, "UTF8"));
                for (String line; (line = reader.readLine()) != null;) {
                    error += line;
                }
                reader.close();
                errorStream.close();
                JSONObject errResponse = new JSONObject(error);
                JSONObject remoteException = errResponse.getJSONObject("RemoteException");
                Assert.assertEquals(("java.lang.Exception: The default static user cannot carry out " + "this operation."), remoteException.getString("message"));
            }
            conn.disconnect();
        }
    }
}

