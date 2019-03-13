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
package org.apache.hadoop.yarn.service.client;


import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_OK;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.KerberosSecurityTestcase;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.client.util.YarnClientUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Spnego Client Login.
 */
public class TestSecureApiServiceClient extends KerberosSecurityTestcase {
    private String clientPrincipal = "client";

    private String server1Protocol = "HTTP";

    private String server2Protocol = "server2";

    private String host = "localhost";

    private String server1Principal = ((server1Protocol) + "/") + (host);

    private String server2Principal = ((server2Protocol) + "/") + (host);

    private File keytabFile;

    private Configuration testConf = new Configuration();

    private Map<String, String> props;

    private static Server server;

    private static Logger LOG = Logger.getLogger(TestSecureApiServiceClient.class);

    private ApiServiceClient asc;

    /**
     * A mocked version of API Service for testing purpose.
     */
    @SuppressWarnings("serial")
    public static class TestServlet extends HttpServlet {
        private static boolean headerFound = false;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                String header = headers.nextElement();
                TestSecureApiServiceClient.LOG.info(header);
            } 
            if ((req.getHeader("Authorization")) != null) {
                TestSecureApiServiceClient.TestServlet.headerFound = true;
                resp.setStatus(SC_OK);
            } else {
                TestSecureApiServiceClient.TestServlet.headerFound = false;
                resp.setStatus(SC_NOT_FOUND);
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
        }

        public static boolean isHeaderExist() {
            return TestSecureApiServiceClient.TestServlet.headerFound;
        }
    }

    @Test
    public void testHttpSpnegoChallenge() throws Exception {
        UserGroupInformation.loginUserFromKeytab(clientPrincipal, keytabFile.getCanonicalPath());
        String challenge = YarnClientUtils.generateToken("localhost");
        Assert.assertNotNull(challenge);
    }

    @Test
    public void testAuthorizationHeader() throws Exception {
        UserGroupInformation.loginUserFromKeytab(clientPrincipal, keytabFile.getCanonicalPath());
        String rmAddress = asc.getRMWebAddress();
        if (TestSecureApiServiceClient.TestServlet.isHeaderExist()) {
            Assert.assertEquals(rmAddress, "http://localhost:8088");
        } else {
            Assert.fail("Did not see Authorization header.");
        }
    }
}

