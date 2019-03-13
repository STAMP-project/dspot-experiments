package org.keycloak.testsuite.url;


import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.util.AdminClientUtil;
import org.keycloak.testsuite.util.OAuthClient;


public class FixedHostnameTest extends AbstractKeycloakTest {
    @ArquillianResource
    protected ContainerController controller;

    private String authServerUrl;

    @Test
    public void fixedHostname() throws Exception {
        authServerUrl = oauth.AUTH_SERVER_ROOT;
        oauth.baseUrl(authServerUrl);
        oauth.clientId("direct-grant");
        try (Keycloak testAdminClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), AuthServerTestEnricher.getAuthServerContextRoot())) {
            assertWellKnown("test", (((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://localhost:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)));
            configureFixedHostname((-1), (-1), false);
            assertWellKnown("test", (((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://keycloak.127.0.0.1.nip.io:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)));
            assertWellKnown("hostname", (((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://custom-domain.127.0.0.1.nip.io:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)));
            assertTokenIssuer("test", (((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://keycloak.127.0.0.1.nip.io:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)));
            assertTokenIssuer("hostname", (((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://custom-domain.127.0.0.1.nip.io:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)));
            assertInitialAccessTokenFromMasterRealm(testAdminClient, "test", (((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://keycloak.127.0.0.1.nip.io:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)));
            assertInitialAccessTokenFromMasterRealm(testAdminClient, "hostname", (((AbstractKeycloakTest.AUTH_SERVER_SCHEME) + "://custom-domain.127.0.0.1.nip.io:") + (AbstractKeycloakTest.AUTH_SERVER_PORT)));
        } finally {
            clearFixedHostname();
        }
    }

    @Test
    public void fixedHttpPort() throws Exception {
        // Make sure request are always sent with http
        authServerUrl = "http://localhost:8180/auth";
        oauth.baseUrl(authServerUrl);
        oauth.clientId("direct-grant");
        try (Keycloak testAdminClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), "http://localhost:8180")) {
            assertWellKnown("test", "http://localhost:8180");
            configureFixedHostname(80, (-1), false);
            assertWellKnown("test", "http://keycloak.127.0.0.1.nip.io");
            assertWellKnown("hostname", "http://custom-domain.127.0.0.1.nip.io");
            assertTokenIssuer("test", "http://keycloak.127.0.0.1.nip.io");
            assertTokenIssuer("hostname", "http://custom-domain.127.0.0.1.nip.io");
            assertInitialAccessTokenFromMasterRealm(testAdminClient, "test", "http://keycloak.127.0.0.1.nip.io");
            assertInitialAccessTokenFromMasterRealm(testAdminClient, "hostname", "http://custom-domain.127.0.0.1.nip.io");
        } finally {
            clearFixedHostname();
        }
    }

    @Test
    public void fixedHostnameAlwaysHttpsHttpsPort() throws Exception {
        // Make sure request are always sent with http
        authServerUrl = "http://localhost:8180/auth";
        oauth.baseUrl(authServerUrl);
        oauth.clientId("direct-grant");
        try (Keycloak testAdminClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), "http://localhost:8180")) {
            assertWellKnown("test", "http://localhost:8180");
            configureFixedHostname((-1), 443, true);
            assertWellKnown("test", "https://keycloak.127.0.0.1.nip.io");
            assertWellKnown("hostname", "https://custom-domain.127.0.0.1.nip.io");
            assertTokenIssuer("test", "https://keycloak.127.0.0.1.nip.io");
            assertTokenIssuer("hostname", "https://custom-domain.127.0.0.1.nip.io");
            assertInitialAccessTokenFromMasterRealm(testAdminClient, "test", "https://keycloak.127.0.0.1.nip.io");
            assertInitialAccessTokenFromMasterRealm(testAdminClient, "hostname", "https://custom-domain.127.0.0.1.nip.io");
        } finally {
            clearFixedHostname();
        }
    }
}

