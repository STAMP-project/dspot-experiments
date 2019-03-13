/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.adapter.jaas;


import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.jaas.RolePrincipal;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class LoginModulesTest extends AbstractKeycloakTest {
    public static final URI DIRECT_GRANT_CONFIG;

    public static final URI BEARER_CONFIG;

    private static final File DIRECT_GRANT_CONFIG_FILE;

    private static final File BEARER_CONFIG_FILE;

    static {
        try {
            DIRECT_GRANT_CONFIG = MethodHandles.lookup().lookupClass().getResource("/adapter-test/customer-portal/WEB-INF/keycloak.json").toURI();
            BEARER_CONFIG = MethodHandles.lookup().lookupClass().getResource("/adapter-test/customer-db-audience-required/WEB-INF/keycloak.json").toURI();
            DIRECT_GRANT_CONFIG_FILE = File.createTempFile("LoginModulesTest", "testDirectAccessGrantLoginModuleLoginFailed");
            BEARER_CONFIG_FILE = File.createTempFile("LoginModulesTest", "testBearerLoginFailedLogin");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDirectAccessGrantLoginModuleLoginFailed() throws Exception {
        Assume.assumeTrue(AbstractKeycloakTest.AUTH_SERVER_SSL_REQUIRED);
        LoginContext loginContext = new LoginContext("does-not-matter", null, createJaasCallbackHandler("bburke@redhat.com", "bad-password"), createJaasConfigurationForDirectGrant(null));
        try {
            loginContext.login();
            org.keycloak.testsuite.Assert.fail("Not expected to successfully login");
        } catch (LoginException le) {
            // Ignore
        }
    }

    @Test
    public void testDirectAccessGrantLoginModuleLoginSuccess() throws Exception {
        Assume.assumeTrue(AbstractKeycloakTest.AUTH_SERVER_SSL_REQUIRED);
        oauth.realm("demo");
        LoginContext loginContext = directGrantLogin(null);
        Subject subject = loginContext.getSubject();
        // Assert principals in subject
        KeycloakPrincipal principal = subject.getPrincipals(KeycloakPrincipal.class).iterator().next();
        org.keycloak.testsuite.Assert.assertEquals("bburke@redhat.com", principal.getKeycloakSecurityContext().getToken().getPreferredUsername());
        assertToken(principal.getKeycloakSecurityContext().getTokenString(), true);
        Set<RolePrincipal> roles = subject.getPrincipals(RolePrincipal.class);
        org.keycloak.testsuite.Assert.assertEquals(1, roles.size());
        org.keycloak.testsuite.Assert.assertEquals("user", roles.iterator().next().getName());
        // Logout and assert token not valid anymore
        loginContext.logout();
        assertToken(principal.getKeycloakSecurityContext().getTokenString(), false);
    }

    @Test
    public void testBearerLoginFailedLogin() throws Exception {
        Assume.assumeTrue(AbstractKeycloakTest.AUTH_SERVER_SSL_REQUIRED);
        oauth.realm("demo");
        LoginContext directGrantCtx = directGrantLogin(null);
        String accessToken = directGrantCtx.getSubject().getPrincipals(KeycloakPrincipal.class).iterator().next().getKeycloakSecurityContext().getTokenString();
        LoginContext bearerCtx = new LoginContext("does-not-matter", null, createJaasCallbackHandler("doesn-not-matter", accessToken), createJaasConfigurationForBearer());
        // Login should fail due insufficient audience in the token
        try {
            bearerCtx.login();
            org.keycloak.testsuite.Assert.fail("Not expected to successfully login");
        } catch (LoginException le) {
            // Ignore
        }
        directGrantCtx.logout();
    }

    @Test
    public void testBearerLoginSuccess() throws Exception {
        Assume.assumeTrue(AbstractKeycloakTest.AUTH_SERVER_SSL_REQUIRED);
        oauth.realm("demo");
        LoginContext directGrantCtx = directGrantLogin("customer-db-audience-required");
        String accessToken = directGrantCtx.getSubject().getPrincipals(KeycloakPrincipal.class).iterator().next().getKeycloakSecurityContext().getTokenString();
        LoginContext bearerCtx = new LoginContext("does-not-matter", null, createJaasCallbackHandler("doesn-not-matter", accessToken), createJaasConfigurationForBearer());
        // Login should be successful
        bearerCtx.login();
        // Assert subject
        Subject subject = bearerCtx.getSubject();
        KeycloakPrincipal principal = subject.getPrincipals(KeycloakPrincipal.class).iterator().next();
        org.keycloak.testsuite.Assert.assertEquals("bburke@redhat.com", principal.getKeycloakSecurityContext().getToken().getPreferredUsername());
        assertToken(principal.getKeycloakSecurityContext().getTokenString(), true);
        Set<RolePrincipal> roles = subject.getPrincipals(RolePrincipal.class);
        org.keycloak.testsuite.Assert.assertEquals(1, roles.size());
        org.keycloak.testsuite.Assert.assertEquals("user", roles.iterator().next().getName());
        // Logout
        bearerCtx.logout();
        directGrantCtx.logout();
    }
}

