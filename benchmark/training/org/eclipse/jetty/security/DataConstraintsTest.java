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


import Constraint.DC_CONFIDENTIAL;
import Constraint.DC_INTEGRAL;
import HttpMethod.POST;
import java.util.Arrays;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class DataConstraintsTest {
    private Server _server;

    private LocalConnector _connector;

    private LocalConnector _connectorS;

    private SessionHandler _session;

    private ConstraintSecurityHandler _security;

    @Test
    public void testIntegral() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setAuthenticate(false);
        constraint0.setName("integral");
        constraint0.setDataConstraint(DC_INTEGRAL);
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/integral/*");
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/some/thing HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connector.getResponse("GET /ctx/integral/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 302 Found"));
        MatcherAssert.assertThat(response, Matchers.containsString("Location: BWTP://"));
        MatcherAssert.assertThat(response, Matchers.containsString(":9999"));
        response = _connectorS.getResponse("GET /ctx/integral/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
    }

    @Test
    public void testConfidential() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setAuthenticate(false);
        constraint0.setName("confid");
        constraint0.setDataConstraint(DC_CONFIDENTIAL);
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/confid/*");
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/some/thing HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connector.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 302 Found"));
        MatcherAssert.assertThat(response, Matchers.containsString("Location: BWTP://"));
        MatcherAssert.assertThat(response, Matchers.containsString(":9999"));
        response = _connectorS.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
    }

    @Test
    public void testConfidentialWithNoRolesSetAndNoMethodRestriction() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setName("confid");
        constraint0.setDataConstraint(DC_CONFIDENTIAL);
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/confid/*");
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 302 Found"));
        response = _connectorS.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
    }

    @Test
    public void testConfidentialWithNoRolesSetAndMethodRestriction() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setName("confid");
        constraint0.setDataConstraint(DC_CONFIDENTIAL);
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/confid/*");
        mapping0.setMethod(POST.asString());
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connectorS.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connector.getResponse("POST /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 302 Found"));
        response = _connectorS.getResponse("POST /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
    }

    @Test
    public void testConfidentialWithRolesSetAndMethodRestriction() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setRoles(new String[]{ "admin" });
        constraint0.setName("confid");
        constraint0.setDataConstraint(DC_CONFIDENTIAL);
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/confid/*");
        mapping0.setMethod(POST.asString());
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connectorS.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connector.getResponse("POST /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 302 Found"));
        response = _connectorS.getResponse("POST /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
    }

    @Test
    public void testConfidentialWithRolesSetAndMethodRestrictionAndAuthenticationRequired() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setRoles(new String[]{ "admin" });
        constraint0.setAuthenticate(true);
        constraint0.setName("confid");
        constraint0.setDataConstraint(DC_CONFIDENTIAL);
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/confid/*");
        mapping0.setMethod(POST.asString());
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        DefaultIdentityService identityService = new DefaultIdentityService();
        _security.setLoginService(new DataConstraintsTest.CustomLoginService(identityService));
        _security.setIdentityService(identityService);
        _security.setAuthenticator(new BasicAuthenticator());
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connectorS.getResponse("GET /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connector.getResponse("POST /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 302 Found"));
        response = _connectorS.getResponse("POST /ctx/confid/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 401 Unauthorized"));
        response = _connector.getResponse("GET /ctx/confid/info HTTP/1.0\r\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connector.getResponse("POST /ctx/confid/info HTTP/1.0\r\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 302 Found"));
        response = _connectorS.getResponse("POST /ctx/confid/info HTTP/1.0\r\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
    }

    @Test
    public void testRestrictedWithoutAuthenticator() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setAuthenticate(true);
        constraint0.setRoles(new String[]{ "admin" });
        constraint0.setName("restricted");
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/restricted/*");
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/restricted/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 403 Forbidden"));
        response = _connectorS.getResponse("GET /ctx/restricted/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 403 Forbidden"));
        response = _connector.getResponse("GET /ctx/restricted/info HTTP/1.0\r\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 403 Forbidden"));
        response = _connectorS.getResponse("GET /ctx/restricted/info HTTP/1.0\r\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 403 Forbidden"));
    }

    @Test
    public void testRestrictedWithoutAuthenticatorAndMethod() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setAuthenticate(true);
        constraint0.setRoles(new String[]{ "admin" });
        constraint0.setName("restricted");
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/restricted/*");
        mapping0.setMethod("GET");
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/restricted/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 403 Forbidden"));
        response = _connectorS.getResponse("GET /ctx/restricted/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 403 Forbidden"));
        response = _connector.getResponse("GET /ctx/restricted/info HTTP/1.0\r\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 403 Forbidden"));
        response = _connectorS.getResponse("GET /ctx/restricted/info HTTP/1.0\r\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 403 Forbidden"));
    }

    @Test
    public void testRestricted() throws Exception {
        Constraint constraint0 = new Constraint();
        constraint0.setAuthenticate(true);
        constraint0.setRoles(new String[]{ "admin" });
        constraint0.setName("restricted");
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/restricted/*");
        mapping0.setMethod("GET");
        mapping0.setConstraint(constraint0);
        _security.setConstraintMappings(Arrays.asList(new ConstraintMapping[]{ mapping0 }));
        DefaultIdentityService identityService = new DefaultIdentityService();
        _security.setLoginService(new DataConstraintsTest.CustomLoginService(identityService));
        _security.setIdentityService(identityService);
        _security.setAuthenticator(new BasicAuthenticator());
        _server.start();
        String response;
        response = _connector.getResponse("GET /ctx/restricted/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 401 Unauthorized"));
        response = _connectorS.getResponse("GET /ctx/restricted/info HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 401 Unauthorized"));
        response = _connector.getResponse("GET /ctx/restricted/info HTTP/1.0\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\n\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
        response = _connectorS.getResponse("GET /ctx/restricted/info HTTP/1.0\nAuthorization: Basic YWRtaW46cGFzc3dvcmQ=\n\n");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 404 Not Found"));
    }

    private class CustomLoginService implements LoginService {
        private IdentityService identityService;

        public CustomLoginService(IdentityService identityService) {
            this.identityService = identityService;
        }

        @Override
        public String getName() {
            return "name";
        }

        @Override
        public UserIdentity login(String username, Object credentials, ServletRequest request) {
            if (("admin".equals(username)) && ("password".equals(credentials)))
                return new DefaultUserIdentity(null, null, new String[]{ "admin" });

            return null;
        }

        @Override
        public boolean validate(UserIdentity user) {
            return false;
        }

        @Override
        public IdentityService getIdentityService() {
            return identityService;
        }

        @Override
        public void setIdentityService(IdentityService service) {
        }

        @Override
        public void logout(UserIdentity user) {
        }
    }
}

