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
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import javax.net.ssl.SSLContext;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case covering the core of Client-Cert
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(DefaultServer.class)
public class ClientCertTestCase extends AuthenticationTestBase {
    private static SSLContext clientSSLContext;

    @Test
    public void testClientCertSuccess() throws Exception {
        TestHttpClient client = new TestHttpClient();
        client.setSSLContext(ClientCertTestCase.clientSSLContext);
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
}

