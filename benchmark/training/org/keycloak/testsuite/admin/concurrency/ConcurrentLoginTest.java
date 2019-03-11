/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.admin.concurrency;


import OAuth2Constants.CODE;
import OAuth2Constants.STATE;
import OAuthClient.AccessTokenResponse;
import OAuthClient.AuthorizationEndpointResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.Retry;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.AccessToken;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class ConcurrentLoginTest extends AbstractConcurrencyTest {
    protected static final int DEFAULT_THREADS = 4;

    protected static final int CLIENTS_PER_THREAD = 30;

    protected static final int DEFAULT_CLIENTS_COUNT = (ConcurrentLoginTest.CLIENTS_PER_THREAD) * (ConcurrentLoginTest.DEFAULT_THREADS);

    @Test
    public void concurrentLoginSingleUser() throws Throwable {
        log.info("*********************************************");
        long start = System.currentTimeMillis();
        AtomicReference<String> userSessionId = new AtomicReference<>();
        ConcurrentLoginTest.LoginTask loginTask = null;
        try (CloseableHttpClient httpClient = getHttpsAwareClient()) {
            loginTask = new ConcurrentLoginTest.LoginTask(httpClient, userSessionId, 100, 1, false, Arrays.asList(createHttpClientContextForUser(httpClient, "test-user@localhost", "password")));
            run(ConcurrentLoginTest.DEFAULT_THREADS, ConcurrentLoginTest.DEFAULT_CLIENTS_COUNT, loginTask);
            int clientSessionsCount = testingClient.testing().getClientSessionsCountInUserSession("test", userSessionId.get());
            Assert.assertEquals((1 + (ConcurrentLoginTest.DEFAULT_CLIENTS_COUNT)), clientSessionsCount);
        } finally {
            long end = (System.currentTimeMillis()) - start;
            log.infof("Statistics: %s", (loginTask == null ? "??" : loginTask.getHistogram()));
            log.info((("concurrentLoginSingleUser took " + (end / 1000)) + "s"));
            log.info("*********************************************");
        }
    }

    @Test
    public void concurrentLoginSingleUserSingleClient() throws Throwable {
        log.info("*********************************************");
        long start = System.currentTimeMillis();
        AtomicReference<String> userSessionId = new AtomicReference<>();
        ConcurrentLoginTest.LoginTask loginTask = null;
        try (CloseableHttpClient httpClient = getHttpsAwareClient()) {
            loginTask = new ConcurrentLoginTest.LoginTask(httpClient, userSessionId, 100, 1, true, Arrays.asList(createHttpClientContextForUser(httpClient, "test-user@localhost", "password")));
            run(ConcurrentLoginTest.DEFAULT_THREADS, ConcurrentLoginTest.DEFAULT_CLIENTS_COUNT, loginTask);
            int clientSessionsCount = testingClient.testing().getClientSessionsCountInUserSession("test", userSessionId.get());
            Assert.assertEquals(2, clientSessionsCount);
        } finally {
            long end = (System.currentTimeMillis()) - start;
            log.infof("Statistics: %s", (loginTask == null ? "??" : loginTask.getHistogram()));
            log.info((("concurrentLoginSingleUserSingleClient took " + (end / 1000)) + "s"));
            log.info("*********************************************");
        }
    }

    @Test
    public void concurrentLoginMultipleUsers() throws Throwable {
        log.info("*********************************************");
        long start = System.currentTimeMillis();
        AtomicReference<String> userSessionId = new AtomicReference<>();
        ConcurrentLoginTest.LoginTask loginTask = null;
        try (CloseableHttpClient httpClient = getHttpsAwareClient()) {
            loginTask = new ConcurrentLoginTest.LoginTask(httpClient, userSessionId, 100, 1, false, Arrays.asList(createHttpClientContextForUser(httpClient, "test-user@localhost", "password"), createHttpClientContextForUser(httpClient, "john-doh@localhost", "password"), createHttpClientContextForUser(httpClient, "roleRichUser", "password")));
            run(ConcurrentLoginTest.DEFAULT_THREADS, ConcurrentLoginTest.DEFAULT_CLIENTS_COUNT, loginTask);
            int clientSessionsCount = testingClient.testing().getClientSessionsCountInUserSession("test", userSessionId.get());
            Assert.assertEquals(((1 + ((ConcurrentLoginTest.DEFAULT_CLIENTS_COUNT) / 3)) + (((ConcurrentLoginTest.DEFAULT_CLIENTS_COUNT) % 3) <= 0 ? 0 : 1)), clientSessionsCount);
        } finally {
            long end = (System.currentTimeMillis()) - start;
            log.infof("Statistics: %s", (loginTask == null ? "??" : loginTask.getHistogram()));
            log.info((("concurrentLoginMultipleUsers took " + (end / 1000)) + "s"));
            log.info("*********************************************");
        }
    }

    @Test
    public void concurrentCodeReuseShouldFail() throws Throwable {
        log.info("*********************************************");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            OAuthClient oauth1 = new OAuthClient();
            oauth1.init(driver);
            oauth1.clientId("client0");
            OAuthClient.AuthorizationEndpointResponse resp = oauth1.doLogin("test-user@localhost", "password");
            String code = resp.getCode();
            Assert.assertNotNull(code);
            String codeURL = driver.getCurrentUrl();
            AtomicInteger codeToTokenSuccessCount = new AtomicInteger(0);
            AtomicInteger codeToTokenErrorsCount = new AtomicInteger(0);
            AbstractConcurrencyTest.KeycloakRunnable codeToTokenTask = new AbstractConcurrencyTest.KeycloakRunnable() {
                @Override
                public void run(int threadIndex, Keycloak keycloak, RealmResource realm) throws Throwable {
                    log.infof("Trying to execute codeURL: %s, threadIndex: %d", codeURL, threadIndex);
                    OAuthClient.AccessTokenResponse resp = oauth1.doAccessTokenRequest(code, "password");
                    if (((resp.getAccessToken()) != null) && ((resp.getError()) == null)) {
                        codeToTokenSuccessCount.incrementAndGet();
                    } else
                        if (((resp.getAccessToken()) == null) && ((resp.getError()) != null)) {
                            codeToTokenErrorsCount.incrementAndGet();
                        }

                }
            };
            run(ConcurrentLoginTest.DEFAULT_THREADS, ConcurrentLoginTest.DEFAULT_THREADS, codeToTokenTask);
            oauth1.openLogout();
            // Code should be successfully exchanged for the token at max once. In some cases (EG. Cross-DC) it may not be even successfully exchanged
            Assert.assertThat(codeToTokenSuccessCount.get(), Matchers.lessThanOrEqualTo(1));
            Assert.assertThat(codeToTokenErrorsCount.get(), Matchers.greaterThanOrEqualTo(((ConcurrentLoginTest.DEFAULT_THREADS) - 1)));
            log.infof("Iteration %d passed successfully", i);
        }
        long end = (System.currentTimeMillis()) - start;
        log.info((("concurrentCodeReuseShouldFail took " + (end / 1000)) + "s"));
        log.info("*********************************************");
    }

    public class LoginTask implements AbstractConcurrencyTest.KeycloakRunnable {
        private final AtomicInteger clientIndex = new AtomicInteger();

        private final ThreadLocal<OAuthClient> oauthClient = new ThreadLocal<OAuthClient>() {
            @Override
            protected OAuthClient initialValue() {
                OAuthClient oauth1 = new OAuthClient();
                oauth1.init(driver);
                // Add some randomness to state, nonce and redirectUri. Verify that login is successful and "state" and "nonce" will match
                oauth1.stateParamHardcoded(KeycloakModelUtils.generateId());
                oauth1.nonce(KeycloakModelUtils.generateId());
                oauth1.redirectUri((((oauth.getRedirectUri()) + "?some=") + (new Random().nextInt(1024))));
                return oauth1;
            }
        };

        private final CloseableHttpClient httpClient;

        private final AtomicReference<String> userSessionId;

        private final int retryDelayMs;

        private final int retryCount;

        private final AtomicInteger[] retryHistogram;

        private final AtomicInteger totalInvocations = new AtomicInteger();

        private final boolean sameClient;

        private final List<HttpClientContext> clientContexts;

        public LoginTask(CloseableHttpClient httpClient, AtomicReference<String> userSessionId, int retryDelayMs, int retryCount, boolean sameClient, List<HttpClientContext> clientContexts) {
            this.httpClient = httpClient;
            this.userSessionId = userSessionId;
            this.retryDelayMs = retryDelayMs;
            this.retryCount = retryCount;
            this.retryHistogram = new AtomicInteger[retryCount];
            for (int i = 0; i < (retryHistogram.length); i++) {
                retryHistogram[i] = new AtomicInteger();
            }
            this.sameClient = sameClient;
            this.clientContexts = clientContexts;
        }

        @Override
        public void run(int threadIndex, Keycloak keycloak, RealmResource realm) throws Throwable {
            int i = (sameClient) ? 0 : clientIndex.getAndIncrement();
            OAuthClient oauth1 = oauthClient.get();
            oauth1.clientId(("client" + i));
            log.infof("%d [%s]: Accessing login page for %s", threadIndex, Thread.currentThread().getName(), oauth1.getClientId());
            final HttpClientContext templateContext = clientContexts.get((i % (clientContexts.size())));
            final HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(templateContext.getCookieStore());
            String pageContent = getPageContent(oauth1.getLoginFormUrl(), httpClient, context);
            Assert.assertThat(pageContent, Matchers.containsString("<title>AUTH_RESPONSE</title>"));
            Assert.assertThat(context.getRedirectLocations(), Matchers.notNullValue());
            Assert.assertThat(context.getRedirectLocations(), Matchers.not(Matchers.empty()));
            String currentUrl = context.getRedirectLocations().get(0).toString();
            Map<String, String> query = ConcurrentLoginTest.getQueryFromUrl(currentUrl);
            String code = query.get(CODE);
            String state = query.get(STATE);
            Assert.assertEquals("Invalid state.", state, oauth1.getState());
            AtomicReference<OAuthClient.AccessTokenResponse> accessResRef = new AtomicReference<>();
            totalInvocations.incrementAndGet();
            // obtain access + refresh token via code-to-token flow
            OAuthClient.AccessTokenResponse accessRes = oauth1.doAccessTokenRequest(code, "password");
            Assert.assertEquals((((((("AccessTokenResponse: client: " + (oauth1.getClientId())) + ", error: '") + (accessRes.getError())) + "' desc: '") + (accessRes.getErrorDescription())) + "'"), 200, accessRes.getStatusCode());
            accessResRef.set(accessRes);
            // Refresh access + refresh token using refresh token
            AtomicReference<OAuthClient.AccessTokenResponse> refreshResRef = new AtomicReference<>();
            int invocationIndex = Retry.execute(() -> {
                OAuthClient.AccessTokenResponse refreshRes = oauth1.doRefreshTokenRequest(accessResRef.get().getRefreshToken(), "password");
                Assert.assertEquals((((((("AccessTokenResponse: client: " + (oauth1.getClientId())) + ", error: '") + (refreshRes.getError())) + "' desc: '") + (refreshRes.getErrorDescription())) + "'"), 200, refreshRes.getStatusCode());
                refreshResRef.set(refreshRes);
            }, retryCount, retryDelayMs);
            retryHistogram[invocationIndex].incrementAndGet();
            AccessToken token = JsonSerialization.readValue(getContent(), AccessToken.class);
            Assert.assertEquals("Invalid nonce.", token.getNonce(), oauth1.getNonce());
            AccessToken refreshedToken = JsonSerialization.readValue(getContent(), AccessToken.class);
            Assert.assertEquals("Invalid nonce.", refreshedToken.getNonce(), oauth1.getNonce());
            if ((userSessionId.get()) == null) {
                userSessionId.set(token.getSessionState());
            }
        }

        public int getRetryDelayMs() {
            return retryDelayMs;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public Map<Integer, Integer> getHistogram() {
            Map<Integer, Integer> res = new LinkedHashMap<>(retryCount);
            for (int i = 0; i < (retryHistogram.length); i++) {
                AtomicInteger item = retryHistogram[i];
                res.put((i * (retryDelayMs)), item.get());
            }
            return res;
        }
    }
}

