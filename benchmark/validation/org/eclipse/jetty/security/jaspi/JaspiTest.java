/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.security.jaspi;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.util.security.Credential;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class JaspiTest {
    Server _server;

    LocalConnector _connector;

    public class TestLoginService extends AbstractLoginService {
        protected Map<String, UserPrincipal> _users = new HashMap<>();

        protected Map<String, String[]> _roles = new HashMap();

        public TestLoginService(String name) {
            setName(name);
        }

        public void putUser(String username, Credential credential, String[] roles) {
            UserPrincipal userPrincipal = new UserPrincipal(username, credential);
            _users.put(username, userPrincipal);
            _roles.put(username, roles);
        }

        /**
         *
         *
         * @see org.eclipse.jetty.security.AbstractLoginService#loadRoleInfo(org.eclipse.jetty.security.AbstractLoginService.UserPrincipal)
         */
        @Override
        protected String[] loadRoleInfo(UserPrincipal user) {
            return _roles.get(user.getName());
        }

        /**
         *
         *
         * @see org.eclipse.jetty.security.AbstractLoginService#loadUserInfo(java.lang.String)
         */
        @Override
        protected UserPrincipal loadUserInfo(String username) {
            return _users.get(username);
        }
    }

    @Test
    public void testNoConstraint() throws Exception {
        String response = _connector.getResponse("GET /ctx/test HTTP/1.0\n\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
    }

    @Test
    public void testConstraintNoAuth() throws Exception {
        String response = _connector.getResponse("GET /ctx/jaspi/test HTTP/1.0\n\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 401 Unauthorized"));
        MatcherAssert.assertThat(response, Matchers.containsString("WWW-Authenticate: basic realm=\"TestRealm\""));
    }

    @Test
    public void testConstraintWrongAuth() throws Exception {
        String response = _connector.getResponse(((("GET /ctx/jaspi/test HTTP/1.0\n" + "Authorization: Basic ") + (B64Code.encode("user:wrong"))) + "\n\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 401 Unauthorized"));
        MatcherAssert.assertThat(response, Matchers.containsString("WWW-Authenticate: basic realm=\"TestRealm\""));
    }

    @Test
    public void testConstraintAuth() throws Exception {
        String response = _connector.getResponse(((("GET /ctx/jaspi/test HTTP/1.0\n" + "Authorization: Basic ") + (B64Code.encode("user:password"))) + "\n\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
    }

    @Test
    public void testOtherNoAuth() throws Exception {
        String response = _connector.getResponse("GET /other/test HTTP/1.0\n\n");
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 403 Forbidden"));
    }

    @Test
    public void testOtherAuth() throws Exception {
        String response = _connector.getResponse(("GET /other/test HTTP/1.0\n" + "X-Forwarded-User: user\n\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
    }

    public class TestHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            response.setContentType("text/plain");
            response.getWriter().println("All OK");
            response.getWriter().println(("requestURI=" + (request.getRequestURI())));
        }
    }
}

