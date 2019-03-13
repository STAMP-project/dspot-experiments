package org.keycloak.testsuite.ssl;


import org.junit.Assert;
import org.junit.Test;
import org.keycloak.protocol.oidc.representations.OIDCConfigurationRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;


/**
 * This test checks if TLS can be explicitly switched off.
 *
 * Note, it should run only if TLS is enabled by default.
 */
public class TLSTest extends AbstractTestRealmKeycloakTest {
    public static final String AUTH_SERVER_ROOT_WITHOUT_TLS = ("http://localhost:" + (System.getProperty("auth.server.http.port", "8180"))) + "/auth";

    @Test
    public void testTurningTLSOn() throws Exception {
        // given
        oauth.baseUrl(TLSTest.AUTH_SERVER_ROOT_WITHOUT_TLS);
        // when
        OIDCConfigurationRepresentation config = oauth.doWellKnownRequest("test");
        // then
        org.keycloak.testsuite.Assert.assertTrue(config.getAuthorizationEndpoint().startsWith(TLSTest.AUTH_SERVER_ROOT_WITHOUT_TLS));
    }
}

