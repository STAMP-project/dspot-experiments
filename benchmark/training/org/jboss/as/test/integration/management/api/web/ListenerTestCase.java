/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.management.api.web;


import Listener.AJP;
import Listener.HTTP;
import Listener.HTTPS;
import io.undertow.util.FileUtils;
import java.io.File;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.http.util.TestHttpClientUtils;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.integration.management.util.WebUtil;
import org.jboss.as.test.integration.security.common.AbstractSecurityRealmsServerSetupTask;
import org.jboss.as.test.integration.security.common.SSLTruststoreUtil;
import org.jboss.as.test.integration.security.common.config.realm.Authentication;
import org.jboss.as.test.integration.security.common.config.realm.RealmKeystore;
import org.jboss.as.test.integration.security.common.config.realm.SecurityRealm;
import org.jboss.as.test.integration.security.common.config.realm.ServerIdentity;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Dominik Pospisil <dpospisi@redhat.com>
 */
@RunWith(Arquillian.class)
@ServerSetup(ListenerTestCase.SecurityRealmsSetup.class)
@RunAsClient
public class ListenerTestCase extends ContainerResourceMgmtTestBase {
    /**
     * We use a different socket binding name for each test, as if the socket is still up the service
     * will not be removed. Rather than adding a sleep we use this approach
     */
    private static int socketBindingCount = 0;

    @ArquillianResource
    private URL url;

    private static final char[] GENERATED_KEYSTORE_PASSWORD = "changeit".toCharArray();

    private static final String WORKING_DIRECTORY_LOCATION = "./target/test-classes/security";

    private static final String CLIENT_ALIAS = "client";

    private static final String CLIENT_2_ALIAS = "client2";

    private static final String TEST_ALIAS = "test";

    private static final File KEY_STORE_FILE = new File(ListenerTestCase.WORKING_DIRECTORY_LOCATION, "server.keystore");

    private static final File TRUST_STORE_FILE = new File(ListenerTestCase.WORKING_DIRECTORY_LOCATION, "jsse.keystore");

    private static final String TEST_CLIENT_DN = "CN=Test Client, OU=JBoss, O=Red Hat, L=Raleigh, ST=North Carolina, C=US";

    private static final String TEST_CLIENT_2_DN = "CN=Test Client 2, OU=JBoss, O=Red Hat, L=Raleigh, ST=North Carolina, C=US";

    private static final String AS_7_DN = "CN=AS7, OU=JBoss, O=Red Hat, L=Raleigh, ST=North Carolina, C=US";

    private static final String SHA_1_RSA = "SHA1withRSA";

    @Test
    public void testDefaultConnectorList() throws Exception {
        // only http connector present as a default
        Map<String, Set<String>> listeners = getListenerList();
        Set<String> listenerNames = listeners.get("http");
        Assert.assertEquals(1, listenerNames.size());
        Assert.assertTrue("HTTP connector missing.", listenerNames.contains("default"));
    }

    @Test
    public void testHttpConnector() throws Exception {
        addListener(HTTP);
        // check that the connector is live
        String cURL = ("http://" + (url.getHost())) + ":8181";
        String response = HttpRequest.get(cURL, 10, TimeUnit.SECONDS);
        Assert.assertTrue(("Invalid response: " + response), ((response.indexOf("JBoss")) >= 0));
        removeListener(HTTP, 5000);
    }

    @Test
    public void testHttpsConnector() throws Exception {
        addListener(HTTPS);
        // check that the connector is live
        try (CloseableHttpClient httpClient = TestHttpClientUtils.getHttpsClient(null)) {
            String cURL = ("https://" + (url.getHost())) + ":8181";
            HttpGet get = new HttpGet(cURL);
            HttpResponse hr = httpClient.execute(get);
            String response = EntityUtils.toString(hr.getEntity());
            Assert.assertTrue(("Invalid response: " + response), ((response.indexOf("JBoss")) >= 0));
        } finally {
            removeListener(HTTPS);
        }
    }

    @Test
    public void testAjpConnector() throws Exception {
        addListener(AJP);
        removeListener(AJP);
    }

    @Test
    public void testAddAndRemoveRollbacks() throws Exception {
        // execute and rollback add socket
        ModelNode addSocketOp = getAddSocketBindingOp(HTTP);
        ModelNode ret = executeAndRollbackOperation(addSocketOp);
        Assert.assertTrue("failed".equals(ret.get("outcome").asString()));
        // add socket again
        executeOperation(addSocketOp);
        // execute and rollback add connector
        ModelNode addConnectorOp = getAddListenerOp(HTTP, false);
        ret = executeAndRollbackOperation(addConnectorOp);
        Assert.assertTrue("failed".equals(ret.get("outcome").asString()));
        // add connector again
        executeOperation(addConnectorOp);
        // check it is listed
        Assert.assertTrue(getListenerList().get("http").contains((("test-" + (HTTP.getName())) + "-listener")));
        // execute and rollback remove connector
        ModelNode removeConnOp = getRemoveConnectorOp(HTTP);
        ret = executeAndRollbackOperation(removeConnOp);
        Assert.assertEquals("failed", ret.get("outcome").asString());
        // execute remove connector again
        executeOperation(removeConnOp);
        Thread.sleep(1000);
        // check that the connector is not live
        String cURL = (((HTTP.getScheme()) + "://") + (url.getHost())) + ":8181";
        Assert.assertFalse("Connector not removed.", WebUtil.testHttpURL(cURL));
        // execute and rollback remove socket binding
        ModelNode removeSocketOp = getRemoveSocketBindingOp(HTTP);
        ret = executeAndRollbackOperation(removeSocketOp);
        Assert.assertEquals("failed", ret.get("outcome").asString());
        // execute remove socket again
        executeOperation(removeSocketOp);
    }

    @Test
    public void testProxyProtocolOverHTTP() throws Exception {
        addListener(HTTP, true);
        try (Socket s = new Socket(url.getHost(), 8181)) {
            s.getOutputStream().write("PROXY TCP4 1.2.3.4 5.6.7.8 444 555\r\nGET /proxy/addr HTTP/1.0\r\n\r\n".getBytes(StandardCharsets.US_ASCII));
            String result = FileUtils.readFile(s.getInputStream());
            Assert.assertTrue(result, result.contains("result:1.2.3.4:444 5.6.7.8:555"));
        } finally {
            removeListener(HTTP);
        }
    }

    @Test
    public void testProxyProtocolOverHTTPS() throws Exception {
        addListener(HTTPS, true);
        try (Socket s = new Socket(url.getHost(), 8181)) {
            s.getOutputStream().write("PROXY TCP4 1.2.3.4 5.6.7.8 444 555\r\n".getBytes(StandardCharsets.US_ASCII));
            Socket ssl = TestHttpClientUtils.getSslContext().getSocketFactory().createSocket(s, url.getHost(), SSLTruststoreUtil.HTTPS_PORT, true);
            ssl.getOutputStream().write("GET /proxy/addr HTTP/1.0\r\n\r\n".getBytes(StandardCharsets.US_ASCII));
            String result = FileUtils.readFile(ssl.getInputStream());
            Assert.assertTrue(result, result.contains("result:1.2.3.4:444 5.6.7.8:555"));
        } finally {
            removeListener(HTTPS);
        }
    }

    static class SecurityRealmsSetup extends AbstractSecurityRealmsServerSetupTask {
        @Override
        protected SecurityRealm[] getSecurityRealms() throws Exception {
            ListenerTestCase.setUpKeyStores();
            URL keystoreResource = Thread.currentThread().getContextClassLoader().getResource("security/server.keystore");
            URL truststoreResource = Thread.currentThread().getContextClassLoader().getResource("security/jsse.keystore");
            RealmKeystore keystore = new RealmKeystore.Builder().keystorePassword("changeit").keystorePath(keystoreResource.getPath()).build();
            RealmKeystore truststore = new RealmKeystore.Builder().keystorePassword("changeit").keystorePath(truststoreResource.getPath()).build();
            return new SecurityRealm[]{ new SecurityRealm.Builder().name("ssl-realm").serverIdentity(new ServerIdentity.Builder().ssl(keystore).build()).authentication(new Authentication.Builder().truststore(truststore).build()).build() };
        }
    }
}

