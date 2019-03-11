/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.security;


import EventType.AUTHENTICATED;
import StatusCodes.OK;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.ProxyIgnore;
import io.undertow.testutils.TestHttpClient;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case covering the core of Client-Cert
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@ProxyIgnore
public class ClientCertRenegotiationTestCase extends AuthenticationTestBase {
    private static SSLContext clientSSLContext;

    @Test
    public void testClientCertSuccess() throws Exception {
        TestHttpClient client = new TestHttpClient();
        client.setSSLContext(ClientCertRenegotiationTestCase.clientSSLContext);
        HttpGet get = new HttpGet(DefaultServer.getDefaultServerSSLAddress());
        HttpResponse result = client.execute(get);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders("ProcessedBy");
        Assert.assertEquals("ProcessedBy Headers", 1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        values = result.getHeaders("AuthenticatedUser");
        Assert.assertEquals("AuthenticatedUser Headers", 1, values.length);
        Assert.assertEquals("CN=Test Client,OU=OU,O=Org,L=City,ST=State,C=GB", values[0].getValue());
        HttpClientUtils.readResponse(result);
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
    }

    @Test
    public void testClientCertSuccessWithPostBody() throws Exception {
        TestHttpClient client = new TestHttpClient();
        try {
            client.setSSLContext(ClientCertRenegotiationTestCase.clientSSLContext);
            HttpPost post = new HttpPost(DefaultServer.getDefaultServerSSLAddress());
            post.setEntity(new StringEntity("hi"));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Header[] values = result.getHeaders("ProcessedBy");
            Assert.assertEquals("ProcessedBy Headers", 1, values.length);
            Assert.assertEquals("ResponseHandler", values[0].getValue());
            values = result.getHeaders("AuthenticatedUser");
            Assert.assertEquals("AuthenticatedUser Headers", 1, values.length);
            Assert.assertEquals("CN=Test Client,OU=OU,O=Org,L=City,ST=State,C=GB", values[0].getValue());
            HttpClientUtils.readResponse(result);
            AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testClientCertSuccessWithLargePostBody() throws Exception {
        PooledByteBuffer buf = DefaultServer.getBufferPool().allocate();
        int requestSize = (buf.getBuffer().limit()) - 1;
        buf.close();
        final StringBuilder messageBuilder = new StringBuilder(requestSize);
        for (int i = 0; i < requestSize; ++i) {
            messageBuilder.append("*");
        }
        TestHttpClient client = new TestHttpClient();
        client.setSSLContext(ClientCertRenegotiationTestCase.clientSSLContext);
        HttpPost post = new HttpPost(DefaultServer.getDefaultServerSSLAddress());
        post.setEntity(new StringEntity(messageBuilder.toString()));
        HttpResponse result = client.execute(post);
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders("ProcessedBy");
        Assert.assertEquals("ProcessedBy Headers", 1, values.length);
        Assert.assertEquals("ResponseHandler", values[0].getValue());
        values = result.getHeaders("AuthenticatedUser");
        Assert.assertEquals("AuthenticatedUser Headers", 1, values.length);
        Assert.assertEquals("CN=Test Client,OU=OU,O=Org,L=City,ST=State,C=GB", values[0].getValue());
        HttpClientUtils.readResponse(result);
        AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
    }
}

