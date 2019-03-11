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
package org.apache.hadoop.security.token.delegation.web;


import HttpServletResponse.SC_UNAUTHORIZED;
import KerberosAuthenticator.WWW_AUTHENTICATE;
import KerberosDelegationTokenAuthenticationHandler.TOKEN_KIND;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.authentication.server.AuthenticationHandler;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import org.apache.hadoop.security.token.Token;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;

import static DelegationTokenAuthenticationHandler.JSON_MAPPER_PREFIX;
import static DelegationTokenAuthenticator.DELEGATION_PARAM;
import static DelegationTokenAuthenticator.OP_PARAM;
import static DelegationTokenAuthenticator.RENEWER_PARAM;
import static DelegationTokenAuthenticator.TOKEN_PARAM;
import static DelegationTokenOperation.DelegationTokenOperation.GETDELEGATIONTOKEN;
import static DelegationTokenOperation.DelegationTokenOperation.RENEWDELEGATIONTOKEN;


public class TestDelegationTokenAuthenticationHandlerWithMocks {
    public static class MockDelegationTokenAuthenticationHandler extends DelegationTokenAuthenticationHandler {
        public MockDelegationTokenAuthenticationHandler() {
            super(new AuthenticationHandler() {
                @Override
                public String getType() {
                    return "T";
                }

                @Override
                public void init(Properties config) throws ServletException {
                }

                @Override
                public void destroy() {
                }

                @Override
                public boolean managementOperation(AuthenticationToken token, HttpServletRequest request, HttpServletResponse response) throws IOException, AuthenticationException {
                    return false;
                }

                @Override
                public AuthenticationToken authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException, AuthenticationException {
                    response.setStatus(SC_UNAUTHORIZED);
                    response.setHeader(WWW_AUTHENTICATE, "mock");
                    return null;
                }
            });
        }
    }

    private DelegationTokenAuthenticationHandler handler;

    @Rule
    public Timeout testTimeout = new Timeout(120000);

    @Test
    public void testManagementOperations() throws Exception {
        final Text testTokenKind = new Text("foo");
        final String testRenewer = "bar";
        final String testService = "192.168.64.101:8888";
        testNonManagementOperation();
        testManagementOperationErrors();
        testGetToken(null, null, testTokenKind);
        testGetToken(testRenewer, null, testTokenKind);
        testCancelToken();
        testRenewToken(testRenewer);
        // Management operations against token requested with service parameter
        Token<DelegationTokenIdentifier> testToken = testGetToken(testRenewer, testService, testTokenKind);
        testRenewToken(testToken, testRenewer);
        testCancelToken(testToken);
    }

    @Test
    public void testAuthenticate() throws Exception {
        testValidDelegationTokenQueryString();
        testValidDelegationTokenHeader();
        testInvalidDelegationTokenQueryString();
        testInvalidDelegationTokenHeader();
    }

    @Test
    public void testCannotGetTokenUsingToken() throws Exception {
        DelegationTokenAuthenticator.DelegationTokenOperation op = GETDELEGATIONTOKEN;
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getMethod()).thenReturn(op.getHttpMethod());
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        String tokenStr = getToken();
        // Try get a new token using the fetched token, should get 401.
        Mockito.when(request.getQueryString()).thenReturn((((((((((((OP_PARAM) + "=") + (op.toString())) + "&") + (RENEWER_PARAM)) + "=") + null) + "&") + (DELEGATION_PARAM)) + "=") + tokenStr));
        Mockito.reset(response);
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        Mockito.when(response.getWriter()).thenReturn(pwriter);
        Assert.assertFalse(handler.managementOperation(null, request, response));
        Mockito.verify(response).setStatus(SC_UNAUTHORIZED);
    }

    @Test
    public void testCannotRenewTokenUsingToken() throws Exception {
        DelegationTokenAuthenticator.DelegationTokenOperation op = RENEWDELEGATIONTOKEN;
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getMethod()).thenReturn(op.getHttpMethod());
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        String tokenStr = getToken();
        // Try renew a token using itself, should get 401.
        Mockito.when(request.getQueryString()).thenReturn((((((((((((OP_PARAM) + "=") + (op.toString())) + "&") + (TOKEN_PARAM)) + "=") + tokenStr) + "&") + (DELEGATION_PARAM)) + "=") + tokenStr));
        Mockito.reset(response);
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        Mockito.when(response.getWriter()).thenReturn(pwriter);
        Assert.assertFalse(handler.managementOperation(null, request, response));
        Mockito.verify(response).setStatus(SC_UNAUTHORIZED);
    }

    @Test
    public void testWriterNotClosed() throws Exception {
        Properties conf = new Properties();
        conf.put(TOKEN_KIND, "foo");
        conf.put(((JSON_MAPPER_PREFIX) + "AUTO_CLOSE_TARGET"), "false");
        DelegationTokenAuthenticationHandler noAuthCloseHandler = new TestDelegationTokenAuthenticationHandlerWithMocks.MockDelegationTokenAuthenticationHandler();
        try {
            noAuthCloseHandler.initTokenManager(conf);
            noAuthCloseHandler.initJsonFactory(conf);
            DelegationTokenAuthenticator.DelegationTokenOperation op = GETDELEGATIONTOKEN;
            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(request.getQueryString()).thenReturn((((OP_PARAM) + "=") + (op.toString())));
            Mockito.when(request.getMethod()).thenReturn(op.getHttpMethod());
            AuthenticationToken token = Mockito.mock(AuthenticationToken.class);
            Mockito.when(token.getUserName()).thenReturn("user");
            final MutableBoolean closed = new MutableBoolean();
            PrintWriter printWriterCloseCount = new PrintWriter(new StringWriter()) {
                @Override
                public void close() {
                    closed.setValue(true);
                    super.close();
                }

                @Override
                public void write(String str) {
                    if (closed.booleanValue()) {
                        throw new RuntimeException("already closed!");
                    }
                    super.write(str);
                }
            };
            Mockito.when(response.getWriter()).thenReturn(printWriterCloseCount);
            Assert.assertFalse(noAuthCloseHandler.managementOperation(token, request, response));
        } finally {
            noAuthCloseHandler.destroy();
        }
    }
}

