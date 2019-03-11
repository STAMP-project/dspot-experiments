package org.keycloak.testsuite.admin;


import Algorithm.ES256;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.AdminClientUtil;
import org.keycloak.testsuite.util.TokenSignatureUtil;


public class AdminSignatureAlgorithmTest extends AbstractKeycloakTest {
    private CloseableHttpClient client;

    @Test
    public void changeRealmTokenAlgorithm() throws Exception {
        TokenSignatureUtil.registerKeyProvider("master", "P-256", adminClient, testContext);
        TokenSignatureUtil.changeRealmTokenSignatureProvider("master", adminClient, ES256);
        try (Keycloak adminClient = AdminClientUtil.createAdminClient(suiteContext.isAdapterCompatTesting(), suiteContext.getAuthServerInfo().getContextRoot().toString())) {
            AccessTokenResponse accessToken = adminClient.tokenManager().getAccessToken();
            TokenVerifier<AccessToken> verifier = TokenVerifier.create(accessToken.getToken(), AccessToken.class);
            Assert.assertEquals(ES256, verifier.getHeader().getAlgorithm().name());
            Assert.assertNotNull(adminClient.realms().findAll());
            String whoAmiUrl = (suiteContext.getAuthServerInfo().getContextRoot().toString()) + "/auth/admin/master/console/whoami";
            JsonNode jsonNode = SimpleHttp.doGet(whoAmiUrl, client).auth(accessToken.getToken()).asJson();
            Assert.assertNotNull(jsonNode.get("realm"));
            Assert.assertNotNull(jsonNode.get("userId"));
        }
    }
}

