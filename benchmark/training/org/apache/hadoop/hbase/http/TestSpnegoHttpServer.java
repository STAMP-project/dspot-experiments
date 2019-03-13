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
package org.apache.hadoop.hbase.http;


import AuthSchemes.SPNEGO;
import HttpServer.HTTP_MAX_THREADS;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.http.resource.JerseyResource;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.KerberosCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.kerby.kerberos.kerb.client.JaasKrbUtil;
import org.apache.kerby.kerberos.kerb.server.SimpleKdcServer;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for SPNEGO authentication on the HttpServer. Uses Kerby's MiniKDC and Apache
 * HttpComponents to verify that a simple Servlet is reachable via SPNEGO and unreachable w/o.
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestSpnegoHttpServer extends HttpServerFunctionalTest {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSpnegoHttpServer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSpnegoHttpServer.class);

    private static final String KDC_SERVER_HOST = "localhost";

    private static final String CLIENT_PRINCIPAL = "client";

    private static HttpServer server;

    private static URL baseUrl;

    private static SimpleKdcServer kdc;

    private static File infoServerKeytab;

    private static File clientKeytab;

    @Test
    public void testUnauthorizedClientsDisallowed() throws IOException {
        URL url = new URL(HttpServerFunctionalTest.getServerURL(TestSpnegoHttpServer.server), "/echo?a=b");
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        Assert.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, conn.getResponseCode());
    }

    @Test
    public void testAllowedClient() throws Exception {
        // Create the subject for the client
        final Subject clientSubject = JaasKrbUtil.loginUsingKeytab(TestSpnegoHttpServer.CLIENT_PRINCIPAL, TestSpnegoHttpServer.clientKeytab);
        final Set<Principal> clientPrincipals = clientSubject.getPrincipals();
        // Make sure the subject has a principal
        Assert.assertFalse(clientPrincipals.isEmpty());
        // Get a TGT for the subject (might have many, different encryption types). The first should
        // be the default encryption type.
        Set<KerberosTicket> privateCredentials = clientSubject.getPrivateCredentials(KerberosTicket.class);
        Assert.assertFalse(privateCredentials.isEmpty());
        KerberosTicket tgt = privateCredentials.iterator().next();
        Assert.assertNotNull(tgt);
        // The name of the principal
        final String principalName = clientPrincipals.iterator().next().getName();
        // Run this code, logged in as the subject (the client)
        HttpResponse resp = Subject.doAs(clientSubject, new PrivilegedExceptionAction<HttpResponse>() {
            @Override
            public HttpResponse run() throws Exception {
                // Logs in with Kerberos via GSS
                GSSManager gssManager = GSSManager.getInstance();
                // jGSS Kerberos login constant
                Oid oid = new Oid("1.2.840.113554.1.2.2");
                GSSName gssClient = gssManager.createName(principalName, GSSName.NT_USER_NAME);
                GSSCredential credential = gssManager.createCredential(gssClient, GSSCredential.DEFAULT_LIFETIME, oid, GSSCredential.INITIATE_ONLY);
                HttpClientContext context = HttpClientContext.create();
                Lookup<AuthSchemeProvider> authRegistry = RegistryBuilder.<AuthSchemeProvider>create().register(SPNEGO, new SPNegoSchemeFactory(true, true)).build();
                HttpClient client = HttpClients.custom().setDefaultAuthSchemeRegistry(authRegistry).build();
                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new KerberosCredentials(credential));
                URL url = new URL(HttpServerFunctionalTest.getServerURL(TestSpnegoHttpServer.server), "/echo?a=b");
                context.setTargetHost(new HttpHost(url.getHost(), url.getPort()));
                context.setCredentialsProvider(credentialsProvider);
                context.setAuthSchemeRegistry(authRegistry);
                HttpGet get = new HttpGet(url.toURI());
                return client.execute(get, context);
            }
        });
        Assert.assertNotNull(resp);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, resp.getStatusLine().getStatusCode());
        Assert.assertEquals("a:b", EntityUtils.toString(resp.getEntity()).trim());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingConfigurationThrowsException() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(HTTP_MAX_THREADS, TestHttpServer.MAX_THREADS);
        // Enable Kerberos (pre-req)
        conf.set("hbase.security.authentication", "kerberos");
        // Intentionally skip keytab and principal
        HttpServer customServer = HttpServerFunctionalTest.createTestServerWithSecurity(conf);
        customServer.addServlet("echo", "/echo", TestHttpServer.EchoServlet.class);
        customServer.addJerseyResourcePackage(JerseyResource.class.getPackage().getName(), "/jersey/*");
        customServer.start();
    }
}

