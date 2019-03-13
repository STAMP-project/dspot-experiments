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
import StatusCodes.UNAUTHORIZED;
import io.undertow.security.api.GSSAPIServerSubjectFactory;
import io.undertow.testutils.AjpIgnore;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.FlexBase64;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivilegedExceptionAction;
import java.util.Base64;
import javax.security.auth.Subject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * A test case to test the SPNEGO authentication mechanism.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(DefaultServer.class)
@AjpIgnore(apacheOnly = true, value = "SPNEGO requires a single connection to the server, and apache cannot guarantee that")
public class SpnegoAuthenticationTestCase extends AuthenticationTestBase {
    private static Oid SPNEGO;

    @Test
    public void testSpnegoSuccess() throws Exception {
        final TestHttpClient client = new TestHttpClient();
        HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL());
        HttpResponse result = client.execute(get);
        Assert.assertEquals(UNAUTHORIZED, result.getStatusLine().getStatusCode());
        Header[] values = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
        String header = AuthenticationTestBase.getAuthHeader(Headers.NEGOTIATE, values);
        Assert.assertEquals(Headers.NEGOTIATE.toString(), header);
        HttpClientUtils.readResponse(result);
        Subject clientSubject = KerberosKDCUtil.login("jduke", "theduke".toCharArray());
        Subject.doAs(clientSubject, new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                GSSManager gssManager = GSSManager.getInstance();
                GSSName serverName = gssManager.createName(("HTTP/" + (DefaultServer.getDefaultServerAddress().getHostString())), null);
                GSSContext context = gssManager.createContext(serverName, SpnegoAuthenticationTestCase.SPNEGO, null, GSSContext.DEFAULT_LIFETIME);
                byte[] token = new byte[0];
                boolean gotOur200 = false;
                while (!(context.isEstablished())) {
                    token = context.initSecContext(token, 0, token.length);
                    if ((token != null) && ((token.length) > 0)) {
                        HttpGet get = new HttpGet(DefaultServer.getDefaultServerURL());
                        get.addHeader(Headers.AUTHORIZATION.toString(), (((Headers.NEGOTIATE) + " ") + (FlexBase64.encodeString(token, false))));
                        HttpResponse result = client.execute(get);
                        Header[] headers = result.getHeaders(Headers.WWW_AUTHENTICATE.toString());
                        if ((headers.length) > 0) {
                            String header = AuthenticationTestBase.getAuthHeader(Headers.NEGOTIATE, headers);
                            byte[] headerBytes = header.getBytes(StandardCharsets.US_ASCII);
                            // FlexBase64.decode() returns byte buffer, which can contain backend array of greater size.
                            // when on such ByteBuffer is called array(), it returns the underlying byte array including the 0 bytes
                            // at the end, which makes the token invalid. => using Base64 mime decoder, which returnes directly properly sized byte[].
                            token = Base64.getMimeDecoder().decode(ArrayUtils.subarray(headerBytes, ((Headers.NEGOTIATE.toString().length()) + 1), headerBytes.length));
                        }
                        if ((result.getStatusLine().getStatusCode()) == (StatusCodes.OK)) {
                            Header[] values = result.getHeaders("ProcessedBy");
                            Assert.assertEquals(1, values.length);
                            Assert.assertEquals("ResponseHandler", values[0].getValue());
                            HttpClientUtils.readResponse(result);
                            AuthenticationTestBase.assertSingleNotificationType(AUTHENTICATED);
                            gotOur200 = true;
                        } else
                            if ((result.getStatusLine().getStatusCode()) == (StatusCodes.UNAUTHORIZED)) {
                                Assert.assertTrue("We did get a header.", ((headers.length) > 0));
                                HttpClientUtils.readResponse(result);
                            } else {
                                Assert.fail(String.format("Unexpected status code %d", result.getStatusLine().getStatusCode()));
                            }

                    }
                } 
                Assert.assertTrue(gotOur200);
                Assert.assertTrue(context.isEstablished());
                return null;
            }
        });
    }

    private class SubjectFactory implements GSSAPIServerSubjectFactory {
        @Override
        public Subject getSubjectForHost(String hostName) throws GeneralSecurityException {
            return KerberosKDCUtil.login(("HTTP/" + hostName), "servicepwd".toCharArray());
        }
    }
}

