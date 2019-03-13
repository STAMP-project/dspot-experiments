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
package org.eclipse.jetty.security;


import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.B64Code;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @version $Revision: 1441 $ $Date: 2010-04-02 12:28:17 +0200 (Fri, 02 Apr 2010) $
 */
public class SpecExampleConstraintTest {
    private static final String TEST_REALM = "TestRealm";

    private static Server _server;

    private static LocalConnector _connector;

    private static SessionHandler _session;

    private ConstraintSecurityHandler _security;

    @Test
    public void testUncoveredHttpMethodDetection() throws Exception {
        _security.setAuthenticator(new BasicAuthenticator());
        SpecExampleConstraintTest._server.start();
        Set<String> paths = _security.getPathsWithUncoveredHttpMethods();
        Assertions.assertEquals(1, paths.size());
        Assertions.assertEquals("/*", paths.iterator().next());
    }

    @Test
    public void testUncoveredHttpMethodsDenied() throws Exception {
        try {
            _security.setDenyUncoveredHttpMethods(false);
            _security.setAuthenticator(new BasicAuthenticator());
            SpecExampleConstraintTest._server.start();
            // There are uncovered methods for GET/POST at url /*
            // without deny-uncovered-http-methods they should be accessible
            String response;
            response = SpecExampleConstraintTest._connector.getResponse("GET /ctx/index.html HTTP/1.0\r\n\r\n");
            MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
            // set deny-uncovered-http-methods true
            _security.setDenyUncoveredHttpMethods(true);
            // check they cannot be accessed
            response = SpecExampleConstraintTest._connector.getResponse("GET /ctx/index.html HTTP/1.0\r\n\r\n");
            Assertions.assertTrue(response.startsWith("HTTP/1.1 403 Forbidden"));
        } finally {
            _security.setDenyUncoveredHttpMethods(false);
        }
    }

    @Test
    public void testBasic() throws Exception {
        _security.setAuthenticator(new BasicAuthenticator());
        SpecExampleConstraintTest._server.start();
        String response;
        /* /star                 all methods except GET/POST forbidden
        /acme/wholesale/star  all methods except GET/POST forbidden
        /acme/retail/star     all methods except GET/POST forbidden
        /acme/wholesale/star  GET must be in role CONTRACTOR or SALESCLERK
        /acme/wholesale/star  POST must be in role CONTRACTOR and confidential transport
        /acme/retail/star     GET must be in role CONTRACTOR or HOMEOWNER
        /acme/retail/star     POST must be in role CONTRACTOR or HOMEOWNER
         */
        // a user in role HOMEOWNER is forbidden HEAD request
        response = SpecExampleConstraintTest._connector.getResponse("HEAD /ctx/index.html HTTP/1.0\r\n\r\n");
        Assertions.assertTrue(response.startsWith("HTTP/1.1 403 Forbidden"));
        response = SpecExampleConstraintTest._connector.getResponse((((("HEAD /ctx/index.html HTTP/1.0\r\n" + "Authorization: Basic ") + (B64Code.encode("harry:password"))) + "\r\n") + "\r\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 403 Forbidden"));
        response = SpecExampleConstraintTest._connector.getResponse((((("HEAD /ctx/acme/wholesale/index.html HTTP/1.0\r\n" + "Authorization: Basic ") + (B64Code.encode("harry:password"))) + "\r\n") + "\r\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 403 Forbidden"));
        response = SpecExampleConstraintTest._connector.getResponse((((("HEAD /ctx/acme/retail/index.html HTTP/1.0\r\n" + "Authorization: Basic ") + (B64Code.encode("harry:password"))) + "\r\n") + "\r\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 403 Forbidden"));
        // a user in role CONTRACTOR can do a GET
        response = SpecExampleConstraintTest._connector.getResponse((((("GET /ctx/acme/wholesale/index.html HTTP/1.0\r\n" + "Authorization: Basic ") + (B64Code.encode("chris:password"))) + "\r\n") + "\r\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
        // a user in role CONTRACTOR can only do a post if confidential
        response = SpecExampleConstraintTest._connector.getResponse((((("POST /ctx/acme/wholesale/index.html HTTP/1.0\r\n" + "Authorization: Basic ") + (B64Code.encode("chris:password"))) + "\r\n") + "\r\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 403 !"));
        // a user in role HOMEOWNER can do a GET
        response = SpecExampleConstraintTest._connector.getResponse((((("GET /ctx/acme/retail/index.html HTTP/1.0\r\n" + "Authorization: Basic ") + (B64Code.encode("harry:password"))) + "\r\n") + "\r\n"));
        MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 200 OK"));
    }

    private class RequestHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().println(("URI=" + (request.getRequestURI())));
            String user = request.getRemoteUser();
            response.getWriter().println(("user=" + user));
            if ((request.getParameter("test_parameter")) != null)
                response.getWriter().println(request.getParameter("test_parameter"));

        }
    }
}

