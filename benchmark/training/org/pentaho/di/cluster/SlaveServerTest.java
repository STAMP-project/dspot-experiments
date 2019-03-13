/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.cluster;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.utils.TestUtils;
import org.pentaho.di.www.GetPropertiesServlet;


/**
 * Tests for SlaveServer class
 *
 * @author Pavel Sakun
 * @see SlaveServer
 */
public class SlaveServerTest {
    SlaveServer slaveServer;

    @Test(expected = KettleException.class)
    public void testExecService() throws Exception {
        HttpGet httpGetMock = Mockito.mock(HttpGet.class);
        URI uriMock = new URI("fake");
        Mockito.doReturn(uriMock).when(httpGetMock).getURI();
        Mockito.doReturn(httpGetMock).when(slaveServer).buildExecuteServiceMethod(ArgumentMatchers.anyString(), ArgumentMatchers.anyMapOf(String.class, String.class));
        slaveServer.setHostname("hostNameStub");
        slaveServer.setUsername("userNAmeStub");
        slaveServer.execService("wrong_app_name");
        Assert.fail("Incorrect connection details had been used, but no exception was thrown");
    }

    @Test(expected = KettleException.class)
    public void testSendXML() throws Exception {
        slaveServer.setHostname("hostNameStub");
        slaveServer.setUsername("userNAmeStub");
        HttpPost httpPostMock = Mockito.mock(HttpPost.class);
        URI uriMock = new URI("fake");
        Mockito.doReturn(uriMock).when(httpPostMock).getURI();
        Mockito.doReturn(httpPostMock).when(slaveServer).buildSendXMLMethod(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());
        slaveServer.sendXML("", "");
        Assert.fail("Incorrect connection details had been used, but no exception was thrown");
    }

    @Test(expected = KettleException.class)
    public void testSendExport() throws Exception {
        slaveServer.setHostname("hostNameStub");
        slaveServer.setUsername("userNAmeStub");
        HttpPost httpPostMock = Mockito.mock(HttpPost.class);
        URI uriMock = new URI("fake");
        Mockito.doReturn(uriMock).when(httpPostMock).getURI();
        Mockito.doReturn(httpPostMock).when(slaveServer).buildSendExportMethod(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(InputStream.class));
        File tempFile;
        tempFile = File.createTempFile("PDI-", "tmp");
        tempFile.deleteOnExit();
        slaveServer.sendExport(tempFile.getAbsolutePath(), "", "");
        Assert.fail("Incorrect connection details had been used, but no exception was thrown");
    }

    @Test
    public void testSendExportOk() throws Exception {
        slaveServer.setUsername("uname");
        slaveServer.setPassword("passw");
        slaveServer.setHostname("hname");
        slaveServer.setPort("1111");
        HttpPost httpPostMock = Mockito.mock(HttpPost.class);
        URI uriMock = new URI("fake");
        final String responseContent = "baah";
        Mockito.when(httpPostMock.getURI()).thenReturn(uriMock);
        Mockito.doReturn(uriMock).when(httpPostMock).getURI();
        HttpClient client = Mockito.mock(HttpClient.class);
        Mockito.when(client.execute(ArgumentMatchers.any(), ArgumentMatchers.any(HttpContext.class))).then(new Answer<HttpResponse>() {
            @Override
            public HttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpClientContext context = getArgumentAt(1, HttpClientContext.class);
                Credentials cred = context.getCredentialsProvider().getCredentials(new AuthScope("hname", 1111));
                Assert.assertEquals("uname", cred.getUserPrincipal().getName());
                return mockResponse(200, responseContent);
            }
        });
        // override init
        Mockito.when(slaveServer.getHttpClient()).thenReturn(client);
        Mockito.when(slaveServer.getResponseBodyAsString(ArgumentMatchers.any())).thenCallRealMethod();
        Mockito.doReturn(httpPostMock).when(slaveServer).buildSendExportMethod(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(InputStream.class));
        File tempFile;
        tempFile = File.createTempFile("PDI-", "tmp");
        tempFile.deleteOnExit();
        String result = slaveServer.sendExport(tempFile.getAbsolutePath(), null, null);
        Assert.assertEquals(responseContent, result);
    }

    @Test
    public void testAddCredentials() throws IOException, ClassNotFoundException {
        String testUser = "test_username";
        slaveServer.setUsername(testUser);
        String testPassword = "test_password";
        slaveServer.setPassword(testPassword);
        String host = "somehost";
        slaveServer.setHostname(host);
        int port = 1000;
        slaveServer.setPort(("" + port));
        HttpClientContext auth = slaveServer.getAuthContext();
        Credentials cred = auth.getCredentialsProvider().getCredentials(new AuthScope(host, port));
        Assert.assertEquals(testUser, cred.getUserPrincipal().getName());
        Assert.assertEquals(testPassword, cred.getPassword());
        String user2 = "user2";
        slaveServer.setUsername(user2);
        slaveServer.setPassword("pass2");
        auth = slaveServer.getAuthContext();
        cred = auth.getCredentialsProvider().getCredentials(new AuthScope(host, port));
        Assert.assertEquals(user2, cred.getUserPrincipal().getName());
    }

    @Test
    public void testAuthCredentialsSchemeWithSSL() {
        slaveServer.setUsername("admin");
        slaveServer.setPassword("password");
        slaveServer.setHostname("localhost");
        slaveServer.setPort("8443");
        slaveServer.setSslMode(true);
        AuthCache cache = slaveServer.getAuthContext().getAuthCache();
        Assert.assertNotNull(cache.get(new HttpHost("localhost", 8443, "https")));
        Assert.assertNull(cache.get(new HttpHost("localhost", 8443, "http")));
    }

    @Test
    public void testAuthCredentialsSchemeWithoutSSL() {
        slaveServer.setUsername("admin");
        slaveServer.setPassword("password");
        slaveServer.setHostname("localhost");
        slaveServer.setPort("8080");
        slaveServer.setSslMode(false);
        AuthCache cache = slaveServer.getAuthContext().getAuthCache();
        Assert.assertNull(cache.get(new HttpHost("localhost", 8080, "https")));
        Assert.assertNotNull(cache.get(new HttpHost("localhost", 8080, "http")));
    }

    @Test
    public void testModifyingName() {
        slaveServer.setName("test");
        List<SlaveServer> list = new ArrayList<SlaveServer>();
        list.add(slaveServer);
        SlaveServer slaveServer2 = Mockito.spy(new SlaveServer());
        slaveServer2.setName("test");
        slaveServer2.verifyAndModifySlaveServerName(list, null);
        Assert.assertTrue((!(slaveServer.getName().equals(slaveServer2.getName()))));
    }

    @Test
    public void testEqualsHashCodeConsistency() throws Exception {
        SlaveServer slave = new SlaveServer();
        slave.setName("slave");
        TestUtils.checkEqualsHashCodeConsistency(slave, slave);
        SlaveServer slaveSame = new SlaveServer();
        slaveSame.setName("slave");
        Assert.assertTrue(slave.equals(slaveSame));
        TestUtils.checkEqualsHashCodeConsistency(slave, slaveSame);
        SlaveServer slaveCaps = new SlaveServer();
        slaveCaps.setName("SLAVE");
        TestUtils.checkEqualsHashCodeConsistency(slave, slaveCaps);
        SlaveServer slaveOther = new SlaveServer();
        slaveOther.setName("something else");
        TestUtils.checkEqualsHashCodeConsistency(slave, slaveOther);
    }

    @Test
    public void testGetKettleProperties() throws Exception {
        String encryptedResponse = "3c3f786d6c2076657273696f6e3d22312e302220656e636f64696e6" + ((((("73d225554462d38223f3e0a3c21444f43545950452070726f706572" + "746965730a202053595354454d2022687474703a2f2f6a6176612e737") + "56e2e636f6d2f6474642f70726f706572746965732e647464223e0a3c") + "70726f706572746965733e0a2020203c636f6d6d656e743e3c2f636f6d6d6") + "56e743e0a2020203c656e747279206b65793d224167696c6542494461746162") + "617365223e4167696c6542493c2f656e7470c7a6a5f445d7808bbb1cbc64d797bc84");
        Mockito.doReturn(encryptedResponse).when(slaveServer).execService(((GetPropertiesServlet.CONTEXT_PATH) + "/?xml=Y"));
        slaveServer.getKettleProperties().getProperty("AgileBIDatabase");
        Assert.assertEquals("AgileBI", slaveServer.getKettleProperties().getProperty("AgileBIDatabase"));
    }
}

