package org.keycloak.testsuite.client;


import OAuthClient.AccessTokenResponse;
import java.util.function.Supplier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.util.MutualTLSUtils;
import org.keycloak.testsuite.util.OAuthClient;


/**
 * Mutual TLS Client tests.
 */
public class MutualTLSClientTest extends AbstractTestRealmKeycloakTest {
    private static final boolean sslRequired = Boolean.parseBoolean(System.getProperty("auth.server.ssl.required"));

    private static final String CLIENT_ID = "confidential-x509";

    private static final String DISABLED_CLIENT_ID = "confidential-disabled-x509";

    private static final String EXACT_SUBJECT_DN_CLIENT_ID = "confidential-subjectdn-x509";

    private static final String USER = "keycloak-user@localhost";

    private static final String PASSWORD = "password";

    private static final String REALM = "test";

    private static final String EXACT_CERTIFICATE_SUBJECT_DN = "CN=Keycloak, OU=Keycloak, O=Red Hat, L=Boston, ST=MA, C=US";

    @Test
    public void testSuccessfulClientInvocationWithProperCertificate() throws Exception {
        // given
        Supplier<CloseableHttpClient> clientWithProperCertificate = MutualTLSUtils::newCloseableHttpClientWithDefaultKeyStoreAndTrustStore;
        // when
        OAuthClient.AccessTokenResponse token = loginAndGetAccessTokenResponse(MutualTLSClientTest.CLIENT_ID, clientWithProperCertificate);
        // then
        assertTokenObtained(token);
    }

    @Test
    public void testSuccessfulClientInvocationWithProperCertificateAndSubjectDN() throws Exception {
        // given
        Supplier<CloseableHttpClient> clientWithProperCertificate = MutualTLSUtils::newCloseableHttpClientWithDefaultKeyStoreAndTrustStore;
        // when
        OAuthClient.AccessTokenResponse token = loginAndGetAccessTokenResponse(MutualTLSClientTest.EXACT_SUBJECT_DN_CLIENT_ID, clientWithProperCertificate);
        // then
        assertTokenObtained(token);
    }

    @Test
    public void testSuccessfulClientInvocationWithClientIdInQueryParams() throws Exception {
        // given//when
        OAuthClient.AccessTokenResponse token = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            login(MutualTLSClientTest.CLIENT_ID);
            token = getAccessTokenResponseWithQueryParams(MutualTLSClientTest.CLIENT_ID, client);
        }
        // then
        assertTokenObtained(token);
    }

    @Test
    public void testFailedClientInvocationWithProperCertificateAndWrongSubjectDN() throws Exception {
        // given
        Supplier<CloseableHttpClient> clientWithProperCertificate = MutualTLSUtils::newCloseableHttpClientWithOtherKeyStoreAndTrustStore;
        // when
        OAuthClient.AccessTokenResponse token = loginAndGetAccessTokenResponse(MutualTLSClientTest.EXACT_SUBJECT_DN_CLIENT_ID, clientWithProperCertificate);
        // then
        assertTokenNotObtained(token);
    }

    @Test
    public void testFailedClientInvocationWithoutCertificateCertificate() throws Exception {
        // given
        Supplier<CloseableHttpClient> clientWithoutCertificate = MutualTLSUtils::newCloseableHttpClientWithoutKeyStoreAndTrustStore;
        // when
        OAuthClient.AccessTokenResponse token = loginAndGetAccessTokenResponse(MutualTLSClientTest.CLIENT_ID, clientWithoutCertificate);
        // then
        assertTokenNotObtained(token);
    }

    @Test
    public void testFailedClientInvocationWithDisabledClient() throws Exception {
        // given//when
        OAuthClient.AccessTokenResponse token = null;
        try (CloseableHttpClient client = MutualTLSUtils.newCloseableHttpClientWithDefaultKeyStoreAndTrustStore()) {
            login(MutualTLSClientTest.DISABLED_CLIENT_ID);
            disableClient(MutualTLSClientTest.DISABLED_CLIENT_ID);
            token = getAccessTokenResponse(MutualTLSClientTest.DISABLED_CLIENT_ID, client);
        }
        // then
        assertTokenNotObtained(token);
    }
}

