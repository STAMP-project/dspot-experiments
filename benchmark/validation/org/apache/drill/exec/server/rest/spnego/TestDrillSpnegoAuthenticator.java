/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.server.rest.spnego;


import Authentication.SEND_CONTINUE;
import Authentication.UNAUTHENTICATED;
import HttpHeader.AUTHORIZATION;
import HttpHeader.NEGOTIATE;
import HttpHeader.WWW_AUTHENTICATE;
import SessionAuthentication.__J_AUTHENTICATED;
import WebServerConstants.LOGOUT_RESOURCE_PATH;
import WebServerConstants.SPENGO_LOGIN_RESOURCE_PATH;
import WebServerConstants.WEBSERVER_ROOT_PATH;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.apache.drill.categories.SecurityTest;
import org.apache.drill.exec.rpc.security.KerberosHelper;
import org.apache.drill.exec.server.rest.auth.DrillSpnegoAuthenticator;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.kerby.kerberos.kerb.client.JaasKrbUtil;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import sun.security.jgss.GSSUtil;


/**
 * Test for validating {@link DrillSpnegoAuthenticator}
 */
@Ignore("See DRILL-5387")
@Category(SecurityTest.class)
public class TestDrillSpnegoAuthenticator {
    private static KerberosHelper spnegoHelper;

    private static final String primaryName = "HTTP";

    private static DrillSpnegoAuthenticator spnegoAuthenticator;

    private static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    /**
     * Test to verify response when request is sent for {@link WebServerConstants#SPENGO_LOGIN_RESOURCE_PATH} from
     * unauthenticated session. Expectation is client will receive response with Negotiate header.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNewSessionReqForSpnegoLogin() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.when(request.getRequestURI()).thenReturn(SPENGO_LOGIN_RESOURCE_PATH);
        final Authentication authentication = TestDrillSpnegoAuthenticator.spnegoAuthenticator.validateRequest(request, response, false);
        Assert.assertEquals(authentication, SEND_CONTINUE);
        Mockito.verify(response).sendError(401);
        Mockito.verify(response).setHeader(WWW_AUTHENTICATE.asString(), NEGOTIATE.asString());
    }

    /**
     * Test to verify response when request is sent for {@link WebServerConstants#SPENGO_LOGIN_RESOURCE_PATH} from
     * authenticated session. Expectation is server will find the authenticated UserIdentity.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAuthClientRequestForSpnegoLoginResource() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final HttpSession session = Mockito.mock(HttpSession.class);
        final Authentication authentication = Mockito.mock(UserAuthentication.class);
        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.when(request.getRequestURI()).thenReturn(SPENGO_LOGIN_RESOURCE_PATH);
        Mockito.when(session.getAttribute(__J_AUTHENTICATED)).thenReturn(authentication);
        final UserAuthentication returnedAuthentication = ((UserAuthentication) (TestDrillSpnegoAuthenticator.spnegoAuthenticator.validateRequest(request, response, false)));
        Assert.assertEquals(authentication, returnedAuthentication);
        Mockito.verify(response, Mockito.never()).sendError(401);
        Mockito.verify(response, Mockito.never()).setHeader(WWW_AUTHENTICATE.asString(), NEGOTIATE.asString());
    }

    /**
     * Test to verify response when request is sent for any other resource other than
     * {@link WebServerConstants#SPENGO_LOGIN_RESOURCE_PATH} from authenticated session. Expectation is server will
     * find the authenticated UserIdentity and will not perform the authentication again for new resource.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAuthClientRequestForOtherPage() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final HttpSession session = Mockito.mock(HttpSession.class);
        final Authentication authentication = Mockito.mock(UserAuthentication.class);
        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.when(request.getRequestURI()).thenReturn(WEBSERVER_ROOT_PATH);
        Mockito.when(session.getAttribute(__J_AUTHENTICATED)).thenReturn(authentication);
        final UserAuthentication returnedAuthentication = ((UserAuthentication) (TestDrillSpnegoAuthenticator.spnegoAuthenticator.validateRequest(request, response, false)));
        Assert.assertEquals(authentication, returnedAuthentication);
        Mockito.verify(response, Mockito.never()).sendError(401);
        Mockito.verify(response, Mockito.never()).setHeader(WWW_AUTHENTICATE.asString(), NEGOTIATE.asString());
    }

    /**
     * Test to verify that when request is sent for {@link WebServerConstants#LOGOUT_RESOURCE_PATH} then the UserIdentity
     * will be removed from the session and returned authentication will be null from
     * {@link DrillSpnegoAuthenticator#validateRequest(ServletRequest, ServletResponse, boolean)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAuthClientRequestForLogOut() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final HttpSession session = Mockito.mock(HttpSession.class);
        final Authentication authentication = Mockito.mock(UserAuthentication.class);
        Mockito.when(request.getSession(true)).thenReturn(session);
        Mockito.when(request.getRequestURI()).thenReturn(LOGOUT_RESOURCE_PATH);
        Mockito.when(session.getAttribute(__J_AUTHENTICATED)).thenReturn(authentication);
        final UserAuthentication returnedAuthentication = ((UserAuthentication) (TestDrillSpnegoAuthenticator.spnegoAuthenticator.validateRequest(request, response, false)));
        Assert.assertNull(returnedAuthentication);
        Mockito.verify(session).removeAttribute(__J_AUTHENTICATED);
        Mockito.verify(response, Mockito.never()).sendError(401);
        Mockito.verify(response, Mockito.never()).setHeader(WWW_AUTHENTICATE.asString(), NEGOTIATE.asString());
    }

    /**
     * Test to verify authentication fails when client sends invalid SPNEGO token for the
     * {@link WebServerConstants#SPENGO_LOGIN_RESOURCE_PATH} resource.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSpnegoLoginInvalidToken() throws Exception {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final HttpSession session = Mockito.mock(HttpSession.class);
        // Create client subject using it's principal and keytab
        final Subject clientSubject = JaasKrbUtil.loginUsingKeytab(TestDrillSpnegoAuthenticator.spnegoHelper.CLIENT_PRINCIPAL, TestDrillSpnegoAuthenticator.spnegoHelper.clientKeytab.getAbsoluteFile());
        // Generate a SPNEGO token for the peer SERVER_PRINCIPAL from this CLIENT_PRINCIPAL
        final String token = Subject.doAs(clientSubject, new PrivilegedExceptionAction<String>() {
            @Override
            public String run() throws Exception {
                final GSSManager gssManager = GSSManager.getInstance();
                GSSContext gssContext = null;
                try {
                    final Oid oid = GSSUtil.GSS_SPNEGO_MECH_OID;
                    final GSSName serviceName = gssManager.createName(TestDrillSpnegoAuthenticator.spnegoHelper.SERVER_PRINCIPAL, GSSName.NT_USER_NAME, oid);
                    gssContext = gssManager.createContext(serviceName, oid, null, GSSContext.DEFAULT_LIFETIME);
                    gssContext.requestCredDeleg(true);
                    gssContext.requestMutualAuth(true);
                    byte[] outToken = new byte[0];
                    outToken = gssContext.initSecContext(outToken, 0, outToken.length);
                    return Base64.encodeBase64String(outToken);
                } finally {
                    if (gssContext != null) {
                        gssContext.dispose();
                    }
                }
            }
        });
        Mockito.when(request.getSession(true)).thenReturn(session);
        final String httpReqAuthHeader = String.format("%s:%s", NEGOTIATE.asString(), String.format("%s%s", "1234", token));
        Mockito.when(request.getHeader(AUTHORIZATION.asString())).thenReturn(httpReqAuthHeader);
        Mockito.when(request.getRequestURI()).thenReturn(SPENGO_LOGIN_RESOURCE_PATH);
        Assert.assertEquals(TestDrillSpnegoAuthenticator.spnegoAuthenticator.validateRequest(request, response, false), UNAUTHENTICATED);
        Mockito.verify(session, Mockito.never()).setAttribute(__J_AUTHENTICATED, null);
        Mockito.verify(response, Mockito.never()).sendError(401);
        Mockito.verify(response, Mockito.never()).setHeader(WWW_AUTHENTICATE.asString(), NEGOTIATE.asString());
    }
}

