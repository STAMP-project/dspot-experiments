package org.keycloak.testsuite.openshift;


import Algorithm.HS256;
import Details.CODE_ID;
import Details.REASON;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import OIDCConfigAttributes.ACCESS_TOKEN_LIFESPAN;
import OpenShiftTokenReviewRequestRepresentation.Spec;
import OpenShiftTokenReviewResponseRepresentation.User;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.common.util.Base64Url;
import org.keycloak.crypto.Algorithm;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.protocol.openshift.OpenShiftTokenReviewRequestRepresentation;
import org.keycloak.protocol.openshift.OpenShiftTokenReviewResponseRepresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.util.OAuthClient;


public class OpenShiftTokenReviewEndpointTest extends AbstractTestRealmKeycloakTest {
    private static boolean flowConfigured;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void basicTest() {
        OpenShiftTokenReviewEndpointTest.Review r = new OpenShiftTokenReviewEndpointTest.Review().invoke();
        String userId = testRealm().users().search(r.username).get(0).getId();
        OpenShiftTokenReviewResponseRepresentation.User user = r.response.getStatus().getUser();
        Assert.assertEquals(userId, user.getUid());
        Assert.assertEquals("test-user@localhost", user.getUsername());
        Assert.assertNotNull(user.getExtra());
        r.assertScope("openid", "email", "profile");
    }

    @Test
    public void longExpiration() {
        ClientResource client = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
        ClientRepresentation clientRep = client.toRepresentation();
        try {
            clientRep.getAttributes().put(ACCESS_TOKEN_LIFESPAN, "-1");
            client.update(clientRep);
            // Set time offset just before SSO idle, to get session last refresh updated
            setTimeOffset(1500);
            OpenShiftTokenReviewEndpointTest.Review review = new OpenShiftTokenReviewEndpointTest.Review();
            review.invoke().assertSuccess();
            // Bump last refresh updated again
            setTimeOffset(3000);
            review.invoke().assertSuccess();
            // And, again
            setTimeOffset(4500);
            // Token should still be valid as session last refresh should have been updated
            review.invoke().assertSuccess();
        } finally {
            clientRep.getAttributes().put(ACCESS_TOKEN_LIFESPAN, null);
            client.update(clientRep);
        }
    }

    @Test
    public void hs256() {
        RealmResource realm = adminClient.realm("test");
        RealmRepresentation rep = realm.toRepresentation();
        try {
            rep.setDefaultSignatureAlgorithm(HS256);
            realm.update(rep);
            OpenShiftTokenReviewEndpointTest.Review r = new OpenShiftTokenReviewEndpointTest.Review().algorithm(HS256).invoke().assertSuccess();
            String userId = testRealm().users().search(r.username).get(0).getId();
            OpenShiftTokenReviewResponseRepresentation.User user = r.response.getStatus().getUser();
            Assert.assertEquals(userId, user.getUid());
            Assert.assertEquals("test-user@localhost", user.getUsername());
            Assert.assertNotNull(user.getExtra());
            r.assertScope("openid", "email", "profile");
        } finally {
            rep.setDefaultSignatureAlgorithm(null);
            realm.update(rep);
        }
    }

    @Test
    public void groups() {
        new OpenShiftTokenReviewEndpointTest.Review().username("groups-user").invoke().assertSuccess().assertGroups("topGroup", "level2group");
    }

    @Test
    public void customScopes() {
        ClientScopeRepresentation clientScope = new ClientScopeRepresentation();
        clientScope.setProtocol("openid-connect");
        clientScope.setId("user:info");
        clientScope.setName("user:info");
        testRealm().clientScopes().create(clientScope);
        ClientRepresentation clientRep = testRealm().clients().findByClientId("test-app").get(0);
        testRealm().clients().get(clientRep.getId()).addOptionalClientScope("user:info");
        try {
            oauth.scope("user:info");
            new OpenShiftTokenReviewEndpointTest.Review().invoke().assertSuccess().assertScope("openid", "user:info", "profile", "email");
        } finally {
            testRealm().clients().get(clientRep.getId()).removeOptionalClientScope("user:info");
        }
    }

    @Test
    public void emptyScope() {
        ClientRepresentation clientRep = testRealm().clients().findByClientId("test-app").get(0);
        List<String> scopes = new LinkedList<>();
        for (ClientScopeRepresentation s : testRealm().clients().get(clientRep.getId()).getDefaultClientScopes()) {
            scopes.add(s.getId());
        }
        for (String s : scopes) {
            testRealm().clients().get(clientRep.getId()).removeDefaultClientScope(s);
        }
        oauth.openid(false);
        try {
            new OpenShiftTokenReviewEndpointTest.Review().invoke().assertSuccess().assertEmptyScope();
        } finally {
            oauth.openid(true);
            for (String s : scopes) {
                testRealm().clients().get(clientRep.getId()).addDefaultClientScope(s);
            }
        }
    }

    @Test
    public void expiredToken() {
        try {
            new OpenShiftTokenReviewEndpointTest.Review().runAfterTokenRequest(( i) -> setTimeOffset(((testRealm().toRepresentation().getAccessTokenLifespan()) + 10))).invoke().assertError(401, "Token verification failure");
        } finally {
            resetTimeOffset();
        }
    }

    @Test
    public void invalidPublicKey() {
        new OpenShiftTokenReviewEndpointTest.Review().runAfterTokenRequest(( i) -> {
            String header = i.token.split("\\.")[0];
            String s = new String(Base64Url.decode(header));
            s = s.replace(",\"kid\" : \"", ",\"kid\" : \"x");
            String newHeader = Base64Url.encode(s.getBytes());
            i.token = i.token.replaceFirst(header, newHeader);
        }).invoke().assertError(401, "Token verification failure");
    }

    @Test
    public void noUserSession() {
        new OpenShiftTokenReviewEndpointTest.Review().runAfterTokenRequest(( i) -> {
            String userId = testRealm().users().search(i.username).get(0).getId();
            testRealm().users().get(userId).logout();
        }).invoke().assertError(401, "Token verification failure");
    }

    @Test
    public void invalidTokenSignature() {
        new OpenShiftTokenReviewEndpointTest.Review().runAfterTokenRequest(( i) -> i.token += "x").invoke().assertError(401, "Token verification failure");
    }

    @Test
    public void realmDisabled() {
        RealmRepresentation r = testRealm().toRepresentation();
        try {
            new OpenShiftTokenReviewEndpointTest.Review().runAfterTokenRequest(( i) -> {
                r.setEnabled(false);
                testRealm().update(r);
            }).invoke().assertError(401, null);
        } finally {
            r.setEnabled(true);
            testRealm().update(r);
        }
    }

    @Test
    public void publicClientNotPermitted() {
        ClientRepresentation clientRep = testRealm().clients().findByClientId("test-app").get(0);
        clientRep.setPublicClient(true);
        testRealm().clients().get(clientRep.getId()).update(clientRep);
        try {
            new OpenShiftTokenReviewEndpointTest.Review().invoke().assertError(401, "Public client is not permitted to invoke token review endpoint");
        } finally {
            clientRep.setPublicClient(false);
            testRealm().clients().get(clientRep.getId()).update(clientRep);
        }
    }

    private class Review {
        private String realm = "test";

        private String clientId = "test-app";

        private String username = "test-user@localhost";

        private String password = "password";

        private String algorithm = Algorithm.RS256;

        private OpenShiftTokenReviewEndpointTest.InvokeRunnable runAfterTokenRequest;

        private String token;

        private int responseStatus;

        private OpenShiftTokenReviewResponseRepresentation response;

        public OpenShiftTokenReviewEndpointTest.Review username(String username) {
            this.username = username;
            return this;
        }

        public OpenShiftTokenReviewEndpointTest.Review algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public OpenShiftTokenReviewEndpointTest.Review runAfterTokenRequest(OpenShiftTokenReviewEndpointTest.InvokeRunnable runnable) {
            this.runAfterTokenRequest = runnable;
            return this;
        }

        public OpenShiftTokenReviewEndpointTest.Review invoke() {
            try {
                if ((token) == null) {
                    String userId = testRealm().users().search(username).get(0).getId();
                    oauth.doLogin(username, password);
                    EventRepresentation loginEvent = events.expectLogin().user(userId).assertEvent();
                    String code = oauth.getCurrentQuery().get(CODE);
                    OAuthClient.AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(code, "password");
                    events.expectCodeToToken(loginEvent.getDetails().get(CODE_ID), loginEvent.getSessionId()).detail("client_auth_method", "testsuite-client-dummy").user(userId).assertEvent();
                    token = accessTokenResponse.getAccessToken();
                }
                Assert.assertEquals(algorithm, new JWSInput(token).getHeader().getAlgorithm().name());
                if ((runAfterTokenRequest) != null) {
                    runAfterTokenRequest.run(this);
                }
                CloseableHttpClient client = HttpClientBuilder.create().build();
                String url = ((((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth/realms/") + (realm)) + "/protocol/openid-connect/ext/openshift-token-review/") + (clientId);
                OpenShiftTokenReviewRequestRepresentation request = new OpenShiftTokenReviewRequestRepresentation();
                OpenShiftTokenReviewRequestRepresentation.Spec spec = new OpenShiftTokenReviewRequestRepresentation.Spec();
                spec.setToken(token);
                request.setSpec(spec);
                SimpleHttp.Response r = SimpleHttp.doPost(url, client).json(request).asResponse();
                responseStatus = r.getStatus();
                response = r.asJson(OpenShiftTokenReviewResponseRepresentation.class);
                Assert.assertEquals("authentication.k8s.io/v1beta1", response.getApiVersion());
                Assert.assertEquals("TokenReview", response.getKind());
                client.close();
                return this;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public OpenShiftTokenReviewEndpointTest.Review assertSuccess() {
            Assert.assertEquals(200, responseStatus);
            Assert.assertTrue(response.getStatus().isAuthenticated());
            Assert.assertNotNull(response.getStatus().getUser());
            return this;
        }

        private OpenShiftTokenReviewEndpointTest.Review assertError(int expectedStatus, String expectedReason) {
            Assert.assertEquals(expectedStatus, responseStatus);
            Assert.assertFalse(response.getStatus().isAuthenticated());
            Assert.assertNull(response.getStatus().getUser());
            if (expectedReason != null) {
                EventRepresentation poll = events.poll();
                Assert.assertEquals(expectedReason, poll.getDetails().get(REASON));
            }
            return this;
        }

        private void assertScope(String... expectedScope) {
            List<String> actualScopes = Arrays.asList(response.getStatus().getUser().getExtra().getScopes());
            Assert.assertEquals(expectedScope.length, actualScopes.size());
            Assert.assertThat(actualScopes, containsInAnyOrder(expectedScope));
        }

        private void assertEmptyScope() {
            Assert.assertNull(response.getStatus().getUser().getExtra());
        }

        private void assertGroups(String... expectedGroups) {
            List<String> actualGroups = new LinkedList(response.getStatus().getUser().getGroups());
            Assert.assertEquals(expectedGroups.length, actualGroups.size());
            Assert.assertThat(actualGroups, containsInAnyOrder(expectedGroups));
        }
    }

    private interface InvokeRunnable {
        void run(OpenShiftTokenReviewEndpointTest.Review i);
    }
}

